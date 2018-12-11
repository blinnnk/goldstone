package io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.keystore.generateETHSeriesAddress
import io.goldstone.blockchain.crypto.keystore.getBigIntegerPrivateKeyByWalletID
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
import io.goldstone.blockchain.crypto.litecoin.LitecoinNetParams
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view.PrivateKeyExportFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bitcoinj.core.ECKey
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params

/**
 * @date  getPrivateKey(
fragment.context!!,
address,
chainType,
password
) { privateKey, error ->06/04/2018 1:02 AM
 * @author KaySaith
 */
class PrivateKeyExportPresenter(
	override val fragment: PrivateKeyExportFragment
) : BasePresenter<PrivateKeyExportFragment>() {

	companion object {
		@WorkerThread
		fun getPrivateKey(
			address: String,
			chainType: ChainType,
			password: String,
			hold: (privateKey: String?, error: AccountError) -> Unit
		) = GlobalScope.launch(Dispatchers.Default) {
			if (password.isEmpty()) hold(null, AccountError.WrongPassword)
			else {
				val wallet = WalletTable.dao.findWhichIsUsing(true) ?: return@launch
				if (wallet.getWalletType().isMultiChain()) GoldStoneApp.appContext.getPrivateKeyByWalletID(
					password,
					wallet.id,
					chainType,
					hold
				) else getPrivateKeyByPath(
					GoldStoneApp.appContext,
					wallet,
					address,
					chainType,
					password,
					hold
				)
			}
		}

		private fun getPrivateKeyByPath(
			context: Context,
			wallet: WalletTable,
			address: String,
			chainType: ChainType,
			password: String,
			hold: (privateKey: String?, error: AccountError) -> Unit
		) {
			// 校验密码是否正确
			context.getBigIntegerPrivateKeyByWalletID(password, wallet.id) { privateKeyInteger, error ->
				when {
					privateKeyInteger.isNotNull() && error.isNone() -> {
						val mnemonic = JavaKeystoreUtil().decryptData(wallet.encryptMnemonic!!)
						when {
							chainType.isETH() || chainType.isETC() -> {
								val path =
									if (chainType.isETH()) wallet.ethPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, ChainType.ETH)}")
									else wallet.etcPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, ChainType.ETC)}")
								val privateKey =
									generateETHSeriesAddress(mnemonic, path).privateKey.toString(16)
								hold(privateKey, error)
							}
							chainType.isBTC() || (chainType.isBTCSeries() && SharedValue.isTestEnvironment()) || chainType.isAllTest() -> {
								val path = if (!SharedValue.isTestEnvironment()) wallet.btcPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, ChainType.BTC)}")
								else wallet.btcTestPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, ChainType.AllTest)}")
								BTCWalletUtils.getBitcoinWalletByMnemonic(mnemonic, path) { _, secret ->
									hold(secret, error)
								}
							}

							chainType.isLTC() && !SharedValue.isTestEnvironment() -> {
								val path = wallet.ltcPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, ChainType.LTC)}")
								val privateKey = LTCWalletUtils.generateBase58Keypair(mnemonic, path).privateKey
								hold(privateKey, error)
							}
							chainType.isBCH() && !SharedValue.isTestEnvironment() -> {
								val path = wallet.bchPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, ChainType.BCH)}")
								val privateKey = BCHWalletUtils.generateBCHKeyPair(mnemonic, path).privateKey
								hold(privateKey, error)
							}
							chainType.isEOS() -> {
								val path = wallet.eosPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, ChainType.EOS)}")
								val privateKey = EOSWalletUtils.generateKeyPair(mnemonic, path).privateKey
								hold(privateKey, error)
							}
						}
					}
					error.isNone() -> hold(null, AccountError.None)
					else -> hold(null, AccountError.WrongPassword)
				}
			}
		}

		// 内部无线程, 调用的时候尽量在异步调用
		private fun Context.getPrivateKeyByWalletID(
			password: String,
			walletID: Int,
			chainType: ChainType,
			hold: (privateKey: String?, error: AccountError) -> Unit
		) {
			getBigIntegerPrivateKeyByWalletID(password, walletID) { privateKeyInteger, error ->
				if (privateKeyInteger.isNotNull() && error.isNone()) hold(when {
					ChainType.isSamePrivateKeyRule(chainType) -> {
						val net =
							if (SharedValue.isTestEnvironment()) TestNet3Params.get() else MainNetParams.get()
						ECKey.fromPrivate(privateKeyInteger).getPrivateKeyAsWiF(net)
					}
					chainType.isLTC() -> {
						// 测试网的私钥同一是 BTC TestNet 的格式
						if (SharedValue.isTestEnvironment()) ECKey.fromPrivate(privateKeyInteger)
							.getPrivateKeyAsWiF(TestNet3Params.get())
						else ECKey.fromPrivate(privateKeyInteger).getPrivateKeyAsWiF(LitecoinNetParams())
					}
					chainType.isEOS() ->
						EOSWalletUtils.generateKeyPairByPrivateKey(privateKeyInteger).privateKey
					chainType.isETC() || chainType.isETH() -> privateKeyInteger.toString(16)
					else -> null
				},
					error
				) else hold(null, error)
			}
		}
	}
}