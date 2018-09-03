package io.goldstone.blockchain.crypto.eos.transaction

import io.goldstone.blockchain.crypto.eos.EOSUtils

enum class ExpirationType(val second: Long) {
	Min(30),
	OneMinute(60),
	FiveMinute(300),
	TenMinute(600),
	HalfAnHour(1800),
	Max(3600);

	companion object {
		fun generate(type: ExpirationType): Long {
			return EOSUtils.getCurrentUTCStamp() + type.second
		}
	}
}