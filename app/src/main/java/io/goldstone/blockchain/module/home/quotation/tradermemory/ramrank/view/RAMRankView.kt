package io.goldstone.blockchain.module.home.quotation.tradermemory.ramrank.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.extension.into
import com.blinnnk.extension.setCenterInHorizontal
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.model.EOSRAMRankModel
import org.jetbrains.anko.*

/**
 * @date: 2018/9/25.
 * @author: yanglihai
 * @description:
 */
class RAMRankView(context: Context): LinearLayout(context) {
	
	private var rankRecyclerView: BaseRecyclerView
	
	private var title: RoundButton
	
	init {
		orientation = LinearLayout.VERTICAL
	  layoutParams = LayoutParams(matchParent, wrapContent)
		topPadding = 10.uiPX()
		
		title = RoundButton(context)
		title.setBlueStyle(width = 100.uiPX(), height = 40.uiPX())
		title.setHorizontalGravity(Gravity.CENTER_HORIZONTAL)
		title.text = "持量大户"
		
		rankRecyclerView = BaseRecyclerView(context).apply {
			layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
			isNestedScrollingEnabled = false
			
		}
		
		addView(title)
		addView(rankRecyclerView)
		
	}
	
	fun setData(dataRows: ArrayList<EOSRAMRankModel>) {
		rankRecyclerView.adapter = RAMRankAdapter(dataRows)
	}
}

class RAMRankAdapter(
	override val dataSet: ArrayList<EOSRAMRankModel>
): HoneyBaseAdapter<EOSRAMRankModel, RAMRankItemView>() {
	
	override fun generateCell(context: Context): RAMRankItemView {
		return RAMRankItemView(context)
	}
	
	override fun RAMRankItemView.bindCell(
		data: EOSRAMRankModel,
		position: Int
	) {
		model = data
	}
}
