package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.RelativeLayout
import android.widget.TextView
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
	private val context: Context,
	private val buyList: List<TradingInfoModel>,
	private val sellList: List<TradingInfoModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	
	
	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	) = if (viewType == 0) {
		TradingTitleHolder(TitleView(context))
	} else {
		TradingHolder(TradingItemView(context))
	}
	
	
	override fun getItemViewType(position: Int): Int {
		return if (position / (buyList.size + 1) == 0) 0 else 1
	}
	
	override fun getItemCount(): Int = 12
	
	override fun onBindViewHolder(
		holder: RecyclerView.ViewHolder,
		position: Int
	) {
		
		if (holder is TradingTitleHolder) {
			(holder.itemView as? TitleView)?.apply {
				title.text = if (position == 0) "买入" else "卖出"
				title.textColor = if (position == 0) Spectrum.red else Spectrum.red
			}
		} else if (holder is TradingHolder) {
			(holder.itemView as? TradingItemView)?.apply {
				if (position <= buyList.size ) {
					val model = buyList[position - 1]
					val maxValue = buyList.maxBy { it.quantity }?.quantity?:0.toDouble()
					setData(model.account, model.quantity, maxValue, Spectrum.green)
				} else {
					val model = sellList[position - 6]
					val maxValue = sellList.maxBy { it.quantity }?.quantity?:0.toDouble()
					setData(model.account, model.quantity, maxValue, Spectrum.red)
				}
			}
		}
		
	}
	
	inner class TradingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
	inner class TradingTitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
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
		}
	}
}

class TradingItemView(context: Context) : RelativeLayout(context) {
	val name: TextView
	val transactionAmount: TextView
	var percent: Float = 0f
	val backgroundPaint = Paint()
	
	init {
		gravity = Gravity.CENTER_VERTICAL
		name = textView {
			textColor = GrayScale.midGray
			textSize = fontSize(12)
			typeface = GoldStoneFont.heavy(context)
		}
		transactionAmount = textView {
			textColor = GrayScale.midGray
			textSize = fontSize(12)
			typeface = GoldStoneFont.heavy(context)
			layoutParams = RelativeLayout.LayoutParams(
				wrapContent,
				wrapContent
			).apply {
				alignParentRight()
			}
		}
	}
	
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		canvas?.drawRect(width * (1 - percent), 0f, width.toFloat(), height.toFloat(), backgroundPaint)
	}
	
	fun setData(account: String, quantity: Double, maxValue:Double, backgroundColor: Int) {
		backgroundPaint.color = backgroundColor
		percent = if (maxValue == 0.toDouble()) 0f else (quantity / maxValue).toFloat()
		name.text = account
		transactionAmount.text = if (quantity > 10000) (quantity/ 1000f).formatCount(1) + "k" else quantity.formatCount(1)
	}
	
	
}










