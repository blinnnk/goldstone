package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.view

import android.content.Context
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.uikit.RippleMode
import io.goldstone.blockchain.common.component.button.ButtonMenu
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.textColor

/**
 * @date: 2018-11-13.
 * @author: yangLiHai
 * @description:
 */
class TradingDashboardMenu(context: Context) : ButtonMenu(context) {
	override fun selected(index: Int) {
		getButton {
			it.apply {
				textColor = if (id == index) {
					if (index == 0) {
						addTouchRippleAnimation(Spectrum.green, Spectrum.yellow, RippleMode.Square, CornerSize.small)
					} else {
						addTouchRippleAnimation(Spectrum.lightRed, Spectrum.yellow, RippleMode.Square, CornerSize.small)
					}
					Spectrum.white
				} else {
					addTouchRippleAnimation(Spectrum.white, GrayScale.lightGray, RippleMode.Square, CornerSize.small)
					GrayScale.midGray
				}
			}
		}
	}
	
}