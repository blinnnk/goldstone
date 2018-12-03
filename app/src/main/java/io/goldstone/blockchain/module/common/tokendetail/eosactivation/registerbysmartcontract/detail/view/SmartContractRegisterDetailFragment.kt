package io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbysmartcontract.detail.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.DescriptionView
import io.goldstone.blockchain.common.component.KeyValueView
import io.goldstone.blockchain.common.component.SpaceSplitLine
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.cell.buttonSquareCell
import io.goldstone.blockchain.common.component.title.AttentionView
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.component.title.sessionTitle
import io.goldstone.blockchain.common.component.title.twoLineTitles
import io.goldstone.blockchain.common.language.EOSAccountText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.BorderSize
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbysmartcontract.detail.presenter.SmartContractRegisterDetailPresenter
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/09/25
 */
class SmartContractRegisterDetailFragment : BaseFragment<SmartContractRegisterDetailPresenter>() {

	override val pageTitle: String
		get() = getParentFragment<TokenDetailOverlayFragment>()?.token?.symbol?.symbol.orEmpty()
	private val accountName by lazy {
		arguments?.getString(ArgumentKey.eosAccountRegister)
	}

	private lateinit var smartContractLink: TwoLineTitles
	private val availableResultView by lazy { KeyValueView(context!!) }
	private val copyResultButton by lazy { RoundButton(context!!) }
	override val presenter = SmartContractRegisterDetailPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, matchParent)
				DescriptionView(context).isRegisterBySmartContract().into(this)

				smartContractLink = twoLineTitles {
					isCenter = true
					setDescriptionTitles()
					title.text = EOSAccountText.smartContract
					subtitle.text = "https://github.com/Dappub/signupeoseos"
				}.click {
					context.clickToCopy(it.subtitle.text.toString())
				}
				// 分割线
				SpaceSplitLine(context).apply {
					setStyle(GrayScale.whiteGray, BorderSize.default)
					layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 30.uiPX())
				}.into(this)
				// 提醒界面
				AttentionView(context).isSmartContractRegister().into(this)

				sessionTitle(EOSAccountText.transferTo)
				buttonSquareCell {
					layoutParams = LinearLayout.LayoutParams(ScreenSize.card, wrapContent)
					val contractName = if (SharedValue.isTestEnvironment()) "goldstonenew" else "signupeoseos"
					setTitle(EOSAccountText.receiver)
					setSubtitle(contractName)
					showOnlyCopyButton {
						context.clickToCopy(contractName)
					}
				}
				sessionTitle(EOSAccountText.memoInfo)
				availableResultView.apply {
					text = accountName + "-" + SharedAddress.getCurrentEOS()
				}.into(this)

				copyResultButton.apply {
					setBlueStyle(20.uiPX())
					text = EOSAccountText.copyResult
				}.click {
					availableResultView.apply {
						context.clickToCopy(getContent())
					}
				}.into(this)
			}
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<TokenDetailOverlayFragment> {
			presenter.popFragmentFrom<SmartContractRegisterDetailFragment>()
		}
	}

}