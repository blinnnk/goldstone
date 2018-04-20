package io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.blinnnk.extension.forEachOrEnd
import com.blinnnk.extension.isFalse
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.UnsafeReasons
import com.blinnnk.util.checkPasswordInRules
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.crypto.generateWallet
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.agreementfragment.view.AgreementFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.view.CreateWalletFragment
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.view.MnemonicBackupFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TinyNumber
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 22/03/2018 2:46 AM
 * @author KaySaith
 */

class CreateWalletPresenter(
  override val fragment: CreateWalletFragment
) : BasePresenter<CreateWalletFragment>() {

  private var nameText = ""
  private var passwordText = ""
  private var repeatPasswordText = ""

  fun showAgreementFragment() {
    showTargetFragment<AgreementFragment, WalletGenerationFragment>(
      CreateWalletText.agreement, CreateWalletText.mnemonicBackUp
    )
  }

  fun generateWalletWith(isAgree: Boolean) {
    checkInputValue(
      nameText, passwordText, repeatPasswordText, isAgree, fragment.context
    ) { password, walletName ->
      fragment.context?.generateWalletWith(password, walletName)
    }
  }

  fun updateConfirmButtonStyle(
    nameInput: EditText,
    passwordInput: EditText,
    repeatPasswordInput: EditText,
    confirmButton: RoundButton
  ) {
    nameInput.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(string: Editable?) {
        string?.apply { nameText = this.toString() }
        setConfirmButtonStyle(confirmButton)
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
    passwordInput.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(string: Editable?) {
        string?.apply { passwordText = this.toString() }
        setConfirmButtonStyle(confirmButton)
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
    repeatPasswordInput.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(string: Editable?) {
        string?.apply { repeatPasswordText = this.toString() }
        setConfirmButtonStyle(confirmButton)
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
  }

  private fun setConfirmButtonStyle(confirmButton: RoundButton) {
    if (nameText.count() * passwordText.count() * repeatPasswordText.count() != 0) {
      confirmButton.setBlueStyle(20.uiPX())
    } else {
      confirmButton.setGrayStyle(20.uiPX())
    }
  }

  private fun Context.generateWalletWith(password: String, name: String) {
    generateWallet(password) { mnemonicCode, address ->
      // 将基础的不存在安全问题的信息插入数据库
      WalletTable.insert(WalletTable(0, name, address, true)) {
        generateMyTokenInfo(address, fragment.getMainActivity(), true) {
          // 传递数据到下一个 `Fragment`
          val arguments = Bundle().apply {
            putString(ArgumentKey.mnemonicCode, mnemonicCode)
          }
          showMnemonicBackupFragment(arguments)
        }

        XinGePushReceiver.registerWalletAddressForPush()
      }
    }
  }

  private fun showMnemonicBackupFragment(arguments: Bundle) {
    showTargetFragment<MnemonicBackupFragment, WalletGenerationFragment>(
      CreateWalletText.mnemonicBackUp, CreateWalletText.create, arguments
    )
  }

  companion object {
    /**
     * 手下拉取 `GoldStone` 默认显示的 `Token` 清单插入数据库
     */
    fun generateMyTokenInfo(
      ownerAddress: String,
      activity: MainActivity?,
      isNewAccount: Boolean = false,
      callback: () -> Unit = {}
    ) {
      activity?.showLoadingView()
      DefaultTokenTable.getTokens {
        it.filter {
          // 初始的时候显示后台要求标记为 `force show` 的 `Token`
          it.forceShow == TinyNumber.True.value
        }.apply {
          // 如果是新建账户就不用查账了直接是 `0.0`
          if (isNewAccount) {
            insertNewAccountTokenRecord(ownerAddress, callback)
          } else {
            checkAddressBalance(ownerAddress, activity, callback)
          }
        }
      }
    }

    fun updateMyTokensValue(
      walletAddress: String = WalletTable.current.address, callback: () -> Unit = {}
    ) {
      DefaultTokenTable.getTokens { tokenInfo ->
        MyTokenTable.getTokensWith(walletAddress) { myTokens ->
          myTokens.forEachOrEnd { token, isEnd ->
            val tokenContract = tokenInfo.find { it.symbol == token.symbol }?.contract
            // 获取选中的 `Symbol` 的 `Token` 对应 `WalletAddress` 的 `Balance`
            if (token.symbol == CryptoSymbol.eth) {
              GoldStoneEthCall.getEthBalance(walletAddress) {
                GoldStoneDataBase.database.myTokenDao().update(token.apply { balance = it })
              }
            } else {
              GoldStoneEthCall.getTokenBalanceWithContract(tokenContract!!, walletAddress) {
                GoldStoneDataBase.database.myTokenDao().update(token.apply { balance = it })
              }
            }
            if (isEnd) callback()
          }
        }
      }
    }

    fun checkInputValue(
      name: String,
      password: String,
      repeatPassword: String,
      isAgree: Boolean,
      context: Context?,
      callback: (password: String, walletName: String) -> Unit
    ) {
      isAgree.isFalse {
        context?.alert(CreateWalletText.agreeRemind)
        return
      }

      if (password != repeatPassword) {
        context?.alert(CreateWalletText.repeatPassword)
        return
      }

      val walletName = if (name.isEmpty()) "Wallet" else name
      doAsync {
        password.checkPasswordInRules { _, reasons ->
          context?.apply {
            runOnUiThread {
              if (reasons == UnsafeReasons.None) {
                callback(password, walletName)
              } else {
                alert(reasons.info)
              }
            }
          }
        }
      }
    }

    private fun List<DefaultTokenTable>.insertNewAccountTokenRecord(
      address: String, callback: () -> Unit
    ) {
      object : ConcurrentAsyncCombine() {
        override var asyncCount: Int = size
        override fun concurrentJobs() {
          forEach {
            MyTokenTable.insert(MyTokenTable(0, address, it.symbol, 0.0))
            completeMark()
          }
        }
        override fun mergeCallBack() = callback()
      }.start()
    }

    private fun List<DefaultTokenTable>.checkAddressBalance(
      address: String, activity: MainActivity?, callback: () -> Unit
    ) {
      // 不是新建账号就检查余额
      object : ConcurrentAsyncCombine() {
        override var asyncCount: Int = size
        override fun concurrentJobs() {
          forEach { tokenInfo ->
            // 获取选中的 `Symbol` 的 `Token` 对应 `WalletAddress` 的 `Balance`
            if (tokenInfo.symbol.equals(CryptoSymbol.eth, true)) {
              GoldStoneEthCall.getEthBalance(address) {
                MyTokenTable.insert(MyTokenTable(0, address, tokenInfo.symbol, it))
                completeMark()
              }
            } else {
              GoldStoneEthCall.getTokenBalanceWithContract(tokenInfo.contract, address) {
                MyTokenTable.insert(MyTokenTable(0, address, tokenInfo.symbol, it))
                completeMark()
              }
            }
          }
        }
        override fun mergeCallBack() {
          activity?.removeLoadingView()
          callback()
        }
      }.start()
    }

  }
}