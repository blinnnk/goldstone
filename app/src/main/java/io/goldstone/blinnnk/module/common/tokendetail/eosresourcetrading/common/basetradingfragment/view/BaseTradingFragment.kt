package io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view

import android.support.v4.app.Fragment
import android.view.Gravity
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.suffix
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blinnnk.common.base.basefragment.BaseFragment
import io.goldstone.blinnnk.common.component.ProcessType
import io.goldstone.blinnnk.common.component.title.SessionTitleView
import io.goldstone.blinnnk.common.component.title.sessionTitle
import io.goldstone.blinnnk.common.error.AccountError
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.language.QuotationText
import io.goldstone.blinnnk.common.language.TokenDetailText
import io.goldstone.blinnnk.common.sharedpreference.SharedAddress
import io.goldstone.blinnnk.common.sharedpreference.SharedValue
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.safeShowError
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.crypto.eos.account.EOSAccount
import io.goldstone.blinnnk.crypto.eos.base.showDialog
import io.goldstone.blinnnk.crypto.multichain.ChainType
import io.goldstone.blinnnk.crypto.utils.formatCount
import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.common.TradingCardView
import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.presenter.BaseTradingPresenter
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import io.goldstone.blinnnk.module.home.profile.contacts.component.showContactDashboard
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.toast
import java.math.BigInteger

/**
 * @author KaySaith
 * @date  2018/09/18
 */
open class BaseTradingFragment : BaseFragment<BaseTradingPresenter>() {

	open val tradingType: TradingType = TradingType.CPU
	override val pageTitle
		get() = when (tradingType) {
			TradingType.CPU -> TokenDetailText.tradingCPU
			TradingType.NET -> TokenDetailText.tradingNET
			TradingType.RAM -> TokenDetailText.tradingRAM
		}
	private val delegateTitle by lazy {
		when (tradingType) {
			TradingType.CPU -> TokenDetailText.delegateTitle suffix TokenDetailText.cpu
			TradingType.NET -> TokenDetailText.delegateTitle suffix TokenDetailText.net
			TradingType.RAM -> TokenDetailText.buyRam
		}
	}
	private val refundTitle by lazy {
		when (tradingType) {
			TradingType.CPU -> TokenDetailText.refundTitle suffix TokenDetailText.cpu
			TradingType.NET -> TokenDetailText.refundTitle suffix TokenDetailText.net
			TradingType.RAM -> TokenDetailText.sellRam
		}
	}

	private lateinit var delegateSession: SessionTitleView
	private lateinit var refundSession: SessionTitleView

	private val incomeTradingCard by lazy {
		TradingCardView(context!!).apply {
			setAccountHint(SharedAddress.getCurrentEOSAccount().name)
			if (tradingType.isRAM()) {
				amountEditTextChanged = Runnable {
					showRAMAmount(getInputValue().second / SharedValue.getRAMUnitPrice())
				}
			}
			setConfirmClickEvent {
				showLoading(true)
				presenter.gainConfirmEvent(
					cancelAction = { showLoading(false) }
				) { response, error ->
					if (response.isNotNull() && error.isNone()) {
						launchUI {
							getParentContainer()?.apply {
								response.showDialog(context)
							}
							clearInputValue()
							toast(CommonText.succeed)
							// 更新数据库数据并且更新界面的数据
						}
						presenter.updateLocalDataAndUI()
					} else if (error.hasError() && !error.isIgnoreError()) safeShowError(error)
					showLoading(false)
				}
			}
			setContactButtonClickEvent {
				getSelectedAccountFromContacts {
					if (it.isEmpty()) safeShowError(AccountError.InvalidAccountName)
					else setAccount(it)
				}
			}
		}
	}

	private val expendTradingCard by lazy {
		TradingCardView(context!!).apply {
			setAccountHint(SharedAddress.getCurrentEOSAccount().name)
			if (tradingType.isRAM()) {
				setSellingRAMStyle()
				amountEditTextChanged = Runnable {
					showRAMEOSCount(getInputValue().second * SharedValue.getRAMUnitPrice() / 1024)
				}
			}
			setConfirmClickEvent {
				showLoading(true)
				presenter.refundOrSellConfirmEvent(
					cancelAction = { showLoading(false) }
				) { response, error ->
					if (response.isNotNull() && error.isNone()) {
						launchUI {
							getParentContainer()?.apply {
								response.showDialog(context)
							}
							clearInputValue()
						}
						// 更新数据库数据并且更新界面的数据
						presenter.updateLocalDataAndUI()
					} else if (error.hasError() && !error.isIgnoreError()) safeShowError(error)
					showLoading(false)
				}
			}
			setContactButtonClickEvent {
				getSelectedAccountFromContacts {
					if (it.isEmpty()) safeShowError(AccountError.InvalidAddress)
					else setAccount(it)
				}
			}
		}
	}

	override val presenter = BaseTradingPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams(matchParent, matchParent)
				gravity = Gravity.CENTER_HORIZONTAL
				topPadding = 10.uiPX()
				bottomPadding = 10.uiPX()
				delegateSession = sessionTitle {
					setTitle(delegateTitle)
					setSubtitle(CommonText.calculating, "${QuotationText.currentPrice}: ${CommonText.calculating} EOS/MS/Day", Spectrum.blue)
				}
				incomeTradingCard.into(this)
				refundSession = sessionTitle {
					setTitle(refundTitle)
					setSubtitle(CommonText.calculating, "${QuotationText.currentPrice}: ${CommonText.calculating} EOS/Byte/Day", Spectrum.blue)
				}
				expendTradingCard.into(this)
			}
			// 某些行为不需要设置 `租赁或转出` 的选项
			if (tradingType.isRAM()) {
				incomeTradingCard.showRadios(false)
				expendTradingCard.showRadios(false)
			} else {
				expendTradingCard.showRadios(false)
			}
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		val overlayFragment =
			getParentFragment<TokenDetailOverlayFragment>()
		overlayFragment?.presenter?.popFragmentFrom<BaseTradingFragment>()
	}

	fun setProcessUsage(weight: String, available: BigInteger, total: BigInteger, priceEOS: Double) {
		val title = when (tradingType) {
			TradingType.CPU -> TokenDetailText.cpu
			TradingType.NET -> TokenDetailText.net
			TradingType.RAM -> TokenDetailText.ram
		}
		val unitDescription = when (tradingType) {
			TradingType.CPU -> "EOS/MS/Day"
			TradingType.NET -> "EOS/Bytes/Day"
			TradingType.RAM -> "KB/EOS"
		}
		val processType = when (tradingType) {
			TradingType.CPU -> ProcessType.Time
			else -> ProcessType.Disk
		}
		val formattedPriceEOS = "≈ " + priceEOS.formatCount(4)
		delegateSession.setSubtitle(formattedPriceEOS, "${QuotationText.currentPrice}: $formattedPriceEOS $unitDescription", Spectrum.blue)
		refundSession.setSubtitle(formattedPriceEOS, "${QuotationText.currentPrice}: $formattedPriceEOS $unitDescription", Spectrum.blue)
		incomeTradingCard.setProcessValue(title, weight, available, total, processType)
		expendTradingCard.setProcessValue(title, weight, available, total, processType)
	}

	fun getInputValue(stakeType: StakeType): Pair<EOSAccount, Double> {
		return if (stakeType.isDelegate() || stakeType.isBuyRam()) incomeTradingCard.getInputValue()
		else expendTradingCard.getInputValue()
	}

	private fun clearInputValue() {
		incomeTradingCard.clearInput()
		expendTradingCard.clearInput()
	}

	fun isTransfer(stakeType: StakeType): Boolean =
		if (stakeType.isDelegate()) incomeTradingCard.isSelectedTransfer()
		else expendTradingCard.isSelectedTransfer()

	private fun getSelectedAccountFromContacts(hold: (address: String) -> Unit) {
		getParentFragment<TokenDetailOverlayFragment> {
			showContactDashboard(ChainType.EOS, hold)
		}
	}
}

enum class TradingType(val value: String) {
	CPU("cpu"),
	NET("net"),
	RAM("ram");

	fun isCPU(): Boolean = value.equals(CPU.value, true)
	fun isNET(): Boolean = value.equals(NET.value, true)
	fun isRAM(): Boolean = value.equals(RAM.value, true)
}

enum class StakeType(val value: String) {
	Delegate("delegatebw"),
	Refund("undelegatebw"),
	RefundCPU("undelegatebwCPU"),
	RefundNET("undelegatebwNET"),
	BuyRam("buyram"),
	SellRam("sellram"),
	Trade("trade"),
	Register("register");

	fun isBuyRam(): Boolean = value.equals(BuyRam.value, true)
	fun isSellRam(): Boolean = value.equals(SellRam.value, true)
	fun isDelegate(): Boolean = value.equals(Delegate.value, true)
	fun isRefundCPU(): Boolean = value.equals(RefundCPU.value, true)
	fun isRefundNET(): Boolean = value.equals(RefundNET.value, true)
	fun isRefund(): Boolean =
		value.equals(RefundCPU.value, true)
			|| value.equals(RefundNET.value, true)
			|| value.equals(Refund.value, true)
}