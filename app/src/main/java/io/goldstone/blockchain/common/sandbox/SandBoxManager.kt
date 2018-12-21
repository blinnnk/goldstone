@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package io.goldstone.blockchain.common.sandbox

import android.support.annotation.WorkerThread
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.utils.toJsonArray
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
	
	fun getLanguage(): Int {
		val language = getSandBoxContentByName(languageFileName)
		return if (language.isEmpty()) -1 else language.toInt()
	}
	
	fun updateLanguage(language: Int) {
		updateSandBoxContentByName(languageFileName, language.toString())
	}
	
	fun getCurrency(): String {
		return getSandBoxContentByName(currencyFileName)
	}
	
	fun updateCurrency(currency: String) {
		updateSandBoxContentByName(currencyFileName, currency)
	}
	
	fun recoveryDefaultMarketSelected(defaultMarketList: List<ExchangeTable>) {
		val marketString = getSandBoxContentByName(marketListFileName)
		if (marketString.isNullOrEmpty()) return
		val sandboxMarketList = try {
			Gson().fromJson<List<Int>>(marketString, object : TypeToken<List<Int>>() {}.type)
		} catch (error: Exception) {
			error.printStackTrace()
			return
		}
		if (sandboxMarketList.isNotEmpty()) {
			defaultMarketList.forEach {
				if (sandboxMarketList.contains(it.marketId)) {
					it.isSelected = true
				}
			}
		}
	}
	
	
	fun updateMarketList(newMarketList: List<Int>) {
		updateSandBoxContentByName(marketListFileName, Gson().toJson(newMarketList))
	}
	
	fun updateQuotationPairs(newPairs: List<String>) {
		updateSandBoxContentByName(quotationPairsFileName, Gson().toJson(newPairs))
	}
	
	
	fun recoveryQuotationSelections(@WorkerThread callback: () -> Unit) {
		val quotationSelectionString = getSandBoxContentByName(quotationPairsFileName)
		if (quotationSelectionString.isNullOrEmpty()) return
		val pairList = Gson().fromJson<List<String>>(quotationSelectionString, object : TypeToken<List<String>>() {}.type).toJsonArray()
		if (pairList.size() == 0) {
			callback()
			return
		}
		GoldStoneAPI.getQuotationSelectionsByPairs(pairList) { quotationTables, error ->
			if (!quotationTables.isNullOrEmpty() && error.isNone()) {
				GoldStoneAPI.getCurrencyLineChartData(pairList) { lineChartDataSet, lineChartError ->
					if (!lineChartDataSet.isNullOrEmpty() && lineChartError.isNone()) {
						lineChartDataSet.forEach { lineChartData ->
							quotationTables.find { it.pair == lineChartData.pair }?.apply {
								QuotationSelectionTable.insertSelection(
									QuotationSelectionTable(
										this,
										lineChartData.pointList.toString(),
										true
									)
								)
							}
						}
						callback()
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