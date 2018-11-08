package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import android.widget.LinearLayout
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.value.ElementID
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * @date: 2018/8/29.
 * @author: yanglihai
 * @description:
 */
class ExchangeRecyclerView(context: Context) : BaseRecyclerView(context) {
	init {
		id = ElementID.recyclerView
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
	}
}