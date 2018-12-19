@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package io.goldstone.blockchain.common.sandbox

import android.support.annotation.WorkerThread
import com.google.gson.Gson
import com.google.gson.JsonArray
import io.goldstone.blockchain.GoldStoneApp
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
	private const val sandBoxName = "sandbox"
	
	private fun getFile(): File {
		val path = "$storagePath/$sandBoxName"
		val file = File(path)
		if (!file.exists()) {
			file.createNewFile()
		}
		return file
	}
	
	fun getSandBoxModel(): SandBoxModel {
		val stringBuilder = StringBuilder("")
		val inputStream = FileInputStream(getFile())
		val buffer = ByteArray(size = 1024)
		var length = inputStream.read(buffer)
		while(length > 0) {
			stringBuilder.append(String(buffer, 0, length))
			length = inputStream.read(buffer)
		}
		inputStream.close()
		return try {
			Gson().fromJson<SandBoxModel>(stringBuilder.toString(), SandBoxModel::class.java)
		} catch (error: Exception) {
			SandBoxModel()
		}
	}
	
	private fun updateSandBoxModel(model: SandBoxModel) {
		val jsonString = Gson().toJson(model)
		val outputStream = FileOutputStream(getFile())
		outputStream.write(jsonString.toByteArray())
		outputStream.flush()
		outputStream.close()
	}
	
	fun updateLanguage(language: Int) {
		val model = getSandBoxModel()
		model.language = language
		updateSandBoxModel(model)
	}
	
	fun getCurrency(): String {
		return getSandBoxModel().currency
	}
	
	fun updateCurrency(currency: String) {
		val model = getSandBoxModel()
		model.currency = currency
		updateSandBoxModel(model)
	}
	
	fun notifyDefaultMarketSelected(defaultMarketList: List<ExchangeTable>) {
		val sandboxMarketList = getSandBoxModel().marketList
		if (sandboxMarketList.isNotEmpty()) {
			defaultMarketList.forEach {
				if (sandboxMarketList.contains(it.marketId)) {
					it.isSelected = true
				}
			}
		}
	}
	
	
	fun updateMarketList(newMarketList: List<Int>) {
		val model = getSandBoxModel()
		model.marketList = newMarketList
		updateSandBoxModel(model)
	}
	
	fun updateQuotationPairs(newPairs: List<String>) {
		val model = getSandBoxModel()
		model.quotationPairs = newPairs
		updateSandBoxModel(model)
	}
	
	
	fun updateSelectionsFromSandboxPairs( @WorkerThread callback: () -> Unit) {
		val pairList = JsonArray().apply {
			getSandBoxModel().quotationPairs.forEach { add(it) }
		}
		if (pairList.size() == 0) {
			callback()
			return
		}
		GoldStoneAPI.getPairsByExactKey(pairList) { selectionTables, error ->
			if (selectionTables != null && error.isNone()) {
				if (selectionTables.isNotEmpty()) {
					GoldStoneAPI.getCurrencyLineChartData(pairList) { lineChartDataSet, lineChartError ->
						if (lineChartDataSet != null && lineChartError.isNone()) {
							lineChartDataSet.forEach { lineChartData ->
								selectionTables.find { it.pair ==  lineChartData.pair}?.apply {
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
	}
	
	
}