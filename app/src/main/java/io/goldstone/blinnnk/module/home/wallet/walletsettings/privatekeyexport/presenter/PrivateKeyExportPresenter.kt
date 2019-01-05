package io.goldstone.blinnnk.module.home.wallet.walletsettings.privatekeyexport.presenter

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import io.goldstone.blinnnk.GoldStoneApp
import io.goldstone.blinnnk.common.base.basefragment.BasePresenter
import io.goldstone.blinnnk.common.error.AccountError
import io.goldstone.blinnnk.common.sharedpreference.SharedValue
import io.goldstone.blinnnk.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blinnnk.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blinnnk.crypto.eos.EOSWalletUtils
import io.goldstone.blinnnk.crypto.keystore.generateETHSeriesAddress
import io.goldstone.blinnnk.crypto.keystore.getBigIntegerPrivateKeyByWalletID
import io.goldstone.blinnnk.crypto.litecoin.LTCWalletUtils
import io.goldstone.blinnnk.crypto.litecoin.LitecoinNetParams
import io.goldstone.blinnnk.crypto.multichain.*
import io.goldstone.blinnnk.crypto.utils.JavaKeystoreUtil
import io.goldstone.blinnnk.crypto.utils.KeystoreInfo
import io.goldstone.blinnnk.crypto.utils.MultiChainUtils
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blinnnk.module.home.wallet.walletsettings.privatekeyexport.view.PrivateKeyExportFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bitcoinj.core.ECKey
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import java.math.BigInteger

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
		// 导出指定地址的私钥需要用到 `Address` 不同单纯用 `chainType` 推到, 所以这里保留 `Address` 字段
		@WorkerThread
		fun getPrivateKey(
			address: String,
			chainType: ChainType,
			password: String,
			hold: (privateKey: String?, error: AccountError) -> Unit
		) = GlobalScope.launch(Dispatchers.Default) {
			if (password.isEmpty()) hold(null, AccountError.WrongPassword)
			else {
				val wallet = WalletTable.dao.findWhichIsUsing() ?: return@launch
				if (wallet.getWalletType().isMultiChain()) GoldStoneApp.appContext.verifyAndGetPrivateKey(
					password,
					wallet.id,
					chainType,
					hold
				) else verifyAndGeneratePrivatekey(
					GoldStoneApp.appContext,
					wallet,
					address,
					chainType,
					password,
					hold
				)
			}
		}

		/**
		 * 指纹识别后会用 `cipher` 解密本地的加密 `key`, 可能是 `Mnemonic` 也可能是 `Root Privatekey`
		 */
		@WorkerThread
		fun getPrivateKeyByRootData(
			address: String,
			chainType: ChainType,
			rootData: String,
			hold: (privateKey: String) -> Unit
		) = GlobalScope.launch(Dispatchers.Default) {
			WalletTable.dao.findWhichIsUsing()?.apply {
				if (getWalletType().isMultiChain()) {
					getSubKeyFromRootKey(
						chainType,
						MultiChainUtils.getRootPrivateKey(rootData)
					).let(hold)
				} else generateKeyByMnemonic(address, this, chainType, rootData, hold)
			}
		}

		// 需要检验身份进行解密
		private fun verifyAndGeneratePrivatekey(
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
						val mnemonic =
							JavaKeystoreUtil(KeystoreInfo.isMnemonic()).decryptData(wallet.encryptMnemonic!!)
						generateKeyByMnemonic(address, wallet, chainType, mnemonic) {
							hold(it, error)
						}
					}
					error.isNone() -> hold(null, AccountError.None)
					else -> hold(null, AccountError.WrongPassword)
				}
			}
		}

		// 从指纹检验身份通过后直接获取数据生成
		private fun generateKeyByMnemonic(
			address: String,
			wallet: WalletTable,
			chainType: ChainType,
			mnemonic: String,
			hold: (privateKey: String) -> Unit
		) {
			when {
				chainType.isETH() || chainType.isETC() -> {
					val path =
						if (chainType.isETH()) wallet.ethPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, ChainType.ETH)}")
						else wallet.etcPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, ChainType.ETC)}")
					val privateKey =
						generateETHSeriesAddress(mnemonic, path).privateKey.toString(16)
					hold(privateKey)
				}
				chainType.isBTC() || (chainType.isBTCSeries() && SharedValue.isTestEnvironment()) || chainType.isAllTest() -> {
					val path = if (!SharedValue.isTestEnvironment()) wallet.btcPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, ChainType.BTC)}")
					else wallet.btcTestPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, ChainType.AllTest)}")
					BTCWalletUtils.getBitcoinWalletByMnemonic(mnemonic, path) { _, secret ->
						hold(secret)
					}
				}

				chainType.isLTC() && !SharedValue.isTestEnvironment() -> {
					val path = wallet.ltcPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, ChainType.LTC)}")
					val privateKey = LTCWalletUtils.generateBase58Keypair(mnemonic, path).privateKey
					hold(privateKey)
				}
				chainType.isBCH() && !SharedValue.isTestEnvironment() -> {
					val path = wallet.bchPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, ChainType.BCH)}")
					val privateKey = BCHWalletUtils.generateBCHKeyPair(mnemonic, path).privateKey
					hold(privateKey)
				}
				chainType.isEOS() -> {
					val path = wallet.eosPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, ChainType.EOS)}")
					val privateKey = EOSWalletUtils.generateKeyPair(mnemonic, path).privateKey
					hold(privateKey)
				}
			}
		}

		// 内部无线程, 调用的时候尽量在异步调用
		// 通过 `WalletID` 从 `Geth` 的 `KeyStore` 获取存入的 `Root PrivateKey`
		private fun Context.verifyAndGetPrivateKey(
			password: String,
			walletID: Int,
			chainType: ChainType,
			hold: (privateKey: String?, error: AccountError) -> Unit
		) {
			getBigIntegerPrivateKeyByWalletID(password, walletID) { rootKey, error ->
				if (rootKey.isNotNull() && error.isNone())
					hold(getSubKeyFromRootKey(chainType, rootKey), error)
				else hold(null, error)
			}
		}

		@WorkerThread
		fun getSubKeyFromRootKey(chainType: ChainType, rootKey: BigInteger): String {
			return when {
				ChainType.isSamePrivateKeyRule(chainType) -> {
					val net =
						if (SharedValue.isTestEnvironment()) TestNet3Params.get() else MainNetParams.get()
					ECKey.fromPrivate(rootKey).getPrivateKeyAsWiF(net)
				}
				chainType.isLTC() -> {
					// 测试网的私钥同一是 BTC TestNet 的格式
					if (SharedValue.isTestEnvironment()) ECKey.fromPrivate(rootKey)
						.getPrivateKeyAsWiF(TestNet3Params.get())
					else ECKey.fromPrivate(rootKey).getPrivateKeyAsWiF(LitecoinNetParams())
				}
				chainType.isEOS() ->
					EOSWalletUtils.generateKeyPairByPrivateKey(rootKey).privateKey
				chainType.isETC() || chainType.isETH() -> rootKey.toString(16)
				else -> throw Throwable("unknown chain type")
			}
		}
	}
}