package io.goldstone.blockchain.module.common.tokendetail.eosactivation.activationmode.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AttentionView
import io.goldstone.blockchain.common.language.CreateWalletText
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.activationmode.presenter.EOSActivationModePresenter
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/09/11
 */

class EOSActivationModeFragment : BaseFragment<EOSActivationModePresenter>() {
	private val attentionView by lazy { AttentionView(context!!) }
	override val presenter = EOSActivationModePresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				topPadding = 20.uiPX()
				lparams(matchParent, wrapContent)
				gravity = Gravity.CENTER_HORIZONTAL
				attentionView.apply {
					text = CreateWalletText.attention
					textSize = fontSize(13)
					textColor = Spectrum.white
				}.into(this)
			}
		}
	}
}