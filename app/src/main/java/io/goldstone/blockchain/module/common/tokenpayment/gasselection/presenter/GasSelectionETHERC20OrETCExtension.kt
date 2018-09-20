package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import android.widget.LinearLayout
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.orElse
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.ethereum.Address
import io.goldstone.blockchain.crypto.ethereum.ChainDefinition
import io.goldstone.blockchain.crypto.ethereum.Transaction
import io.goldstone.blockchain.crypto.keystore.getPrivateKey
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.*
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.GasSelectionModel
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.GasSelectionPresenter.Companion.goToTransactionDetailFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionCell
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFooter
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentPrepareModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.math.BigInteger

/**
 * @date 2018/7/25 4:02 PM
 * @author KaySaith
 */
fun GasSelectionPresenter.checkBalanceIsValid(
	token: WalletDetailCellModel?,
	hold: Boolean.() -> Unit
) {
	when {
		// 如果是 `ETH` 或 `ETC` 转账刚好就是判断转账金额加上燃气费费用
		token?.contract.isETH() -> {
			MyTokenTable.getBalanceByContract(
				token?.contract.orEmpty(),
				Config.getCurrentEthereumAddress(),
				{
					hold(false)
					GoldStoneDialog.chainError(it, fragment.context)
				}
			) {
				hold(it >= getTransferCount().toDouble() + getUsedGasFee().orElse(0.0))
			}
		}

		token?.contract.isETC() -> {
			MyTokenTable.getBalanceByContract(
				token?.contract.orEmpty(),
				Config.getCurrentETCAddress(),
				{
					hold(false)
					GoldStoneDialog.chainError(it, fragment.context)
				}
			) {
				hold(it >= getTransferCount().toDouble() + getUsedGasFee().orElse(0.0))
			}
		}

		else -> {
			// 如果当前不是 `ETH` 需要额外查询用户的 `ETH` 余额是否够支付当前燃气费用
			// 首先查询 `Token Balance` 余额
			MyTokenTable.getBalanceByContract(
				token?.contract.orEmpty(),
				Config.getCurrentEthereumAddress(),
				{
					hold(false)
					GoldStoneDialog.chainError(it, fragment.context)
				}
			) { tokenBalance ->
				// 查询 `ETH` 余额
				MyTokenTable.getBalanceByContract(
					TokenContract.getETH(),
					Config.getCurrentEthereumAddress(),
					{
						hold(false)
						LogUtil.error("checkBalanceIsValid", it)
					}
				) { ethBalance ->
					// Token 的余额和 ETH 用于转账的 `MinerFee` 的余额是否同时足够
					hold(
						tokenBalance >= getTransferCount().toDouble() && ethBalance > getUsedGasFee().orElse(0.0)
					)
				}
			}
		}
	}
}

fun GasSelectionPresenter.prepareToTransfer(
	footer: GasSelectionFooter,
	callback: () -> Unit
) {
	checkBalanceIsValid(getToken()) {
		GoldStoneAPI.context.runOnUiThread {
			isTrue {
				showConfirmAttentionView(footer, callback)
			} otherwise {
				footer.setCanUseStyle(false)
				fragment.context?.alert(AlertText.balanceNotEnough)
				callback()
				fragment.showMaskView(false)
			}
		}
	}
}

fun GasSelectionPresenter.insertCustomGasData() {
	val gasPrice =
		BigInteger.valueOf(gasFeeFromCustom()?.gasPrice.orElse(0).scaleToGwei())
	currentMinerType = MinerFeeType.Custom.content
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
	if (getToken()?.contract.isETC())
		Config.getCurrentETCAddress()
	else Config.getCurrentEthereumAddress()

private fun GasSelectionPresenter.getCurrentETHORETCPrivateKey(
	password: String,
	hold: (String?) -> Unit
) {
	doAsync {
		val isSingleChainWallet = !Config.getCurrentWalletType().isBIP44()
		// 获取当前账户的私钥
		fragment.context?.getPrivateKey(
			getETHERC20OrETCAddress(),
			password,
			false,
			isSingleChainWallet,
			{
				hold(null)
				fragment.showMaskView(false)
			},
			hold
		)
	}
}

fun GasSelectionPresenter.transfer(password: String, callback: () -> Unit) {
	doAsync {
		getCurrentETHORETCPrivateKey(password) { privateKey ->
			if (privateKey.isNullOrBlank()) {
				// 当私钥为 `null` 的时候意味着获取私钥出错, 直接返回
				callback()
				return@getCurrentETHORETCPrivateKey
			}

			prepareModel?.apply model@{
				// 更新 `prepareModel`  的 `gasPrice` 的值
				this.gasPrice = getSelectedGasPrice(currentMinerType)
				// Generate Transaction Model
				val raw = Transaction().apply {
					chain =
						ChainDefinition(getToken()?.contract?.getCurrentChainID()?.id?.toLongOrNull().orElse(0))
					nonce = this@model.nonce
					gasPrice = getSelectedGasPrice(currentMinerType)
					gasLimit =
						BigInteger.valueOf(prepareGasLimit(getSelectedGasPrice(currentMinerType).toLong()))
					to = Address(toAddress)
					value = if (CryptoUtils.isERC20TransferByInputCode(inputData)) BigInteger.valueOf(0)
					else countWithDecimal
					input = inputData.hexToByteArray().toList()
				}
				val signedHex = TransactionUtils.signTransaction(raw, privateKey!!)
				// 发起 `sendRawTransaction` 请求
				GoldStoneEthCall.sendRawTransaction(
					signedHex,
					{ error, reason ->
						fragment.context?.apply {
							alert(reason ?: error.toString())
							fragment.showMaskView(false)
							callback()
						}
					},
					CoinSymbol(getToken()?.symbol).getCurrentChainName()
				) { taxHash ->
					LogUtil.debug(this.javaClass.simpleName, "taxHash: $taxHash")
					// 如 `nonce` 或 `gas` 导致的失败 `taxHash` 是错误的
					taxHash.isValidTaxHash() isTrue {
						// 把本次交易先插入到数据库, 方便用户从列表也能再次查看到处于 `pending` 状态的交易信息
						insertPendingDataToTransactionTable(
							toWalletAddress,
							countWithDecimal,
							this@model,
							taxHash,
							prepareModel?.memo ?: TransactionText.noMemo
						)
					}
					// 主线程跳转到账目详情界面
					fragment.context?.runOnUiThread {
						goToTransactionDetailFragment(
							rootFragment,
							fragment,
							prepareReceiptModel(
								this@model,
								countWithDecimal,
								taxHash
							)
						)
						callback()
						fragment.showMaskView(false)
					}
				}
			}
		}
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

private fun getSelectedGasPrice(type: String): BigInteger {
	return when (type) {
		MinerFeeType.Fast.content -> BigInteger.valueOf(MinerFeeType.Fast.value.scaleToGwei())
		MinerFeeType.Cheap.content -> BigInteger.valueOf(MinerFeeType.Cheap.value.scaleToGwei())
		MinerFeeType.Recommend.content -> BigInteger.valueOf(
			MinerFeeType.Recommend.value.scaleToGwei()
		)
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
				currentMinerType,
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
			gasPrice = getSelectedGasPrice(currentMinerType).toString()
			gasUsed = raw.gasLimit.toString()
			isPending = true
			recordOwnerAddress = getETHERC20OrETCAddress()
			tokenReceiveAddress = toWalletAddress
			isERC20Token = token!!.symbol == CoinSymbol.eth
			nonce = raw.nonce.toString()
			to = raw.toWalletAddress
			input = raw.inputData
			contractAddress = token?.contract?.contract.orEmpty()
			chainID = TokenContract(contractAddress).getCurrentChainID().id
			memo = memoData
			minerFee =
				CryptoUtils.toGasUsedEther(raw.gasLimit.toString(), raw.gasPrice.toString(), false)
		}.let {
			GoldStoneDataBase.database.transactionDao().insert(it)
			GoldStoneDataBase.database.transactionDao().insert(it.apply { isFee = true })
		}
	}
}