package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.view

import android.content.Context
import android.graphics.*
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import org.jetbrains.anko.*

/**
 * @date: 2018/10/31.
 * @author: yanglihai
 * @description:
 */
class RecentTradingAdapter(
	private val buyList: List<TradingInfoModel>,
	private val sellList: List<TradingInfoModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	
	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	) = if (viewType == 0) {
		TradingTitleHolder(TitleView(parent.context))
	} else {
		TradingHolder(TradingCell(parent.context))
	}
	
	
	override fun getItemViewType(position: Int): Int {
		return if (position % (buyList.size + 1) == 0) 0 else 1
	}
	
	override fun getItemCount(): Int = buyList.size + sellList.size + 2
	
	override fun onBindViewHolder(
		holder: RecyclerView.ViewHolder,
		position: Int
	) {
		if (holder is TradingTitleHolder) {
			(holder.itemView as? TitleView)?.apply {
				title.text = if (position == 0) "买入" else "卖出"
				title.textColor = if (position == 0) Spectrum.green else Spectrum.lightRed
			}
		} else if (holder is TradingHolder) {
			(holder.itemView as? TradingCell)?.apply {
				if (position <= buyList.size ) {
					val model = buyList[position - 1]
					val maxValue = buyList.maxBy { it.quantity }?.quantity?:0.toDouble()
					setData(model.account, model.quantity, maxValue, Color.parseColor("#0F1CC881"))
				} else {
					val model = sellList[position - buyList.size - 2]
					val maxValue = sellList.maxBy { it.quantity }?.quantity?:0.toDouble()
					setData(model.account, model.quantity, maxValue, Color.parseColor("#0FFF6464"))
				}
			}
		}
		
	}
	
	inner class TradingHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
	inner class TradingTitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

class TitleView(context: Context) : RelativeLayout(context) {
	val title: TextView
	
	init {
		gravity = Gravity.CENTER_VERTICAL
		title = textView {
			textColor = Spectrum.green
			textSize = fontSize(11)
			typeface = GoldStoneFont.black(context)
			layoutParams = RelativeLayout.LayoutParams(
				wrapContent,
				wrapContent
			).apply {
				alignParentRight()
			}
			setMargins<RelativeLayout.LayoutParams> {
				topMargin = 10.uiPX()
				bottomMargin = 10.uiPX()
			}
		}
	}
}

class TradingCell(context: Context) : RelativeLayout(context) {
	private val viewWidth = ScreenSize.Width - ScreenSize.Width/2 - 20.uiPX()
	private val name: TextView
	private val transactionAmount: TextView
	private val backgroundView: View
	var percent: Float = 0f
	
	init {
		gravity = Gravity.CENTER_VERTICAL
		layoutParams = ViewGroup.LayoutParams(matchParent, 22.uiPX())
		backgroundView = view {
			layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
		}
		name = textView {
			textColor = GrayScale.midGray
			textSize = fontSize(12)
			typeface = GoldStoneFont.heavy(context)
		}
		transactionAmount = textView {
			textColor = GrayScale.midGray
			textSize = fontSize(12)
			typeface = GoldStoneFont.heavy(context)
			layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent).apply {
				alignParentRight()
			}
			setMargins<RelativeLayout.LayoutParams> { rightMargin = 5.uiPX() }
		}
	}
	
	fun setData(accountName: String, quantity: Double, maxValue:Double, backgroundColor: Int) {
		percent = if (maxValue == 0.toDouble()) 0f else (quantity / maxValue).toFloat()
		name.text = accountName
		transactionAmount.text = if (quantity > 10000) (quantity/ 1000f).formatCount(1) + "k" else quantity.formatCount(1)
		backgroundView.setMargins<RelativeLayout.LayoutParams> {
			leftMargin = (viewWidth * (1 - percent)).toInt()
		}
		backgroundView.backgroundColor = backgroundColor
	}
	
	
}










