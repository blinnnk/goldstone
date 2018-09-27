package io.goldstone.blockchain.common.error

import io.goldstone.blockchain.common.language.FingerprintUnlockText

class WalletSecurityError(val content: String) : GoldStoneError(content) {
	companion object {
		@JvmStatic
		val TheDeviceIsNotFingerprinted = WalletSecurityError(FingerprintUnlockText.theDeviceIsNotFingerprinted)
		@JvmStatic
		val TheDeviceHasNotDetectedTheFingerprintHardware = WalletSecurityError(FingerprintUnlockText.theDeviceHasNotDetectedTheFingerprintHardware)
		@JvmStatic
		val None = WalletSecurityError(GoldStoneError.None.message)
	}
}