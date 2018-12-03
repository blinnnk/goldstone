package io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.presenter

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.keystore.generateTemporaryKeyStore
import io.goldstone.blockchain.crypto.keystore.getKeystoreFileByWalletID
import io.goldstone.blockchain.crypto.keystore.verifyKeystorePasswordByWalletID
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
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

	private val chainType by lazy {
		fragment.arguments?.getInt(ArgumentKey.coinType)
	}

	fun getKeystoreJSON(
		password: String,
		@WorkerThread hold: (keyStoreFile: String?, error: AccountError) -> Unit
	) {
		if (password.isEmpty()) hold(null, AccountError.WrongPassword)
		else {
			if (address.isNotNull() && chainType.isNotNull()) {
				WalletTable.getCurrent(Dispatchers.Default) {
					if (getWalletType().isMultiChain()) getKeystoreByWalletID(password, id, hold)
					else fragment.context?.getKeystoreByAddress(
						this,
						address!!,
						ChainType(chainType!!),
						password,
						hold
					)
				}
			} else hold(null, AccountError.WrongPassword)
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

	private fun Context.getKeystoreByAddress(
		wallet: WalletTable,
		address: String,
		chainType: ChainType,
		password: String,
		hold: (keyJSON: String?, error: AccountError) -> Unit
	) {
		verifyKeystorePasswordByWalletID(password, wallet.id) { isCorrect ->
			if (isCorrect) {
				val mnemonic = JavaKeystoreUtil().decryptData(wallet.encryptMnemonic!!)
				val path = when {
					chainType.isETH() -> wallet.ethPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, chainType)}")
					chainType.isETC() -> wallet.etcPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, chainType)}")
					chainType.isBTC() -> wallet.btcPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, chainType)}")
					chainType.isAllTest() -> wallet.btcTestPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, chainType)}")
					chainType.isLTC() -> wallet.ltcPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, chainType)}")
					chainType.isBCH() -> wallet.bchPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, chainType)}")
					chainType.isEOS() -> wallet.eosPath.replaceAfterLast("/", "${wallet.getAddressPathIndex(address, chainType)}")
					else -> throw Throwable("wrong path")
				}
				generateTemporaryKeyStore(mnemonic, path, password, hold)
			} else hold(null, AccountError.WrongPassword)
		}

	}

}