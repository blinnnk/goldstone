package io.goldstone.blinnnk.crypto.eos.transaction

import io.goldstone.blinnnk.crypto.eos.EOSUtils

/**
 * @author KaySaith
 * @date 2018/09/03
 */

enum class ExpirationType(val second: Long) {
	Min(30),
	OneMinute(60),
	FiveMinutes(300),
	TenMinutes(600),
	HalfAnHour(1800),
	Max(3600);

	fun generate(): Long {
		return EOSUtils.getCurrentUTCStamp() + second
	}

	fun getCodeFromDate(): String {
		return EOSUtils.getExpirationCode(generate())
	}
}