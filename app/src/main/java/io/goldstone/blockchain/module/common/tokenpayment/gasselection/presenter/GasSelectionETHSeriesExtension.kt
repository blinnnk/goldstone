package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.widget.LinearLayout
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.crypto.ethereum.Address
import io.goldstone.blockchain.crypto.ethereum.ChainDefinition
import io.goldstone.blockchain.crypto.ethereum.Transaction
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.*
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.ethereum.GoldStoneEthCall
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.GasSelectionModel
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.GasSelectionPresenter.Companion.goToTransactionDetailFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionCell
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentPrepareModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter.PrivateKeyExportPresenter
import org.jetbrains.anko.runOnUiThread
import java.math.BigInteger

/**
 * @date 2018/7/25 4:02 PM
 * @author KaySaith
 */
fun GasSelectionPresenter.checkBalanceIsValid(
	token: WalletDetailCellModel?,
	@WorkerThread hold: (isEnough: Boolean, error: GoldStoneError) -> Unit
) {
	when {
		// 如果是 `ETH` 或 `ETC` 转账刚好就是判断转账金额加上燃气费费用
		token?.contract.isETH() -> {
			MyTokenTable.getBalanceByContract(
				token?.contract.orEmpty(),
				SharedAddress.getCurrentEthereum()
			) { balance, error ->
				hold(balance.orZero() >= getTransferCount().toDouble() + getUsedGasFee().orElse(0.0), error)
			}
		}

		token?.contract.isETC() -> {
			MyTokenTable.getBalanceByContract(
				token?.contract.orEmpty(),
				SharedAddress.getCurrentETC()
			) { balance, error ->
				hold(balance.orZero() >= getTransferCount().toDouble() + getUsedGasFee().orElse(0.0), error)
			}
		}

		else -> {
			// 如果当前不是 `ETH` 需要额外查询用户的 `ETH` 余额是否够支付当前燃气费用
			// 首先查询 `Token Balance` 余额
			MyTokenTable.getBalanceByContract(
				token?.contract.orEmpty(),
				SharedAddress.getCurrentEthereum()
			) { tokenBalance, error ->
				// 查询 `ETH` 余额
				MyTokenTable.getBalanceByContract(
					TokenContract.ETH,
					SharedAddress.getCurrentEthereum()
				) { ethBalance, ethError ->
					// Token 的余额和 ETH 用于转账的 `MinerFee` 的余额是否同时足够
					val isEnough =
						tokenBalance.orZero() >= getTransferCount().toDouble() && ethBalance.orZero() > getUsedGasFee().orElse(0.0)
					hold(isEnough, RequestError.PostFailed(error.message + ethError.message))
				}
			}
		}
	}
}

fun GasSelectionPresenter.prepareToTransfer(@UiThread callback: (GoldStoneError) -> Unit) {
	checkBalanceIsValid(getToken()) { isEnough, error ->
		GoldStoneAPI.context.runOnUiThread {
			when {
				isEnough -> showConfirmAttentionView(callback)
				error.isNone() -> callback(TransferError.BalanceIsNotEnough)
				else -> callback(error)
			}
		}
	}
}

fun GasSelectionPresenter.insertCustomGasData() {
	val gasPrice =
		BigInteger.valueOf(gasFeeFromCustom()?.gasPrice.orElse(0).scaleToGwei())
	currentMinerType = MinerFeeType.Custom
	if (defaultGasPrices.size == 4) {
		defaultGasPrices.remove(defaultGasPrices.last())
	}
	defaultGasPrices.add(gasPrice)
	fragment.clearGasLayout()
	generateGasSelections(fragment.getGasLayout())
}

/**
 * 交易包括判断选择的交易燃气使用方式，以及生成签名并直接和链上交互.发起转账.
 * 交易开始后进行当前 `taxHash` 监听判断是否完成交易.
 */
private fun GasSelectionPresenter.getETHERC20OrETCAddress() =
	if (getToken()?.contract.isETC()) SharedAddress.getCurrentETC()
	else SharedAddress.getCurrentEthereum()

fun GasSelectionPresenter.transfer(
	address: String,
	chainType: ChainType,
	password: String,
	@WorkerThread callback: (GoldStoneError) -> Unit
) {
	PrivateKeyExportPresenter.getPrivateKey(
		fragment.context!!,
		address,
		chainType,
		password
	) { privateKey, error ->
		if (!privateKey.isNull() && error.isNone()) prepareModel?.apply model@{
			// 更新 `prepareModel`  的 `gasPrice` 的值
			this.gasPrice = currentMinerType.getSelectedGasPrice()
			// Generate Transaction Model
			val raw = Transaction().apply {
				chain = ChainDefinition(getToken()?.contract?.getCurrentChainID()?.id?.toLongOrNull().orElse(0))
				nonce = this@model.nonce
				gasPrice = currentMinerType.getSelectedGasPrice()
				gasLimit = BigInteger.valueOf(prepareGasLimit(currentMinerType.getSelectedGasPrice().toLong()))
				to = Address(toAddress)
				value = if (CryptoUtils.isERC20TransferByInputCode(inputData)) BigInteger.valueOf(0)
				else countWithDecimal
				input = inputData.hexToByteArray().toList()
			}
			val signedHex = raw.sign(privateKey!!)
			// 发起 `sendRawTransaction` 请求
			GoldStoneEthCall.sendRawTransaction(
				signedHex,
				getToken()?.contract?.getChainURL()!!
			) { taxHash, error ->
				// API 错误的时候
				if (taxHash.isNullOrEmpty() || error.hasError()) {
					callback(error)
					return@sendRawTransaction
				}
				// 如 `nonce` 或 `gas` 导致的失败 `taxHash` 是错误的
				// 把本次交易先插入到数据库, 方便用户从列表也能再次查看到处于 `pending` 状态的交易信息
				if (taxHash!!.isValidTaxHash()) insertPendingDataToTransactionTable(
					toWalletAddress,
					countWithDecimal,
					this@model,
					taxHash,
					prepareModel?.memo ?: TransactionText.noMemo
				)
				// 主线程跳转到账目详情界面
				fragment.context?.runOnUiThread {
					goToTransactionDetailFragment(
						rootFragment,
						fragment,
						prepareReceiptModel(this@model, countWithDecimal, taxHash)
					)
				}
			}
		} else callback(error)
	}
}

private fun GasSelectionPresenter.prepareReceiptModel(
	raw: PaymentPrepareModel,
	value: BigInteger,
	taxHash: String
): ReceiptModel {
	return ReceiptModel(
		raw.fromAddress,
		raw.toWalletAddress,
		(raw.gasLimit * raw.gasPrice).toEthCount().toBigDecimal().toPlainString(),
		value,
		getToken()!!,
		taxHash,
		System.currentTimeMillis(),
		prepareModel?.memo
	)
}

fun GasSelectionPresenter.prepareGasLimit(gasPrice: Long): Long {
	return if (gasPrice == MinerFeeType.Custom.value)
		gasFeeFromCustom()?.gasLimit.orElse(0)
	else prepareModel?.gasLimit?.toLong().orElse(0)
}

fun MinerFeeType.getSelectedGasPrice(): BigInteger {
	return when {
		isFast() -> BigInteger.valueOf(MinerFeeType.Fast.value.scaleToGwei())
		isCheap() -> BigInteger.valueOf(MinerFeeType.Cheap.value.scaleToGwei())
		isRecommend() -> BigInteger.valueOf(MinerFeeType.Recommend.value.scaleToGwei())
		else -> BigInteger.valueOf(MinerFeeType.Custom.value.scaleToGwei())
	}
}

fun GasSelectionPresenter.updateGasSettings(container: LinearLayout) {
	defaultGasPrices.forEachIndexed { index, miner ->
		container.findViewById<GasSelectionCell>(index)?.let { cell ->
			cell.model = GasSelectionModel(
				index,
				miner.toDouble(),
				prepareGasLimit(miner.toDouble().toGwei()).toDouble(),
				currentMinerType.type,
				getUnitSymbol()
			)
		}
	}
}

fun GasSelectionPresenter.getUnitSymbol() = getToken()?.contract.getSymbol().symbol.orEmpty()

private fun GasSelectionPresenter.insertPendingDataToTransactionTable(
	toWalletAddress: String,
	value: BigInteger,
	raw: PaymentPrepareModel,
	taxHash: String,
	memoData: String
) {
	fragment.getParentFragment<TokenDetailOverlayFragment> {
		TransactionTable().apply {
			isReceive = false
			symbol = token?.symbol.orEmpty()
			timeStamp =
				(System.currentTimeMillis() / 1000).toString() // 以太坊返回的是 second, 本地的是 mills 在这里转化一下
			fromAddress = getETHERC20OrETCAddress()
			this.value = CryptoUtils.toCountByDecimal(value, token!!.decimal).formatCount()
			hash = taxHash
			gasPrice = currentMinerType.getSelectedGasPrice().toString()
			gasUsed = raw.gasLimit.toString()
			isPending = true
			recordOwnerAddress = getETHERC20OrETCAddress()
			tokenReceiveAddress = toWalletAddress
			isERC20Token = token!!.symbol == CoinSymbol.eth
			nonce = raw.nonce.toString()
			to = raw.toWalletAddress
			input = raw.inputData
			contractAddress = token?.contract?.contract.orEmpty()
			chainID = TokenContract(contractAddress, symbol, null).getCurrentChainID().id
			memo = memoData
			minerFee = CryptoUtils.toGasUsedEther(raw.gasLimit.toString(), raw.gasPrice.toString(), false)
		}.let {
			GoldStoneDataBase.database.transactionDao().insert(it)
			GoldStoneDataBase.database.transactionDao().insert(it.apply { isFee = true })
		}
	}
}