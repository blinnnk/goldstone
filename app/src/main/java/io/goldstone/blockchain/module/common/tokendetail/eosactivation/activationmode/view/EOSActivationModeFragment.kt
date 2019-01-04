package io.goldstone.blockchain.module.common.tokendetail.eosactivation.activationmode.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.title.AttentionView
import io.goldstone.blockchain.common.language.EOSAccountText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.activationmode.presenter.EOSActivationModePresenter
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/09/11
 */

class EOSActivationModeFragment : BaseFragment<EOSActivationModePresenter>() {

	override val pageTitle: String
		get() = getParentFragment<TokenDetailOverlayFragment>()?.token?.symbol?.symbol.orEmpty()
	private val activationByFriendButton by lazy {
		RoundButton(context!!)
	}
	private val activationByContractButton by lazy {
		RoundButton(context!!)
	}

	private val copyAddressButton by lazy {
		RoundButton(context!!)
	}
	private val attentionView by lazy { AttentionView(context!!) }
	private val explanationTextView by lazy { TextView(context) }
	override val presenter = EOSActivationModePresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				topPadding = 20.uiPX()
				lparams(matchParent, wrapContent)
				gravity = Gravity.CENTER_HORIZONTAL
				attentionView.apply {
					gravity = Gravity.CENTER_HORIZONTAL
					text = "${EOSAccountText.inactivationAccount} \n${EOSAccountText.publicKey}: ${SharedAddress.getCurrentEOS()}"
					textSize = fontSize(14)
					typeface = GoldStoneFont.black(context)
					textColor = Spectrum.white
					setBackgroundColor(Spectrum.lightRed)
				}.into(this)
				showExplanation()
				showButtons()
			}
		}
	}

	private fun ViewGroup.showButtons() {
		activationByFriendButton.click {
			presenter.showRegisterByFriendFragment()
		}.into(this)
		activationByFriendButton.setBlueStyle()
		activationByFriendButton.text = EOSAccountText.activeByFriend
		activationByContractButton.click {
			presenter.showRegisterBySmartContractFragment()
		}.into(this)
		activationByContractButton.setBlueStyle()
		activationByContractButton.text = EOSAccountText.activeByContract
		copyAddressButton.click {
			context?.clickToCopy(SharedAddress.getCurrentEOS())
		}.into(this)
		copyAddressButton.setBlueStyle()
		copyAddressButton.text = EOSAccountText.activeManually
	}

	private fun ViewGroup.showExplanation() {
		explanationTextView.apply {
			setPadding(20.uiPX(), 30.uiPX(), 20.uiPX(), 30.uiPX())
			gravity = Gravity.CENTER_HORIZONTAL
			textSize = fontSize(14)
			typeface = GoldStoneFont.medium(context)
			textColor = GrayScale.gray
			text = EOSAccountText.inactivationAccountHint
			layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
		}.into(this)
	}
}