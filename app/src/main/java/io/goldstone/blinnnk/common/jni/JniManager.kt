package io.goldstone.blinnnk.common.jni


/**
 * @author KaySaith
 * @date  2019/01/07
 */
object JniManager {

	init {
		System.loadLibrary("native-utils")
	}
 // `api` 加密 `key` 拆分的部分存放在 `c++` 代码里面进行返回, 加大反编译获取 `key` 的难度
	external fun getKey(): String
	external fun getDecryptKey(): String
}
