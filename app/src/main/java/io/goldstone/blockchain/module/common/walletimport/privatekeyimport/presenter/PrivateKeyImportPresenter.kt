package io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter

import android.support.v4.app.Fragment
import android.widget.EditText
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.crypto.getWalletByPrivateKey
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view.PrivateKeyImportFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity

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
    privateKeyInput.text.isEmpty() isTrue {
      fragment.context?.alert("privateKey is not correct")
      return
    }
    CreateWalletPresenter.checkInputValue(
      nameInput.text.toString(),
      passwordInput.text.toString(),
      repeatPasswordInput.text.toString(),
      isAgree,
      fragment.context
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
        WalletTable.getWalletByAddress(address!!) {
          it.isNull() isTrue {
            // 在数据库记录钱包信息
            WalletTable.insertAddress(address, name) {
              // 创建钱包并获取默认的 `token` 信息
              CreateWalletPresenter.generateMyTokenInfo(address) {
                fragment.activity?.jump<SplashActivity>()
              }
              // 注册钱包地址用于发送 `Push`
              XinGePushReceiver.registerWalletAddressForPush()
            }
          } otherwise {
            fragment.context?.alert("There is already this account in gold stone")
          }
        }
      }
    }

  }
}