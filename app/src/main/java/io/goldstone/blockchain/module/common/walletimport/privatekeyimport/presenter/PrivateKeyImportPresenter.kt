package io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter

import android.support.v4.app.Fragment
import android.widget.EditText
import com.blinnnk.extension.*
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.crypto.getWalletByPrivateKey
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view.PrivateKeyImportFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.alert
import org.jetbrains.anko.appcompat.v7.Appcompat

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
      fragment.context?.alert(Appcompat, "privateKey is not correct")?.show()
      return
    }
    CreateWalletPresenter.checkInputValue(
      nameInput.text.toString(),
      passwordInput.text.toString(),
      repeatPasswordInput.text.toString(),
      isAgree
    ) { passwordValue, walletName ->
      importWallet(privateKeyInput.text.toString(), passwordValue, walletName, fragment)
    }
  }
  companion object {

    /**
     * 导入 `keystore` 是先把 `keystore` 解密成 `private key` 在存储, 所以这个方法是公用的
     */
    fun importWallet(privateKey: String, password: String, name: String, fragment: Fragment) {
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
          }) { fragment.activity?.jump<SplashActivity>() }
        } otherwise {
          System.out.println("import failed $address")
        }
      }
    }

  }
}