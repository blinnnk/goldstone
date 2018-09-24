package io.goldstone.blockchain.module.home.dapp.eosaccountregister.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.view.ColumnSectionTitle
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.cell.GraySquareCell
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.edittext.WalletEditText
import io.goldstone.blockchain.common.component.overlay.DashboardOverlay
import io.goldstone.blockchain.common.component.title.AttentionTextView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.utils.MutablePair
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.convertToDouble
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.crypto.eos.EOSValue
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.module.home.dapp.eosaccountregister.presenter.EOSAccountRegisterPresenter
import org.jetbrains.anko.*
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/21
 */
class EOSAccountRegisterFragment : BaseFragment<EOSAccountRegisterPresenter>() {

	private val confirmButton by lazy { RoundButton(context!!) }
	private val accountNameInput by lazy { RoundInput(context!!) }
	private val publickeyInput by lazy { WalletEditText(context!!) }
	private val settingButton by lazy { GraySquareCell(context!!) }
	private val resourceCoast by lazy { GraySquareCell(context!!) }
	private val gridSessionTitle by lazy { ColumnSectionTitle(context!!) }
	private var assignResources =
		listOf(MutablePair("RAM (Bytes)", "4096"), MutablePair("CPU (EOS)", "0.1"), MutablePair("NET (EOS)", "0.1"))

	override val presenter = EOSAccountRegisterPresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams(matchParent, matchParent)
				gravity = Gravity.CENTER_HORIZONTAL
				AttentionTextView(context).apply {
					isCenter()
					bottomPadding = 20.uiPX()
					text = "The username must be 12 characters long, and the character content can only contain the letters A~Z or the numbers 1~5."
				}.into(this)
				accountNameInput.apply {
					title = ImportWalletText.eosAccountName
					afterTextChanged = Runnable {
						val checker = EOSAccount(getContent()).checker()
						if (checker.isValid()) setValidStatus(true, "Valid")
						else setValidStatus(false, checker.shortDescription)
					}
				}.into(this)

				AttentionTextView(context).apply {
					isCenter()
					topPadding = 20.uiPX()
					text = "Registering an account requires injecting a certain amount of resources into the new account, so that the new account can complete the most basic operations."
				}.into(this)

				publickeyInput.apply {
					hint = ImportWalletText.registerEOSPublicKey
					setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
				}.into(this)

				gridSessionTitle.apply {
					showTitles(assignResources)
					setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
				}.into(this)

				settingButton.apply {
					showArrow()
					setTitle("Advanced Settings")
				}.click {
					getParentContainer()?.showCustomDashboard(assignResources)
				}.into(this)

				resourceCoast.apply {
					setTitle("Estimated Expenditure")
					setSubtitle(CommonText.calculating)
				}.into(this)

				confirmButton.apply {
					text = CommonText.confirm.toUpperCase()
					setBlueStyle(20.uiPX())
				}.click { button ->
					button.showLoadingStatus()
					presenter.registerAccount(
						EOSAccount(accountNameInput.getContent()),
						publickeyInput.getContent(),
						BigInteger.valueOf(assignResources[0].right.toLong()),
						assignResources[1].right.toDouble(),
						assignResources[1].right.toDouble()
					) {
						if (!it.isNone()) context.alert(it.message)
						button.showLoadingStatus(false)
					}
				}.into(this)
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setExpenditure()
	}

	private fun setExpenditure() {
		presenter.getEOSCurrencyAndRAMPrice { currency, ramPrice, error ->
			if (!currency.isNull() && !ramPrice.isNull() && error.isNone()) {
				val eosCount = assignResources[1].right.toDouble() + assignResources[2].right.toDouble() + assignResources[0].right.toIntOrZero() * ramPrice!!
				val totalCurrency = eosCount * currency!!
				resourceCoast.setSubtitle("≈ ${eosCount.formatCount(4)} EOS ≈ ${totalCurrency.formatCurrency()} (${Config.getCurrencyCode()})")
			} else context.alert(error.message)
		}
	}

	private fun ViewGroup.showCustomDashboard(values: List<MutablePair<String, String>>) {
		DashboardOverlay(context) {
			values.forEachIndexed { index, mutablePair ->
				RoundInput(context!!).apply {
					setNumberInput()
					id = index
					layoutParams = LinearLayout.LayoutParams(ScreenSize.overlayContentWidth, 56.uiPX())
					this.title = mutablePair.left
					hint = mutablePair.right
				}.into(this)
			}
		}.apply {
			confirmEvent = Runnable {
				(0 until values.size).forEach { index ->
					val newValue = findViewById<RoundInput>(index)?.getContent()
					if (!newValue.isNullOrEmpty()) {
						val formattedNumber =
							if (values[index].left.contains(TokenDetailText.ram, true))
								"${newValue?.toIntOrNull().orElse(EOSValue.defaultRegisterAssignRAM)}"
							else "${newValue?.convertToDouble(CryptoValue.eosDecimal).orElse(EOSValue.defaultRegisterAssignBandWidth)}"
						// 更新界面上的值
						gridSessionTitle.updateValues(index, formattedNumber)
						// 更新内存里面的值
						assignResources[index].right = formattedNumber
						// 更新预估价值
						setExpenditure()
					}
				}
				activity?.let { SoftKeyboard.hide(it) }
			}
		}.showTitle("CUSTOM ASSIGN RESOURCES").into(this)
	}

}