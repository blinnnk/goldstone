package io.goldstone.blockchain.crypto

/**
 * @date 01/04/2018 7:54 PM
 * @author KaySaith
 */

object CryptoUtils {

  fun scaleAddress(address: String): String {
    return address.substring(0, 5) + " ··· " + address.substring(address.length - 5, address.length)
  }

}