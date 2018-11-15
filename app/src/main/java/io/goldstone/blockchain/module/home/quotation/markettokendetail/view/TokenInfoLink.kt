package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.cell.GraySquareCell
import io.goldstone.blockchain.common.component.cell.TopBottomLineCell
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.TokenInformationModel
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout

/**
 * @date 25/04/2018 9:48 AM
 * @author KaySaith
 */
@SuppressLint("ViewConstructor")
@Suppress("DEPRECATION")
class TokenInfoLink(
	context: Context,
	clickEvent: (link: String, title: String) -> Unit
) : TopBottomLineCell(context) {
	
	var model: TokenInformationModel by observing(TokenInformationModel()) {
		website.setSubtitle(model.website.scaleTo(28))
		website.onClick {
			clickEvent(model.website, QuotationText.website)
			website.preventDuplicateClicks()
		}
		// Is there white paper link exist
		if (model.whitePaper.isNotEmpty()) {
			whitePaper.setSubtitle(model.whitePaper.scaleTo(28))
			whitePaper.onClick {
				clickEvent(model.whitePaper, QuotationText.whitePaper)
				whitePaper.preventDuplicateClicks()
			}
		} else {
			whitePaper.visibility = View.GONE
			layoutParams.height = 90.uiPX()
		}
	}
	private val website = GraySquareCell(context).apply { showArrow() }
	private val whitePaper = GraySquareCell(context).apply { showArrow() }
	
	init {
		setHorizontalPadding(PaddingSize.device.toFloat())
		setTitle(QuotationText.tokenInfoLink)
		layoutParams = RelativeLayout.LayoutParams(matchParent, 150.uiPX())
		verticalLayout {
			lparams(matchParent, matchParent)
			gravity = Gravity.CENTER_HORIZONTAL

			website.into(this)
			whitePaper.into(this)
			
			website.setTitle(QuotationText.website)
			whitePaper.setTitle(QuotationText.whitePaper)
		}.alignParentBottom()
	}
}