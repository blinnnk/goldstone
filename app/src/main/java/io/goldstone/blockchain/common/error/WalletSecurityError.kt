package io.goldstone.blockchain.common.error

import io.goldstone.blockchain.common.language.FingerprintUnlockText

class WalletSecurityError(val content: String) : GoldStoneError(content) {
	companion object {
		@JvmStatic
		val UnregisteredFingerprint = WalletSecurityError(FingerprintUnlockText.unregisteredFingerprint)
		@JvmStatic
		val HardwareDoesNotSupportFingerprints = WalletSecurityError(FingerprintUnlockText.hardwareDoesNotSupportFingerprints)
		@JvmStatic
		val None = WalletSecurityError(GoldStoneError.None.message)
	}
}