package io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.presenter

import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WalletType
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.bitcoin.exportBase58KeyStoreFile
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.getKeystoreFile
import io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.view.KeystoreExportFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.uiThread

/**
 * @date 06/04/2018 1:46 AM
 * @author KaySaith
 */
class KeystoreExportPresenter(
	override val fragment: KeystoreExportFragment
) : BasePresenter<KeystoreExportFragment>() {

	private val address by lazy {
		fragment.arguments?.getString(ArgumentKey.address)
	}

	fun getKeystoreJson(password: String, hold: (String?) -> Unit) {
		if (password.isEmpty()) {
			fragment.toast(ImportWalletText.exportWrongPassword)
			hold(null)
			return
		}

		fragment.activity?.apply {
			SoftKeyboard.hide(this)
		}
		address?.let { getKeystoreByAddress(password, it, hold) }
	}

	private fun getKeystoreByAddress(
		password: String,
		address: String,
		hold: String?.() -> Unit
	) {
		doAsync {
			val isSingleChainWallet =
				!Config.getCurrentWalletType().equals(WalletType.MultiChain.content, true)
			if (CryptoValue.isBTCSeriesAddress(address) || EOSWalletUtils.isValidAddress(address)) {
				getBTCSeriesKeystoreFile(address, password, isSingleChainWallet) { keystoreJSON ->
					uiThread { hold(keystoreJSON) }
				}
			} else {
				getETHERC20OrETCKeystoreFile(address, password, isSingleChainWallet) { keystoreJSON ->
					uiThread { hold(keystoreJSON) }
				}
			}
		}
	}

	private fun getBTCSeriesKeystoreFile(
		walletAddress: String,
		password: String,
		isSingleChainWallet: Boolean,
		hold: (String?) -> Unit
	) {
		fragment.context?.exportBase58KeyStoreFile(
			walletAddress,
			password,
			isSingleChainWallet
		) {
			hold(it)
		}
	}

	private fun getETHERC20OrETCKeystoreFile(
		address: String,
		password: String,
		isSingleChainWallet: Boolean,
		hold: (String?) -> Unit
	) {
		fragment.context?.getKeystoreFile(
			address,
			password,
			false,
			isSingleChainWallet,
			{
				hold(null)
			}
		) { it ->
			hold(it)
		}
	}
}