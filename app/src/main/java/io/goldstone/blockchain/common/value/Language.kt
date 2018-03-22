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