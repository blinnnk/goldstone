package io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbysmartcontract.register.view

import android.support.v4.app.Fragment
import android.view.Gravity
import com.blinnnk.extension.*
import com.blinnnk.model.MutablePair
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.clickToCopy
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.view.ColumnSectionTitle
import io.goldstone.blockchain.common.component.DescriptionView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.button.roundButton
import io.goldstone.blockchain.common.component.cell.GraySquareCell
import io.goldstone.blockchain.common.component.cell.graySquareCell
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.edittext.roundInput
import io.goldstone.blockchain.common.component.title.SessionTitleView
import io.goldstone.blockchain.common.component.valueView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.EOSAccountText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbysmartcontract.register.presenter.SmartContractRegisterPresenter
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.home.dapp.eosaccountregister.presenter.EOSAccountRegisterPresenter
import io.goldstone.blockchain.module.home.home.view.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/09/25
 */

class SmartContractRegisterFragment : BaseFragment<SmartContractRegisterPresenter>() {

	override val pageTitle: String
		get() = getParentFragment<TokenDetailOverlayFragment>()?.token?.symbol?.symbol.orEmpty()
	private lateinit var accountNameInput: RoundInput
	private lateinit var confirmButton: RoundButton
	private var isValidAccountName = false
	private val gridSessionTitle by lazy { ColumnSectionTitle(context!!) }
	private lateinit var resourceCoast: GraySquareCell
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
				accountNameInput = roundInput {
					horizontalPaddingSize = PaddingSize.gsCard
					title = ImportWalletText.eosAccountName
					afterTextChanged = Runnable {
						val checker = EOSAccount(getContent()).checker()
						if (checker.isValid()) {
							isValidAccountName = true
							setValidStatus(true, EOSAccountText.checkNameResultValid)
						} else {
							isValidAccountName = false
							setValidStatus(false, checker.shortDescription)
						}
					}
				}
				// 显示公钥地址
				SessionTitleView(context).apply { setTitle(EOSAccountText.copyPublicKey) }.into(this)
				valueView {
					gravity = Gravity.CENTER
					setContent(SharedAddress.getCurrentEOS())
				}.click {
					it.context.clickToCopy(SharedAddress.getCurrentEOS())
				}

				DescriptionView(context).isRegisterResource().into(this)
				// 显示 `EOS` 分配的明细
				gridSessionTitle.apply {
					showTitles(assignResources)
				}.into(this)

				resourceCoast = graySquareCell {
					if (NetworkUtil.hasNetwork()) presenter.getEOSCurrencyPrice { currency, error ->
						if (currency.isNotNull() && error.isNone()) GlobalScope.launch(Dispatchers.Main) {
							setSubtitle("2.0 EOS ≈ ${(2 * currency).formatCurrency() suffix SharedWallet.getCurrencyCode()}")
						} else safeShowError(error)
					}
					setTitle(EOSAccountText.estimatedSpentOfActiveAccount)
					setSubtitle("2.0 EOS ${CommonText.calculating}")
				}

				confirmButton = roundButton {
					setBlueStyle(20.uiPX())
					text = EOSAccountText.checkNameAvailability
				}.click {
					val account = EOSAccount(accountNameInput.getContent())
					when {
						isValidAccountName -> {
							it.showLoadingStatus()
							EOSAccountRegisterPresenter.checkNameIsAvailableInChain(account) { isAvailable, error ->
								it.showLoadingStatus(false)
								activity?.apply { SoftKeyboard.hide(this) }
								if (!isAvailable.isNull() && error.isNone()) {
									if (isAvailable) presenter.showSmartContractRegisterDetailFragment(account.name)
									else safeShowError(Throwable(EOSAccountText.checkNameResultUnavailable))
								} else safeShowError(error)
							}
						}
						account.name.isEmpty() -> safeShowError(Throwable(EOSAccountText.checkNameResultEmpty))
						else -> safeShowError(Throwable(EOSAccountText.checkNameResultInvalid))
					}
				}
			}
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<TokenDetailOverlayFragment> {
			presenter.popFragmentFrom<SmartContractRegisterFragment>()
		}
	}
}