package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.*
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.presenter.GasFee
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.view.GasEditorFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.GasSelectionModel
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionCell
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFooter
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentBTCSeriesModel
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

	var currentMinerType = MinerFeeType.Recommend
	var gasFeeFromCustom: () -> GasFee? = {
		fragment.arguments?.getSerializable(ArgumentKey.gasEditor) as? GasFee
	}
	val rootFragment by lazy {
		fragment.getParentFragment<TokenDetailOverlayFragment>()
	}
	val prepareModel by lazy {
		fragment.arguments?.getSerializable(ArgumentKey.gasPrepareModel) as? PaymentPrepareModel
	}
	val prepareBTCSeriesModel by lazy {
		fragment.arguments?.getSerializable(ArgumentKey.btcSeriesPrepareModel) as? PaymentBTCSeriesModel
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
			if (CoinSymbol(getToken()?.symbol).isBTCSeries()) defaultSatoshiValue
			else defaultGasPrices

		gasPrice.forEachIndexed { index, miner ->
			GasSelectionCell(parent.context).apply {
				id = index
				model = if (CoinSymbol(getToken()?.symbol).isBTCSeries())
					GasSelectionModel(
						index,
						miner.toString().toLong(),
						prepareBTCSeriesModel?.signedMessageSize ?: 226,
						currentMinerType.type,
						getToken()?.symbol.orEmpty()
					)
				else
					GasSelectionModel(
						index,
						miner.toString().toDouble(),
						prepareGasLimit(miner.toDouble().toGwei()).toDouble(),
						currentMinerType.type,
						getUnitSymbol()
					)

				if (model.type == currentMinerType.type) {
					getGasCurrencyPrice(model.count) {
						fragment.setSpendingValue(it)
					}
					/** 更新默认的燃气花销的 `ETH`, `ETC` 用于用户余额判断 */
					gasUsedGasFee = getGasUnitCount(model.count)
				}
			}.click { it ->
				currentMinerType = MinerFeeType.getTypeByValue(it.model.type)
				if (CoinSymbol(getToken()?.symbol).isBTCSeries())
					updateBTCGasSettings(getToken()?.symbol.orEmpty(), parent)
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
				Bundle().apply {
					putLong(
						ArgumentKey.gasSize,
						if (CoinSymbol(getToken()?.symbol).isBTCSeries()) {
							prepareBTCSeriesModel?.signedMessageSize ?: 226L
						} else prepareModel?.gasLimit?.toLong().orElse(0L)
					)
					putBoolean(
						ArgumentKey.isBTCSeries,
						CoinSymbol(getToken()?.symbol).isBTCSeries()
					)
				}
			)
		}
	}

	fun confirmTransfer(
		footer: GasSelectionFooter,
		callback: (GoldStoneError) -> Unit
	) {
		val token = getToken()
		// 如果输入的 `Decimal` 不合规就提示竞购并返回
		if (!getTransferCount().toString().checkDecimalIsValid(token)) {
			callback(TransferError.IncorrectDecimal)
		} else if (NetworkUtil.hasNetworkWithAlert(fragment.context)) when {
			// 检查网络并执行转账操作
			getToken()?.contract.isBTC() ->
				prepareToTransferBTC(footer, callback)
			getToken()?.contract.isLTC() ->
				prepareToTransferLTC(footer, callback)
			getToken()?.contract.isBCH() ->
				prepareToTransferBCH(footer, callback)
			else -> prepareToTransfer(footer, callback)
		}
	}

	fun getTransferCount(): BigDecimal {
		return prepareModel?.count?.toBigDecimal() ?: BigDecimal.ZERO
	}

	private fun String.checkDecimalIsValid(token: WalletDetailCellModel?): Boolean {
		val isValid = isValidDecimal(token?.decimal.orZero())
		if (!isValid) fragment.context?.alert(AlertText.transferWrongDecimal)
		return isValid
	}

	fun showConfirmAttentionView(
		footer: GasSelectionFooter,
		callback: (GoldStoneError) -> Unit
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
			when {
				getToken()?.contract.isBTC() ->
					prepareBTCSeriesModel?.apply {
						transferBTC(this, it?.text.toString(), callback)
					}
				getToken()?.contract.isLTC() ->
					prepareBTCSeriesModel?.apply {
						transferLTC(this, it?.text.toString(), callback)
					}
				getToken()?.contract.isBCH() ->
					prepareBTCSeriesModel?.apply {
						transferBCH(this, it?.text.toString(), callback)
					}
				else -> transfer(it?.text.toString(), callback)
			}
		}
	}

	fun insertBTCSeriesPendingDataDatabase(
		raw: PaymentBTCSeriesModel,
		fee: Long,
		size: Int,
		taxHash: String
	) {
		fragment.getParentFragment<TokenDetailOverlayFragment> {
			val myAddress = AddressUtils.getCurrentBTCAddress()
			BTCSeriesTransactionTable(
				0,
				0, // TODO 插入 Pending Data 应该是 localMaxDataIndex + 1
				getToken()?.symbol.orEmpty(),
				"Waiting",
				0,
				System.currentTimeMillis().toString(),
				taxHash,
				myAddress,
				raw.toAddress,
				myAddress,
				false,
				raw.value.toBTCCount().formatCount(),
				fee.toBTCCount().formatCount(),
				size.toString(),
				0,
				false,
				true,
				ChainType.getChainTypeBySymbol(getToken()?.symbol).id
			).apply {
				// 插入 PendingData
				GoldStoneDataBase.database
					.btcSeriesTransactionDao()
					.insert(this)
				// 插入 FeeData
				GoldStoneDataBase.database
					.btcSeriesTransactionDao()
					.insert(this.apply {
						isPending = false
						isFee = true
					})
			}
		}
	}

	fun prepareReceiptModelFromBTCSeries(
		raw: PaymentBTCSeriesModel,
		fee: Long,
		taxHash: String
	): ReceiptModel {
		return ReceiptModel(
			raw.fromAddress,
			raw.toAddress,
			fee.toString(),
			raw.value.toBigInteger(),
			getToken()!!,
			taxHash,
			System.currentTimeMillis(),
			prepareModel?.memo
		)
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
				getToken()?.contract.isETC() -> TokenContract.ETC
				getToken()?.contract.isBTC() -> TokenContract.BTC
				getToken()?.contract.isLTC() -> TokenContract.LTC
				getToken()?.contract.isEOS() -> TokenContract.EOS
				getToken()?.contract.isBCH() -> TokenContract.BCH
				else -> TokenContract.ETH
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
		}
	}

	fun backEvent(fragment: TokenDetailOverlayFragment) {
		fragment.apply {
			headerTitle = TokenDetailText.paymentValue
			presenter.popFragmentFrom<GasSelectionFragment>()
		}
	}

	companion object {
		/**
		 * 转账开始后跳转到转账监听界面
		 */
		fun <T : Fragment> goToTransactionDetailFragment(
			fragment: TokenDetailOverlayFragment?,
			currentFragment: T,
			receiptModel: ReceiptModel
		) {
			// 准备跳转到下一个界面
			fragment?.apply {
				// 如果有键盘收起键盘
				activity?.apply { SoftKeyboard.hide(this) }
				removeChildFragment(currentFragment)
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
	}
}