package io.goldstone.blinnnk.module.common.tokendetail.tokeninfo.view

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.suffix
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.component.button.RadiusButton
import io.goldstone.blinnnk.common.component.title.TwoLineTitles
import io.goldstone.blinnnk.common.language.TokenDetailText
import io.goldstone.blinnnk.common.utils.glideImage
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick


/**
 * @author KaySaith
 * @date  2018/09/13
 */
class TokenInfoView(context: Context) : LinearLayout(context) {

	private val qrImage = ImageView(context)
	private val coinInfo = TwoLineTitles(context)
	private val checkDetailButton =
		RadiusButton(context).apply { setTitle(TokenDetailText.checkDetail) }

	init {
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
		topPadding = 15.uiPX()
		qrImage.apply {
			x -= 10.uiPX()
			layoutParams = LinearLayout.LayoutParams(150.uiPX(), 150.uiPX())
		}.into(this)
		verticalLayout {
			lparams(ScreenSize.widthWithPadding - 160.uiPX(), wrapContent)
			topPadding = 20.uiPX()
			coinInfo.apply {
				setBlackTitles(fontSize(14), 5.uiPX())
				layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
				isFloatRight = true
				bottomPadding = 15.uiPX()
			}.into(this)
			checkDetailButton.into(this)
		}
	}

	fun setData(
		code: Bitmap?,
		title: String,
		subtitle: String,
		icon: Int,
		action: () -> Unit
	) {
		qrImage.glideImage(code)
		checkDetailButton.setIcon(icon)
		checkDetailButton.onClick {
			checkDetailButton.preventDuplicateClicks()
			action()
		}
		coinInfo.apply {
			this.title.text = title
			this.subtitle.text = TokenDetailText.latestActivationTime suffix subtitle
		}
	}

	fun setLatestActivation(title: String, subtitle: String) {
		coinInfo.apply {
			this.title.text = title
			this.subtitle.text = TokenDetailText.latestActivationTime suffix subtitle
		}
	}

	fun updateLatestActivationDate(date: String) {
		coinInfo.subtitle.text = TokenDetailText.latestActivationTime suffix date
	}
}