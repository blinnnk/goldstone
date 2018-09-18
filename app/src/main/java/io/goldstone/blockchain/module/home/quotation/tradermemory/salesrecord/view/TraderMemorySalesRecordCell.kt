package io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecord.view

import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListCell
import org.jetbrains.anko.imageResource

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */

class TraderMemorySalesRecordCell(context: Context) : BaseCell(context) {

	private val dragIcon by lazy { ImageView(context) }

	init {
		dragIcon.apply {
			imageResource = R.drawable.drag_icon
			setColorFilter(GrayScale.lightGray)
			layoutParams = RelativeLayout.LayoutParams(30.uiPX(), 30.uiPX())
			x -= 50.uiPX()
		}.into(this)

		dragIcon.setCenterInVertical()
		dragIcon.setAlignParentRight()
	}

}