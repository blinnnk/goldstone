package io.goldstone.blockchain.module.common.tokendetail.tokeninfo.contract

import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/13
 */
data class EOSTokenCountInfo(
	val totalSent: Int,
	val totalReceived: Int,
	val totalCount: Int
) : Serializable