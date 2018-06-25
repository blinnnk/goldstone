package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.addCircleBorder
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.verticalLayout

/**
 * @date 25/03/2018 9:44 PM
 * @author KaySaith
 */
class WalletSettingsHeader(context: Context) : LinearLayout(context) {
	
	val walletInfo = TwoLineTitles(context)
	val avatarImage = ImageView(context)
	private val copyButton = TextView(context)
	
	init {
		orientation = VERTICAL
		layoutParams = LinearLayout.LayoutParams(ScreenSize.Width - 100.uiPX(), matchParent)
		
		x += 50.uiPX()
		y += 20.uiPX()
		
		gravity = Gravity.CENTER_HORIZONTAL
		
		
		verticalLayout {
			gravity = Gravity.CENTER
			lparams(76.uiPX(), 76.uiPX())
			addCircleBorder(80.uiPX(), 3.uiPX(), Spectrum.white)
			avatarImage
				.apply {
					layoutParams = LinearLayout.LayoutParams(70.uiPX(), 70.uiPX())
				}
				.into(this)
		}
		
		walletInfo
			.apply {
				setBigWhiteStyle(fontSize(16).toInt())
				isCenter = true
				subtitle.gravity = Gravity.CENTER_HORIZONTAL
			}
			.into(this)
		
		copyButton
			.apply {
				text = WalletSettingsText.copy
				textSize = fontSize(12)
				textColor = Spectrum.white
				typeface = GoldStoneFont.black(context)
				gravity = Gravity.CENTER_HORIZONTAL
				addTouchRippleAnimation(Color.TRANSPARENT, Spectrum.green, RippleMode.Round)
				onClick {
					context.clickToCopy(walletInfo.getSubtitleValue())
				}
			}
			.into(this)
		
		walletInfo.setMargins<LinearLayout.LayoutParams> {
			topMargin = 10.uiPX()
		}
		copyButton.setMargins<LinearLayout.LayoutParams> { topMargin = 5.uiPX() }
	}
}