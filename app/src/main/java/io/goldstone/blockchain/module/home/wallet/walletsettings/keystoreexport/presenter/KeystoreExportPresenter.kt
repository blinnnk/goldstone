package io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.presenter

import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.bitcoin.exportBase58KeyStoreFile
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.keystore.getKeystoreFile
import io.goldstone.blockchain.crypto.keystore.getKeystoreFileByWalletID
import io.goldstone.blockchain.crypto.multichain.ChainAddresses
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
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

	fun getKeystoreJSON(password: String, hold: (String?) -> Unit) {
		if (password.isEmpty()) {
			fragment.toast(ImportWalletText.exportWrongPassword)
			hold(null)
			return
		}
		fragment.activity?.apply {
			SoftKeyboard.hide(this)
		}

		address?.let {
			WalletTable.getWalletType { walletType, wallet ->
				if (walletType.isMultiChain()) getKeystoreByWalletID(password, wallet.id, hold)
				else getKeystoreByAddress(password, it, hold)
			}
		}
	}

	private fun getKeystoreByWalletID(password: String, walletID: Int, hold: (String?) -> Unit) {
		fragment.context?.getKeystoreFileByWalletID(
			password,
			walletID,
			{
				LogUtil.error("getKeystoreByWalletID", it)
			},
			hold
		)
	}

	private fun getKeystoreByAddress(
		password: String,
		address: String,
		hold: String?.() -> Unit
	) {
		doAsync {
			val isSingleChainWallet =
				!SharedWallet.getCurrentWalletType().isBIP44()
			if (ChainAddresses.isBTCSeriesAddress(address) || EOSWalletUtils.isValidAddress(address)) {
				getBTCSeriesKeystoreFile(address, password, isSingleChainWallet) { keystoreJSON ->
					uiThread { hold(keystoreJSON) }
				}
			} else {
				getETHSeriesKeystoreFile(address, password, isSingleChainWallet) { keystoreJSON ->
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
			isSingleChainWallet,
			hold
		)
	}

	private fun getETHSeriesKeystoreFile(
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
			},
			hold
		)
	}
}