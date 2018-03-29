package io.goldstone.blockchain.crypto

import android.content.Context
import org.ethereum.geth.Geth
import org.ethereum.geth.KeyStore
import org.kethereum.bip39.Mnemonic
import org.kethereum.crypto.Keys
import org.walleth.khex.hexToByteArray
import java.io.File

/**
 * @date 29/03/2018 4:25 PM
 * @author KaySaith
 */

fun Context.generateWallet(
  password: String,
  holdAddress: (mnemonicCode: String, address: String) -> Unit
  ) {
  val keystoreFile by lazy { File(filesDir!!, "keystore") }
  val path = "m/44'/60'/0'/0/0"
  /** Generate Mnemonic */
  val mnemonicCode = Mnemonic.generateMnemonic()
  /** Generate HD Wallet */
  val masterWallet = Mnemonic.mnemonicToKey(mnemonicCode, path)
  /** Generate Keystore */
  val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
  /** Generate Keys */
  val masterKey = masterWallet.getKeyPair()
  /** Get Public Key and Private Key*/
  val publicKey = Keys.getAddress(masterKey.publicKey)
  val address = "0x" + publicKey.toLowerCase()
  holdAddress(mnemonicCode, address)
  /** Import Private Key to Keystore */
  keyStore.importECDSAKey(masterKey.privateKey.toString(16).hexToByteArray(), password)
}