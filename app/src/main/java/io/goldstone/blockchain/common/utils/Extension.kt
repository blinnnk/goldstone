@file:Suppress("UNCHECKED_CAST", "DEPRECATION")

package io.goldstone.blockchain.common.utils

import android.content.Context
import android.graphics.PorterDuff
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.suffix
import com.blinnnk.uikit.uiPX
import com.google.gson.JsonArray
import io.goldstone.blockchain.common.component.overlay.Dashboard
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.crypto.eos.EOSCPUUnit
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.math.BigInteger

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
	cancelAction: () -> Unit,
	action: (EditText?) -> Unit
) {
	val input = linearLayout {
		lparams(matchParent, matchParent)
		setPadding(20.uiPX(), 10.uiPX(), 20.uiPX(), 20.uiPX())
		editText {
			id = ElementID.passwordInput
			layoutParams = LinearLayout.LayoutParams(matchParent, 50.uiPX())
			hintTextColor = GrayScale.Opacity1Black
			textColor = Spectrum.blue
			inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
			hint = CommonText.enterPassword
			textSize = fontSize(14)
			background.mutate().setColorFilter(Spectrum.blue, PorterDuff.Mode.SRC_ATOP)
		}
	}
	Dashboard(this) {
		if (showEditText) showDashboard(
			title,
			input,
			subtitle,
			{ action(it.findViewById(ElementID.passwordInput)) },
			cancelAction
		)
		else showAlert(title, subtitle, CommonText.confirm, cancelAction) {
			action(null)
		}
	}
}

fun <T : Iterable<String>> T.toJsonArray(): JsonArray {
	val stringArray = JsonArray()
	forEach {
		stringArray.add(it)
	}
	return stringArray
}

fun BigInteger.convertToDiskUnit(): String {
	val convertValue = ("$this".length / 3.0).toInt()
	val diskUnit = when (convertValue) {
		0 -> EOSUnit.Byte.value
		1 -> EOSUnit.KB.value
		else -> EOSUnit.MB.value
	}
	val result = CryptoUtils.toTargetUnit(this, convertValue.toDouble(), 1024.0).formatCount(5)
	return result suffix diskUnit
}

fun BigInteger.convertToTimeUnit(): String {
	val convertValue = ("$this".length / 3.0).toInt()
	val sixtyHexadecimal = if (convertValue > 2) (this.toDouble() / 1000 * 1000 / 60).toInt() else 0
	val diskUnit = when {
		convertValue == 0 -> EOSCPUUnit.MUS.value
		convertValue == 1 -> EOSCPUUnit.MS.value
		sixtyHexadecimal == 0 -> EOSCPUUnit.SEC.value
		else -> EOSCPUUnit.MIN.value
	}
	val value = if (convertValue > 2) this / BigInteger.valueOf(1000) * BigInteger.valueOf(1000) else this
	val hexadecimal = if (convertValue > 2) 60.0 else 1000.0
	val decimal = if (convertValue > 2) sixtyHexadecimal else convertValue
	val result = CryptoUtils.toTargetUnit(value, decimal.toDouble(), hexadecimal).formatCount(5)
	return result suffix diskUnit
}

infix fun String.isEmptyThen(other: String): String = if (this.isEmpty()) other else this

@UiThread
fun Fragment.safeShowError(error: Throwable) {
	ErrorDisplayManager(error).show(context)
}

fun <T> List<T>.safeGet(index: Int): T? {
	return if (lastIndex < index) null else get(index)
}
