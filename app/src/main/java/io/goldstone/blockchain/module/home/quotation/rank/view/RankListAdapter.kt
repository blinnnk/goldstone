package io.goldstone.blockchain.module.home.quotation.rank.view

import android.content.Context
import android.view.*
import android.widget.LinearLayout
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.keyboardHeightListener
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.module.home.quotation.rank.model.RankHeaderModel
import io.goldstone.blockchain.module.home.quotation.rank.model.RankTable
import org.jetbrains.anko.matchParent

/**
 * @date: 2018/8/14.
 * @author: yanglihai
 * @description: rank列表的适配器
 */
class RankListAdapter(override val dataSet: ArrayList<RankTable>) :
	HoneyBaseAdapterWithHeaderAndFooter<RankTable, View, RankItemCell, View>() {
	
	lateinit var rankHeaderView: RankHeaderView
	
	private var hasHiddenSoftNavigationBar = false
	
	override fun generateCell(context: Context) = RankItemCell(context)
	
	override fun generateFooter(context: Context) =
		View(context).apply {
			val barHeight =
				if (
					(!hasHiddenSoftNavigationBar && !KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK))
					|| Config.isNotchScreen()
				) {
					55.uiPX()
				} else 10.uiPX()
			layoutParams = LinearLayout.LayoutParams(matchParent, barHeight)
		}
	
	
	override fun generateHeader(context: Context): View {
		rankHeaderView = RankHeaderView(context)
		/**
		 * 判断不同手机的不同 `Navigation` 的状态决定 `Footer` 的补贴高度
		 * 主要是, `Samsung S8, S9` 的 `Navigation` 状态判断
		 */
		rankHeaderView.keyboardHeightListener {
			if (it < 0) {
				hasHiddenSoftNavigationBar = true
			}
		}
		return rankHeaderView
	}
	
	override fun RankItemCell.bindCell(
		data: RankTable,
		position: Int
	) {
		rankModel = data
	}
	
	fun updateRankHeaderViewData(rankHeaderModel: RankHeaderModel) {
		rankHeaderView.updateHeaderData(rankHeaderModel)
	}
	
	
}