package io.goldstone.blockchain.crypto.ethereum.walletfile

/**
 * @date 2018/6/17 10:34 PM
 * @author KaySaith
 */
class CipherException : Exception {
	
	constructor(message: String) : super(message)
	
	constructor(message: String, cause: Throwable) : super(message, cause)
}