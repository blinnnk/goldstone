package io.goldstone.blockchain.module.home.profile.profile.view

import android.content.Context
import android.widget.TextView
import com.blinnnk.animation.updateOriginYAnimation
import com.blinnnk.extension.into
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.SliderHeader
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.ProfileText
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.textColor

/**
 * @date 26/03/2018 12:13 AM
 * @author KaySaith
 */

class ProfileSlideHeader(context: Context) : SliderHeader(context) {

	private val title = TextView(context)

	init {

		title.apply {
			text = ProfileText.profile
			textColor = Spectrum.white
			textSize = 5.uiPX().toFloat()
			typeface = GoldStoneFont.heavy(context)
		}.into(this)

		title.setCenterInParent()

	}

	override fun onHeaderShowedStyle() {
		super.onHeaderShowedStyle()
		title.updateOriginYAnimation(23.uiPX().toFloat())
	}

	override fun onHeaderHidesStyle() {
		super.onHeaderHidesStyle()
		title.updateOriginYAnimation(34.uiPX().toFloat())
	}

}