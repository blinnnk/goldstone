package io.goldstone.blockchain.module.common.walletimport.walletimport.presenter

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.crypto.multichain.ChainAddresses
import io.goldstone.blockchain.crypto.multichain.ChainPath
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
		// 非 `Bip44` 钱包, 本地没有 `Path index` 返回 `-1` 进行标记
		fun childAddressValue(address: String, index: Int): String {
			return if (index == -1) address
			else "$address|$index"
		}

		fun getAddressIndexFromPath(path: String): Int {
			return if (path.isEmpty()) -1
			else path.substringAfterLast("/").toInt()
		}

		fun insertWalletToDatabase(
			multiChainAddresses: ChainAddresses,
			name: String,
			encryptMnemonic: String,
			multiChainPath: ChainPath,
			hint: String?,
			callback: (GoldStoneError) -> Unit
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
				).firstOrNull { it.isNotEmpty() }.orEmpty()

			WalletTable.getWalletByAddress(currentAddress) { it ->
				if (it.isNull()) WalletTable(
					0,
					name,
					currentETHSeriesAddress = multiChainAddresses.eth,
					currentETCAddress = multiChainAddresses.etc,
					currentBTCAddress = multiChainAddresses.btc,
					currentBTCSeriesTestAddress = multiChainAddresses.btcSeriesTest,
					currentLTCAddress = multiChainAddresses.ltc,
					currentBCHAddress = multiChainAddresses.bch,
					currentEOSAddress = multiChainAddresses.eos,
					currentEOSAccountName = EOSDefaultAllChainName(multiChainAddresses.eos, multiChainAddresses.eos),
					isUsing = true,
					hint = hint,
					isWatchOnly = false,
					balance = 0.0,
					encryptMnemonic = encryptMnemonic,
					hasBackUpMnemonic = true,
					ethAddresses = childAddressValue(
						multiChainAddresses.eth,
						getAddressIndexFromPath(multiChainPath.ethPath)
					),
					etcAddresses = childAddressValue(
						multiChainAddresses.etc,
						getAddressIndexFromPath(multiChainPath.etcPath)
					),
					btcAddresses = childAddressValue(
						multiChainAddresses.btc,
						getAddressIndexFromPath(multiChainPath.btcPath)
					),
					btcSeriesTestAddresses = childAddressValue(
						multiChainAddresses.btcSeriesTest,
						getAddressIndexFromPath(multiChainPath.testPath)
					),
					ltcAddresses = childAddressValue(
						multiChainAddresses.ltc,
						getAddressIndexFromPath(multiChainPath.ltcPath)
					),
					bchAddresses = childAddressValue(
						multiChainAddresses.bch,
						getAddressIndexFromPath(multiChainPath.bchPath)
					),
					eosAddresses = childAddressValue(
						multiChainAddresses.eos,
						getAddressIndexFromPath(multiChainPath.eosPath)
					),
					eosAccountNames = listOf(),
					ethPath = multiChainPath.ethPath,
					btcPath = multiChainPath.btcPath,
					etcPath = multiChainPath.etcPath,
					btcTestPath = multiChainPath.testPath,
					ltcPath = multiChainPath.ltcPath,
					bchPath = multiChainPath.bchPath,
					eosPath = multiChainPath.eosPath
				).insertWatchOnlyWallet { wallet ->
					// 创建钱包并获取默认的 `token` 信息
					CreateWalletPresenter.generateMyTokenInfo(multiChainAddresses, callback)
					// 注册钱包地址用于发送 `Push`
					XinGePushReceiver.registerAddressesForPush(wallet)
				} else callback(AccountError.ExistAddress)
			}
		}
	}
}