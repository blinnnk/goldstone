package io.goldstone.blinnnk.crypto.eos.eccutils

/**
 * @author KaySaith
 * @date 2018/09/03
 */

class RefValue<T> {
	@JvmField var data: T? = null
	constructor() {
		data = null
	}
	constructor(initialVal: T) {
		data = initialVal
	}
}
