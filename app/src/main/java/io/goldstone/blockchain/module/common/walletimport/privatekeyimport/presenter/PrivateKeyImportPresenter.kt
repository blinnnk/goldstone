package io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter

import android.content.Context
import android.widget.EditText
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.crypto.keystore.storeRootKeyByWalletID
import io.goldstone.blockchain.crypto.multichain.ChainPath
import io.goldstone.blockchain.crypto.utils.MultiChainUtils
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view.PrivateKeyImportFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.presenter.WalletImportPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import java.math.BigInteger

/**
 * @date 23/03/2018 2:13 AM
 * @author KaySaith
 */
class PrivateKeyImportPresenter(
	override val fragment: PrivateKeyImportFragment
) : BasePresenter<PrivateKeyImportFragment>() {

	fun importWalletByPrivateKey(
		privateKeyInput: EditText,
		passwordInput: EditText,
		repeatPasswordInput: EditText,
		isAgree: Boolean,
		nameInput: EditText,
		hintInput: EditText,
		callback: (GoldStoneError) -> Unit
	) {
		if (privateKeyInput.text.isEmpty()) callback(AccountError.InvalidPrivateKey)
		else CreateWalletPresenter.checkInputValue(
			nameInput.text.toString(),
			passwordInput.text.toString(),
			repeatPasswordInput.text.toString(),
			isAgree,
			callback
		) { passwordValue, walletName ->
			val rootPrivateKey =
				MultiChainUtils.getRootPrivateKey(privateKeyInput.text.toString())
			fragment.context?.let {
				importWalletByRootKey(
					it,
					rootPrivateKey,
					walletName,
					passwordValue,
					hintInput.text.toString(),
					callback
				)
			}
		}
	}

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		setRootChildFragmentBackEvent<WalletImportFragment>(fragment)
	}

	companion object {

		fun importWalletByRootKey(
			context: Context,
			rootPrivateKey: BigInteger,
			walletName: String,
			password: String,
			hint: String,
			callback: (GoldStoneError) -> Unit
		) {
			val multiChainAddresses =
				MultiChainUtils.getMultiChainAddressesByRootKey(rootPrivateKey)
			context.apply {
				// 存储可读信息到数据库
				WalletImportPresenter.insertWalletToDatabase(
					multiChainAddresses,
					walletName,
					"",
					ChainPath(),
					hint
				) { walletID, error ->
					if (!walletID.isNull() && error.isNone()) {
						// 如果成功存储 私钥 到 KeyStore
						storeRootKeyByWalletID(walletID!!, rootPrivateKey, password)
						callback(error)
					} else callback(error)
				}
			}
		}
	}
}