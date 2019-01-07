@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package io.goldstone.blinnnk.common.sandbox

import android.support.annotation.WorkerThread
import com.blinnnk.extension.forEachOrEnd
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blinnnk.GoldStoneApp
import io.goldstone.blinnnk.common.utils.NetworkUtil
import io.goldstone.blinnnk.common.utils.toJsonArray
import io.goldstone.blinnnk.kernel.commontable.AppConfigTable
import io.goldstone.blinnnk.kernel.commontable.SupportCurrencyTable
import io.goldstone.blinnnk.kernel.network.common.GoldStoneAPI
import io.goldstone.blinnnk.module.home.quotation.quotationsearch.model.ExchangeTable
import io.goldstone.blinnnk.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

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
	private const val quotationRankGlobalName = "quotationRankGlobal"
	
	@WorkerThread
	fun recoveryData(callback: () -> Unit) {
		recoveryLanguage()
		recoveryCurrency()
		recoveryExchangeSelectedStatus()
		recoveryQuotationSelections(callback)
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