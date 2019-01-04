package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.widget.RelativeLayout
import com.blinnnk.animation.updateAlphaAnimation
import com.blinnnk.animation.updateColorAnimation
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.value.ShadowSize
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent

/**
 * @date 21/04/2018 3:56 AM
 * @author KaySaith
 */

@SuppressLint("SetTextI18n")
open class SliderHeader(context: Context) : RelativeLayout(context) {

	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, 75.uiPX())
		elevation = ShadowSize.Header
	}

	open fun onHeaderShowedStyle() {
		updateColorAnimation(Color.TRANSPARENT, Spectrum.deepBlue)
	}

	open fun onHeaderHidesStyle() {
		updateColorAnimation(Spectrum.deepBlue, Color.TRANSPARENT)
	}

}