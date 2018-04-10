package io.goldstone.blockchain.crypto

import android.os.Build
import android.support.annotation.RequiresApi
import android.text.format.DateUtils
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import jnr.ffi.annotations.In
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneId.systemDefault
import java.util.*

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

  fun getTargetDayInMills(distanceSinceToday: Int = 0): Long {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val date = calendar.get(Calendar.DATE) + distanceSinceToday
    calendar.clear()
    calendar.set(year, month, date)
    return calendar.timeInMillis
  }

  val dateInDay: (Long) -> String = {
    DateUtils.formatDateTime(GoldStoneAPI.context, it, DateUtils.FORMAT_NO_YEAR)
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

fun Double.formatCurrency(): String {
  val formatEditor = DecimalFormat("#")
  formatEditor.maximumFractionDigits = 3
  return formatEditor.format(this)
}

fun Double.toEthValue(): Double {
  return this / 1000000000000000000.0
}

fun Int.daysAgoInMills(): Long = CryptoUtils.getTargetDayInMills(-this)


enum class TimeType {
  Second, Minute, Hour, Day
}
fun String.toMills(timeType: TimeType = TimeType.Second): Long {
  return when(timeType) {
    TimeType.Second -> this.toLong() * 1000L
    TimeType.Minute -> this.toLong() * 1000L * 60L
    TimeType.Hour -> this.toLong() * 1000L * 60L * 60L
    TimeType.Day -> this.toLong() * 1000L * 60L * 60L * 12L
  }
}
