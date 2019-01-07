package io.goldstone.blinnnk.module.home.quotation.markettokendetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.alignParentBottom
import com.blinnnk.extension.toUpperCaseFirstLetter
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blinnnk.common.component.button.titleIcon
import io.goldstone.blinnnk.common.component.cell.TopBottomLineCell
import io.goldstone.blinnnk.common.language.QuotationText
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.common.value.PaddingSize
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.common.value.SocialMediaColor
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.module.home.quotation.markettokendetail.model.TokenInformationModel
import org.jetbrains.anko.gridLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * @date 25/04/2018 9:48 AM
 * @author KaySaith
 */
@SuppressLint("ViewConstructor")
@Suppress("DEPRECATION")
class TokenSocialMedia(context: Context, clickEvent: (url: String) -> Unit) : TopBottomLineCell(context) {
	var model: TokenInformationModel by observing(TokenInformationModel()) {
		if (model.socialMedia.isEmpty()) return@observing
		container.apply {
			removeAllViewsInLayout()
			model.socialMedia.forEach { model ->
				titleIcon {
					layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding / 3, wrapContent)
					val iconColor = when {
						model.name.contains("facebook", true) -> SocialMediaColor.facebook
						model.name.contains("twitter", true) -> SocialMediaColor.twitter
						model.name.contains("reddit", true) -> SocialMediaColor.reddit
						model.name.contains("telegram", true) -> SocialMediaColor.telegram
						else -> Spectrum.blue
					}
					setContent(model.iconURL, model.name.toUpperCaseFirstLetter(), iconColor)
				}.click {
					clickEvent(model.url)
				}
			}
		}
	}
	private var container: GridLayout

	init {
		setHorizontalPadding(PaddingSize.content.toFloat())
		setTitle(QuotationText.socimalMedia)
		layoutParams = RelativeLayout.LayoutParams(matchParent, wrapContent)
		minimumHeight = 140.uiPX()
		container = gridLayout {
			layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
			rowCount = 1
			columnCount = 3
		}
		container.alignParentBottom()
	}
}