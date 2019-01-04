package io.goldstone.blockchain.module.home.quotation.quotationrank.view

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.ViewGroup
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.GSCard
import io.goldstone.blockchain.common.component.title.twoLineTitles
import io.goldstone.blockchain.common.language.CoinRankText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.home.quotation.quotationrank.model.QuotationGlobalModel
import io.goldstone.blockchain.module.home.quotation.quotationrank.presenter.QuotationRankPresenter
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent


/**
 * @author KaySaith
 * @date  2019/01/02
 */
@SuppressLint("SetTextI18n")
class QuotationRankHeaderView(context: Context) : GSCard(context) {

	var model: QuotationGlobalModel? by observing(null) {
		model?.let {
			linearLayout {
				lparams(matchParent, matchParent)
				gravity = Gravity.CENTER
				headerLabelNames.forEach { name ->
					twoLineTitles {
						layoutParams = LayoutParams(ScreenSize.widthWithPadding / 3, wrapContent)
						isCenter = true
						setBlackTitles(fontSize(16))
						title.typeface = GoldStoneFont.black(context)
						subtitle.text = name
						title.text = when (name) {
							CoinRankText.marketCap ->
								QuotationRankPresenter.parseVolumeText(it.totalMarketCap, true)
							CoinRankText.volume24h ->
								QuotationRankPresenter.parseVolumeText(it.totalVolume, false)
							else -> "${it.btcPercentageMarketCap}%"
						}
					}
				}
			}
		}
	}
	private val headerLabelNames =
		listOf(
			CoinRankText.marketCap,
			CoinRankText.volume24h,
			CoinRankText.btcDominance
		)

	init {
		layoutParams = ViewGroup.LayoutParams(ScreenSize.widthWithPadding, 80.uiPX())
		setCardBackgroundColor(GrayScale.whiteGray)
	}

	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		setMargins<RecyclerView.LayoutParams> {
			margin = PaddingSize.device
		}
	}
}