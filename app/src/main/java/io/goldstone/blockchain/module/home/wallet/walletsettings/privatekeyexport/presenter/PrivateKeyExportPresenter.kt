package io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter

import android.support.annotation.UiThread
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WalletType
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.bitcoin.exportBase58PrivateKey
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.keystore.getPrivateKey
import io.goldstone.blockchain.crypto.keystore.getPrivateKeyByWalletID
import io.goldstone.blockchain.crypto.litecoin.LitecoinNetParams
import io.goldstone.blockchain.crypto.litecoin.exportLTCBase58PrivateKey
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view.PrivateKeyExportFragment
import org.bitcoinj.core.ECKey
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.support.v4.toast

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
		fragment.arguments?.getInt(ArgumentKey.chainType)
	}

	fun getPrivateKey(
		password: String,
		@UiThread hold: String?.() -> Unit
	) {
		if (password.isEmpty()) {
			fragment.toast(ImportWalletText.exportWrongPassword)
			hold(null)
			return
		}
		fragment.activity?.apply { SoftKeyboard.hide(this) }
		WalletTable.getWalletType { walletType, wallet ->
			if (walletType.content.equals(WalletType.MultiChain.content, true)) {
				getPrivateKeyByWalletID(password, wallet.id, hold)
			} else {
				getPrivateKeyByAddress(password, hold)
			}
		}
	}

	private fun getPrivateKeyByWalletID(
		password: String,
		walletID: Int,
		hold: String?.() -> Unit
	) {
		fragment.context?.getPrivateKeyByWalletID(
			password,
			walletID,
			{
				hold(null)
				LogUtil.error("getPrivateKeyByWalletID", it)
			}
		) {
			hold(when {
				ChainType.isSamePrivateKeyRule(chainType!!) && Config.isTestEnvironment() -> {
					val net =
						if (Config.isTestEnvironment()) TestNet3Params.get() else MainNetParams.get()
					ECKey.fromPrivate(it).getPrivateKeyAsWiF(net)
				}
				chainType == ChainType.LTC.id -> ECKey.fromPrivate(it).getPrivateKeyAsWiF(LitecoinNetParams())
				chainType == ChainType.EOS.id -> EOSWalletUtils.generateKeyPairByPrivateKey(it).privateKey
				chainType == ChainType.ETC.id || chainType == ChainType.ETH.id -> it.toString(16)
				else -> null
			})
		}
	}

	private fun getPrivateKeyByAddress(
		password: String,
		hold: String?.() -> Unit
	) {
		address?.let {
			val isSingleChainWallet =
				!Config.getCurrentWalletType().equals(WalletType.Bip44MultiChain.content, true)
			when (chainType) {
				ChainType.BTC.id,
				ChainType.BCH.id,
				ChainType.EOS.id,
				ChainType.AllTest.id -> getBTCPrivateKeyByAddress(
					it,
					password,
					isSingleChainWallet,
					hold
				)
				ChainType.LTC.id -> fragment.context?.exportLTCBase58PrivateKey(
					it,
					password,
					isSingleChainWallet,
					hold
				)
				else -> getETHERCorETCPrivateKeyByAddress(
					it,
					password,
					isSingleChainWallet,
					hold
				)
			}
		}
	}

	private fun getETHERCorETCPrivateKeyByAddress(
		address: String,
		password: String,
		isSingleChainWallet: Boolean,
		hold: String.() -> Unit
	) {
		doAsync {
			fragment.context?.getPrivateKey(
				address,
				password,
				false,
				isSingleChainWallet,
				{ error ->
					GoldStoneAPI.context.runOnUiThread { hold("") }
					LogUtil.error("getPrivateKey", error)
				},
				hold
			)
		}
	}

	private fun getBTCPrivateKeyByAddress(
		address: String,
		password: String,
		isSingleChainWallet: Boolean,
		hold: String?.() -> Unit
	) {
		doAsync {
			val isTest = BTCUtils.isValidTestnetAddress(address)
			fragment.context?.exportBase58PrivateKey(
				address,
				password,
				isSingleChainWallet,
				isTest,
				hold
			)
		}
	}
}