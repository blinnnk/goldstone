package io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.view

import android.content.Context
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.baserecyclerfragment.BottomLoadingView
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.model.PersonalMemoryTransactionRecordTable
import org.jetbrains.anko.matchParent

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */

class PersonalMemoryTransactionRecordAdapter(
	override var dataSet: ArrayList<PersonalMemoryTransactionRecordTable>,
	private val isSalesRecord: Boolean,
	private val hold: (PersonalMemoryTransactionRecordCell) -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<PersonalMemoryTransactionRecordTable, View, PersonalMemoryTransactionRecordCell, BottomLoadingView>() {

	private var hasHiddenSoftNavigationBar = false
	override fun generateFooter(context: Context): BottomLoadingView {
		return BottomLoadingView(context).apply {
			// 加载更多显示的底部 `LoadingView`
			hide()
			addView(
				View(context).apply {
					val barHeight =
						if ((!hasHiddenSoftNavigationBar && !KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)) || SharedWallet.isNotchScreen()) {
							60.uiPX()
						} else 10.uiPX()
					layoutParams = LinearLayout.LayoutParams(matchParent, barHeight)
				}
			)
		}
	}

	override fun generateHeader(context: Context) = View(context)

	override fun generateCell(context: Context) = PersonalMemoryTransactionRecordCell(context, isSalesRecord)

	override fun PersonalMemoryTransactionRecordCell.bindCell(data: PersonalMemoryTransactionRecordTable, position: Int) {
		model = data
		hold(this)
	}

//	override fun getItemCount(): Int {
//		return dataSet.size
//	}
}