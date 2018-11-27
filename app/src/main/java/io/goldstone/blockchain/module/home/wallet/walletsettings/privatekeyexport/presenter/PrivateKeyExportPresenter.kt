package io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter

import android.content.Context
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.bitcoin.exportBase58PrivateKey
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.keystore.getBigIntegerPrivateKeyByWalletID
import io.goldstone.blockchain.crypto.keystore.getPrivateKey
import io.goldstone.blockchain.crypto.litecoin.LitecoinNetParams
import io.goldstone.blockchain.crypto.litecoin.exportLTCBase58PrivateKey
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
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
				val wallet =
					GoldStoneDataBase.database.walletDao().findWhichIsUsing(true)
				if (wallet?.getWalletType()?.isMultiChain() == true)
					GoldStoneAPI.context.getPrivateKeyByWalletID(password, wallet.id, chainType, hold)
				else GoldStoneAPI.context.getPrivateKeyByAddress(address, chainType, password, hold)
			}
		}

		private fun Context.getPrivateKeyByAddress(
			address: String,
			chainType: ChainType,
			password: String,
			hold: (privateKey: String?, error: AccountError) -> Unit
		) {
			when {
				chainType.isBTC() || chainType.isBCH() || chainType.isAllTest() ->
					getBTCPrivateKeyByAddress(address, password, true, hold)
				chainType.isEOS() ->
					getBTCPrivateKeyByAddress(address, password, false, hold)
				chainType.isLTC() -> {
					// 测试网的 `LTC` 使用的是 `BTCTestSeries` 格式
					if (SharedValue.isTestEnvironment())
						getBTCPrivateKeyByAddress(address, password, true, hold)
					else exportLTCBase58PrivateKey(address, password, hold)
				}
				else -> getETHSeriesPrivateKeyByAddress(address, password, hold)
			}
		}

		private fun Context.getETHSeriesPrivateKeyByAddress(
			address: String,
			password: String,
			@UiThread hold: (privateKey: String?, error: AccountError) -> Unit
		) {
			getPrivateKey(
				address,
				password,
				false,
				hold
			)
		}

		private fun Context.getBTCPrivateKeyByAddress(
			address: String,
			password: String,
			isCompress: Boolean,
			hold: (privateKey: String?, error: AccountError) -> Unit
		) {
			// 所有 `Get PrivateKey` 都在异步获取在主线程返回
			val isTest = BTCUtils.isValidTestnetAddress(address)
			// hold 会在 `exportBase58PrivateKey` 中返回到主线程
			exportBase58PrivateKey(address, password, isTest, isCompress, hold)
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