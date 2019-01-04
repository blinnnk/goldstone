@file:Suppress("UNCHECKED_CAST", "DEPRECATION")

package io.goldstone.blockchain.common.utils

import android.content.Context
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.view.View
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.suffix
import com.google.gson.JsonArray
import io.goldstone.blockchain.crypto.eos.EOSCPUUnit
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.alert
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.math.BigDecimal
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
	val result = CryptoUtils.toTargetUnit(toBigDecimal(), convertValue.toDouble(), 1024.0).formatCount(5)
	return result suffix diskUnit
}

/**
 * 这里需要处理的是, 因为我们的业务在 CPU 价格膨胀的时候
 * 用户余额 CPU 可能出现负值, 这也是我们的需求.
 * 所以这里需要处理负值的一部分显示问题.
 */
fun BigInteger.musTimeStampConverter(): String {
	val isNegative = toDouble() < 0.0
	val musValue = Math.abs(toDouble())
	val msValue = musValue / 1000.0
	val secValue = msValue / 1000.0
	val minValue = secValue / 60.0
	val prefix = if (isNegative) "-" else ""
	return when {
		msValue < 0.1 -> "$prefix$musValue" suffix EOSCPUUnit.MUS.value
		msValue in 0.1 .. 1000.0 -> "$prefix$msValue" suffix EOSCPUUnit.MS.value
		secValue in 0.1 .. 60.0 -> "$prefix${secValue.formatCount(5)}" suffix EOSCPUUnit.SEC.value
		minValue >= 0.1 -> "$prefix${minValue.formatCount(5)}" suffix EOSCPUUnit.MIN.value
		else -> "$prefix${minValue.formatCount(5)}" suffix EOSCPUUnit.MIN.value
	}
}

infix fun String.isEmptyThen(other: String): String = if (this.isEmpty()) other else this
infix fun <T> List<T>.isEmptyThen(other: List<T>): List<T> = if (this.isEmpty()) other else this

@UiThread
fun Fragment.safeShowError(error: Throwable) {
	ErrorDisplayManager(error).show(context)
}

fun String.removeSlash(): String {
	// 有些数据会出现 "{" 前面有多个 "\""
	return replace("\"{", "{")
		.replace("\"{", "{")
		.replace("}\"", "}")
		.replace("}\"", "}")
		.replace("\\\"", "\"")
}
