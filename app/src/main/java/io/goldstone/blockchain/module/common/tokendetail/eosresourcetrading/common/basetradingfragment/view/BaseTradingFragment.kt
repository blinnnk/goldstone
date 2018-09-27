package io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view

import android.support.v4.app.Fragment
import android.view.Gravity
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.suffix
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.title.SessionTitleView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.TradingCardView
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.presenter.BaseTradingPresenter
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.home.profile.contacts.component.showContactDashboard
import org.jetbrains.anko.*
import java.math.BigInteger

/**
 * @author KaySaith
 * @date  2018/09/18
 */
open class BaseTradingFragment : BaseFragment<BaseTradingPresenter>() {

	override val pageTitle: String = "Trading Resource"
	open val tradingType: TradingType = TradingType.CPU
	private val delegateTitle by lazy {
		val title = when (tradingType) {
			TradingType.CPU -> TokenDetailText.delegateTitle suffix TokenDetailText.cpu
			TradingType.NET -> TokenDetailText.delegateTitle suffix TokenDetailText.net
			TradingType.RAM -> "BUY" + TokenDetailText.ram
		}
		SessionTitleView(context!!).setTitle(title)
	}
	private val refundTitle by lazy {
		val title = when (tradingType) {
			TradingType.CPU -> TokenDetailText.refundTitle suffix TokenDetailText.cpu
			TradingType.NET -> TokenDetailText.refundTitle suffix TokenDetailText.net
			TradingType.RAM -> "SELL" + TokenDetailText.ram
		}
		SessionTitleView(context!!).setTitle(title)
	}

	private val incomeTradingCard by lazy {
		TradingCardView(context!!).apply {
			setAccountHint(SharedAddress.getCurrentEOSAccount().accountName)
			setConfirmClickEvent {
				showLoading(true)
				presenter.gainConfirmEvent {
					showLoading(false)
					if (!it.isNone()) context.alert(it.message)
				}
			}
			setContactButtonClickEvent {
				getSelectedAccountFromContacts {
					setAccount(it)
				}
			}
		}
	}

	private val expendTradingCard by lazy {
		TradingCardView(context!!).apply {
			setAccountHint(SharedAddress.getCurrentEOSAccount().accountName)
			if (tradingType.isRAM()) setSellingRAMStyle()
			setConfirmClickEvent {
				showLoading(true)
				presenter.refundOrSellConfirmEvent {
					showLoading(false)
					if (!it.isNone()) context.alert(it.message)
				}
			}
			setContactButtonClickEvent {
				getSelectedAccountFromContacts {
					setAccount(it)
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
				delegateTitle.into(this)
				delegateTitle.setSubtitle(CommonText.calculating, "Current Price: ${CommonText.calculating} EOS/MS/Day", Spectrum.blue)
				incomeTradingCard.into(this)
				refundTitle.into(this)
				refundTitle.setSubtitle(CommonText.calculating, "Current Price: ${CommonText.calculating} EOS/Byte/Day", Spectrum.blue)
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

	fun setProcessUsage(weight: String, available: BigInteger, total: BigInteger, priceEOS: Double) {
		val title = when (tradingType) {
			TradingType.CPU -> TokenDetailText.cpu
			TradingType.NET -> TokenDetailText.net
			TradingType.RAM -> TokenDetailText.ram
		}
		val unitDescription = when (tradingType) {
			TradingType.CPU -> "EOS/MS/Day"
			TradingType.NET -> "EOS/Bytes/Day"
			TradingType.RAM -> "KB/PER EOS"
		}
		val isTime = when (tradingType) {
			TradingType.CPU -> true
			else -> false
		}
		val formattedPriceEOS = "≈ " + priceEOS.formatCount(4)
		delegateTitle.setSubtitle(formattedPriceEOS, "Current Price: $formattedPriceEOS $unitDescription", Spectrum.blue)
		refundTitle.setSubtitle(formattedPriceEOS, "Current Price: $formattedPriceEOS $$unitDescription", Spectrum.blue)
		incomeTradingCard.setProcessValue(title, weight, available, total, isTime)
		expendTradingCard.setProcessValue(title, weight, available, total, isTime)
	}

	fun getInputValue(stakeType: StakeType): Pair<EOSAccount, Double> {
		return if (stakeType.isDelegate() || stakeType.isBuyRam()) incomeTradingCard.getInputValue()
		else expendTradingCard.getInputValue()
	}

	fun clearInputValue() {
		incomeTradingCard.clearInput()
		expendTradingCard.clearInput()
	}

	fun isSelectedTransfer(stakeType: StakeType): Boolean =
		if (stakeType.isDelegate()) incomeTradingCard.isSelectedTransfer()
		else expendTradingCard.isSelectedTransfer()

	private fun getSelectedAccountFromContacts(hold: (address: String) -> Unit) {
		getParentFragment<TokenDetailOverlayFragment> {
			showContactDashboard(ChainType.EOS, hold)
		}
	}
}

enum class TradingType(val value: String) {
	CPU("cpu"), NET("net"), RAM("ram");

	fun isCPU(): Boolean = value.equals(CPU.value, true)
	fun isNET(): Boolean = value.equals(NET.value, true)
	fun isRAM(): Boolean = value.equals(RAM.value, true)
}

enum class StakeType(val value: String) {
	Delegate("delegatebw"),
	Refund("undelegatebw"),
	BuyRam("buyram"),
	SellRam("sellram");

	fun isBuyRam(): Boolean = value.equals(BuyRam.value, true)
	fun isSellRam(): Boolean = value.equals(SellRam.value, true)
	fun isDelegate(): Boolean = value.equals(Delegate.value, true)
	fun isRefund(): Boolean = value.equals(Refund.value, true)
}