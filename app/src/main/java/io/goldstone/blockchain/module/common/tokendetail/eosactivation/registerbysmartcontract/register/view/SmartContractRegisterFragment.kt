package io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbysmartcontract.register.view

import android.support.v4.app.Fragment
import android.view.Gravity
import com.blinnnk.extension.into
import com.blinnnk.extension.isNull
import com.blinnnk.extension.suffix
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.view.ColumnSectionTitle
import io.goldstone.blockchain.common.component.DescriptionView
import io.goldstone.blockchain.common.component.KeyValueView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.cell.GraySquareCell
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.title.SessionTitleView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.utils.MutablePair
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbysmartcontract.register.presenter.SmartContractRegisterPresenter
import io.goldstone.blockchain.module.home.dapp.eosaccountregister.presenter.EOSAccountRegisterPresenter
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/09/25
 */

class SmartContractRegisterFragment() : BaseFragment<SmartContractRegisterPresenter>() {

	private val accountNameInput by lazy { RoundInput(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	private var isValidAccountName = false
	private val gridSessionTitle by lazy { ColumnSectionTitle(context!!) }
	private val resourceCoast by lazy { GraySquareCell(context!!) }
	private var assignResources = listOf(
		MutablePair("RAM (EOS)", "1.8"),
		MutablePair("CPU (EOS)", "0.1"),
		MutablePair("NET (EOS)", "0.1")
	)

	override val presenter = SmartContractRegisterPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				bottomPadding = 20.uiPX()
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, matchParent)
				DescriptionView(context).isRegisterBySmartContract().into(this)
				accountNameInput.apply {
					title = ImportWalletText.eosAccountName
					afterTextChanged = Runnable {
						val checker = EOSAccount(getContent()).checker()
						if (checker.isValid()) {
							isValidAccountName = true
							setValidStatus(true, "Valid")
						} else {
							isValidAccountName = false
							setValidStatus(false, checker.shortDescription)
						}
					}
				}.into(this)
				// 显示公钥地址
				SessionTitleView(context).apply { setTitle("CLICK TO COPY") }.into(this)
				KeyValueView(context).apply {
					gravity = Gravity.CENTER
					text = Config.getCurrentEOSAddress()
				}.click {
					it.context.clickToCopy(Config.getCurrentEOSAddress())
				}.into(this)

				DescriptionView(context).isRegisterResource().into(this)
				// 显示 `EOS` 分配的明细
				gridSessionTitle.apply {
					showTitles(assignResources)
				}.into(this)

				resourceCoast.apply {
					presenter.getEOSCurrencyPrice { currency, error ->
						if (!currency.isNull() && error.isNone()) {
							setSubtitle("2.0 EOS ≈ ${(2 * currency!!).formatCurrency() suffix Config.getCurrencyCode()}")
						} else context.alert(error.message)
					}
					setTitle("Estimated Expenditure")
					setSubtitle("2.0 EOS ${CommonText.calculating}")
				}.into(this)

				confirmButton.apply {
					setBlueStyle(20.uiPX())
					text = "Check Name Is Available In Chain"
				}.click {
					val account = EOSAccount(accountNameInput.getContent())
					when {
						isValidAccountName -> {
							it.showLoadingStatus()
							EOSAccountRegisterPresenter.checkNameIsAvailableInChain(account) { isAvailable, error ->
								it.showLoadingStatus(false)
								activity?.apply { SoftKeyboard.hide(this) }
								if (!isAvailable.isNull() && error.isNone()) {
									if (isAvailable!!) presenter.showSmartContractRegisterDetailFragment(account.accountName)
									else context.alert("unavailable account name")
								} else context.alert(error.message)
							}
						}
						account.accountName.isEmpty() -> context.alert("Empty Account Name")
						else -> context.alert("Invalid Account Name")
					}
				}.into(this)
			}
		}
	}

}