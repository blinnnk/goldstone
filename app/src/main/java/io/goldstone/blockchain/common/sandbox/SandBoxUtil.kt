@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package io.goldstone.blockchain.common.sandbox

import com.google.gson.Gson
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import java.io.*
import java.lang.Exception

/**
 * @date: 2018-12-05.
 * @author: yangLiHai
 * @description:
 */
object SandBoxUtil {
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
		while(length > 0){
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
	
	fun getLanguage(): Int {
		return getSandBoxModel().language
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
	
	fun getMarketList(): List<Int> {
		return getSandBoxModel().marketList
	}
	
	fun updateMarketList(newMarketList: List<Int>) {
		val model = getSandBoxModel()
		model.marketList = newMarketList
		updateSandBoxModel(model)
	}
	
	
}