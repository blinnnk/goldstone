package io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbysmartcontract.detail.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.DescriptionView
import io.goldstone.blockchain.common.component.KeyValueView
import io.goldstone.blockchain.common.component.SpaceSplitLine
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.cell.GraySquareCellWithButtons
import io.goldstone.blockchain.common.component.title.AttentionView
import io.goldstone.blockchain.common.component.title.SessionTitleView
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbysmartcontract.detail.presenter.SmartContractRegisterDetailPresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.verticalLayout


/**
 * @author KaySaith
 * @date  2018/09/25
 */
class SmartContractRegisterDetailFragment : BaseFragment<SmartContractRegisterDetailPresenter>() {

	private val accountName by lazy { arguments?.getString(ArgumentKey.eosAccountRegister) }

	private val smartContractLink by lazy { TwoLineTitles(context!!) }
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

				smartContractLink.apply {
					isCenter = true
					setDescriptionTitles()
					title.text = "Smart Contract Address"
					subtitle.text = "https://github.com/kotlin/smartContract"
				}.click {
					context.clickToCopy(it.subtitle.text.toString())
				}.into(this)
				// 分割线
				SpaceSplitLine(context).apply {
					setStyle(GrayScale.whiteGray, BorderSize.default)
					layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 30.uiPX())
				}.into(this)
				// 提醒界面
				AttentionView(context).isSmartContractRegister().into(this)

				SessionTitleView(context).apply { setTitle("TRANSACTION INFO") }.into(this)
				GraySquareCellWithButtons(context).apply {
					val smartContractName = "goldstonenew"
					setTitle("Receiver")
					setSubtitle(smartContractName)
					showOnlyCopyButton {
						context.clickToCopy(smartContractName)
					}
				}.into(this)

				SessionTitleView(context).apply { setTitle("REGISTER INFO") }.into(this)
				availableResultView.apply {
					text = accountName + "-" + Config.getCurrentEOSAddress()
				}.into(this)

				copyResultButton.apply {
					setBlueStyle(20.uiPX())
					text = "Copy The Result"
				}.click {
					availableResultView.apply {
						context.clickToCopy(getContent())
					}
				}.into(this)
			}
		}
	}

}