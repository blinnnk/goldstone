package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.crypto.ethereum.Address
import io.goldstone.blockchain.crypto.ethereum.ChainDefinition
import io.goldstone.blockchain.crypto.ethereum.Transaction
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.multichain.getSymbol
import io.goldstone.blockchain.crypto.multichain.isETC
import io.goldstone.blockchain.crypto.multichain.isETH
import io.goldstone.blockchain.crypto.multichain.node.ChainURL
import io.goldstone.blockchain.crypto.utils.*
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.ethereum.ETHJsonRPC.sendRawTransaction
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.presenter.GasFee
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.model.PaymentDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import java.math.BigInteger

/**
 * @date 2018/7/25 4:02 PM
 * @author KaySaith
 */
fun GasSelectionPresenter.checkBalanceIsValid(
	contract: TokenContract,
	@WorkerThread callback: (error: GoldStoneError) -> Unit
) {
	when {
		// 如果是 `ETH` 或 `ETC` 转账刚好就是判断转账金额加上燃气费费用
		contract.isETH() || contract.isETC() -> {
			MyTokenTable.getBalanceByContract(contract) { balance, error ->
				if (balance.isNotNull() && error.isNone()) {
					if (
						balance.toWei(TokenContract.ETH.decimal!!) >=
						gasView.getTransferCount().orElse(BigInteger.ZERO) + BigInteger.valueOf(currentFee.getUsedAmount())
					) callback(GoldStoneError.None)
					else callback(TransferError.BalanceIsNotEnough)
				} else callback(error)
			}
		}

		else -> {
			// 如果当前不是 `ETH` 需要额外查询用户的 `ETH` 余额是否够支付当前燃气费用
			// 首先查询 `Token Balance` 余额
			MyTokenTable.getBalanceByContract(contract) { tokenBalance, error ->
				if (tokenBalance.isNull() || error.isNone()) {
					callback(error)
					return@getBalanceByContract
				}
				// 查询 `ETH` 余额
				MyTokenTable.getBalanceByContract(TokenContract.ETH) ethBalance@{ ethBalance, ethError ->
					if (ethBalance.isNull() || ethError.isNone()) {
						callback(error)
						return@ethBalance
					}
					// Token 的余额和 ETH 用于转账的 `MinerFee` 的余额是否同时足够
					val isEnough =
						tokenBalance.toWei(token.decimal) >= gasView.getTransferCount() && ethBalance.toWei(18) > BigInteger.valueOf(currentFee.getUsedAmount())
					if (isEnough) callback(GoldStoneError.None) else callback(TransferError.BalanceIsNotEnough)
				}
			}
		}
	}
}

/**
 * 交易包括判断选择的交易燃气使用方式，以及生成签名并直接和链上交互.发起转账.
 * 交易开始后进行当前 `taxHash` 监听判断是否完成交易.
 */
private fun GasSelectionPresenter.getETHERC20OrETCAddress() =
	if (token.contract.isETC()) SharedAddress.getCurrentETC()
	else SharedAddress.getCurrentEthereum()

fun GasSelectionPresenter.transferETHSeries(
	ethSeriesModel: PaymentDetailModel,
	privateKey: String,
	chainURL: ChainURL,
	gasFee: GasFee,
	@WorkerThread callback: (receiptModel: ReceiptModel?, error: GoldStoneError) -> Unit
) {
	/** `GasFee` 这个 `Model` 是与 `BTC Series` 复用的, 所以这里的 `Price` 需要 `Scale` 到 `Gwei` 的值*/
	with(ethSeriesModel) {
		// 更新 `prepareModel`  的 `gasPrice` 的值
		this.gasPrice = gasFee.gasPrice.scaleToGwei()
		// Generate Transaction Model
		val raw = Transaction().apply {
			chain = ChainDefinition(chainURL.chainID.id.toLongOrNull() ?: 0)
			nonce = this@with.nonce
			gasPrice = gasFee.gasPrice.scaleToGwei()
			gasLimit = BigInteger.valueOf(gasFee.gasLimit)
			to = Address(toAddress)
			value = if (CryptoUtils.isERC20Transfer(inputData)) BigInteger.valueOf(0)
			else amount
			input = inputData.hexToByteArray().toList()
		}
		val signedHex = raw.sign(privateKey)
		// 发起 `sendRawTransaction` 请求
		sendRawTransaction(signedHex, chainURL) { taxHash, hashError ->
			if (!taxHash.isNullOrEmpty() && hashError.isNone()) {
				// 如 `nonce` 或 `gas` 导致的失败 `taxHash` 是错误的
				// 把本次交易先插入到数据库, 方便用户从列表也能再次查看到处于 `pending` 状态的交易信息
				if (taxHash.isValidTaxHash()) insertPendingDataToDatabase(
					amount,
					this@with,
					taxHash,
					ethSeriesModel.memo
				)
				// 主线程跳转到账目详情界面
				callback(prepareReceiptModel(this@with, amount, taxHash), hashError)
			} else callback(null, hashError)
		}
	}
}

private fun GasSelectionPresenter.prepareReceiptModel(
	raw: PaymentDetailModel,
	value: BigInteger,
	taxHash: String
): ReceiptModel {
	return ReceiptModel(
		raw.fromAddress,
		raw.toWalletAddress,
		(raw.gasLimit * raw.gasPrice).toBigDecimal().toETHCount(),
		value,
		token,
		taxHash,
		System.currentTimeMillis(),
		gasView.getMemo()
	)
}

fun GasSelectionPresenter.getUnitSymbol() = token.contract.getSymbol()

private fun GasSelectionPresenter.insertPendingDataToDatabase(
	value: BigInteger,
	raw: PaymentDetailModel,
	taxHash: String,
	memoData: String
) {
	TransactionTable(
		blockNumber = -1,
		// 以太坊返回的是 second, 本地的是 mills 在这里转化一下
		timeStamp = (System.currentTimeMillis() / 1000).toString(),
		hash = taxHash,
		nonce = raw.nonce.toString(),
		blockHash = "",
		transactionIndex = "",
		fromAddress = getETHERC20OrETCAddress(),
		to = raw.toWalletAddress,
		value = value.toString(),
		count = CryptoUtils.toCountByDecimal(value, token.decimal.orZero()),
		gas = "",
		gasPrice = currentFee.gasPrice.toString(),
		hasError = "0",
		txReceiptStatus = "1",
		input = raw.inputData,
		contractAddress = token.contract.contract,
		cumulativeGasUsed = "",
		gasUsed = raw.gasLimit.toString(),
		confirmations = "-1",
		isReceive = false,
		isERC20Token = token.symbol.isETH(),
		symbol = token.symbol.symbol,
		recordOwnerAddress = getETHERC20OrETCAddress(),
		isPending = true,
		memo = memoData,
		chainID = token.chainID,
		isFee = false,
		minerFee = CryptoUtils.toGasUsedEther(raw.gasLimit.toString(), raw.gasPrice.toString(), false)
	).let {
		GoldStoneDataBase.database.transactionDao().insert(it)
		GoldStoneDataBase.database.transactionDao().insert(it.apply { isFee = true })
	}
}