@file:Suppress("UNCHECKED_CAST")

package io.goldstone.blockchain.common.utils

import android.content.Context
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.View
import android.widget.EditText
import com.blinnnk.extension.forEachOrEnd
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import com.google.gson.JsonArray
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.sdk25.coroutines.onClick

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

fun Context.alert(message: String) {
	alert(Appcompat, message).show()
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
		yesButton { if (showEditText) input?.apply(action) else action(input) }
		noButton { cancelAction() }
	}.show()
}

fun <T : Iterable<String>> T.toJsonArray(callback: (JsonArray) -> Unit) {
	val stringArray = JsonArray()
	forEachOrEnd { item, isEnd ->
		stringArray.add(item)
		if (isEnd) callback(stringArray)
	}
}

fun String.getDecimalCount(): Int? {
	return if (contains('.')) {
		val integerPlaces = indexOf('.')
		length - integerPlaces - 1
	} else {
		null
	}
}

fun String.trimAfterColonSYmbol(): String {
	return toString().trimStart {
		it.toString().startsWith(":", true)
	}
}