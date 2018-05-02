package io.goldstone.blockchain.common.component

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.getRealScreenHeight
import com.blinnnk.extension.into
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.EmptyText
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.TokenDetailSize
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.imageView
import org.jetbrains.anko.padding

/**
 * @date 09/04/2018 6:52 PM
 * @author KaySaith
 */

enum class EmptyType {
	TokenDetail, TransactionDetail, Contact, Search, QuotationSearch, Quotation
}

class EmptyView(context: Context) : LinearLayout(context) {

	private val imageSize = (ScreenSize.Width * 0.5).toInt()
	private val introTitles = TwoLineTitles(context)
	private var icon: ImageView

	init {

		orientation = VERTICAL
		gravity = Gravity.CENTER_HORIZONTAL

		layoutParams = LinearLayout.LayoutParams((ScreenSize.Width * 0.6).toInt(), ScreenSize.Width)

		icon = imageView {
			scaleType = ImageView.ScaleType.FIT_XY
			layoutParams = LinearLayout.LayoutParams(imageSize, imageSize)
		}

		introTitles.apply {
			setGrayTitles()
			y -= 30.uiPX().toFloat()
			isCenter = true
		}.into(this)
	}

	fun setStyle(type: EmptyType) {
		when (type) {
			EmptyType.TokenDetail -> {
				x += (ScreenSize.Width * 0.2).toInt()
				y += (context.getRealScreenHeight() - TokenDetailSize.headerHeight - ScreenSize.Width) / 2 + TokenDetailSize.headerHeight - 10.uiPX()
				icon.imageResource = R.drawable.token_detail_empty_icon
				introTitles.title.text = EmptyText.tokenDetailTitle
				introTitles.subtitle.text = EmptyText.tokenDetailSubtitle
			}

			EmptyType.TransactionDetail -> {
				icon.imageResource = R.drawable.transaction_empty_icon
				introTitles.title.text = EmptyText.tokenDetailTitle
				introTitles.subtitle.text = EmptyText.tokenDetailSubtitle
			}

			EmptyType.Contact -> {
				icon.imageResource = R.drawable.contract_empty_icon
				introTitles.title.text = EmptyText.contractTitle
				introTitles.subtitle.text = EmptyText.contractSubtitle
			}

			EmptyType.Search -> {
				icon.imageResource = R.drawable.search_empty_icon
				introTitles.title.text = EmptyText.searchTitle
				introTitles.subtitle.text = EmptyText.searchSubtitle
			}

			EmptyType.QuotationSearch -> {
				icon.imageResource = R.drawable.nopair_icon
				introTitles.title.text = EmptyText.searchTitle
				introTitles.subtitle.text = EmptyText.searchSubtitle
			}

			EmptyType.Quotation -> {
				layoutParams.width = (ScreenSize.Width * 0.8).toInt()
				setPadding(20.uiPX(), 50.uiPX(), 20.uiPX(), 20.uiPX())
				addCorner(CornerSize.default.toInt(), Spectrum.white)
				icon.imageResource = R.drawable.nopair_icon
				introTitles.title.text = EmptyText.searchTitle
				introTitles.subtitle.text = EmptyText.searchSubtitle
			}
		}
	}

}