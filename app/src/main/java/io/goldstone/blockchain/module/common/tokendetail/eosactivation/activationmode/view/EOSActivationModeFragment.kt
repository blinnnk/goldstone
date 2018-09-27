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
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.Config
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
		get() = getParentFragment<TokenDetailOverlayFragment>()?.token?.symbol.orEmpty()
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
					text = TokenDetailText.inactivationAccount + "\n${Config.getCurrentEOSAddress()}"
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
		activationByFriendButton.text = "Active By Friends"
		activationByContractButton.click {
			presenter.showRegisterBySmartContractFragment()
		}.into(this)
		activationByContractButton.setBlueStyle(10.uiPX())
		activationByContractButton.text = "Active By Smart Contract"
		copyAddressButton.click {
			context?.clickToCopy(Config.getCurrentEOSAddress())
		}.into(this)
		copyAddressButton.setBlueStyle(10.uiPX())
		copyAddressButton.text = "Copy Address Manual Activation"
	}

	private fun ViewGroup.showExplanation() {
		explanationTextView.apply {
			setPadding(20.uiPX(), 30.uiPX(), 20.uiPX(), 30.uiPX())
			gravity = Gravity.CENTER_HORIZONTAL
			textSize = fontSize(14)
			typeface = GoldStoneFont.medium(context)
			textColor = GrayScale.gray
			text = "By design, a blockchain is resistant to modification of the data. It is an open, distributed ledger that can record transactions between two parties efficiently and in a verifiable and permanent way. For use as a distributed ledger, a blockchain is typically managed by a peer-to-peer network collectively adhering to a protocol for inter-node communication and validating new blocks. Once recorded, the data in any given block cannot be altered retroactively without alteration of all subsequent blocks, which requires consensus of the network majority."
			layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
		}.into(this)
	}
}