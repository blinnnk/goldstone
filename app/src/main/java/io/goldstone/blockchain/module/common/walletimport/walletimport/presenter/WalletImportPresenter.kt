package io.goldstone.blockchain.module.common.walletimport.walletimport.presenter

import android.support.annotation.WorkerThread
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.crypto.multichain.ChainAddresses
import io.goldstone.blockchain.crypto.multichain.ChainPath
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.EOSDefaultAllChainName
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment

/**
 * @date 23/03/2018 12:55 AM
 * @author KaySaith
 */
class WalletImportPresenter(
	override val fragment: WalletImportFragment
) : BaseOverlayPresenter<WalletImportFragment>() {

	companion object {
		@WorkerThread
		fun insertWalletToDatabase(
			multiChainAddresses: ChainAddresses,
			name: String,
			encryptMnemonic: String,
			multiChainPath: ChainPath,
			hint: String?,
			callback: (walletID: Int?, error: GoldStoneError) -> Unit
		) {
			// 不为空的地址进行
			val currentAddress =
				arrayListOf(
					multiChainAddresses.eth,
					multiChainAddresses.etc,
					multiChainAddresses.btc,
					multiChainAddresses.btcSeriesTest,
					multiChainAddresses.ltc,
					multiChainAddresses.bch,
					multiChainAddresses.eos
				).firstOrNull { it.isNotEmpty() }?.address.orEmpty()
			val allAddress =
				GoldStoneDataBase.database.walletDao()
					.getWalletByAddress(currentAddress)
			if (allAddress == null) WalletTable(
				0,
				name,
				currentETHSeriesAddress = multiChainAddresses.eth.address,
				currentETCAddress = multiChainAddresses.etc.address,
				currentBTCAddress = multiChainAddresses.btc.address,
				currentBTCSeriesTestAddress = multiChainAddresses.btcSeriesTest.address,
				currentLTCAddress = multiChainAddresses.ltc.address,
				currentBCHAddress = multiChainAddresses.bch.address,
				currentEOSAddress = multiChainAddresses.eos.address,
				currentEOSAccountName = EOSDefaultAllChainName(multiChainAddresses.eos.address, multiChainAddresses.eos.address),
				isUsing = true,
				hint = hint,
				isWatchOnly = false,
				balance = 0.0,
				encryptMnemonic = encryptMnemonic,
				hasBackUpMnemonic = true,
				ethAddresses = listOf(multiChainAddresses.eth),
				etcAddresses = listOf(multiChainAddresses.etc),
				btcAddresses = listOf(multiChainAddresses.btc),
				btcSeriesTestAddresses = listOf(multiChainAddresses.btcSeriesTest),
				ltcAddresses = listOf(multiChainAddresses.ltc),
				bchAddresses = listOf(multiChainAddresses.bch),
				eosAddresses = listOf(multiChainAddresses.eos),
				eosAccountNames = listOf(),
				ethPath = multiChainPath.ethPath,
				btcPath = multiChainPath.btcPath,
				etcPath = multiChainPath.etcPath,
				btcTestPath = multiChainPath.testPath,
				ltcPath = multiChainPath.ltcPath,
				bchPath = multiChainPath.bchPath,
				eosPath = multiChainPath.eosPath
			) insert { wallet ->
				// 创建钱包并获取默认的 `token` 信息
				CreateWalletPresenter.generateMyTokenInfo(multiChainAddresses) {
					callback(wallet.id, GoldStoneError.None)
				}
				// 注册钱包地址用于发送 `Push`
				XinGePushReceiver.registerAddressesForPush(wallet)
			} else callback(null, AccountError.ExistAddress)
		}
	}
}