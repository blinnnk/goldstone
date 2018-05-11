package io.goldstone.blockchain.module.home.profile.chainselection.view

import android.content.Context
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.BaseRadioCell
import io.goldstone.blockchain.module.home.profile.chainselection.model.ChainSelectionModel

/**
 * @date 2018/5/11 4:27 PM
 * @author KaySaith
 */

class ChainSelectionCell(context: Context) : BaseRadioCell(context) {

	var model: ChainSelectionModel by observing(ChainSelectionModel()) {
		title.text = model.chainName
		setSwitchStatusBy(model.isUsed)
		showIcon(
			0,
			model.color
		)
	}

	init {

	}

}