package io.goldstone.blockchain.common.value

/**
 * @date 2018/6/13 12:18 AM
 * @author KaySaith
 */
val currentChannel = ApkChannel.Lenovo

enum class ApkChannel(
	val value: String,
	val code: Int
) {
	Home("Home", 1),
	Google("Google", 2),
	Vivo("Vivo", 3),
	Baidu("Baidu", 4),
	Samsung("Samsung", 5),
	Tencent("Tencent", 6),
	Xiaomi("Xiaomi", 7),
	Wandoujia("Wandoujia", 8),
	Meizu("Meizu", 9),
	Lenovo("Lenovo", 10),
	Sogou("Sogou", 11),
}