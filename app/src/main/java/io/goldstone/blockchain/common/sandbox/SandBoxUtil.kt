package io.goldstone.blockchain.common.sandbox

import com.google.gson.Gson
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import java.io.*

/**
 * @date: 2018-12-05.
 * @author: yangLiHai
 * @description:
 */
object SandBoxUtil {
	private val storagePath = GoldStoneAPI.context.filesDir.absolutePath
	private val sandBoxName = "sandbox"
	
	private fun getFile(): File {
		val path = "$storagePath/$sandBoxName"
		val file = File(path)
		if (!file.exists()) {
			file.createNewFile()
		}
		return file
	}
	
	private fun getSandBoxModel(): SandBoxModel? {
		val stringBuilder = StringBuilder("")
		val inputStream = FileInputStream(getFile())
		val buffer = ByteArray(1024)
		var len = inputStream.read(buffer)
		while(len > 0){
			stringBuilder.append(String(buffer, 0, len))
			len = inputStream.read(buffer)
		}
		inputStream.close()
		return Gson().fromJson<SandBoxModel>(stringBuilder.toString(), SandBoxModel::class.java)
	}
	
	private fun updateSandBoxModel(model: SandBoxModel) {
		val jsonString = Gson().toJson(model)
		val outputStream = FileOutputStream(getFile())
		outputStream.write(jsonString.toByteArray())
		outputStream.close()
	}
	
	fun getLanguage(): String? {
		val model = getSandBoxModel()
		return model?.language
	}
	
	fun updateLanguage(language: String) {
		var model = getSandBoxModel()
		if (model == null) {
			model = SandBoxModel()
		}
		model.language = language
		updateSandBoxModel(model)
	}
	
	
	
}