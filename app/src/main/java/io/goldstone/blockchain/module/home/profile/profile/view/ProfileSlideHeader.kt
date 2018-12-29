package io.goldstone.blockchain.module.home.profile.profile.view

import android.content.Context
import android.widget.TextView
import com.blinnnk.extension.centerInParent
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.SliderHeader
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.textColor

/**
 * @date 26/03/2018 12:13 AM
 * @author KaySaith
 */
class ProfileSlideHeader(context: Context) : SliderHeader(context) {

	private val title = TextView(context)

	init {
		title.apply {
			text = ProfileText.settings
			textColor = Spectrum.white
			textSize = fontSize(15)
			typeface = GoldStoneFont.heavy(context)
		}.into(this)
		title.centerInParent()
		title.y = 5.uiPX().toFloat()
	}
}