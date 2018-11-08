package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.content.Context
import android.view.ViewGroup
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.GSCard
import io.goldstone.blockchain.common.component.ProcessType
import io.goldstone.blockchain.common.component.ProgressView
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionProgressModel
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
		progress.setLeftValue(BigInteger.valueOf(model.confirmed.toLong()), "confirmed", ProcessType.Value)
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

}