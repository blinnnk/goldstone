package io.goldstone.blinnnk.module.home.wallet.transactions.transactiondetail.view

import android.content.Context
import android.view.ViewGroup
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blinnnk.common.component.GSCard
import io.goldstone.blinnnk.common.component.ProcessType
import io.goldstone.blinnnk.common.component.ProgressView
import io.goldstone.blinnnk.common.language.TransactionText
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.*
import io.goldstone.blinnnk.module.home.wallet.transactions.transactiondetail.model.TransactionProgressModel
import org.jetbrains.anko.*
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/11/08
 */
class TransactionProgressCardView(context: Context) : GSCard(context) {
	private val paddingSize = 5.uiPX()
	private lateinit var progress: ProgressView
	var model: TransactionProgressModel by observing(TransactionProgressModel()) {
		progress.setRightValue(BigInteger.valueOf(model.totalCount), TransactionText.irreversible, ProcessType.Value)
		progress.setLeftValue(BigInteger.valueOf(model.confirmed.toLong()), TransactionText.confirmed, ProcessType.Value)
	}

	init {
		setContentPadding(PaddingSize.overlay, 20.uiPX(), PaddingSize.overlay, PaddingSize.overlay)
		verticalLayout {
			lparams(matchParent, wrapContent)
			id = ElementID.cardLayout
			textView {
				setPadding(paddingSize, 0, paddingSize, 0)
				layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
				textColor = Spectrum.deepBlue
				textSize = fontSize(12)
				typeface = GoldStoneFont.black(context)
				text = TransactionText.process
			}
			progress = ProgressView(context).apply {
				layoutParams = ViewGroup.LayoutParams(matchParent, 40.uiPX())
				removeTitles()
			}
			addView(progress)
		}
	}

	init {
		resetCardElevation(ShadowSize.Cell)
	}

}