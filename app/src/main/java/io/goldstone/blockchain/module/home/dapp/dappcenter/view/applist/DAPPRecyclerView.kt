package io.goldstone.blockchain.module.home.dapp.dappcenter.view.applist

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.component.EmptyType
import io.goldstone.blockchain.common.component.EmptyView
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPModel
import org.jetbrains.anko.matchParent


/**
 * @author KaySaith
 * @date  2018/12/02
 */
@SuppressLint("ViewConstructor")
class DAPPRecyclerView(
	context: Context,
	private val hold: DAPPModel.() -> Unit
) : RelativeLayout(context) {
	private val recyclerView = BaseRecyclerView(context)
	private var emptyView: EmptyView? = null

	init {
		layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
		recyclerView.into(this)
	}

	fun setData(data: ArrayList<DAPPModel>) {
		if (data.isEmpty()) showEmptyView()
		else {
			if (emptyView.isNotNull()) removeView(emptyView)
			recyclerView.visibility = View.VISIBLE
			recyclerView.adapter = DAPPAdapter(data, hold)
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
		emptyView?.setStyle(EmptyType.NotificationList)
	}

}