package io.goldstone.blockchain.common.utils

import android.content.Context
import android.graphics.Typeface

/**
 * @date 21/03/2018 11:37 PM
 * @author KaySaith
 */

object GoldStoneFont {
	val light: Context.() -> Typeface = fun Context.(): Typeface =
		Typeface.createFromAsset(assets, "font/light.ttf")
	val book: Context.() -> Typeface = fun Context.(): Typeface =
		Typeface.createFromAsset(assets, "font/book.ttf")
	val medium: Context.() -> Typeface = fun Context.(): Typeface =
		Typeface.createFromAsset(assets, "font/medium.ttf")
	val heavy: Context.() -> Typeface = fun Context.(): Typeface =
		Typeface.createFromAsset(assets, "font/heavy.ttf")
	val black: Context.() -> Typeface = fun Context.(): Typeface =
		Typeface.createFromAsset(assets, "font/black.ttf")
	val aleoRegular: Context.() -> Typeface = fun Context.(): Typeface =
		Typeface.createFromAsset(assets, "font/aleo_regular.otf")
	val aleoBold: Context.() -> Typeface = fun Context.(): Typeface =
		Typeface.createFromAsset(assets, "font/aleo_bold.otf")
	val aleoBoldItalic: Context.() -> Typeface = fun Context.(): Typeface =
		Typeface.createFromAsset(assets, "font/aleo_bold_italic.otf")

}