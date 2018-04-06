@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.common.value

import io.goldstone.blockchain.GoldStoneApp.Companion.currentLanguage

/**
 * @date 21/03/2018 7:34 PM
 * @author KaySaith
 */

object CreateWalletText {
  @JvmField
  val attention = when (currentLanguage) {
    HoneyLanguage.English.code -> "Password strength is critical to guard your wallet We can’t recover the password, please back up cautiously"
    else -> ""
  }
  @JvmField
  val create = when (currentLanguage) {
    HoneyLanguage.English.code -> "Create Wallet"
    else -> "创建数字钱包"
  }
  @JvmField
  val mnemonicBackUp = when (currentLanguage) {
    HoneyLanguage.English.code -> "Mnemonic Backup"
    else -> "备份助记词"
  }
  @JvmField
  val agreement = when (currentLanguage) {
    HoneyLanguage.English.code -> "Agreement"
    else -> ""
  }
  @JvmField
  val agreeRemind = when (currentLanguage) {
    HoneyLanguage.English.code -> "You need agree the terms"
    else -> ""
  }
  @JvmField
  val repeatPasswordRemind = when (currentLanguage) {
    HoneyLanguage.English.code -> "repeat password must be the same as password"
    else -> ""
  }
  @JvmField
  val mnemonicBackupAttention = when (currentLanguage) {
    HoneyLanguage.English.code -> "confirm your mnemonic words to remember your account baby tell me why and what happened it"
    else -> ""
  }
  @JvmField
  val mnemonicConfirmationDescription = when (currentLanguage) {
    HoneyLanguage.English.code -> "please confirm your mnemonic words to remember your account baby tell me why and what happened it"
    else -> ""
  }
  @JvmField
  val password = when (currentLanguage) {
    HoneyLanguage.English.code -> "Password"
    else -> "钱包密码"
  }
  @JvmField
  val repeatPassword = when (currentLanguage) {
    HoneyLanguage.English.code -> "Repeat Password"
    else -> "校验密码"
  }
  @JvmField
  val name = when (currentLanguage) {
    HoneyLanguage.English.code -> "Wallet Name"
    else -> "钱包名称"
  }
  @JvmField
  val mnemonicConfirmation = when (currentLanguage) {
    HoneyLanguage.English.code -> "Mnemonic Confirmation"
    else -> "确认助记词"
  }
}

object ImportWalletText {
  @JvmField
  val importWallet = when (currentLanguage) {
    HoneyLanguage.English.code -> "Import Wallet"
    else -> "导入钱包"
  }

  @JvmField
  val address = when (currentLanguage) {
    HoneyLanguage.English.code -> "Address"
    else -> "钱包地址"
  }
}

object WalletText {
  @JvmField
  val totalAssets = when (currentLanguage) {
    HoneyLanguage.English.code -> "Total Assets"
    else -> "钱包所有财产"
  }
  @JvmField
  val manage = when (currentLanguage) {
    HoneyLanguage.English.code -> "Manage My Wallets"
    HoneyLanguage.Chinese.code -> "管理我的钱包"
    else -> ""
  }
  @JvmField
  val section = when (currentLanguage) {
    HoneyLanguage.English.code -> "My tokens type:"
    else -> "我的令牌明细"
  }
  @JvmField
  val addToken = when (currentLanguage) {
    HoneyLanguage.English.code -> "Add More Token".toUpperCase()
    HoneyLanguage.Chinese.code -> "添加更多令牌"
    else -> ""
  }
}

object TransactionText {
  @JvmField
  val transaction = when (currentLanguage) {
    HoneyLanguage.English.code -> "Transaction History"
    else -> "交易历史"
  }

  @JvmField
  val detail = when (currentLanguage) {
    HoneyLanguage.English.code -> "Transaction Detail"
    else -> "交易明细"
  }

  @JvmField
  val url = when (currentLanguage) {
    HoneyLanguage.English.code -> "Open A Url"
    else -> ""
  }


}

object TokenDetailText {

  @JvmField
  val address = when (currentLanguage) {
    HoneyLanguage.English.code -> "Address Selection"
    else -> "选择接收地址"
  }

  @JvmField
  val transferDetail = when (currentLanguage) {
    HoneyLanguage.English.code -> "Transfer Detail"
    else -> ""
  }

  @JvmField
  val customMiner = when (currentLanguage) {
    HoneyLanguage.English.code -> "custom miner fee"
    else -> ""
  }
}

object CommonText {

  @JvmField
  val confirm = when (currentLanguage) {
    HoneyLanguage.English.code -> "Confirm"
    else -> "确认"
  }
  @JvmField
  val create = when (currentLanguage) {
    HoneyLanguage.English.code -> "Create"
    else -> "添加"
  }
  @JvmField
  val cancel = when (currentLanguage) {
    HoneyLanguage.English.code -> "cancel"
    else -> "取消"
  }
  @JvmField
  val next = when (currentLanguage) {
    HoneyLanguage.English.code -> "next"
    else -> "下一步"
  }
  @JvmField
  val saveToAlbum = when (currentLanguage) {
    HoneyLanguage.English.code -> "Save TO Album"
    else -> "保存到相册"
  }
  @JvmField
  val copyAddress = when (currentLanguage) {
    HoneyLanguage.English.code -> "Copy Address"
    else -> "点击赋值地址"
  }
  @JvmField
  val startImporting = when (currentLanguage) {
    HoneyLanguage.English.code -> "Start Importing"
    else -> "开始导入"
  }

  @JvmField
  val enterPassword = when (currentLanguage) {
    HoneyLanguage.English.code -> "Enter your password"
    else -> "输入你的密码"
  }

}

object SymbolText {

  @JvmField
  val usd = when (currentLanguage) {
    HoneyLanguage.English.code -> " (USD)"
    else -> " (USD)"
  }

}

object CurrentWalletText {

  @JvmField
  val Wallets = when (currentLanguage) {
    HoneyLanguage.English.code -> "Wallets"
    else -> "钱包列表"
  }

}

object NotificationText {

  @JvmField
  val notification = when (currentLanguage) {
    HoneyLanguage.English.code -> "Notifications"
    else -> "通知中心"
  }

}

object TokenManagementText {

  @JvmField
  val addToken = when (currentLanguage) {
    HoneyLanguage.English.code -> "Add Token"
    else -> "添加令牌"
  }

}

object WalletSettingsText {

  @JvmField
  val copy = when (currentLanguage) {
    HoneyLanguage.English.code -> "click to copy address"
    else -> "点击复制钱包地址"
  }
  @JvmField
  val checkQRCode = when (currentLanguage) {
    HoneyLanguage.English.code -> "Check QR Code"
    else -> "查看二维码"
  }

  @JvmField
  val balance = when (currentLanguage) {
    HoneyLanguage.English.code -> "Balance"
    else -> "余额"
  }

  @JvmField
  val walletName = when (currentLanguage) {
    HoneyLanguage.English.code -> "Wallet Name"
    else -> "钱包名称"
  }

  @JvmField
  val walletNameSettings = when (currentLanguage) {
    HoneyLanguage.English.code -> "Wallet Name"
    else -> "钱包名称设置"
  }

  @JvmField
  val walletSettings = when (currentLanguage) {
    HoneyLanguage.English.code -> "Wallet Settings"
    else -> "钱包设置"
  }

  @JvmField
  val passwordSettings = when (currentLanguage) {
    HoneyLanguage.English.code -> "Change Password"
    else -> "密码设置"
  }

  @JvmField
  val hint = when (currentLanguage) {
    HoneyLanguage.English.code -> "Password Hint"
    else -> "密码提示"
  }

  @JvmField
  val exportPrivateKey = when (currentLanguage) {
    HoneyLanguage.English.code -> "Export Private Key"
    else -> "导出秘钥"
  }

  @JvmField
  val exportKeystore = when (currentLanguage) {
    HoneyLanguage.English.code -> "Export Keystore"
    else -> "导出 Keystore"
  }

  @JvmField
  val delete = when (currentLanguage) {
    HoneyLanguage.English.code -> "Delete Wallet"
    else -> "删除钱包"
  }

  @JvmField
  val deleteInfoTitle = when (currentLanguage) {
    HoneyLanguage.English.code -> "Are you sure to delete current wallet?"
    else -> ""
  }

  @JvmField
  val deleteInfoSubtitle = when (currentLanguage) {
    HoneyLanguage.English.code -> "Before you delete your wallet please backup your wallet information, we never save your data, so we can't recovery this operation."
    else -> ""
  }

}

object ProfileText {

  @JvmField
  val profile = when (currentLanguage) {
    HoneyLanguage.English.code -> "Profile"
    else -> "个人主页"
  }
  @JvmField
  val contacts = when (currentLanguage) {
    HoneyLanguage.English.code -> "Contacts"
    else -> "通讯录"
  }
  @JvmField
  val currency = when (currentLanguage) {
    HoneyLanguage.English.code -> "Currency Settings"
    else -> "货币"
  }
  @JvmField
  val language = when (currentLanguage) {
    HoneyLanguage.English.code -> "Language"
    else -> "语言"
  }

  @JvmField
  val aboutUs = when (currentLanguage) {
    HoneyLanguage.English.code -> "About Us"
    else -> "关于我们"
  }

}

object QuotationText {

  @JvmField
  val market = when (currentLanguage) {
    HoneyLanguage.English.code -> "Market"
    else -> "市场行情"
  }
}

enum class HoneyLanguage(val code: Int, val language: String) {
  English(0, "English"),
  Chinese(1, "Chinese"),
  Japanese(2, "Japanese"),
  Russian(3, "Russian"),
  Korean(4, "Korean");

  companion object {
    fun getLanguageCode(language: String): Int {
      return when(language) {
        HoneyLanguage.English.language -> HoneyLanguage.English.code
        HoneyLanguage.Chinese.language -> HoneyLanguage.Chinese.code
        HoneyLanguage.Japanese.language -> HoneyLanguage.Japanese.code
        HoneyLanguage.Russian.language -> HoneyLanguage.Russian.code
        HoneyLanguage.Korean.language -> HoneyLanguage.Korean.code
        else -> 100
      }
    }
  }
}
