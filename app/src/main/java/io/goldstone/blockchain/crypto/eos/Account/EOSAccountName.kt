package io.goldstone.blockchain.crypto.eos.account

class EOSAccountName(name: String) {
	init {
		if (!name.isEmpty()) {
			if (name.length > maxAccountNameIndex) {
				throw IllegalArgumentException("account name can only be 12 chars long: $name") // changed from dawn3
			}

			if (name.indexOf(charNotAllowed) >= 0 && !name.startsWith("eosio.")) {
				throw IllegalArgumentException("account name must not contain '.': $name")
			}
		}
	}

	companion object {
		private const val maxAccountNameIndex = 12
		private const val charNotAllowed = '.'
	}
}