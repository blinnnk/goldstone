package io.goldstone.blockchain.common.sandbox

import java.io.Serializable

/**
 * @date: 2018-12-05.
 * @author: yangLiHai
 * @description:
 */
class SandBoxModel(
	var language: Int,
	var currency: String,
	var marketList: List<Int>
) : Serializable {
	constructor() : this(
		-1,
		"",
		listOf()
	)
}