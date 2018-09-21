package io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter

import android.app.Activity
import android.content.Context
import android.support.annotation.UiThread
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.bitcoin.exportBase58PrivateKey
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.keystore.getPrivateKey
import io.goldstone.blockchain.crypto.keystore.getPrivateKeyByWalletID
import io.goldstone.blockchain.crypto.litecoin.LitecoinNetParams
import io.goldstone.blockchain.crypto.litecoin.exportLTCBase58PrivateKey
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view.PrivateKeyExportFragment
import org.bitcoinj.core.ECKey
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import org.jetbrains.anko.toast

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

	fun getPrivateKey(password: String, hold: String?.() -> Unit) {
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
			@UiThread hold: String?.() -> Unit
		) {
			if (password.isEmpty()) {
				context.toast(ImportWalletText.exportWrongPassword)
				hold(null)
				return
			}
			(context as? Activity)?.apply { SoftKeyboard.hide(this) }
			WalletTable.getWalletType { walletType, wallet ->
				if (walletType.isMultiChain()) context.apply {
					getPrivateKeyByWalletID(
						password,
						wallet.id,
						chainType,
						hold
					)
				} else context.getPrivateKeyByAddress(address, chainType, password, hold)
			}
		}

		private fun Context.getPrivateKeyByAddress(
			address: String,
			chainType: ChainType,
			password: String,
			hold: String?.() -> Unit
		) {
			val isSingleChainWallet = !Config.getCurrentWalletType().isBIP44()
			when (chainType) {
				ChainType.BTC, ChainType.BCH, ChainType.EOS, ChainType.AllTest ->
					getBTCPrivateKeyByAddress(address, password, isSingleChainWallet, hold)
				ChainType.LTC -> exportLTCBase58PrivateKey(address, password, isSingleChainWallet, hold)
				else -> getETHSeriesPrivateKeyByAddress(address, password, isSingleChainWallet, hold)
			}
		}

		private fun Context.getETHSeriesPrivateKeyByAddress(
			address: String,
			password: String,
			isSingleChainWallet: Boolean,
			hold: String.() -> Unit
		) {
			getPrivateKey(
				address,
				password,
				false,
				isSingleChainWallet,
				{ error ->
					hold("")
					LogUtil.error("getPrivateKey", error)
				},
				hold
			)
		}

		private fun Context.getBTCPrivateKeyByAddress(
			address: String,
			password: String,
			isSingleChainWallet: Boolean,
			@UiThread hold: String?.() -> Unit
		) {
			// 所有 `Get PrivateKey` 都在异步获取在主线程返回
			val isTest = BTCUtils.isValidTestnetAddress(address)
			// hold 会在 `exportBase58PrivateKey` 中返回到主线程
			exportBase58PrivateKey(address, password, isSingleChainWallet, isTest, hold)
		}

		private fun Context.getPrivateKeyByWalletID(
			password: String,
			walletID: Int,
			chainType: ChainType,
			@UiThread hold: String?.() -> Unit
		) {
			getPrivateKeyByWalletID(
				password,
				walletID,
				{
					hold(null)
					LogUtil.error("getPrivateKeyByWalletID", it)
				}
			) { privateKeyInteger ->
				hold(when {
					ChainType.isSamePrivateKeyRule(chainType) && Config.isTestEnvironment() -> {
						val net =
							if (Config.isTestEnvironment()) TestNet3Params.get() else MainNetParams.get()
						ECKey.fromPrivate(privateKeyInteger).getPrivateKeyAsWiF(net)
					}
					chainType.isLTC() ->
						ECKey.fromPrivate(privateKeyInteger).getPrivateKeyAsWiF(LitecoinNetParams())
					chainType.isEOS() ->
						EOSWalletUtils.generateKeyPairByPrivateKey(privateKeyInteger).privateKey
					chainType.isETC() || chainType.isETH() ->
						privateKeyInteger.toString(16)
					else -> null
				})
			}
		}
	}
}