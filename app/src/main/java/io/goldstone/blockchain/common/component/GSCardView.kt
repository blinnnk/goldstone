package io.goldstone.blockchain.common.component

import android.content.Context
import android.support.v7.widget.CardView
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ShadowSize


/**
 * @author KaySaith
 * @date  2018/11/07
 */
open class GSCard(context: Context) : CardView(context) {
	init {
		maxCardElevation = ShadowSize.Cell
		cardElevation = ShadowSize.Cell
		preventCornerOverlap = false
		radius = CornerSize.normal
		useCompatPadding = true
	}
}