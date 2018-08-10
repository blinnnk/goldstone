package io.goldstone.blockchain.module.home.profile.profile.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.component.button.SquareIcon
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.CommonCellSize
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.home.profile.profile.model.ProfileModel
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.rightPadding
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent

/**
 * @date 25/03/2018 10:55 PM
 * @author KaySaith
 */
class ProfileCell(context: Context) : BaseCell(context) {
	
	var upgradeEvent: Runnable? = null
	var model: ProfileModel by observing(ProfileModel()) {
		icon.src = model.icon
		title.text = model.title
		// 为升级按钮准备的特殊样式
		info.text =
			if (model.info.contains(CommonText.new)) {
				info.addTouchRippleAnimation(
					Spectrum.white,
					Spectrum.green,
					RippleMode.Square,
					30.uiPX().toFloat()
				)
				info.textColor = GrayScale.gray
				info.rightPadding = 10.uiPX()
				info.onClick { upgradeEvent?.run() }
				CustomTargetTextStyle(
					CommonText.new,
					model.info,
					Spectrum.red,
					12.uiPX(),
					false,
					false
				)
			} else {
				upgradeEvent = null
				info.addCorner(0, Color.TRANSPARENT)
				info.textColor = Spectrum.opacity5White
				info.rightPadding = 0
				model.info
			}
	}
	private val icon by lazy { SquareIcon(context) }
	private val title by lazy { TextView(context) }
	private val info by lazy { TextView(context) }
	
	init {
		icon.into(this)
		title.apply {
			textColor = Spectrum.white
			textSize = fontSize(15)
			typeface = GoldStoneFont.heavy(context)
			x += CommonCellSize.iconPadding
		}.into(this)
		
		info.apply {
			layoutParams = RelativeLayout.LayoutParams(wrapContent, 30.uiPX())
			leftPadding = 10.uiPX()
			textSize = fontSize(12)
			typeface = GoldStoneFont.heavy(context)
			gravity = Gravity.CENTER_VERTICAL
			x -= CommonCellSize.rightPadding
		}.into(this)
		
		title.setCenterInVertical()
		icon.setCenterInVertical()
		info.apply {
			setCenterInVertical()
			setAlignParentRight()
		}
	}
}