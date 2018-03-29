package io.goldstone.blockchain.common.utils

import android.text.Editable
import com.blinnnk.extension.isNull

/**
 * @date 29/03/2018 1:16 PM
 * @author KaySaith
 */

object SafeConditions {
  const val minCount: Int = 6
  const val maxSameChars: Int = 3
  // 可以在数组维护更多非法字符
  @JvmField
  val illegalChars: ArrayList<String> = arrayListOf(" ")
  @JvmField
  val capitalRegex = Regex(".*[A-Z].*")
  @JvmField
  val lowercaseRegex = Regex(".*[a-z].*")
  @JvmField
  val highSafeChar = Regex(".*[!@#\$%¥^&*()_=+?].*")
}

enum class UnsafeReasons(val info: String, val code: Any? = null) {
  Count("lack of number", SafeConditions.minCount),
  IllegalChars("illegal chars", SafeConditions.illegalChars),
  NumberAndLetter("you need contains number and letter both"),
  CapitalAndLowercase("you need contains capital and lowercase both"),
  TooMuchSameValue("you have too much same value", SafeConditions.maxSameChars),
  None("Congratulations")
}

enum class SafeLevel(val info: String) {
  Normal("normal"), High("High"), Strong("Strong"), Weak("Weak")
}

// 推荐使用的封装方式
inline fun Editable.checkPasswordInRules(
  holdSafeLevel: (
    safeLevel: SafeLevel,
    reasons: UnsafeReasons
  ) -> Unit) {
  arrayListOf(
    checkValueCountIsCorrect(),
    !checkValueContainsIllegalChars(),
    checkValueContainsNumberAndLetter(),
    containsCapitalAndLowercase(),
    !containsTooMuchSameValue()
  ).indexOfFirst { !it }.let {

    val reasons = arrayListOf(
      UnsafeReasons.Count,
      UnsafeReasons.IllegalChars,
      UnsafeReasons.NumberAndLetter,
      UnsafeReasons.CapitalAndLowercase,
      UnsafeReasons.TooMuchSameValue
    )
    /** 当全部符合标准的时候 `indexOfFirst` 会返回 `-1` */
    val reason = if (it >= 0) { reasons[it] } else { UnsafeReasons.None }
    holdSafeLevel(checkSafeLevel(), reason)
  }
}

fun Editable.checkValueCountIsCorrect() = count() > SafeConditions.minCount

fun Editable.checkValueContainsIllegalChars() = any { char ->
  SafeConditions.illegalChars.contains(char.toString())
}

fun Editable.checkValueContainsNumberAndLetter() =
  filterNot { it.toString().toIntOrNull().isNull() }.count() in 0 until length

fun Editable.containsCapitalAndLowercase() =
  matches(SafeConditions.capitalRegex) && matches(SafeConditions.lowercaseRegex)

fun Editable.containsTooMuchSameValue(): Boolean {
  val splitSameCharArray = arrayListOf<Char>()
  forEachIndexed { index, char ->
    if (index == 0) {
      splitSameCharArray.add(char)
    } else if (char != this[index - 1]) {
      splitSameCharArray.add(char)
    }
  }
  return count() - splitSameCharArray.count() >= SafeConditions.maxSameChars
}

fun Editable.checkSafeLevel(): SafeLevel {
  /** 维护更多安全条件, 增加积分的计数来重新衡量安全度 */
  val countScore = if (count() > 8) 1 else 0
  val specialCharScore = if (matches(SafeConditions.highSafeChar)) 2 else 0
  val safeScore = countScore + specialCharScore
  return when (safeScore) {
    0 -> SafeLevel.Weak
    1 -> SafeLevel.Normal
    2 -> SafeLevel.High
    else -> SafeLevel.Strong
  }
}