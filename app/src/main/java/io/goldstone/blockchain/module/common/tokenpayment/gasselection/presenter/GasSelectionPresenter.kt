package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import android.support.annotation.WorkerThread
import android.widget.LinearLayout
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.crypto.utils.toBTCCount
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.presenter.GasFee
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.contract.GasSelectionContract
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.GasSelectionModel
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionCell
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.model.PaymentBTCSeriesModel
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.model.PaymentDetailModel
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter.PrivateKeyExportPresenter
import java.io.Serializable

/**
 * @date 2018/5/16 3:54 PM
 * @author KaySaith
 */
class GasSelectionPresenter(
	val token: WalletDetailCellModel,
	val gasView: GasSelectionContract.GSView
) : GasSelectionContract.GSPresenter {

	var currentFee =
		if (token.contract.isBTCSeries()) GasFee(gasView.getGasLimit(), 30, MinerFeeType.Recommend)
		else GasFee(gasView.getGasLimit(), 30, MinerFeeType.Recommend)

	override fun start() {
		generateGasSelections(gasView.getGasLayout())
	}

	private val defaultFee by lazy {
		if (token.contract.isBTCSeries()) {
			arrayListOf(
				GasFee(gasView.getGasLimit(), 1, MinerFeeType.Cheap),
				GasFee(gasView.getGasLimit(), 50, MinerFeeType.Recommend),
				GasFee(gasView.getGasLimit(), 100, MinerFeeType.Fast)
			)
		} else {
			arrayListOf(
				GasFee(gasView.getGasLimit(), 1, MinerFeeType.Cheap),
				GasFee(gasView.getGasLimit(), 30, MinerFeeType.Recommend),
				GasFee(gasView.getGasLimit(), 100, MinerFeeType.Fast)
			)
		}
	}

	private fun generateGasSelections(parent: LinearLayout) {
		defaultFee.forEachIndexed { index, fee ->
			GasSelectionCell(parent.context).apply {
				id = index
				model = GasSelectionModel(fee, getUnitSymbol())
				if (fee.type == currentFee.type) {
					getGasCurrencyPrice(model!!.count) {
						gasView.showSpendingValue(it)
					}
					setSelectedStatus(true)
				}
			}.click { it ->
				clearSelectedStatus(parent)
				it.setSelectedStatus(true)
				getGasCurrencyPrice(it.model!!.count) {
					gasView.showSpendingValue(it)
				}
			}.into(parent)
		}
	}

	override fun checkIsValidTransfer(@WorkerThread callback: (GoldStoneError) -> Unit) {
		// 如果输入的 `Decimal` 不合规就提示并返回
		if (!gasView.getTransferCount().toString().checkDecimalIsValid(token)) {
			callback(TransferError.IncorrectDecimal)
		} else when {
			token.contract.isBTCSeries() ->
				checkBTCSeriesBalance(token.contract, callback)
			else -> checkBalanceIsValid(token.contract, callback)
		}
	}

	override fun transfer(
		contract: TokenContract,
		password: String,
		paymentModel: Serializable,
		gasFee: GasFee,
		@WorkerThread callback: (receiptModel: ReceiptModel?, error: GoldStoneError) -> Unit
	) {
		PrivateKeyExportPresenter.getPrivateKey(
			contract.getAddress(),
			contract.getChainType(),
			password
		) { privateKey, error ->
			if (privateKey.isNotNull() && error.isNone()) {
				when {
					contract.isBTCSeries() -> transferBTCSeries(
						paymentModel as PaymentBTCSeriesModel,
						contract.getChainType(),
						privateKey,
						gasFee,
						callback
					)
					else -> transferETHSeries(
						paymentModel as PaymentDetailModel,
						privateKey,
						contract.getChainURL(),
						gasFee,
						callback
					)
				}
			} else callback(null, error)
		}
	}

	override fun addCustomFeeCell() {
		if (defaultFee.size == 4) {
			defaultFee.remove(defaultFee.last())
		}
		currentFee = gasView.getCustomFee()
		defaultFee.add(gasView.getCustomFee())
		gasView.clearGasLayout()
		generateGasSelections(gasView.getGasLayout())
	}

	fun generateReceipt(
		raw: PaymentBTCSeriesModel,
		fee: Long,
		taxHash: String
	): ReceiptModel {
		return ReceiptModel(
			raw.fromAddress,
			raw.toAddress,
			fee.toBTCCount().toBigDecimal().toPlainString() suffix token.symbol.symbol,
			raw.value.toBigInteger(),
			token,
			taxHash,
			System.currentTimeMillis(),
			gasView.getMemo()
		)
	}

	private fun String.checkDecimalIsValid(token: WalletDetailCellModel?): Boolean {
		val isValid = toDoubleOrZero().getDecimalCount().orZero() > token?.decimal.orZero()
		if (!isValid) gasView.showError(Throwable(AlertText.transferWrongDecimal))
		return isValid
	}

	private fun clearSelectedStatus(container: LinearLayout) {
		for (index in 0 until defaultFee.size) {
			container.findViewById<GasSelectionCell>(index)?.setSelectedStatus(false)
		}
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
			token.contract.isETC() -> TokenContract.ETC
			token.contract.isBTC() -> TokenContract.BTC
			token.contract.isLTC() -> TokenContract.LTC
			token.contract.isEOS() -> TokenContract.EOS
			token.contract.isBCH() -> TokenContract.BCH
			else -> TokenContract.ETH
		}
		DefaultTokenTable.getCurrentChainToken(coinContract) {
			hold(
				"≈ " + (getGasUnitCount(value) * it?.price.orElse(0.0)).formatCurrency() + " " + SharedWallet.getCurrencyCode()
			)
		}
	}
}