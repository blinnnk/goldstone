package io.goldstone.blinnnk.module.common.tokendetail.tokeninfo.model

import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/11/12
 */
data class EOSTokenCountInfo(
	val totalSent: Int,
	val totalReceived: Int,
	val totalCount: Int
) : Serializable