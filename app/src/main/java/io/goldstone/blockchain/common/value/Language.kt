@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.common.value

import android.app.Application
import org.jetbrains.anko.configuration

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
    else -> ""
  }
  @JvmField
  val mnemonicBackUp = when (currentLanguage) {
    HoneyLanguage.English.code -> "Mnemonic Backup"
    else -> ""
  }
  @JvmField
  val mnemonicBackupAttention = when(currentLanguage) {
    HoneyLanguage.English.code -> "confirm your mnemonic words to remember your account baby tell me why and what happened it"
    else -> ""
  }
  @JvmField
  val mnemonicConfirmationDescription = when(currentLanguage) {
    HoneyLanguage.English.code -> "please confirm your mnemonic words to remember your account baby tell me why and what happened it"
    else -> ""
  }
  @JvmField
  val password = when (currentLanguage) {
    HoneyLanguage.English.code -> "Password"
    else -> ""
  }
  @JvmField
  val repeatPassword = when (currentLanguage) {
    HoneyLanguage.English.code -> "Repeat Password"
    else -> ""
  }
  @JvmField
  val name = when (currentLanguage) {
    HoneyLanguage.English.code -> "Wallet Name"
    else -> ""
  }
  @JvmField
  val mnemonicConfirmation = when (currentLanguage) {
    HoneyLanguage.English.code -> "Mnemonic Confirmation"
    else -> ""
  }
}

object ImportWalletText {
  @JvmField
  val importWallet = when (currentLanguage) {
    HoneyLanguage.English.code -> "Import Wallet"
    else -> ""
  }

  @JvmField
  val address = when (currentLanguage) {
    HoneyLanguage.English.code -> "Address"
    else -> ""
  }
}

object WalletText {
  @JvmField
  val totalAssets = when (currentLanguage) {
    HoneyLanguage.English.code -> "Total Assets"
    else -> ""
  }
  @JvmField
  val manage = when (currentLanguage) {
    HoneyLanguage.English.code -> "Manage My Wallets"
    else -> ""
  }
  @JvmField
  val section = when (currentLanguage) {
    HoneyLanguage.English.code -> "My tokens type:"
    else -> ""
  }
  @JvmField
  val addToken = when (currentLanguage) {
    HoneyLanguage.English.code -> "Add More Token".toUpperCase()
    else -> ""
  }
}

object TransactionText {
  @JvmField
  val trannsaction = when (currentLanguage) {
    HoneyLanguage.English.code -> "Transaction History"
    else -> ""
  }
  @JvmField
  val manage = when (currentLanguage) {
    HoneyLanguage.English.code -> "Manage My Wallets"
    else -> ""
  }
  @JvmField
  val section = when (currentLanguage) {
    HoneyLanguage.English.code -> "My tokens type:"
    else -> ""
  }
  @JvmField
  val addToken = when (currentLanguage) {
    HoneyLanguage.English.code -> "Add More Token".toUpperCase()
    else -> ""
  }
}

object CommonText {

  @JvmField
  val confirm = when (currentLanguage) {
    HoneyLanguage.English.code -> "Confirm"
    else -> ""
  }
  @JvmField
  val startImporting = when (currentLanguage) {
    HoneyLanguage.English.code -> "Start Importing"
    else -> ""
  }

}

object SymbolText {

  @JvmField
  val usd = when (currentLanguage) {
    HoneyLanguage.English.code -> " (USD)"
    else -> ""
  }

}

object CurrentWalletText {

  @JvmField
  val Wallets = when (currentLanguage) {
    HoneyLanguage.English.code -> "Wallets"
    else -> ""
  }

}

object NotificationText {

  @JvmField
  val notification = when (currentLanguage) {
    HoneyLanguage.English.code -> "Notifications"
    else -> ""
  }

}

object TokenManagementText {

  @JvmField
  val addToken = when (currentLanguage) {
    HoneyLanguage.English.code -> "Add Token"
    else -> ""
  }

}

object WalletSettingsText {

  @JvmField
  val copy = when (currentLanguage) {
    HoneyLanguage.English.code -> "click to copy address"
    else -> ""
  }
  @JvmField
  val checkQRCode = when (currentLanguage) {
    HoneyLanguage.English.code -> "Check QR Code"
    else -> ""
  }

  @JvmField
  val balance = when (currentLanguage) {
    HoneyLanguage.English.code -> "Balance"
    else -> ""
  }

  @JvmField
  val walletName = when (currentLanguage) {
    HoneyLanguage.English.code -> "Wallet Name"
    else -> ""
  }

  @JvmField
  val hint = when (currentLanguage) {
    HoneyLanguage.English.code -> "Password Hint"
    else -> ""
  }

  @JvmField
  val exportPrivateKey = when (currentLanguage) {
    HoneyLanguage.English.code -> "Export Private Key"
    else -> ""
  }

  @JvmField
  val exportKeystore = when (currentLanguage) {
    HoneyLanguage.English.code -> "Export Keystore"
    else -> ""
  }

  @JvmField
  val delete = when (currentLanguage) {
    HoneyLanguage.English.code -> "Delete Wallet"
    else -> ""
  }

}

object ProfileText {

  @JvmField
  val profile = when (currentLanguage) {
    HoneyLanguage.English.code -> "Profile"
    else -> ""
  }
  @JvmField
  val contacts = when (currentLanguage) {
    HoneyLanguage.English.code -> "Contacts"
    else -> ""
  }
  @JvmField
  val currency = when (currentLanguage) {
    HoneyLanguage.English.code -> "Currency Settings"
    else -> ""
  }
  @JvmField
  val language = when (currentLanguage) {
    HoneyLanguage.English.code -> "Language"
    else -> ""
  }

}

// 设定当前系统语言为软件界面语言

var currentLanguage = HoneyLanguage.Chinese.code

enum class HoneyLanguage(val code: Int) {
  English(0), Chinese(1)
}

fun Application.setLanguage() {
  when (configuration.locale.displayLanguage) {
    "English" -> currentLanguage = HoneyLanguage.English.code
    "Chinese" -> currentLanguage = HoneyLanguage.Chinese.code
  }
}