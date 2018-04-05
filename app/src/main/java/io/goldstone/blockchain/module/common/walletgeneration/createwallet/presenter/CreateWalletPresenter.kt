package io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import com.blinnnk.extension.isFalse
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.UnsafeReasons
import io.goldstone.blockchain.common.utils.checkPasswordInRules
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.crypto.generateWallet
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.agreementfragment.view.AgreementFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.view.CreateWalletFragment
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.view.MnemonicBackupFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TinyNumber
import org.jetbrains.anko.toast

/**
 * @date 22/03/2018 2:46 AM
 * @author KaySaith
 */

class CreateWalletPresenter(
  override val fragment: CreateWalletFragment
) : BasePresenter<CreateWalletFragment>() {

  fun showAgreementFragment() {
    showTargetFragment<AgreementFragment, WalletGenerationFragment>(
      CreateWalletText.agreement,
      CreateWalletText.mnemonicBackUp
    )

  }

  fun generateWalletWith(nameInput: EditText, passwordInput: EditText, repeatPasswordInput: EditText, isAgree: Boolean) {
    checkInputValue(
      nameInput.text.toString(),
      passwordInput.text.toString(),
      repeatPasswordInput.text.toString(),
      isAgree
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
      CreateWalletText.mnemonicBackUp,
      CreateWalletText.create,
      arguments
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
      DefaultTokenTable.getTokens { tokenList ->
        tokenList.forEachIndexed { index, tokenInfo ->
          // 显示我的 `Token` 后台要求强制显示 `force show` 的或 用户手动设置 `isUsed` 的
          if (tokenInfo.forceShow == TinyNumber.True.value) {
            // 获取选中的 `Symbol` 的 `Token` 对应 `WalletAddress` 的 `Balance`
            if (tokenInfo.symbol == "ETH") {
              GoldStoneEthCall.getEthBalance(ownerAddress) {
                MyTokenTable.insert(MyTokenTable(0, ownerAddress, tokenInfo.symbol, it))
              }
            } else {
              GoldStoneEthCall.getTokenBalanceWithContract(tokenInfo.contract, ownerAddress) {
                MyTokenTable.insert(MyTokenTable(0, ownerAddress, tokenInfo.symbol, it))
              }
            }
            if(index == tokenList.lastIndex) callback()
          }
        }
      }
    }

    fun checkInputValue(
      name: String,
      password: String,
      repeatPassword: String,
      isAgree: Boolean,
      callback: (password: String, walletName: String) -> Unit
      ) {

      isAgree.isFalse {
        GoldStoneAPI.context.toast(CreateWalletText.agreeRemind)
        return
      }

      if (password != repeatPassword) {
        GoldStoneAPI.context.toast(CreateWalletText.repeatPasswordRemind)
        return
      }

      val walletName = if (name.isEmpty()) "Wallet" else name

      password.checkPasswordInRules { _, reasons ->
        if (reasons == UnsafeReasons.None) {
          callback(password, walletName)
        } else {
          GoldStoneAPI.context.toast(reasons.info)
        }
      }
    }
  }

}