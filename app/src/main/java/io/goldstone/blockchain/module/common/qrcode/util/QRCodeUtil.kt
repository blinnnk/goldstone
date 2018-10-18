package com.zxing.demo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import com.google.zxing.*
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import java.io.FileInputStream
import kotlin.concurrent.thread


/**
 * @date: 2018/9/11.
 * @author: yanglihai
 * @description:
 */

object QRCodeUtil {
	
	fun decodeQRCodeImage(path: String, hold:(String) -> Unit) {
		thread {
			parseQRCodeWithRGB(path)?.apply { hold(this) }
		}
	}
	
	
	/**
	 * 以rgb方式解析二维码
	 * [path] 图片路径
	 */
	private fun parseQRCodeWithRGB(path: String ): String? {
		if (path.isBlank()) return null
		
		val options = BitmapFactory.Options()
		options.inJustDecodeBounds = true // 获取新的大小
		val bitmap= BitmapFactory.decodeStream(FileInputStream(path))
		val pixels = IntArray(bitmap.width * bitmap.height)
		bitmap.getPixels(
			pixels,
			0,
			bitmap.width,
			0,
			0,
			bitmap.width,
			bitmap.height
		)
		val rgbLuminanceSource = RGBLuminanceSource(bitmap.width, bitmap.height, pixels)
		val binaryBitmap = BinaryBitmap(GlobalHistogramBinarizer(rgbLuminanceSource))
		var result: String? = null
		try {
			QRCodeReader().decode(
				binaryBitmap,
				HashMap<DecodeHintType, Boolean>().apply {
					put(DecodeHintType.PURE_BARCODE, true)
			})?.apply {
				result = text
			}
		} catch (e: NotFoundException) {
			e.printStackTrace()
			result = e.toString()
		} catch (e: ChecksumException) {
			e.printStackTrace()
			result = e.toString()
		} catch (e: FormatException) {
			e.printStackTrace()
			result = e.toString()
		}
		
		return result
		
	}
	
	fun getFilePathFromCaptureIntent(data: Intent): String? {
		data.apply {
			val uri = data.data
			uri?.apply {
				val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
				val cursor = GoldStoneAPI.context.contentResolver.query(
					uri,
					filePathColumn,
					null,
					null,
					null
				)
				cursor?.apply {
					cursor.moveToFirst()
					val columnIndex = cursor.getColumnIndex(filePathColumn[0])
					val filePath = cursor.getString(columnIndex)
					cursor.close()
					return filePath
					
				}
				
			}
		}
		return null
	}
}
