package io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter

import android.content.Context
import android.support.annotation.WorkerThread
import android.widget.EditText
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.utils.AddressUtils.hasExistAddress
import io.goldstone.blockchain.crypto.keystore.storeRootKeyByWalletID
import io.goldstone.blockchain.crypto.multichain.ChainPath
import io.goldstone.blockchain.crypto.utils.MultiChainUtils
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view.PrivateKeyImportFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.presenter.WalletImportPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
	) = GlobalScope.launch(Dispatchers.Default) {
		if (privateKeyInput.text.isEmpty()) callback(AccountError.InvalidPrivateKey)
		else CreateWalletPresenter.checkInputValue(
			nameInput.text.toString(),
			passwordInput.text.toString(),
			repeatPasswordInput.text.toString(),
			isAgree
		) { password, walletName, error ->
			when {
				error.hasError() -> callback(error)
				MultiChainUtils.detectPrivateKeyType(privateKeyInput.text.toString()).isNull() -> callback(AccountError.InvalidPrivateKey)
				else -> fragment.context?.apply {
					importWalletByRootKey(
						this,
						MultiChainUtils.getRootPrivateKey(privateKeyInput.text.toString()),
						walletName!!,
						password!!,
						hintInput.text.toString(),
						callback
					)
				}
			}
		}
	}

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		setRootChildFragmentBackEvent<WalletImportFragment>(fragment)
		// 深度回退站恢复
		fragment.getParentFragment<WalletImportFragment> {
			showBackButton(true) {
				presenter.popFragmentFrom<PrivateKeyImportFragment>()
			}
		}
	}

	companion object {

		fun importWalletByRootKey(
			context: Context,
			rootPrivateKey: BigInteger,
			walletName: String,
			password: String,
			hint: String,
			@WorkerThread callback: (GoldStoneError) -> Unit
		) {
			val multiChainAddresses =
				MultiChainUtils.getMultiChainAddressesByRootKey(rootPrivateKey)
			hasExistAddress(multiChainAddresses.getAllAddresses()) {
				if (it) callback(AccountError.ExistAddress)
				else WalletImportPresenter.insertWalletToDatabase(
					multiChainAddresses,
					walletName,
					"",
					ChainPath(),
					hint
				) { walletID, error ->
					if (walletID != null && error.isNone()) {
						// 如果成功存储 私钥 到 KeyStore
						context.storeRootKeyByWalletID(walletID, rootPrivateKey, password)
						callback(error)
					} else callback(error)
				}
			}
		}
	}
}