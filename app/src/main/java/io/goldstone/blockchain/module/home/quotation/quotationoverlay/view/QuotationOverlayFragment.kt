package io.goldstone.blockchain.module.home.quotation.quotationoverlay.view

import android.view.ViewGroup
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.presenter.QuotationOverlayPresenter

/**
 * @date 21/04/2018 4:14 PM
 * @author KaySaith
 * @rewriteDate 16/08/2018 16:33 PM
 * @rewriter wcx
 * @description 添加viewAlarmIndicator查看闹铃列表标示
 */
class QuotationOverlayFragment : BaseOverlayFragment<QuotationOverlayPresenter>() {

  private val title by lazy { arguments?.getString(ArgumentKey.quotationOverlayTitle) }
  private val viewAlarmIndicator by lazy { arguments?.getString(ArgumentKey.priceAlarmClockTitle) }
  private val currencyInfo by lazy {
    arguments?.getSerializable(ArgumentKey.quotationOverlayInfo) as? QuotationModel
  }
  override val presenter = QuotationOverlayPresenter(this)
  override fun ViewGroup.initView() {
    when (title) {
      QuotationText.management -> {
        presenter.showQutationManagementFragment()
        overlayView.header.showSearchButton(true) {
          presenter.showQutationSearchFragment()
        }
      }
      else -> presenter.showMarketTokenCenter(
        currencyInfo,
        viewAlarmIndicator
      )
    }

    headerTitle = title ?: currencyInfo?.pairDisplay.orEmpty()
  }
}