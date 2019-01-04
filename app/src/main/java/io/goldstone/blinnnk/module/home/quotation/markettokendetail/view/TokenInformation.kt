package io.goldstone.blinnnk.module.home.quotation.markettokendetail.view

import android.content.Context
import android.view.Gravity
import android.widget.RelativeLayout
import com.blinnnk.extension.alignParentBottom
import com.blinnnk.extension.into
import com.blinnnk.extension.orZero
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blinnnk.common.component.cell.GraySquareCell
import io.goldstone.blinnnk.common.component.cell.TopBottomLineCell
import io.goldstone.blinnnk.common.language.QuotationText
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.common.value.PaddingSize
import io.goldstone.blinnnk.crypto.utils.formatCurrency
import io.goldstone.blinnnk.module.home.quotation.markettokendetail.model.TokenInformationModel
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 25/04/2018 9:48 AM
 * @author KaySaith
 */
@Suppress("DEPRECATION")
class TokenInformation(context: Context) : TopBottomLineCell(context) {

	var model: TokenInformationModel by observing(TokenInformationModel()) {
		rank.setSubtitle(model.rankValue)
		availableSupply.setSubtitle(model.availableSupply)
		// 服务器返回的是带 `,` 的字符串这里加工成 `Double`
		marketCap.setSubtitle(
			model.marketCap.replace(",", "")
				.toDoubleOrNull().orZero()
				.formatCurrency() + " " + SharedWallet.getCurrencyCode()
		)
		startDate.setSubtitle(model.startDate)
	}
	private val rank = GraySquareCell(context)
	private val availableSupply = GraySquareCell(context)
	private val marketCap = GraySquareCell(context)
	private val startDate = GraySquareCell(context)

	init {
		setHorizontalPadding(PaddingSize.content.toFloat())
		setTitle(QuotationText.tokenInformation)
		layoutParams = RelativeLayout.LayoutParams(matchParent, 250.uiPX())
		verticalLayout {
			lparams(matchParent, matchParent)
			gravity = Gravity.CENTER_HORIZONTAL
			rank.into(this)
			availableSupply.into(this)
			marketCap.into(this)
			startDate.into(this)

			rank.setTitle(QuotationText.rank)
			availableSupply.setTitle(QuotationText.totalSupply)
			marketCap.setTitle(QuotationText.marketCap)
			startDate.setTitle(QuotationText.startDate)
		}.alignParentBottom()
	}
}