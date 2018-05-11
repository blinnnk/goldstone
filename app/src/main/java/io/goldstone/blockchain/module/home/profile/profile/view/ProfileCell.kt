package io.goldstone.blockchain.module.home.profile.profile.view

import android.content.Context
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.component.SquareIcon
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.CommonCellSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.profile.profile.model.ProfileModel
import org.jetbrains.anko.textColor

/**
 * @date 25/03/2018 10:55 PM
 * @author KaySaith
 */

class ProfileCell(context: Context) : BaseCell(context) {

	var model: ProfileModel by observing(ProfileModel()) {
		icon.src = model.icon
		title.text = model.title
		info.text = model.info
	}

	private var hasLayouted = false

	var isCenterInVertical: Boolean by observing(false) {
		if(isCenterInVertical) {
			title.setCenterInVertical()
			icon.setCenterInVertical()
			info.apply {
				setCenterInVertical()
				setAlignParentRight()
			}
			arrowY = 0f
		} else {
			title.setAlignParentBottom()
			icon.setAlignParentBottom()
			info.apply {
				setAlignParentBottom()
				setAlignParentRight()
			}
			if (!hasLayouted) {
				title.y -= 19.uiPX().toFloat()
				icon.y -= 17.uiPX()
				info.y -= 20.uiPX()
				arrowY += 16.uiPX().toFloat()
			}
			hasLayouted = true
		}
	}

	private val icon by lazy { SquareIcon(context) }
	private val title by lazy { TextView(context) }
	private val info by lazy { TextView(context) }

	init {
		icon.into(this)
		title.apply {
			textColor = Spectrum.white
			textSize = 5.uiPX().toFloat()
			typeface = GoldStoneFont.heavy(context)
			x += CommonCellSize.iconPadding
		}.into(this)

		info.apply {
			textColor = Spectrum.opacity5White
			textSize = 4.uiPX().toFloat()
			typeface = GoldStoneFont.heavy(context)
			x -= CommonCellSize.rightPadding
		}.into(this)

	}

}