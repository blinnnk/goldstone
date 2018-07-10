package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 25/03/2018 9:44 PM
 * @author KaySaith
 */
class WalletSettingsHeader(context: Context) : LinearLayout(context) {
	
	val walletInfo = TwoLineTitles(context)
	val avatarImage = ImageView(context)
	private val avatarSize = 75.uiPX()
	
	init {
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, matchParent)
		y += 10.uiPX()
		x += PaddingSize.device
		gravity = Gravity.CENTER_VERTICAL
		
		verticalLayout {
			addCorner(avatarSize, Spectrum.white)
			gravity = Gravity.START
			lparams {
				width = avatarSize
				height = avatarSize
				margin = 10.uiPX()
				elevation = 10.uiPX().toFloat()
			}
			avatarImage
				.apply {
					scaleX = 1.01f
					scaleY = 1.01f
					layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
				}
				.into(this)
		}
		
		walletInfo
			.apply {
				setBigWhiteStyle()
				subtitle.gravity = Gravity.CENTER_HORIZONTAL
			}
			.into(this)
		
		
		walletInfo.setMargins<LinearLayout.LayoutParams> {
			leftMargin = 10.uiPX()
		}
	}
}