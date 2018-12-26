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
import io.goldstone.blockchain.common.component.container.BorderCardView
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.component.title.twoLineTitles
import io.goldstone.blockchain.common.language.EmptyText
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize.heightWithOutHeader
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.imageView
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

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
	QuotationManagement,
	Quotation,
	WalletDetail,
	NotificationList,
	RAMTransactions,
	LatestUsedDAPP
}

class EmptyView(context: Context) : LinearLayout(context) {

	private val imageSize = (ScreenSize.Width * 0.4).toInt()
	private var introTitles: TwoLineTitles
	private var icon: ImageView
	private val emptyViewHeight = imageSize + 60.uiPX()

	init {
		id = ElementID.emptyView
		orientation = VERTICAL
		gravity = Gravity.CENTER_HORIZONTAL

		layoutParams = LinearLayout.LayoutParams((ScreenSize.Width * 0.6).toInt(), wrapContent)

		icon = imageView {
			scaleType = ImageView.ScaleType.FIT_XY
			layoutParams = LinearLayout.LayoutParams(imageSize, imageSize)
		}

		introTitles = twoLineTitles {
			layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
			setGrayTitles()
			y -= 30.uiPX().toFloat()
			isCenter = true
		}
	}

	fun setStyle(type: EmptyType) {
		when (type) {
			EmptyType.TokenDetail -> {
				x += (ScreenSize.Width * 0.2).toInt()
				y += (heightWithOutHeader - TokenDetailSize.headerHeight - emptyViewHeight - 60.uiPX()) / 2 + TokenDetailSize.headerHeight
				icon.imageResource = R.drawable.token_detail_empty_icon
				introTitles.title.text = EmptyText.tokenDetailTitle
				introTitles.subtitle.text = EmptyText.tokenDetailSubtitle
			}

			EmptyType.WalletDetail -> {
				val viewSize = 300.uiPX()
				val hasEnoughSpace =
					WalletDetailSize.headerHeight + viewSize +
						HomeSize.tabBarHeight <
						context.getRealScreenHeight()
				val topValue: (modulus: Float) -> Float = {
					(context.getRealScreenHeight() - WalletDetailSize.headerHeight - viewSize * it) / 2f +
						WalletDetailSize.headerHeight - (context.getRealScreenHeight() - viewSize * it) / 2f +
						HomeSize.tabBarHeight
				}
				val centerY =
					if (hasEnoughSpace) topValue(1f)
					else {
						val modulus = 0.8f
						scaleX = modulus
						scaleY = modulus
						topValue(modulus)
					}
				y = centerY
				layoutParams =
					LinearLayout.LayoutParams(300.uiPX(), 300.uiPX())
				icon.apply {
					layoutParams = LinearLayout.LayoutParams(120.uiPX(), 120.uiPX())
					addCorner(60.uiPX(), Spectrum.opacity3White)
					imageResource = R.drawable.receipt_empty_icon
					scaleType = ImageView.ScaleType.CENTER_INSIDE
					alpha = 0.2f
				}
				introTitles.apply {
					setOpacityWhiteStyle()
					title.text = EmptyText.tokenDetailTitle
					subtitle.text = EmptyText.tokenDetailSubtitle
					y += 45.uiPX()
				}
			}

			EmptyType.NotificationList -> {
				icon.imageResource = R.drawable.notification_list_empty_icon
				introTitles.title.text = EmptyText.notificationListTitle
				introTitles.subtitle.text = EmptyText.notificationListSubtitle
			}
			EmptyType.RAMTransactions -> {
				icon.imageResource = R.drawable.ram_transactions_empty
				introTitles.title.text = EmptyText.ramTransactionSearchTitle
				introTitles.subtitle.text = EmptyText.ramTransactionSearchSubTitle
			}

			EmptyType.LatestUsedDAPP -> {
				icon.imageResource = R.drawable.dapp_empty_icon
				introTitles.title.text = EmptyText.latestUsedDAPP
				introTitles.subtitle.text = EmptyText.latestUsedDAPPSubtitle
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

			EmptyType.QuotationManagement -> {
				icon.imageResource = R.drawable.pair_selection_empty_icon
				introTitles.title.text = EmptyText.quotationManagementTitle
				introTitles.subtitle.text = EmptyText.quotationManagementSubtitle
			}
			EmptyType.QuotationSearch -> {
				icon.imageResource = R.drawable.nopair_icon
				introTitles.title.text = EmptyText.searchTitle
				introTitles.subtitle.text = EmptyText.searchSubtitle
			}
			EmptyType.Quotation -> {
				y = 100.uiPX().toFloat()
				removeView(introTitles)
				removeView(icon)
				layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
				BorderCardView(context).apply {
					setTitles(
						QuotationText.addQuotationChartPlaceholderTitle,
						QuotationText.addQuotationChartPlaceholderSubtitle
					)
				}.into(this)
			}
		}
	}
}