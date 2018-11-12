package io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.bitcoin.exportBase58KeyStoreFile
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.keystore.getKeystoreFile
import io.goldstone.blockchain.crypto.keystore.getKeystoreFileByWalletID
import io.goldstone.blockchain.crypto.multichain.ChainAddresses
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.view.KeystoreExportFragment
import kotlinx.coroutines.Dispatchers

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

	fun getKeystoreJSON(
		password: String,
		@WorkerThread hold: (keyStoreFile: String?, error: AccountError) -> Unit
	) {
		if (password.isEmpty()) hold(null, AccountError.WrongPassword)
		else {
			fragment.activity?.apply { SoftKeyboard.hide(this) }
			address?.let {
				WalletTable.getCurrent(Dispatchers.Default) {
					if (getWalletType().isMultiChain()) getKeystoreByWalletID(password, id, hold)
					else getKeystoreByAddress(password, it, hold)
				}
			}
		}
	}

	private fun getKeystoreByWalletID(
		password: String,
		walletID: Int,
		hold: (keyStoreFile: String?, error: AccountError) -> Unit
	) {
		fragment.context?.getKeystoreFileByWalletID(
			password,
			walletID,
			hold
		)
	}

	private fun getKeystoreByAddress(
		password: String,
		address: String,
		hold: (keystoreFile: String?, error: AccountError) -> Unit
	) {
		if (ChainAddresses.isBTCSeries(address) || EOSWalletUtils.isValidAddress(address)) {
			getBTCSeriesKeystoreFile(address, password) { keystoreJSON, error ->
				if (!keystoreJSON.isNull() && error.isNone()) {
					hold(keystoreJSON, error)
				} else hold(null, error)
			}
		} else {
			getETHSeriesKeystoreFile(address, password) { keystoreJSON, error ->
				if (!keystoreJSON.isNull() && error.isNone()) {
					hold(keystoreJSON, error)
				} else hold(null, error)
			}
		}
	}

	private fun getBTCSeriesKeystoreFile(
		walletAddress: String,
		password: String,
		hold: (keyStoreFile: String?, error: AccountError) -> Unit
	) {
		fragment.context?.exportBase58KeyStoreFile(
			walletAddress,
			password,
			hold
		)
	}

	private fun getETHSeriesKeystoreFile(
		address: String,
		password: String,
		hold: (keyStoreFile: String?, error: AccountError) -> Unit
	) {
		fragment.context?.getKeystoreFile(
			address,
			password,
			false,
			hold
		)
	}
}