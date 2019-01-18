@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
package io.goldstone.blinnnk.common.sandbox

import android.content.Context
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.afollestad.materialdialogs.list.getListAdapter
import com.blinnnk.extension.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blinnnk.GoldStoneApp
import io.goldstone.blinnnk.common.component.overlay.Dashboard
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.sandbox.view.wallet.RecoverWalletAdapter
import io.goldstone.blinnnk.common.thread.launchDefault
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.NetworkUtil
import io.goldstone.blinnnk.common.utils.toJsonArray
import io.goldstone.blinnnk.crypto.multichain.CryptoValue
import io.goldstone.blinnnk.kernel.commontable.AppConfigTable
import io.goldstone.blinnnk.kernel.commontable.SupportCurrencyTable
import io.goldstone.blinnnk.kernel.network.common.GoldStoneAPI
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blinnnk.module.home.quotation.quotationsearch.model.ExchangeTable
import io.goldstone.blinnnk.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONException
import java.io.*


/**
 * @date: 2018-12-05.
 * @author: yangLiHai
 * @description:
 */
object SandBoxManager {
	private const val languageFileName = "language"
	private const val currencyFileName = "currency"
	private const val marketListFileName = "marketList"
	private const val quotationPairsFileName = "quotationPairs"
	private const val walletTableFileName = "walletTable"
	private const val quotationRankGlobalName = "quotationRankGlobal" // 不用恢复的内容
	
	// 是否有需要恢复的数据
	@WorkerThread
	fun hasExtraSandBoxData(): Boolean {
		val directoryFiles = getDirectory().listFiles()
		return if (directoryFiles.isEmpty()) {
			false
		} else {
			!directoryFiles.none {
				it.name != quotationRankGlobalName
					&& it.name != walletTableFileName
					&& it.length() > 0
			}
		}
	}
	
	@WorkerThread
	fun hasWalletData(): Boolean {
		val directoryFiles = getDirectory().listFiles()
		return if (directoryFiles.isEmpty()) {
			false
		} else {
			directoryFiles.any {
				it.name == walletTableFileName && it.length() > 0
			}
		}
	}
	
	// 清除沙盒数据
	@WorkerThread
	fun cleanSandBox() {
		cleanKeyStoreFile(GoldStoneApp.appContext.filesDir)
		val directoryFiles = getDirectory().listFiles()
		if (!directoryFiles.isEmpty()) {
			directoryFiles.forEach {
				it.delete()
			}
		}
	}
	
	@WorkerThread
	fun recoveryExtraData(callback: () -> Unit) {
		recoveryLanguage()
		recoveryCurrency()
		recoveryExchangeSelectedStatus()
		recoveryQuotationSelections(callback)
	}
	
	fun getLanguage(): Int? {
		return getSandBoxContentByName(languageFileName).toIntOrNull()
	}
	
	@WorkerThread
	fun updateQuotationRankGlobal(data: String) {
		updateSandBoxContentByName(quotationRankGlobalName, data)
	}
	
	@WorkerThread
	fun getQuotationRankGlobalData(): String {
		return getSandBoxContentByName(quotationRankGlobalName)
	}

	@WorkerThread
	fun updateCurrency(currency: String) {
		updateSandBoxContentByName(currencyFileName, currency)
	}

	@WorkerThread
	fun updateLanguage(language: Int) {
		updateSandBoxContentByName(languageFileName, language.toString())
	}

	@WorkerThread
	fun updateMyExchanges(newMarketList: List<Int>) {
		updateSandBoxContentByName(marketListFileName, Gson().toJson(newMarketList))
	}

	@WorkerThread
	fun updateQuotationPairs(newPairs: List<String>) {
		updateSandBoxContentByName(quotationPairsFileName, Gson().toJson(newPairs))
	}
	
	@WorkerThread
	fun updateWalletTables() {
		WalletTable.getAll {
			if (isNullOrEmpty()) {
				updateSandBoxContentByName(walletTableFileName, "")
			} else {
				updateSandBoxContentByName(walletTableFileName, Gson().toJson(this.map {
					WalletBackUpModel(it)
				}))
			}
			
		}
		
	}
	
	fun recoveryWallet(context: Context, @WorkerThread callback: () -> Unit) {
		showRecoveryWalletDashboard(context) { hasFinish, finalWalletID ->
			launchDefault {
				if (finalWalletID.isNotNull()) {
					WalletTable.dao.getWalletByID(finalWalletID)?.apply {
						WalletTable.dao.updateLastUsingWalletOff()
						WalletTable.dao.update(this.apply { isUsing = true })
					}
				}
				if (hasFinish) {
					// 更新sandbox中的数据
					updateWalletTables()
					callback()
				}
			}
		}
	}
	private fun recoveryLanguage() {
		val language = getSandBoxContentByName(languageFileName)
		if (language.isNotEmpty()) {
			AppConfigTable.dao.updateLanguageCode(language.toInt())
		}
	}

	private fun recoveryCurrency() {
		val currency = getSandBoxContentByName(currencyFileName)
		if (currency.isNotEmpty()) {
			AppConfigTable.dao.updateCurrency(currency)
			SupportCurrencyTable.dao.setCurrentCurrencyUnused()
			SupportCurrencyTable.dao.setCurrencyInUse(currency)
		}
	}

	private fun recoveryExchangeSelectedStatus() {
		val databaseMarkets = ExchangeTable.dao.getAll()
		val marketListString = getSandBoxContentByName(marketListFileName)
		if (marketListString.isEmpty()) return
		val sandboxMarketList = try {
			Gson().fromJson<List<Int>>(marketListString, object : TypeToken<List<Int>>() {}.type)
		} catch (error: Exception) {
			error.printStackTrace()
			return
		}
		if (sandboxMarketList.isNotEmpty()) {
			databaseMarkets.forEachOrEnd { item, isEnd ->
				if (sandboxMarketList.contains(item.marketId)) item.isSelected = true
				if (isEnd) ExchangeTable.dao.insertAll(databaseMarkets)
			}
		}
	}

	private fun recoveryQuotationSelections(callback: () -> Unit) {
		val quotationListString = getSandBoxContentByName(quotationPairsFileName)
		val models = try {
			Gson().fromJson<List<String>>(quotationListString, object : TypeToken<List<String>>() {}.type).toJsonArray()
		} catch (error: Exception) {
			callback()
			return
		}
		if (NetworkUtil.hasNetwork() && quotationListString.isNotEmpty() && models.size() > 0) {
			GoldStoneAPI.getQuotationByPairs(models) { quotations, error ->
				if (!quotations.isNullOrEmpty() && error.isNone()) {
					GoldStoneAPI.getCurrencyLineChartData(models) { lineChart, lineChartError ->
						if (!lineChart.isNullOrEmpty() && lineChartError.isNone()) {
							lineChart.forEach { lineChartData ->
								quotations.find {
									it.pair.equals(lineChartData.pair, true)
								}?.apply {
									QuotationSelectionTable.insertSelection(
										QuotationSelectionTable(this, lineChartData.pointList.toString(), true)
									)
								}
							}
							callback()
						} else callback()
					}
				} else callback()
			}
		} else callback()
	}
	
	private fun showRecoveryWalletDashboard(context: Context, @UiThread callback: (hasFinish: Boolean, lastWalletID: Int?) -> Unit) {
		val walletModelListString = getSandBoxContentByName(walletTableFileName)
		val models = if (walletModelListString.isEmpty()) arrayListOf()
		else try {
			JSONArray(walletModelListString).toJSONObjectList().map { WalletBackUpModel(it) }.toArrayList()
		} catch (error: JSONException) {
			arrayListOf<WalletBackUpModel>()
		}
		if (models.isNotEmpty()) {
			WalletTable.dao.getAllWalletIDs().forEach { walletID ->
				if (models.any { walletID == it.id }) models.find { it.id == walletID }?.let { models.remove(it) }
			}
		}
		SharedSandBoxValue.updateRestOfWalletCount(models.size)
		if (models.isNotEmpty()) {
			launchUI {
				Dashboard(context) {
					cancelOnTouchOutside()
					showList(
						"Recovery Wallets",
						RecoverWalletAdapter(
							models,
						deleteAction = { position ->
							val walletID = models[position].id
							launchDefault { context.deleteKeystoreFileByWalletID(walletID) }
							models.removeAt(position)
							SharedSandBoxValue.updateRestOfWalletCount(models.size)
							getDialog {
								getListAdapter()?.notifyDataSetChanged()
								context.toast(CommonText.succeed)
							}
							if (models.isEmpty()) {
								dismiss()
								callback(true, null)
							}
						},
						recoverAction = { position ->
							recoveryWalletByType(context, models[position]) { walletID ->
								launchUI {
									models.removeAt(position)
									SharedSandBoxValue.updateRestOfWalletCount(models.size)
									getDialog {
										getListAdapter()?.notifyDataSetChanged()
										context.toast(CommonText.succeed)
									}
									if (models.isEmpty()) dismiss()
									callback(models.isEmpty(), walletID)
								}
							}
						}))
				}.getDialog {
					setCancelable(false)
					negativeButton(text = "DELETE ALL") {
						dismiss()
						callback(true, null)
						launchDefault {
							models.forEach { wallet ->
								context.deleteKeystoreFileByWalletID(wallet.id)
							}
						}
					}
				}
			}
		} else launchUI { callback(true, null) }
	}
	
	private fun recoveryWalletByType(
		context: Context,
		wallet: WalletBackUpModel,
		@WorkerThread callback: (walletID: Int?) -> Unit
	) {
		launchDefault {
			when {
				wallet.isWatchOnly -> {
					// 观察钱包
					recoveryWatchOnlyWallet(wallet) {
						callback(wallet.id)
					}
				}
				!wallet.encryptMnemonic.isNullOrEmpty() -> {
					// 助记词钱包
					recoveryMnemonicWallet(context, wallet) { isSuccess ->
						if (isSuccess) callback(wallet.id)
					}
				}
				else -> {
					// keystore 钱包
					recoveryKeystoreWallet(context, wallet) { isSuccess ->
						if (isSuccess) callback(wallet.id)
					}
				}
			}
		}
		
	}
	
	private fun getSandBoxContentByName(fileName: String): String {
		val sandBoxFile = File("${getDirectory().absolutePath}/$fileName")
		if (!sandBoxFile.exists()) sandBoxFile.createNewFile()
		val stringBuilder = StringBuilder("")
		val inputStream = FileInputStream(sandBoxFile)
		val buffer = ByteArray(1024)
		var length = inputStream.read(buffer)
		while (length > 0) {
			stringBuilder.append(String(buffer, 0, length))
			length = inputStream.read(buffer)
		}
		inputStream.close()
		return stringBuilder.toString()
	}

	private fun updateSandBoxContentByName(fileName: String, text: String) {
		val sandBoxFile = File("${getDirectory().absolutePath}/$fileName")
		if (!sandBoxFile.exists()) sandBoxFile.createNewFile()
		val outputStream = FileOutputStream(sandBoxFile)
		outputStream.write(text.toByteArray())
		outputStream.flush()
		outputStream.close()
	}

	private fun getDirectory(): File {
		val storagePath = GoldStoneApp.appContext.filesDir.absolutePath
		val sandBoxPath = "$storagePath/sandbox"
		val file = File(sandBoxPath)
		if (!file.exists()) file.mkdirs()
		return file
	}
	
	private fun cleanKeyStoreFile(dir: File): Boolean {
		if (dir.isDirectory) {
			val children = dir.list()
			for (index in children.indices) {
				val success = cleanKeyStoreFile(File(dir, children[index]))
				if (!success) return false
			}
		}
		// The directory is now empty so delete it
		return dir.delete()
	}
	
	
	private fun Context.deleteKeystoreFileByWalletID(walletID: Int) {
		val filename = "$walletID${CryptoValue.keystoreFilename}"
		val keystoreFile = File(filesDir!!, filename)
		if (keystoreFile.exists()) {
			if (keystoreFile.isDirectory) {
				keystoreFile.listFiles().forEach { child ->
					child.delete()
				}
			}
			keystoreFile.delete()
		}
	}
}