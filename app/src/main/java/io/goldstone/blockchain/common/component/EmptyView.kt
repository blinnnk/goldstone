package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.getRealScreenHeight
import com.blinnnk.extension.into
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.imageView

/**
 * @date 09/04/2018 6:52 PM
 * @author KaySaith
 */

enum class EmptyType {
	TokenDetail,
	TransactionDetail,
	Contact,
	Search,
	QuotationSearch,
	Quotation,
	WalletDetail
}

class EmptyView(context: Context) : LinearLayout(context) {

	private val imageSize = (ScreenSize.Width * 0.5).toInt()
	private val introTitles = TwoLineTitles(context)
	private var icon: ImageView
	private val emptyViewHeight = ScreenSize.Width

	init {
		id = ElementID.emptyView
		orientation = VERTICAL
		gravity = Gravity.CENTER_HORIZONTAL

		layoutParams = LinearLayout.LayoutParams((ScreenSize.Width * 0.6).toInt(), emptyViewHeight)

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
			EmptyType.TokenDetail       -> {
				x += (ScreenSize.Width * 0.2).toInt()
				y += (context.getRealScreenHeight() - TokenDetailSize.headerHeight - emptyViewHeight) / 2 + TokenDetailSize.headerHeight - 10.uiPX()
				icon.imageResource = R.drawable.token_detail_empty_icon
				introTitles.title.text = EmptyText.tokenDetailTitle
				introTitles.subtitle.text = EmptyText.tokenDetailSubtitle
			}

			EmptyType.WalletDetail       -> {
				y = (context.getRealScreenHeight() - WalletDetailSize.heightHeight - emptyViewHeight) / 2 + WalletDetailSize.heightHeight - 140.uiPX() * 1f
					icon.imageResource = R.drawable.token_detail_empty_icon
				introTitles.title.text = EmptyText.tokenDetailTitle
				introTitles.subtitle.text = EmptyText.tokenDetailSubtitle
			}

			EmptyType.TransactionDetail -> {
				icon.imageResource = R.drawable.transaction_empty_icon
				introTitles.title.text = EmptyText.tokenDetailTitle
				introTitles.subtitle.text = EmptyText.tokenDetailSubtitle
			}

			EmptyType.Contact           -> {
				icon.imageResource = R.drawable.contract_empty_icon
				introTitles.title.text = EmptyText.contractTitle
				introTitles.subtitle.text = EmptyText.contractSubtitle
			}

			EmptyType.Search            -> {
				icon.imageResource = R.drawable.search_empty_icon
				introTitles.title.text = EmptyText.searchTitle
				introTitles.subtitle.text = EmptyText.searchSubtitle
			}

			EmptyType.QuotationSearch   -> {
				icon.imageResource = R.drawable.nopair_icon
				introTitles.title.text = EmptyText.searchTitle
				introTitles.subtitle.text = EmptyText.searchSubtitle
			}

			EmptyType.Quotation         -> {
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