package io.goldstone.blockchain.crypto

import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import java.text.DecimalFormat

/**
 * @date 01/04/2018 7:54 PM
 * @author KaySaith
 */

data class InputCodeData(val type: String, val address: String, val count: Double)
object CryptoUtils {

  fun scaleAddress(address: String): String {
    return address.substring(0, 5) + " ··· " + address.substring(address.length - 5, address.length)
  }

  fun scaleTo16(address: String): String {
    return if (address.length < 16) address
    else address.substring(0, 16) + "···"
  }

  fun scaleTo28(address: String): String {
    return if (address.length < 28) address
    else address.substring(0, 28) + "···"
  }

  fun formatDouble(value: Double): Double {
    return DecimalFormat("0.00").format(value).toDouble()
  }

  fun formatFeeDouble(value: Double): Double {
    return DecimalFormat("0.0000000").format(value).toDouble()
  }

  fun toCountByDecimal(value: Double, decimal: Double): Double {
    return value / Math.pow(10.0, decimal)
  }

  fun loadTransferInfoFromInputData(inputCode: String): InputCodeData?  {
    var address: String
    var count: Double
    isTransferInputCode(inputCode).isTrue {
      // analysis input code and get the received address
      address = inputCode.substring(SolidityCode.contractTransfer.length, SolidityCode.contractTransfer.length + 64)
      address = toHexValue(address.substring(address.length - 40, address.length))
      // analysis input code and get the received count
      count = inputCode.substring(inputCode.length - 64, inputCode.length).hexToDecimal()
      return InputCodeData("transfer", address, count)
    } otherwise {
      System.out.println("not a contract transfer")
      return null
    }
  }

  fun isERC20Transfer(transactionTable: TransactionTable, hold: () -> Unit): Boolean {
    return if (
      transactionTable.value == "0"
      && transactionTable.input.length > 10
      && isTransferInputCode(transactionTable.input)
    ) {
      hold()
      true
    } else {
      false
    }
  }

  private fun toHexValue(value: String): String {
    return "0x$value"
  }

  private fun isTransferInputCode(inputCode: String) =
    inputCode.length == 138
    && inputCode.substring(0, SolidityCode.contractTransfer.length) == SolidityCode.contractTransfer
}

fun Double.toEthCount(): String {
  val formatEditor = DecimalFormat("#")
  formatEditor.maximumFractionDigits = 18
  return "0" + formatEditor.format(this / 1000000000000000000.0) + " ETH"
}
