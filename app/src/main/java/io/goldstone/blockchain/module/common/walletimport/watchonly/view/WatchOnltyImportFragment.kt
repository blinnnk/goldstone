package io.goldstone.blockchain.module.common.walletimport.watchonly.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.extension.isNotNull
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AttentionTextView
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.into
import io.goldstone.blockchain.common.utils.setMargins
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.common.walletimport.watchonly.presenter.WatchOnlyImportPresenter
import org.jetbrains.anko.*

/**
 * @date 23/03/2018 2:15 AM
 * @author KaySaith
 */

class WatchOnlyImportFragment : BaseFragment<WatchOnlyImportPresenter>() {

  private val attentionView by lazy { AttentionTextView(context!!) }
  private val addressInput by lazy { RoundInput(context!!) }
  private val confirmButton by lazy { RoundButton(context!!) }

  override val presenter =  WatchOnlyImportPresenter(this)

  @SuppressLint("SetTextI18n")
  override fun AnkoContext<Fragment>.initView() {

    verticalLayout {
      lparams(matchParent, matchParent)

      (attentionView.parent as? ViewGroup)?.apply {
        findViewById<AttentionTextView>(ElementID.attentionText).isNotNull {
          /** 临时解决异常的 `The specified child already has a parent` 错误 */
          removeAllViews()
        }
      }

      attentionView
        .apply {
          setMargins<LinearLayout.LayoutParams> { topMargin = 80.uiPX() }
          text = "You are convinced your brain is working at peak efficiency today, yet you wonder why you continue to run into obstacles that you"
        }
        .into(this)


      addressInput
        .apply {
          setMargins<LinearLayout.LayoutParams> { topMargin = 30.uiPX() }
          text = ImportWalletText.address
        }
        .into(this)

      confirmButton
        .apply {
          marginTop = 20.uiPX()
          setBlueStyle()
          text = CommonText.startImporting.toUpperCase()
        }
        .into(this)

      textView("What is watch only wallet?") {
        textSize = 5.uiPX().toFloat()
        typeface = GoldStoneFont.heavy(context)
        layoutParams = LinearLayout.LayoutParams(ScreenSize.Width, 30.uiPX()).apply {
          topMargin = 20.uiPX()
        }
        textColor = Spectrum.blue
        gravity = Gravity.CENTER
      }
    }
  }

}