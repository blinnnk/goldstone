package io.goldstone.blinnnk.crypto.eos.account

import io.goldstone.blinnnk.common.language.EOSAccountText
import io.goldstone.blinnnk.crypto.eos.EOSValue
import io.goldstone.blinnnk.crypto.eos.EOSWalletUtils
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
			else value.length in 1 .. EOSValue.maxSpecialNameLength
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
			if (isNormalName) {
				value.none { !it.toString().matches(legalCharsIn12) }
			} else {
				// 如果是特定用户名检查前 `12` 位的规则并且额外检查第 `13` 位的规则
				if (value.length > EOSValue.maxNameLength) {
					value.substring(0, 11).none { !it.toString().matches(legalCharsIn12) } &&
						value.substring(12).none { !it.toString().matches(legalCharsAt13th) }
				} else {
					value.replace(".", "").none { !it.toString().matches(legalCharsIn12) }
				}
			}
		return if (!isLegalCharacter) {
			when {
				value.matches(Regex(".*[6-9].*")) || value.contains("0") ->
					EOSAccountNameChecker.NumberOtherThan1To5
				value.length > EOSValue.maxSpecialNameLength ->
					if (value.substring(12).matches(Regex(".*[k-z].*")))
						EOSAccountNameChecker.IllegalCharacterAt13th
					else EOSAccountNameChecker.IsLongName
				value.length < EOSValue.maxNameLength -> EOSAccountNameChecker.IsShortName
				else -> EOSAccountNameChecker.IsInvalid
			}
		} else EOSAccountNameChecker.IsValid
	}

	fun isValid(onlyNormalName: Boolean = true): Boolean {
		return checker(onlyNormalName).isValid()
	}

	fun isSame(account: EOSAccount) : Boolean = name.equals(account.name, true)
}

enum class EOSAccountNameChecker(val content: String, val shortDescription: String) {
	TooLong(EOSAccountText.checkNameResultTooLong, EOSAccountText.checkNameResultTooLongShortDescription),
	TooShort(EOSAccountText.checkNameResultTooShort, EOSAccountText.checkNameResultTooShortShortDescription),
	NumberOtherThan1To5(EOSAccountText.checkNameResultNumberOtherThan1To5, EOSAccountText.checkNameResultNumberOtherThan1To5ShortDescription),
	IllegalCharacterAt13th(EOSAccountText.checkNameResultIllegalCharacterAt13th, EOSAccountText.checkNameResultIllegalCharacterAt13thShortDescription),
	ContainsIllegalSymbol(EOSAccountText.checkNameResultContainsIllegalSymbol, EOSAccountText.checkNameResultContainsIllegalSymbolShortDescription),
	IllegalSuffix(EOSAccountText.checkNameResultIllegalSuffix, EOSAccountText.checkNameResultIllegalSuffixShortDescription),
	IsShortName(EOSAccountText.checkNameResultIsShortName, EOSAccountText.checkNameResultIsShortNameShortDescription),
	IsLongName(EOSAccountText.checkNameResultIsLongName, EOSAccountText.checkNameResultIsLongNameShortDescription),
	IsInvalid(EOSAccountText.checkNameResultIsInvalid, EOSAccountText.checkNameResultIsInvalidShortDescription),
	IsValid(EOSAccountText.checkNameResultIsValid, EOSAccountText.checkNameResultIsValidShortDescription);

	fun isValid(): Boolean = content.equals(IsValid.content, true)
}