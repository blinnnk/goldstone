package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import android.os.Bundle
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.crypto.utils.scaleToGwei
import io.goldstone.blockchain.crypto.utils.toGwei
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.presenter.GasFee
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.view.GasEditorFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.GasSelectionModel
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionCell
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFooter
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentPrepareBTCModel
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentPrepareModel
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import java.math.BigDecimal
import java.math.BigInteger

/**
 * @date 2018/5/16 3:54 PM
 * @author KaySaith
 */
class GasSelectionPresenter(
	override val fragment: GasSelectionFragment
) : BasePresenter<GasSelectionFragment>() {
	
	var currentMinerType = MinerFeeType.Recommend.content
	var gasFeeFromCustom: () -> GasFee? = {
		fragment.arguments?.getSerializable(ArgumentKey.gasEditor) as? GasFee
	}
	private val rootFragment by lazy {
		fragment.getParentFragment<TokenDetailOverlayFragment>()
	}
	val prepareModel by lazy {
		fragment.arguments?.getSerializable(ArgumentKey.gasPrepareModel) as? PaymentPrepareModel
	}
	val prepareBTCModel by lazy {
		fragment.arguments?.getSerializable(ArgumentKey.btcPrepareModel) as? PaymentPrepareBTCModel
	}
	val defaultGasPrices by lazy {
		arrayListOf(
			BigInteger.valueOf(MinerFeeType.Cheap.value.scaleToGwei()), // cheap
			BigInteger.valueOf(MinerFeeType.Fast.value.scaleToGwei()), // fast
			BigInteger.valueOf(MinerFeeType.Recommend.value.scaleToGwei()) // recommend
		)
	}
	val defaultSatoshiValue by lazy {
		arrayListOf(
			BigInteger.valueOf(MinerFeeType.Cheap.satoshi), // cheap
			BigInteger.valueOf(MinerFeeType.Fast.satoshi), // fast
			BigInteger.valueOf(MinerFeeType.Recommend.satoshi) // recommend
		)
	}
	var gasUsedGasFee: Double? = null
	
	fun getUsedGasFee(): Double? = gasUsedGasFee
	fun getToken(): WalletDetailCellModel? = rootFragment?.token
	
	fun generateGasSelections(parent: LinearLayout) {
		val gasPrice =
			if (isBTC()) defaultSatoshiValue else defaultGasPrices
		
		gasPrice.forEachIndexed { index, minner ->
			GasSelectionCell(parent.context).apply {
				id = index
				model = if (isBTC())
					GasSelectionModel(
						index,
						minner.toString().toLong(),
						prepareBTCModel?.signedMessageSize ?: 226,
						currentMinerType
					)
				else
					GasSelectionModel(
						index,
						minner.toString().toDouble(),
						prepareGasLimit(minner.toDouble().toGwei()).toDouble(),
						currentMinerType,
						getUnitSymbol()
					)
				
				if (model.type == currentMinerType) {
					getGasCurrencyPrice(model.count) {
						fragment.setSpendingValue(it)
					}
					/** 更新默认的燃气花销的 `ETH`, `ETC` 用于用户余额判断 */
					gasUsedGasFee = getGasUnitCount(model.count)
				}
			}.click {
				currentMinerType = it.model.type
				if (isBTC()) updateBTCGasSettings(parent)
				else updateGasSettings(parent)
				getGasCurrencyPrice(it.model.count) {
					fragment.setSpendingValue(it)
				}
				/** 更新当前选择的燃气花销的 `ETH`, `ETC` 用于用户余额判断 */
				gasUsedGasFee = getGasUnitCount(it.model.count)
			}.into(parent)
		}
	}
	
	fun goToGasEditorFragment() {
		rootFragment?.apply {
			presenter.showTargetFragment<GasEditorFragment>(
				TokenDetailText.customGas,
				TokenDetailText.paymentValue,
				Bundle().apply {
					putLong(
						ArgumentKey.gasSize,
						if (isBTC()) {
							prepareBTCModel?.signedMessageSize ?: 226L
						} else prepareModel?.gasLimit?.toLong().orElse(0L)
					)
					putBoolean(
						ArgumentKey.isBTC,
						isBTC()
					)
				}
			)
		}
	}
	
	fun confirmTransfer(footer: GasSelectionFooter, callback: () -> Unit) {
		// Prevent user click the other button at this time
		fragment.showMaskView(true)
		val token = getToken()
		// 如果输入的 `Decimal` 不合规就提示竞购并返回
		if (!getTransferCount().toString().checkDecimalIsValid(token)) {
			callback()
			fragment.showMaskView(false)
			return
		}
		// 检查网络并执行转账操作
		NetworkUtil.hasNetworkWithAlert(fragment.context) isTrue {
			if (isBTC()) prepareToTransferBTC(footer, callback)
			else prepareToTransfer(footer, callback)
		}
	}
	
	fun getTransferCount(): BigDecimal {
		return prepareModel?.count?.toBigDecimal() ?: BigDecimal.ZERO
	}
	
	private fun isBTC(): Boolean {
		return getToken()?.symbol.equals(CryptoSymbol.btc, true)
	}
	
	private fun String.checkDecimalIsValid(token: WalletDetailCellModel?): Boolean {
		return when {
			getDecimalCount().isNull() -> return true
			
			getDecimalCount().orZero() > token?.decimal.orElse(0.0) -> {
				fragment.context?.alert(AlertText.transferWrongDecimal)
				false
			}
			
			else -> true
		}
	}
	
	fun showConfirmAttentionView(
		footer: GasSelectionFooter,
		callback: () -> Unit
	) {
		fragment.context?.showAlertView(
			TransactionText.confirmTransaction,
			CommonText.enterPassword.toUpperCase(),
			true,
			{
				// 点击 `Alert` 取消按钮
				footer.getConfirmButton {
					showLoadingStatus(false)
				}
				fragment.showMaskView(false)
			}) {
			if (isBTC()) transferBTC(it?.text.toString(), callback)
			else transfer(it?.text.toString(), callback)
		}
	}
	
	/**
	 * 转账开始后跳转到转账监听界面
	 */
	fun goToTransactionDetailFragment(receiptModel: ReceiptModel) {
		// 准备跳转到下一个界面
		fragment.getParentFragment<TokenDetailOverlayFragment> {
			// 如果有键盘收起键盘
			activity?.apply { SoftKeyboard.hide(this) }
			removeChildFragment(fragment)
			
			addFragmentAndSetArgument<TransactionDetailFragment>(ContainerID.content) {
				putSerializable(ArgumentKey.transactionDetail, receiptModel)
			}
			overlayView.header.apply {
				showBackButton(false)
				showCloseButton(true)
			}
			headerTitle = TokenDetailText.transferDetail
		}
	}
	
	private fun getGasUnitCount(info: String): Double {
		return if (info.length > 3) {
			info.substringBefore(" ").toDoubleOrNull().orZero()
		} else {
			0.0
		}
	}
	
	private fun getGasCurrencyPrice(
		value: String,
		hold: (String) -> Unit
	) {
		val coinContract =
			when {
				rootFragment?.token?.contract.equals(
					CryptoValue.etcContract,
					true
				) -> CryptoValue.etcContract
				rootFragment?.token?.contract.equals(
					CryptoValue.btcContract,
					true
				) -> CryptoValue.btcContract
				else -> CryptoValue.ethContract
			}
		DefaultTokenTable.getCurrentChainToken(coinContract) {
			hold(
				"≈ " + (getGasUnitCount(value) * it?.price.orElse(0.0)).formatCurrency() + " " + Config.getCurrencyCode()
			)
		}
	}
	
	override fun onFragmentShowFromHidden() {
		// 从下一个页面返回后通过显示隐藏监听重设回退按钮的事件
		rootFragment?.apply {
			overlayView.header.showBackButton(true) {
				backEvent(this@apply)
			}
			// 有可能从 `WebViewFragment` 返回 需要重新恢复 `ValueHeader`
			setValueHeader(token)
		}
	}
	
	fun backEvent(fragment: TokenDetailOverlayFragment) {
		fragment.apply {
			setValueHeader(token)
			headerTitle = TokenDetailText.paymentValue
			presenter.popFragmentFrom<GasSelectionFragment>()
		}
	}
}