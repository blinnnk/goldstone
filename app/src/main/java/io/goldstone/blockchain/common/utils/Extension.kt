@file:Suppress("UNCHECKED_CAST")

package io.goldstone.blockchain.common.utils

import android.content.Context
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.View
import android.widget.EditText
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.safeGet
import com.blinnnk.uikit.uiPX
import com.google.gson.JsonArray
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.json.JSONArray
import org.json.JSONObject

/**
 * @date 21/03/2018 11:12 PM
 * @author KaySaith
 */
/**
 * `View` 的便捷链式调用的方法
 * */
fun <T : View> T.click(callback: (T) -> Unit): T {
	onClick {
		callback(this@click)
		preventDuplicateClicks()
	}
	return this
}

fun Fragment.getMainActivity() = activity as? MainActivity

fun Context.getMainActivity() = this as? MainActivity

fun Context?.alert(message: String) {
	this?.alert(Appcompat, message)?.show()
}

fun Context.showAlertView(
	title: String,
	subtitle: String,
	showEditText: Boolean = true,
	cancelAction: () -> Unit = {},
	action: (EditText?) -> Unit
) {
	var input: EditText? = null
	alert(subtitle, title) {
		showEditText isTrue {
			customView {
				verticalLayout {
					lparams {
						padding = 20.uiPX()
					}
					input = editText {
						hintTextColor = GrayScale.Opacity1Black
						textColor = Spectrum.blue
						inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
						hint = CommonText.enterPassword
					}
				}
			}
		}
		onCancelled {
			cancelAction()
		}
		yesButton { if (showEditText) input?.apply(action) else action(input) }
		noButton { cancelAction() }
	}.show()
}

fun <T : Iterable<String>> T.toJsonArray(): JsonArray {
	val stringArray = JsonArray()
	forEach {
		stringArray.add(it)
	}
	return stringArray
}

fun String.showAfterColonContent(): String {
	return if (contains(":")) toString().substringAfter(":")
	else this
}

fun <T> Fragment.getGrandFather(): T? {
	return try {
		parentFragment?.parentFragment as? T
	} catch (error: Exception) {
		LogUtil.error("getGrandFather", error)
		null
	}
}

fun JSONArray.toList(): List<String> {
	var result = listOf<String>()
	(0 until length()).forEach {
		result += get(it).toString()
	}
	return result
}

// 自己解 `SONObject` 的时候用来可能用 `String` 判断 `Null`
fun String.isNullValue(): Boolean {
	return contains("null")
}

infix fun String.suffix(content: String) = this + " " + content

fun Long.convertToDiskUnit(): String {
	val convertValue = ("$this".length / 3.0).toInt()
	val diskUnit = when (convertValue) {
		0 -> EOSUnit.Byte.value
		1 -> EOSUnit.KB.value
		else -> EOSUnit.MB.value
	}
	val result = CryptoUtils.toTargetUnit(this, convertValue.toDouble(), 1024.0).formatCount(5)
	return result suffix diskUnit
}

fun Long.convertToTimeUnit(): String {
	val convertValue = ("$this".length / 3.0).toInt()
	val sixtyHexadecimal = if (convertValue > 2) (this / 1000 * 1000 / 60).toInt() else 0
	val diskUnit = when {
		convertValue == 0 -> EOSUnit.MUS.value
		convertValue == 1 -> EOSUnit.MS.value
		sixtyHexadecimal == 0 -> EOSUnit.SEC.value
		else -> EOSUnit.MIN.value
	}
	val value = if (convertValue > 2) this / 1000 * 1000 else this
	val hexadecimal = if (convertValue > 2) 60.0 else 1000.0
	val decimal = if (convertValue > 2) sixtyHexadecimal else convertValue
	val result = CryptoUtils.toTargetUnit(value, decimal.toDouble(), hexadecimal).formatCount(5)
	return result suffix diskUnit
}

@Throws
fun JSONObject.getTargetChild(vararg keys: String): String {
	try {
		var willGetChildObject = this
		keys.forEachIndexed { index, content ->
			if (index == keys.lastIndex) return@forEachIndexed
			willGetChildObject = JSONObject(willGetChildObject.safeGet(content))
		}
		return willGetChildObject.safeGet(keys.last())
	} catch (error: Exception) {
		throw Exception("goldstone getTargetChild has error")
	}
}

@Throws
fun JSONObject.getTargetObject(vararg keys: String): JSONObject {
	try {
		var willGetChildObject = this
		keys.forEach {
			willGetChildObject = JSONObject(willGetChildObject.safeGet(it))
		}
		return willGetChildObject
	} catch (error: Exception) {
		throw Exception("goldstone getTargetObject has error")
	}
}
