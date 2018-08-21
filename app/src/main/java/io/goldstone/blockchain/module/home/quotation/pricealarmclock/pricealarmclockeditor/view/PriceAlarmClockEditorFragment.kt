package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclockeditor.view

import android.content.res.Resources
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AlarmCell
import io.goldstone.blockchain.common.component.AlarmTypeView
import io.goldstone.blockchain.common.component.PriceAlarmClockCreatorView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.cell.TopBottomLineCell
import io.goldstone.blockchain.common.component.overlay.ContentScrollOverlayView
import io.goldstone.blockchain.common.component.overlay.DashboardOverlay
import io.goldstone.blockchain.common.language.AlarmClockText
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclockeditor.presenter.PriceAlarmClockEditorPresenter
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclockoverlay.view.PriceAlarmClockOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationFragment
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 14/08/2018 6:52 PM
 * @author wcx
 */
class PriceAlarmClockEditorFragment : BaseFragment<PriceAlarmClockEditorPresenter>() {

  private val priceAlarmClockInfo by lazy {
    arguments?.getSerializable(ArgumentKey.priceAlarmClockEditorInfo) as? PriceAlarmClockTable
  }
  private var priceAlarmClockCreatorView: PriceAlarmClockCreatorView? = null
  private var alarmCell: AlarmCell? = null
  private var alarmTypeView: AlarmTypeView? = null

  override val presenter = PriceAlarmClockEditorPresenter(this)

  override fun AnkoContext<Fragment>.initView() {
    scrollView {
      lparams(
        matchParent,
        matchParent
      )
      verticalLayout {
        object : TopBottomLineCell(context) {
          init {
            orientation = VERTICAL
          }
        }.apply {
          y += 20.uiPX()
          layoutParams = LinearLayout.LayoutParams(
            matchParent,
            185.uiPX()
          ).apply {
            bottomMargin = 20.uiPX()
            leftMargin = PaddingSize.device
            rightMargin = PaddingSize.device
          }
          setTitle(
            AlarmClockText.alarmPriceValue,
            fontSize(14),
            GrayScale.midGray
          )

          alarmCell = AlarmCell(context).apply {
            showArrow()
            setTimeTitle(AlarmClockText.priceThreshold)
            getPriceValueTextView(this)
            setAlarmInfoSubtitle("${priceAlarmClockInfo?.marketName} pro ${priceAlarmClockInfo?.pairDisplay}")
            onClick {
              getParentFragment<PriceAlarmClockOverlayFragment> {
                overlayView.apply {
                  DashboardOverlay(context) {
                    priceAlarmClockCreatorView = PriceAlarmClockCreatorView(context).apply {
                      setTitle(AlarmClockText.modifyAlarm)
                      setPriceType(priceAlarmClockInfo?.priceType)
                      setTargetPriceEditTextListener(priceAlarmClockInfo)
                      setPriceChooseContent(priceAlarmClockInfo)
                      setCurrencyName(priceAlarmClockInfo?.currencyName)

                      val moreThanCell = getMoreThanCell()
                      val lessThanCell = getLessThanCell()
                      moreThanCell.setOnClickListener {
                        moreThanCell.setSwitchStatusBy(true)
                        lessThanCell.setSwitchStatusBy(false)
                        setPriceType(0)
                        setAutomaticChoosePriceType(true)
                      }

                      lessThanCell.setOnClickListener {
                        moreThanCell.setSwitchStatusBy(false)
                        lessThanCell.setSwitchStatusBy(true)
                        setPriceType(1)
                        setAutomaticChoosePriceType(true)
                      }
                    }
                    (priceAlarmClockCreatorView as LinearLayout).into(this)
                  }.apply {
                    confirmEvent = Runnable {
                      priceAlarmClockInfo?.price = priceAlarmClockCreatorView?.getTargetPriceEditTextContent()
                      priceAlarmClockInfo?.priceType = priceAlarmClockCreatorView?.getPriceType()
                      alarmCell?.let { getPriceValueTextView(it) }
                    }
                  }.into(this)
                }
              }
            }
          }
          addCustomizeView(alarmCell as RelativeLayout)
        }.into(this)

        AlarmTypeView(context).apply {
          layoutParams = LinearLayout.LayoutParams(
            matchParent, wrapContent
          ).apply { topMargin = 20.uiPX() }
          showAlarmTypeTitleTextView()
          setAlarmType(priceAlarmClockInfo?.alarmType)
          if (priceAlarmClockInfo?.alarmType == 0) {
            getRepeatingCell().setSwitchStatusBy(true)
            getOneTimeCell().setSwitchStatusBy(false)
          } else {
            getRepeatingCell().setSwitchStatusBy(false)
            getOneTimeCell().setSwitchStatusBy(true)
          }
          alarmTypeView = this
        }.into(this)

        textView {
          text = AlarmClockText.modifyDescription
          layoutParams = LinearLayout.LayoutParams(
            matchParent,
            wrapContent
          ).apply {
            topMargin = 30.uiPX()
            leftMargin = PaddingSize.device
            rightMargin = PaddingSize.device
            bottomMargin = 40.uiPX()
          }
          gravity = Gravity.CENTER
          setCenterInHorizontal()
        }

        RoundButton(context).apply {
          click {
            getConfirmEvent().run()
          }
          text = CommonText.confirm
          setBlueStyle(Resources.getSystem().displayMetrics.widthPixels)
          layoutParams = RelativeLayout.LayoutParams(
            matchParent,
            45.uiPX()
          ).apply {
            leftMargin = PaddingSize.device
            rightMargin = PaddingSize.device
          }
          setCenterInHorizontal()
        }.into(this)
      }.lparams {
        width = matchParent
        height = matchParent
      }
    }
  }

  fun getPriceAlarmInfo(): PriceAlarmClockTable? {
    return priceAlarmClockInfo
  }

  fun getConfirmEvent(): Runnable {
    return Runnable {
      // 点击事件
      val formatEnglishDate = TimeUtils.formatEnglishDate(System.currentTimeMillis())
      priceAlarmClockCreatorView?.let {
        priceAlarmClockInfo?.price = it.getTargetPriceEditTextContent()
        priceAlarmClockInfo?.priceType = it.getPriceType()
      }
      priceAlarmClockInfo?.let {
        it.alarmType = alarmTypeView?.getAlarmType()
        it.createTime = formatEnglishDate
        presenter.modifyAlarmClock(it) {
          getParentFragment<PriceAlarmClockOverlayFragment> {
            presenter.removeSelfFromActivity()
            getHandler()?.sendEmptyMessage(0)
          }
        }
      }
    }
  }

  private fun getPriceValueTextView(alarmCell: AlarmCell) {
    priceAlarmClockInfo?.apply {
      if (priceType == 0) {
        alarmCell.setAlarmInfoTitle("1 $symbol > ${price.toString()} $currencyName")
      } else {
        alarmCell.setAlarmInfoTitle("1 $symbol < ${price.toString()} $currencyName")
      }
    }
  }

  override fun setBaseBackEvent(
    activity: MainActivity?,
    parent: Fragment?
  ) {
    val overlay = getParentContainer()
      ?.findViewById<ContentScrollOverlayView>(ElementID.contentScrollview)
    if (overlay.isNull()) {
      super.setBaseBackEvent(activity, parent)
      // 恢复回退事件
      activity?.getHomeFragment()
        ?.findChildFragmentByTag<QuotationFragment>(FragmentTag.quotation)
        ?.apply {
          updateBackEvent()
        }
    } else {
      // 如果存在悬浮层销毁悬浮层
      overlay?.remove()
    }
  }
}