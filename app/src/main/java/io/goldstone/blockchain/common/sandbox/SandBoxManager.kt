@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package io.goldstone.blockchain.common.sandbox

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.kernel.commontable.AppConfigTable
import io.goldstone.blockchain.kernel.commontable.SupportCurrencyTable
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import org.json.JSONArray
import java.io.*
import java.util.concurrent.atomic.AtomicInteger

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
	
	private var walletCount = AtomicInteger(0)

	// 是否有需要恢复的数据
	@WorkerThread
	fun hasSandBoxData(): Boolean {
		val directoryFiles = getDirectory().listFiles()
		return if (directoryFiles.isEmpty()) {
			false
		} else {
			var length = 0L
			directoryFiles.forEach {
				length += it.length()
			}
			length != 0L
		}
	}
	
	// 清除沙盒数据
	@WorkerThread
	fun cleanSandBox() {
		val directoryFiles = getDirectory().listFiles()
		if (!directoryFiles.isEmpty()) {
			directoryFiles.forEach {
				it.delete()
			}
		}
	}
	
	@WorkerThread
	fun recoveryData(context: Context, callback: () -> Unit) {
		recoveryLanguage()
		recoveryCurrency()
		recoveryExchangeSelectedStatus()
		recoveryQuotationSelections {
			recoveryWallet(context, callback)
			
		}
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
			updateSandBoxContentByName(walletTableFileName, Gson().toJson(this.map {
				WalletModel(it)
			}))
			
		}
		
	}

	private fun recoveryLanguage() {
		val language = getSandBoxContentByName(languageFileName)
		if (language.isNotEmpty()) {
			AppConfigTable.dao.updateLanguageCode(language.toInt())
			language.toInt()
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
		val pairList = try {
			Gson().fromJson<List<String>>(quotationListString, object : TypeToken<List<String>>() {}.type).toJsonArray()
		} catch (error: Exception) {
			callback()
			return
		}
		if (NetworkUtil.hasNetwork() && quotationListString.isNotEmpty() && pairList.size() > 0) {
			GoldStoneAPI.getQuotationByPairs(pairList) { quotations, error ->
				if (!quotations.isNullOrEmpty() && error.isNone()) {
					GoldStoneAPI.getCurrencyLineChartData(pairList) { lineChart, lineChartError ->
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
	
	private fun recoveryWallet(context: Context, callback: () -> Unit) {
		recoveryWalletTables(context) { finalWalletId ->
			if (finalWalletId.isNull()) {
				WalletTable.getAll {
					if (isNotEmpty()) WalletTable.dao.update(this[0].apply { isUsing = true })
					callback()
				}
			} else {
				WalletTable.dao.getWalletByID(finalWalletId).apply {
					if (this.isNull()) {
						WalletTable.getAll {
							if (isNotEmpty()) WalletTable.dao.update(this[0].apply { isUsing = true })
							callback()
						}
					} else {
						WalletTable.dao.update(this.apply { isUsing = true })
						callback()
					}
				}
			}
		}
	}
	
	private fun recoveryWalletTables(context: Context, callback: (lastWalletID: Int?) -> Unit) {
		val walletModelListString = getSandBoxContentByName(walletTableFileName)
		val pairList = arrayListOf<WalletModel>()
		pairList.apply {
			try {
				val jsonArray = JSONArray(walletModelListString)
				for (index in 0 until jsonArray.length()) {
					add(WalletModel(jsonArray.getJSONObject(index)))
				}
			} catch (error: Exception) {
				error.printStackTrace()
			}
		}
		if (pairList.isNotEmpty()) {
			walletCount = AtomicInteger(pairList.size)
			pairList.forEach {
				when {
					it.isWatchOnly -> {
						// 观察钱包
						recoveryWatchOnlyWallet(it) {
							walletCount.getAndDecrement()
							if (walletCount.get() == 0) callback(null)
						}
					}
					!it.encryptMnemonic.isNullOrEmpty() -> {
						// 助记词钱包
						recoveryMnemonicWallet(context, it) { isSuccess ->
							walletCount.getAndDecrement()
							if (walletCount.get() == 0) callback(if (isSuccess) it.id else null)
						}
					}
					else -> {
						// keystore 钱包
						recoveryKeystoreWallet(context, it) { isSuccess ->
							walletCount.getAndDecrement()
							if (walletCount.get() == 0) callback(if (isSuccess) it.id else null)
						}
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
		val storagePath = GoldStoneApp.appContext.getExternalFilesDir(null).absolutePath
		val sandBoxPath = "$storagePath/sandbox"
		val file = File(sandBoxPath)
		if (!file.exists()) file.mkdirs()
		return file
	}
}