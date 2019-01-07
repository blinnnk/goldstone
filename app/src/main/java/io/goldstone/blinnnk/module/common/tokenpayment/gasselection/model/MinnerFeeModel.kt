package io.goldstone.blinnnk.module.common.tokenpayment.gasselection.model

import io.goldstone.blinnnk.common.language.PrepareTransferText
import java.io.Serializable

/**
 * @date 2018/5/25 2:05 AM
 * @author KaySaith
 */
enum class MinerFeeType(val type: String) : Serializable {

	Recommend(PrepareTransferText.recommend),
	Cheap(PrepareTransferText.cheap),
	Fast(PrepareTransferText.fast),
	Custom(PrepareTransferText.customize);

}