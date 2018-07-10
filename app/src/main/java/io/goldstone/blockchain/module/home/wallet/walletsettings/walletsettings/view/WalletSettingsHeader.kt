package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.extension.addCircleBorder
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 25/03/2018 9:44 PM
 * @author KaySaith
 */
class WalletSettingsHeader(context: Context) : LinearLayout(context) {
	
	val walletInfo = TwoLineTitles(context)
	val avatarImage = ImageView(context)
	
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
		
		
		walletInfo.setMargins<LinearLayout.LayoutParams> {
			topMargin = 10.uiPX()
		}
	}
}