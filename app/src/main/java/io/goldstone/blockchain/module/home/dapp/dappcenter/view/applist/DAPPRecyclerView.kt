package io.goldstone.blockchain.module.home.dapp.dappcenter.view.applist

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.centerInHorizontal
import com.blinnnk.extension.into
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.isNull
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.component.EmptyType
import io.goldstone.blockchain.common.component.EmptyView
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable
import org.jetbrains.anko.matchParent
import android.view.MotionEvent




/**
 * @author KaySaith
 * @date  2018/12/02
 */
@SuppressLint("ViewConstructor")
class DAPPRecyclerView(
	context: Context,
	private val clickCellEvent: DAPPTable.() -> Unit,
	private val checkAllEvent: () -> Unit
) : RelativeLayout(context) {
	private val recyclerView = BaseRecyclerView(context)
	private var emptyView: EmptyView? = null

	init {
		layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
		recyclerView.isNestedScrollingEnabled = false
		recyclerView.into(this)
	}

	fun setData(data: ArrayList<DAPPTable>) {
		if (data.isEmpty()) showEmptyView()
		else {
			if (emptyView.isNotNull()) removeView(emptyView)
			recyclerView.visibility = View.VISIBLE
			val dappAdapter = DAPPAdapter(data, clickCellEvent, checkAllEvent)
			recyclerView.adapter = dappAdapter
		}
	}

	private fun showEmptyView() {
		recyclerView.visibility = View.GONE
		if (emptyView.isNull()) {
			emptyView = EmptyView(context)
			emptyView?.into(this)
			emptyView?.centerInHorizontal()
			emptyView?.y = 100.uiPX().toFloat()
		}
		emptyView?.setStyle(EmptyType.LatestUsedDAPP)
	}

}