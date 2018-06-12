package io.goldstone.blockchain.common.value

/**
 * @date 2018/6/13 12:18 AM
 * @author KaySaith
 */
enum class ApkChannel(
	val value: String,
	val code: Int
) {
	Home("Home", 1),
	Google("Google", 2)
}