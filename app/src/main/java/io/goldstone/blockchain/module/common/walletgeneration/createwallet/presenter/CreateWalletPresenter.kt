package io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import com.blinnnk.extension.forEachOrEnd
import com.blinnnk.extension.isFalse
import com.blinnnk.util.UnsafeReasons
import com.blinnnk.util.checkPasswordInRules
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.crypto.generateWallet
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.common.walletgeneration.agreementfragment.view.AgreementFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.view.CreateWalletFragment
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.view.MnemonicBackupFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TinyNumber

/**
 * @date 22/03/2018 2:46 AM
 * @author KaySaith
 */

class CreateWalletPresenter(
  override val fragment: CreateWalletFragment
) : BasePresenter<CreateWalletFragment>() {

  fun showAgreementFragment() {
    showTargetFragment<AgreementFragment, WalletGenerationFragment>(
      CreateWalletText.agreement, CreateWalletText.mnemonicBackUp
    )
  }

  fun generateWalletWith(
    nameInput: EditText, passwordInput: EditText, repeatPasswordInput: EditText, isAgree: Boolean
  ) {
    checkInputValue(
      nameInput.text.toString(),
      passwordInput.text.toString(),
      repeatPasswordInput.text.toString(),
      isAgree,
      fragment.context
    ) { password, walletName ->
      fragment.context?.generateWalletWith(password, walletName)
    }
  }

  private fun Context.generateWalletWith(password: String, name: String) {
    generateWallet(password) { mnemonicCode, address ->

      generateMyTokenInfo(address) { hasCreatedWallet?.run() }

      // 将基础的不存在安全问题的信息插入数据库
      WalletTable.insert(WalletTable(0, name, address, true)) {
        // 传递数据到下一个 `Fragment`
        val arguments = Bundle().apply {
          putString(ArgumentKey.mnemonicCode, mnemonicCode)
          putString(ArgumentKey.walletAddress, address)
        }
        showMnemonicBackupFragment(arguments)
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
     * 创建钱包首先会从服务拉取默认显示的 `Tokens Type` 这一步需要经过
     * 耗时的查询, 余额, 信息等. 所以在这里增加一个结束的回调判断.
     */
    var hasCreatedWallet: Runnable? = null

    /**
     * 手下拉取 `GoldStone` 默认显示的 `Token` 清单插入数据库
     */
    fun generateMyTokenInfo(ownerAddress: String, callback: () -> Unit = {}) {
      DefaultTokenTable.getTokens {
        it.filter {
          // 初始的时候显示后台要求标记为 `force show` 的 `Token`
          it.forceShow == TinyNumber.True.value
        }.apply {
          object : ConcurrentAsyncCombine() {
            override var asyncCount: Int = size
            override fun concurrentJobs() {
              forEach { tokenInfo ->
                // 获取选中的 `Symbol` 的 `Token` 对应 `WalletAddress` 的 `Balance`
                if (tokenInfo.symbol.equals(CryptoSymbol.eth, true)) {
                  GoldStoneEthCall.getEthBalance(ownerAddress) {
                    MyTokenTable.insert(MyTokenTable(0, ownerAddress, tokenInfo.symbol, it))
                    completeMark()
                  }
                } else {
                  GoldStoneEthCall.getTokenBalanceWithContract(tokenInfo.contract, ownerAddress) {
                    MyTokenTable.insert(MyTokenTable(0, ownerAddress, tokenInfo.symbol, it))
                    completeMark()
                  }
                }
              }
            }
            override fun mergeCallBack() = callback()
          }.start()
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

      password.checkPasswordInRules { _, reasons ->
        if (reasons == UnsafeReasons.None) {
          callback(password, walletName)
        } else {
          context?.alert(reasons.info)
        }
      }
    }
  }
}