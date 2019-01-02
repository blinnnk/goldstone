package io.goldstone.blockchain.module.home.quotation.quotationrank.view

import android.content.Context
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.module.home.quotation.quotationrank.model.QuotationRankModel


/**
 * @author KaySaith
 * @date  2019/01/02
 */
class QuotationRankCell(context: Context) : BaseCell(context) {

	var model: QuotationRankModel? by observing(null) {

	}

	init {
		hasArrow = true
		setGrayStyle()
	}
}