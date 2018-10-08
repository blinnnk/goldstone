package io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter

import android.app.Activity
import android.content.Context
import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.bitcoin.exportBase58PrivateKey
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.keystore.getBigIntegerPrivateKeyByWalletID
import io.goldstone.blockchain.crypto.keystore.getPrivateKey
import io.goldstone.blockchain.crypto.litecoin.LitecoinNetParams
import io.goldstone.blockchain.crypto.litecoin.exportLTCBase58PrivateKey
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view.PrivateKeyExportFragment
import org.bitcoinj.core.ECKey
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params

/**
 * @date 06/04/2018 1:02 AM
 * @author KaySaith
 */
class PrivateKeyExportPresenter(
	override val fragment: PrivateKeyExportFragment
) : BasePresenter<PrivateKeyExportFragment>() {

	private val address by lazy {
		fragment.arguments?.getString(ArgumentKey.address)
	}
	private val chainType by lazy {
		fragment.arguments?.getInt(ArgumentKey.chainType)?.let { ChainType(it) }
	}

	fun getPrivateKey(password: String, hold: (privateKey: String?, error: AccountError) -> Unit) {
		fragment.context?.apply {
			getPrivateKey(this, address!!, chainType!!, password, hold)
		}
	}

	companion object {
		fun getPrivateKey(
			context: Context,
			address: String,
			chainType: ChainType,
			password: String,
			@UiThread hold: (privateKey: String?, error: AccountError) -> Unit
		) {
			if (password.isEmpty())
				hold(null, AccountError.WrongPassword)
			else WalletTable.getWalletType { walletType, wallet ->
				if (walletType.isMultiChain()) {
					context.getPrivateKeyByWalletID(password, wallet.id, chainType, hold)
				} else context.getPrivateKeyByAddress(address, chainType, password, hold)
			}
			(context as? Activity)?.apply { SoftKeyboard.hide(this) }
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
				chainType.isLTC() -> exportLTCBase58PrivateKey(address, password, hold)
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
				true,
				hold
			)
		}

		private fun Context.getBTCPrivateKeyByAddress(
			address: String,
			password: String,
			isCompress: Boolean,
			@UiThread hold: (privateKey: String?, error: AccountError) -> Unit
		) {
			// 所有 `Get PrivateKey` 都在异步获取在主线程返回
			val isTest = BTCUtils.isValidTestnetAddress(address)
			// hold 会在 `exportBase58PrivateKey` 中返回到主线程
			exportBase58PrivateKey(address, password, isTest, isCompress, hold)
		}

		private fun Context.getPrivateKeyByWalletID(
			password: String,
			walletID: Int,
			chainType: ChainType,
			@UiThread hold: (privateKey: String?, error: AccountError) -> Unit
		) {
			getBigIntegerPrivateKeyByWalletID(password, walletID) { privateKeyInteger, error ->
				if (!privateKeyInteger.isNull() && error.isNone()) hold(when {
					ChainType.isSamePrivateKeyRule(chainType) -> {
						val net =
							if (SharedValue.isTestEnvironment()) TestNet3Params.get() else MainNetParams.get()
						ECKey.fromPrivate(privateKeyInteger!!).getPrivateKeyAsWiF(net)
					}
					chainType.isLTC() ->
						ECKey.fromPrivate(privateKeyInteger).getPrivateKeyAsWiF(LitecoinNetParams())
					chainType.isEOS() ->
						EOSWalletUtils.generateKeyPairByPrivateKey(privateKeyInteger!!).privateKey
					chainType.isETC() || chainType.isETH() ->
						privateKeyInteger!!.toString(16)
					else -> null
				}, AccountError.None) else hold(null, error)
			}
		}
	}
}