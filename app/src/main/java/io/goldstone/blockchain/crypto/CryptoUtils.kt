package io.goldstone.blockchain.crypto

import org.web3j.crypto.Keys
import java.math.BigInteger
import java.text.DecimalFormat

/**
 * @date 01/04/2018 7:54 PM
 * @author KaySaith
 */

object CryptoUtils {

  fun scaleAddress(address: String): String {
    return address.substring(0, 5) + " ··· " + address.substring(address.length - 5, address.length)
  }

  fun scaleTO16(address: String): String {
    return address.substring(0, 16) + "···"
  }

  fun formatDouble(value: Double): Double {
    return DecimalFormat("0.00").format(value).toDouble()
  }

  fun toHexValue(value: BigInteger): String {
    return "0x" + Keys.getAddress(value)
  }
}