package io.goldstone.blockchain.module.common.walletimport.keystoreimport.presenter

import android.widget.EditText
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.crypto.convertKeystoreToModel
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletimport.keystoreimport.view.KeystoreImportFragment
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter.PrivateKeyImportPresenter
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.DecryptKeystore
import org.jetbrains.anko.alert
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.toast
import org.web3j.crypto.Wallet

/**
 * @date 23/03/2018 1:49 AM
 * @author KaySaith
 */

class KeystoreImportPresenter(
  override val fragment: KeystoreImportFragment
  ) : BasePresenter<KeystoreImportFragment>() {

  fun importKeystoreWallet(keystore: String, password: EditText, nameInput: EditText, isAgree: Boolean) {
    isAgree.isTrue {
      Wallet.decrypt(
        password.text.toString(),
        DecryptKeystore.GenerateFile(keystore.convertKeystoreToModel())
      )?.let {
        PrivateKeyImportPresenter.importWallet(
          it.privateKey.toString(16),
          password.text.toString(),
          nameInput.text.toString(),
          fragment
        )
      }
    } otherwise {
      fragment.context?.alert(Appcompat, "You must agree terms")?.show()
    }

  }
}