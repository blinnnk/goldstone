package io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter

import android.content.Context
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.widget.EditText
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.crypto.keystore.storeRootKeyByWalletID
import io.goldstone.blockchain.crypto.multichain.ChainAddresses
import io.goldstone.blockchain.crypto.multichain.ChainPath
import io.goldstone.blockchain.crypto.utils.MultiChainUtils
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view.PrivateKeyImportFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.presenter.WalletImportPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
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
			if (MultiChainUtils.detectPrivateKeyType(privateKeyInput.text.toString()).isNull()) {
				callback(AccountError.InvalidPrivateKey)
				return@checkInputValue
			}
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
		// 深度回退站恢复
		fragment.getParentFragment<WalletImportFragment> {
			overlayView.header.showBackButton(true) {
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
			@UiThread callback: (GoldStoneError) -> Unit
		) {
			val multiChainAddresses =
				MultiChainUtils.getMultiChainAddressesByRootKey(rootPrivateKey)
			hasExistAddress(multiChainAddresses) {
				if (it) context.runOnUiThread {
					callback(AccountError.ExistAddress)
				} else WalletImportPresenter.insertWalletToDatabase(
					multiChainAddresses,
					walletName,
					"",
					ChainPath(),
					hint
				) { walletID, error ->
					if (!walletID.isNull() && error.isNone()) {
						// 如果成功存储 私钥 到 KeyStore
						context.storeRootKeyByWalletID(walletID!!, rootPrivateKey, password)
						callback(error)
					} else callback(error)
				}
			}
		}

		private fun hasExistAddress(
			newMultipleAddresses: ChainAddresses,
			@WorkerThread hold: (hasExistAddress: Boolean) -> Unit
		) {
			doAsync {
				val allNewAddresses = newMultipleAddresses.getAllAddresses()
				val allWallet = GoldStoneDataBase.database.walletDao().getAllWallets()
				if (allWallet.isEmpty()) hold(false)
				else allWallet.map {
					it.ethAddresses + it.btcAddresses + it.ltcAddresses + it.etcAddresses + it.btcSeriesTestAddresses + it.eosAddresses + it.bchAddresses
				}.flatMap { allBIP44Address ->
					allBIP44Address
				}.asSequence().map {
					it.address
				}.any {
					!allNewAddresses.find { new -> new.equals(it, true) }.isNull()
				}.let(hold)
			}
		}
	}
}