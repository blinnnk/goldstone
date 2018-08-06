package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.scaleTo
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.GraySqualCell
import io.goldstone.blockchain.common.component.TopBottomLineCell
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.QuotationText
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.TokenInformationModel
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
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
	private val website = GraySqualCell(context).apply { showArrow() }
	private val whitePaper = GraySqualCell(context).apply { showArrow() }
	
	init {
		setHorizontalPadding(PaddingSize.device.toFloat())
		setTitle(QuotationText.tokenInfoLink)
		layoutParams = RelativeLayout.LayoutParams(matchParent, 150.uiPX())
		verticalLayout {
			website.into(this)
			whitePaper.into(this)
			
			website.setTitle(QuotationText.website)
			whitePaper.setTitle(QuotationText.whitePaper)
		}.setAlignParentBottom()
	}
}