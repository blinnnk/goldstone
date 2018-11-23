package io.goldstone.blockchain.crypto.eos.account

import io.goldstone.blockchain.crypto.eos.EOSValue
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import java.io.Serializable

/**
 * @author KaySaith
 * @date 2018/09/05
 */

class EOSAccount(private val value: String) : Serializable {

	val name = if (EOSWalletUtils.isValidAddress(value)) value else value.toLowerCase()

	/**
	 * EOS Account Name 没有找到特别清晰的官方文档进行校验
	 * 这里采用了这个官方的回答进行校验
	 * https://github.com/EOSIO/eos/issues/955
	 */
	fun checker(isNormalName: Boolean = true): EOSAccountNameChecker {
		val legalCharsIn12 = Regex(".*[a-z1-5.]{0,11}[a-z1-5].*")
		val legalCharsAt13th = Regex(".*[a-j1-5].*")
		// 是否是特殊账号决定长度判断的不同
		val isLegalLength =
			if (isNormalName) value.length == EOSValue.maxNameLength
			else value.length in 2 .. EOSValue.maxSpecialNameLength
		if (!isLegalLength) {
			return if (isNormalName) {
				if (value.length < EOSValue.maxNameLength) EOSAccountNameChecker.TooShort
				else EOSAccountNameChecker.TooLong
			} else when {
				value.length > EOSValue.maxSpecialNameLength -> EOSAccountNameChecker.TooLong
				else -> EOSAccountNameChecker.TooShort
			}
		}
		val isIllegalSuffixSymbol = value.last().toString() == "."
		if (isIllegalSuffixSymbol) {
			return EOSAccountNameChecker.IllegalSuffix
		}
		val isLegalCharacter =
		// 如果是普通用户名检查 `12` 位的规则
			if (isNormalName) value.none { !it.toString().matches(legalCharsIn12) }
			// 如果是特定用户名检查前 `12` 位的规则并且额外检查第 `13` 位的规则
			else {
				if (value.length > EOSValue.maxNameLength)
					value.substring(0, 11).none { !it.toString().matches(legalCharsIn12) } &&
						value.substring(12).none { !it.toString().matches(legalCharsAt13th) }
				else value.replace(".", "").none { !it.toString().matches(legalCharsIn12) }
			}
		return if (!isLegalCharacter) {
			if (value.matches(Regex(".*[6-9].*")) || value.contains("0")) {
				EOSAccountNameChecker.NumberOtherThan1To5
			} else if (value.length > EOSValue.maxSpecialNameLength) {
				if (value.substring(12).matches(Regex(".*[k-z].*")))
					EOSAccountNameChecker.IllegalCharacterAt13th
				else EOSAccountNameChecker.IsLongName
			} else if (value.length < EOSValue.maxNameLength)
				EOSAccountNameChecker.IsShortName
			else EOSAccountNameChecker.IsValid
		} else EOSAccountNameChecker.IsValid
	}

	fun isValid(onlyNormalName: Boolean = true): Boolean = checker(onlyNormalName).isValid()
}

enum class EOSAccountNameChecker(val content: String, val shortDescription: String) {
	TooLong("Wrong length, this account name is longer than 12", "Length Too Long"),
	TooShort("Wrong Length, this account name is shorter than 12", "Length Too Short"),
	NumberOtherThan1To5("Illegal number in this account name, Only allowed in 1 ~ 5", "Invalid Number"),
	IllegalCharacterAt13th("the 13th character is must in a~j or 1~5", "Invalid 13th Value"),
	ContainsIllegalSymbol("Illegal symbol in this account name, Only allowed '.'", "Illegal Symbol"),
	IllegalSuffix("Illegal suffix in this account name, it never be allowed that contains '.' in name end", "Illegal Suffix"),
	IsShortName("Attention this is a special short account name ", "Special Shot Name"),
	IsLongName("Attention this is a special long account name ", "Special Long Name"),
	IsValid("Is Valid", "Is Valid");

	fun isValid(): Boolean = content.equals(IsValid.content, true)
}