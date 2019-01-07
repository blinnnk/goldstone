package io.goldstone.blinnnk.module.common.tokendetail.eosactivation.registerbysmartcontract.detail.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import com.blinnnk.util.getParentFragment
import io.goldstone.blinnnk.common.base.basefragment.BaseFragment
import io.goldstone.blinnnk.common.component.DescriptionView
import io.goldstone.blinnnk.common.component.SpaceSplitLine
import io.goldstone.blinnnk.common.component.ValueView
import io.goldstone.blinnnk.common.component.button.RoundButton
import io.goldstone.blinnnk.common.component.button.roundButton
import io.goldstone.blinnnk.common.component.cell.buttonSquareCell
import io.goldstone.blinnnk.common.component.title.AttentionView
import io.goldstone.blinnnk.common.component.title.TwoLineTitles
import io.goldstone.blinnnk.common.component.title.sessionTitle
import io.goldstone.blinnnk.common.component.title.twoLineTitles
import io.goldstone.blinnnk.common.component.valueView
import io.goldstone.blinnnk.common.language.EOSAccountText
import io.goldstone.blinnnk.common.sharedpreference.SharedAddress
import io.goldstone.blinnnk.common.sharedpreference.SharedValue
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.common.value.BorderSize
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.module.common.tokendetail.eosactivation.registerbysmartcontract.detail.presenter.SmartContractRegisterDetailPresenter
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blinnnk.module.home.home.view.MainActivity
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
	private lateinit var availableResultView: ValueView
	private lateinit var copyResultButton: RoundButton
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
				availableResultView = valueView {
					setContent(accountName + "-" + SharedAddress.getCurrentEOS())
				}

				copyResultButton = roundButton {
					setBlueStyle(20.uiPX())
					text = EOSAccountText.copyResult
				}.click {
					availableResultView.apply {
						context.clickToCopy(getContent())
					}
				}
			}
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<TokenDetailOverlayFragment> {
			presenter.popFragmentFrom<SmartContractRegisterDetailFragment>()
		}
	}

}