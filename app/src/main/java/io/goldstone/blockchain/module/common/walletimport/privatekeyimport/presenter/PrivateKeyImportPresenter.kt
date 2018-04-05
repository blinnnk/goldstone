package io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter

import android.widget.EditText
import com.blinnnk.extension.*
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.crypto.getWalletByPrivateKey
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view.PrivateKeyImportFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.toast

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
    nameInput: EditText
  ) {
    privateKeyInput.text.isEmpty().isTrue {
      fragment.context?.toast("privateKey is not correct")
      return
    }
    CreateWalletPresenter.checkInputValue(
      nameInput.text.toString(),
      passwordInput.text.toString(),
      repeatPasswordInput.text.toString(),
      isAgree
    ) { passwordValue, walletName ->
      importWallet(privateKeyInput.text.toString(), passwordValue, walletName)
    }
  }

  private fun importWallet(privateKey: String, password: String, name: String) {
    fragment.context?.getWalletByPrivateKey(privateKey, password) { address ->
      address.isNull().isFalse {
        coroutinesTask({
          GoldStoneDataBase.database.walletDao().findWhichIsUsing(true).let {
            it.isNull().isFalse {
              GoldStoneDataBase.database.walletDao().update(it!!.apply{ isUsing = false } )
            }
            WalletTable.insert(WalletTable(0, name, address!!, true))
            CreateWalletPresenter.generateMyTokenInfo(address)
          }
        }) { fragment.activity?.jump<MainActivity>() }
      } otherwise {
        System.out.println("import failed $address")
      }
    }
  }

}