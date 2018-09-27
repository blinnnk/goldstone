package io.goldstone.blockchain.module.home.quotation.tradermemory.ramrank.view

import android.content.Context
import android.view.*
import android.widget.*
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.model.EOSRAMRankModel
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

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
		rankRecyclerView.adapter = RAMRankAdapter(dataRows) { cell, position ->
			cell.onClick {
				Toast.makeText(cell.context, "position is $position", Toast.LENGTH_LONG).show()
			}
		}
	}
}

class RAMRankAdapter(
	override val dataSet: ArrayList<EOSRAMRankModel>,
	val hold: (RAMRankCell, position: Int) -> Unit
): HoneyBaseAdapter<EOSRAMRankModel, RAMRankCell>() {
	
	override fun generateCell(context: Context): RAMRankCell {
		return RAMRankCell(context)
	}
	
	override fun RAMRankCell.bindCell(
		data: EOSRAMRankModel,
		position: Int
	) {
		model = data
		hold(this, position)
	}
}
