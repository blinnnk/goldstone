@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package io.goldstone.blockchain.common.sandbox
import android.support.annotation.WorkerThread
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.kernel.commontable.AppConfigTable
import io.goldstone.blockchain.kernel.commontable.SupportCurrencyTable
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import java.io.*
import java.lang.Exception

/**
 * @date: 2018-12-05.
 * @author: yangLiHai
 * @description:
 */
object SandBoxManager {
	private val storagePath = GoldStoneApp.appContext.getExternalFilesDir(null).absolutePath
	private val sandBoxPath = "$storagePath/sandbox"
	private const val languageFileName = "language"
	private const val currencyFileName = "currency"
	private const val marketListFileName = "marketList"
	private const val quotationPairsFileName = "quotationPairs"
	
	@WorkerThread
	fun recoveryData(callback: () -> Unit) {
		recoveryLanguage()
		recoveryCurrency()
		recoveryMarketSelectedStatus()
		recoveryQuotationSelections(callback)
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
	fun updateMarketList(newMarketList: List<Int>) {
		updateSandBoxContentByName(marketListFileName, Gson().toJson(newMarketList))
	}
	@WorkerThread
	fun updateQuotationPairs(newPairs: List<String>) {
		updateSandBoxContentByName(quotationPairsFileName, Gson().toJson(newPairs))
	}
	
	private fun recoveryLanguage() {
		val language = getSandBoxContentByName(languageFileName)
		if (!language.isNullOrEmpty()) {
			AppConfigTable.dao.updateLanguageCode(language.toInt())
			language.toInt()
		}
	}
	
	private fun recoveryCurrency() {
		val currency = getSandBoxContentByName(currencyFileName)
		if (!currency.isNullOrEmpty()) {
			AppConfigTable.dao.updateCurrency(currency)
			SupportCurrencyTable.dao.setCurrentCurrencyUnused()
			SupportCurrencyTable.dao.setCurrencyInUse(currency)
		}
	}
	
	private fun recoveryMarketSelectedStatus() {
		val defaultMarketList = ExchangeTable.dao.getAll()
		val marketListString = getSandBoxContentByName(marketListFileName)
		if (marketListString.isNullOrEmpty()) return
		val sandboxMarketList = try {
			Gson().fromJson<List<Int>>(marketListString, object : TypeToken<List<Int>>() {}.type)
		} catch (error: Exception) {
			error.printStackTrace()
			return
		}
		if (sandboxMarketList.isNotEmpty()) {
			defaultMarketList.forEach {
				if (sandboxMarketList.contains(it.marketId)) {
					it.isSelected = true
				}
				ExchangeTable.dao.update(it)
			}
		}
		
		
	}
	
	private fun recoveryQuotationSelections(callback: () -> Unit) {
		val quotationSelectionListString = getSandBoxContentByName(quotationPairsFileName)
		val pairList = try {
			Gson().fromJson<List<String>>(quotationSelectionListString, object : TypeToken<List<String>>() {}.type).toJsonArray()
		} catch (error: Exception) {
			error.printStackTrace()
			callback()
			return
		}
		if (NetworkUtil.hasNetwork()
			&& !quotationSelectionListString.isEmpty()
			&& pairList.size() != 0
		) {
			GoldStoneAPI.getQuotationSelectionsByPairs(pairList) { quotationTables, error ->
				if (!quotationTables.isNullOrEmpty() && error.isNone()) {
					GoldStoneAPI.getCurrencyLineChartData(pairList) { lineChartDataSet, lineChartError ->
						if (!lineChartDataSet.isNullOrEmpty() && lineChartError.isNone()) {
							lineChartDataSet.forEach { lineChartData ->
								quotationTables.find { it.pair == lineChartData.pair }?.apply {
									QuotationSelectionTable.insertSelection(
										QuotationSelectionTable(this, lineChartData.pointList.toString(), true)
									)
								}
							}
							callback()
						} else {
							callback()
						}
					}
				} else {
					callback()
				}
			}
		} else {
			callback()
		}
	}
	
	
	private fun getSandBoxContentByName(fileName: String): String {
		val sandBoxFile = File("${getDirectory().absolutePath}/$fileName")
		if (!sandBoxFile.exists()) sandBoxFile.createNewFile()
		val stringBuilder = StringBuilder("")
		val inputStream = FileInputStream(sandBoxFile)
		val buffer = ByteArray(size = 1024)
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
		val file = File(sandBoxPath)
		if (!file.exists()) {
			file.mkdirs()
		}
		return file
	}
	
	
}