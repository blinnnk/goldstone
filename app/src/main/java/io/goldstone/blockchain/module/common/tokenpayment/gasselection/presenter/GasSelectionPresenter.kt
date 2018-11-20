package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import android.content.Context
import android.support.annotation.WorkerThread
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
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.*
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.presenter.GasFee
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.GasSelectionModel
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionCell
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.model.PaymentBTCSeriesModel
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.model.PaymentDetailModel
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter.PrivateKeyExportPresenter
import java.io.Serializable
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
	private val rootFragment by lazy {
		fragment.getParentFragment<TokenDetailOverlayFragment>()
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
			if (getToken()?.symbol.isBTCSeries()) defaultSatoshiValue
			else defaultGasPrices

		gasPrice.forEachIndexed { index, miner ->
			GasSelectionCell(parent.context).apply {
				id = index
				model = if (getToken()?.symbol.isBTCSeries())
					GasSelectionModel(
						index,
						miner.toString().toLong(),
						fragment.btcSeriesPaymentModel?.signedMessageSize ?: 226,
						currentMinerType.type,
						getToken()?.symbol?.symbol.orEmpty()
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
				if (getToken()?.symbol.isBTCSeries())
					updateBTCGasSettings(getToken()?.symbol?.symbol.orEmpty(), parent)
				else updateGasSettings(parent)
				getGasCurrencyPrice(it.model.count) {
					fragment.setSpendingValue(it)
				}
				/** 更新当前选择的燃气花销的 `ETH`, `ETC` 用于用户余额判断 */
				gasUsedGasFee = getGasUnitCount(it.model.count)
			}.into(parent)
		}
	}

	fun checkIsValidTransfer(
		@WorkerThread callback: (GoldStoneError) -> Unit
	) {
		val token = getToken() ?: return
		// 如果输入的 `Decimal` 不合规就提示竞购并返回
		if (!getTransferCount().toString().checkDecimalIsValid(token)) {
			callback(TransferError.IncorrectDecimal)
		} else if (NetworkUtil.hasNetworkWithAlert(fragment.context)) when {
			// 检查网络并执行转账操作
			token.contract.isBTCSeries() ->
				checkBTCSeriesBalance(token.contract, callback)
			else -> checkBalanceIsValid(token.contract, callback)
		}
	}

	fun getTransferCount(): Double {
		return when {
			getToken()?.contract.isBTCSeries() -> fragment.btcSeriesPaymentModel?.value?.toDouble() ?: 0.0
			else -> fragment.ethSeriesPaymentModel?.count ?: 0.0
		}
	}

	private fun String.checkDecimalIsValid(token: WalletDetailCellModel?): Boolean {
		val isValid = isValidDecimal(token?.decimal.orZero())
		if (!isValid) fragment.context?.alert(AlertText.transferWrongDecimal)
		return isValid
	}

	fun showConfirmAttentionView(
		context: Context,
		paymentModel: Serializable,
		@WorkerThread callback: (receiptModel: ReceiptModel?, error: GoldStoneError) -> Unit
	) {
		context.showAlertView(
			TransactionText.confirmTransactionTitle,
			TransactionText.confirmTransaction,
			true,
			// 点击取消按钮
			{ callback(null, GoldStoneError.None) }
		) {
			val password = it?.text.toString()
			val tokenContract = getToken()?.contract ?: return@showAlertView
			PrivateKeyExportPresenter.getPrivateKey(
				context,
				tokenContract.getAddress(),
				getToken()?.contract.getChainType(),
				password
			) { privateKey, error ->
				if (privateKey.isNotNull() && error.isNone()) {
					when {
						tokenContract.isBTCSeries() -> transferBTCSeries(
							paymentModel as PaymentBTCSeriesModel,
							tokenContract.getChainType(),
							privateKey,
							callback
						)
						else -> transfer(
							paymentModel as PaymentDetailModel,
							privateKey,
							tokenContract.getChainURL(),
							callback
						)
					}
				} else callback(null, error)
			}
		}
	}

	fun insertBTCSeriesPendingData(
		raw: PaymentBTCSeriesModel,
		fee: Long,
		size: Int,
		taxHash: String
	) {
		fragment.getParentFragment<TokenDetailOverlayFragment> {
			val myAddress = AddressUtils.getCurrentBTCAddress()
			BTCSeriesTransactionTable(
				0, // TODO 插入 Pending Data 应该是 localMaxDataIndex + 1
				getToken()?.symbol?.symbol.orEmpty(),
				-1,
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
				-1,
				false,
				true,
				getToken()?.contract.getChainType().id
			).apply {
				val transactionDao =
					GoldStoneDataBase.database.btcSeriesTransactionDao()
				// 插入 PendingData
				transactionDao.insert(this)
				// 插入 FeeData
				transactionDao.insert(this.apply {
					isPending = false
					isFee = true
				})
			}
		}
	}

	fun generateReceipt(
		raw: PaymentBTCSeriesModel,
		fee: Long,
		taxHash: String
	): ReceiptModel {
		return ReceiptModel(
			raw.fromAddress,
			raw.toAddress,
			fee.toBTCCount().toBigDecimal().toPlainString(),
			raw.value.toBigInteger(),
			getToken()!!,
			taxHash,
			System.currentTimeMillis(),
			fragment.ethSeriesPaymentModel?.memo.orEmpty()
		)
	}

	private fun getGasUnitCount(info: String): Double {
		return if (info.length > 3) {
			info.substringBefore(" ").toDoubleOrNull().orZero()
		} else {
			0.0
		}
	}

	private fun getGasCurrencyPrice(value: String, hold: (String) -> Unit) {
		val coinContract = when {
			getToken()?.contract.isETC() -> TokenContract.ETC
			getToken()?.contract.isBTC() -> TokenContract.BTC
			getToken()?.contract.isLTC() -> TokenContract.LTC
			getToken()?.contract.isEOS() -> TokenContract.EOS
			getToken()?.contract.isBCH() -> TokenContract.BCH
			else -> TokenContract.ETH
		}
		DefaultTokenTable.getCurrentChainToken(coinContract) {
			hold(
				"≈ " + (getGasUnitCount(value) * it?.price.orElse(0.0)).formatCurrency() + " " + SharedWallet.getCurrencyCode()
			)
		}
	}

	override fun onFragmentShowFromHidden() {
		// 从下一个页面返回后通过显示隐藏监听重设回退按钮的事件
		rootFragment?.apply {
			showBackButton(true) {
				presenter.popFragmentFrom<GasSelectionFragment>()
			}
		}
	}
}

/**
 * 转账开始后跳转到转账监听界面
 */
fun <T : Fragment> TokenDetailOverlayFragment.goToTransactionDetailFragment(
	currentFragment: T,
	receiptModel: ReceiptModel
) {
	// 准备跳转到下一个界面
	// 如果有键盘收起键盘
	activity?.apply { SoftKeyboard.hide(this) }
	removeChildFragment(currentFragment)
	addFragmentAndSetArgument<TransactionDetailFragment>(ContainerID.content) {
		putSerializable(ArgumentKey.transactionDetail, receiptModel)
	}
	showCloseButton(true) { presenter.removeSelfFromActivity() }
}