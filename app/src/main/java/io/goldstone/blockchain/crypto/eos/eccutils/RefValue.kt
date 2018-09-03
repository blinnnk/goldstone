package io.goldstone.blockchain.crypto.eos.eccutils

class RefValue<T> {
	@JvmField var data: T? = null
	constructor() {
		data = null
	}
	constructor(initialVal: T) {
		data = initialVal
	}
}
