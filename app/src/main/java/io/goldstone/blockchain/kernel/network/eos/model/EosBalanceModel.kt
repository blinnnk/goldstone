package io.goldstone.blockchain.kernel.network.eos.model

/**
 * @date: 2018/9/19.
 * @author: yanglihai
 * @description: eos的balance查询结果
 */
class EosBalanceModel(
	var supply: String,
	val base: BalanceBase,
	val quote: BalanceBase
)

class BalanceBase(
	var balance: String,
	var weight: String
)