package io.goldstone.blockchain.common.utils

import android.graphics.LinearGradient
import android.graphics.Shader
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.blinnnk.uikit.ScreenSize
import io.goldstone.blockchain.R.drawable.*
import java.util.regex.Pattern

/**
 * @date 21/03/2018 9:07 PM
 * @author KaySaith
 */

object UIUtils {
	// easy to set gradient color
	fun setGradientColor(
		startColor: Int,
		endColor: Int,
		width: Float = ScreenSize.Width.toFloat(),
		height: Float = ScreenSize.Height.toFloat()
	) =
		LinearGradient(
			0f, 0f, width, height, startColor, endColor, Shader.TileMode.CLAMP
		)

	fun subtractThenHalf(
		first: Int,
		second: Int
	) =
		(first - second) / 2

	fun generateAvatar(id: Int): Int {
		val avatars = arrayListOf(
			avatar_1, avatar_2, avatar_3, avatar_4, avatar_5, avatar_6, avatar_7, avatar_8, avatar_9,
			avatar_10, avatar_11, avatar_12, avatar_13, avatar_14, avatar_15
		)
		return avatars[id % 15]
	}
}

fun String.toUpperCaseFirstLetter(): String {
	isNotEmpty() isTrue {
		if (length == 1) return substring(
			0, 1
		).toUpperCase()
		return substring(
			0, 1
		).toUpperCase() + substring(
			1, length
		)
	} otherwise {
		return ""
	}
}

fun String.replaceWithPattern(
	replace: String = " "
): String {
	return Pattern.compile("\\s+").matcher(this).replaceAll(replace)
}

fun String.removeStartAndEndValue(value: String = "\n"): String {
	var finalValue = this
	if (finalValue.last().toString() == value) {
		finalValue = finalValue.substring(
			0, finalValue.length - 1
		)
	}
	if (finalValue.first().toString() == value) {
		finalValue = finalValue.substring(
			1, finalValue.length
		)
	}
	// 去除前后的空格或回车
	return finalValue
}

private fun String.checkChineseCount(
	callback: (Int) -> Unit = {}
): Boolean {
	var result = false
	var chineseCount = 0
	forEachIndexed { index, char ->
		if (char.isChinese()) {
			result = true
			chineseCount += 1
		}
		if (index == lastIndex) {
			callback(chineseCount)
		}
	}
	return result
}

private fun Char.isChinese(): Boolean {
	val ub = Character.UnicodeBlock.of(this)
	return ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub === Character.UnicodeBlock.GENERAL_PUNCTUATION || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
}
