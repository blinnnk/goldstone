package io.goldstone.blockchain.common.component

import android.content.Context
import android.support.v7.widget.CardView
import android.view.ViewManager
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.ShadowSize
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.custom.ankoView


/**
 * @author KaySaith
 * @date  2018/11/07
 */
open class GSCard(context: Context) : CardView(context) {

	init {
		maxCardElevation = ShadowSize.Card
		cardElevation = ShadowSize.Card
		preventCornerOverlap = false
		radius = CornerSize.normal
		useCompatPadding = true
		addRippleEffect()
		this.setCardBackgroundColor(Spectrum.white)
	}

	private fun addRippleEffect() {
		val attrs = intArrayOf(R.attr.selectableItemBackground)
		val typedArray = context.obtainStyledAttributes(attrs)
		val selectableItemBackground = typedArray.getResourceId(0, 0)
		typedArray.recycle()
		foreground = context.getDrawable(selectableItemBackground)
		isClickable = true
	}

	fun resetCardElevation(size: Float) {
		maxCardElevation = size
		cardElevation = size
	}

	fun resetCornerRadius(radius: Float) {
		this.radius = radius
	}
}

fun ViewManager.gsCard() = gsCard {}
inline fun ViewManager.gsCard(init: GSCard.() -> Unit) = ankoView({ GSCard(it) }, 0, init)