package io.goldstone.blockchain.module.common.walletimport.mnemonicimport.presenter

import android.widget.EditText
import com.blinnnk.extension.*
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.crypto.getWalletByMnemonic
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.mnemonicimport.view.MnemonicImportDetailFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import org.jetbrains.anko.alert
import org.jetbrains.anko.appcompat.v7.Appcompat

/**
 * @date 23/03/2018 1:46 AM
 * @author KaySaith
 */

class MnemonicImportDetailPresenter(
  override val fragment: MnemonicImportDetailFragment
) : BasePresenter<MnemonicImportDetailFragment>() {

  fun importWalletByMnemonic(
    mnemonicInput: EditText,
    passwordInput: EditText,
    repeatPasswordInput: EditText,
    isAgree: Boolean,
    nameInput: EditText
    ) {
    mnemonicInput.text.isEmpty().isTrue {
      fragment.context?.alert(Appcompat, "mnemonic is not correct")?.show()
      return
    }
    CreateWalletPresenter.checkInputValue(
      nameInput.text.toString(),
      passwordInput.text.toString(),
      repeatPasswordInput.text.toString(),
      isAgree
      ) { passwordValue, walletName ->
      importWallet(mnemonicInput.text.toString(), passwordValue, walletName)
    }
  }

  private fun importWallet(mnemonic: String, password: String, name: String) {
    fragment.context?.getWalletByMnemonic(mnemonic, password) { address ->
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