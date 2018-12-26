package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.view

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.math.BigDecimal

/**
 * @date: 2018/10/31.
 * @author: yanglihai
 * @description:
 */
class RecentTradingAdapter(
	private val buyList: List<TradingInfoModel>,
	private val sellList: List<TradingInfoModel>,
	private val hold: TradingInfoModel.() -> Unit
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
				title.text = if (position == 0) EOSRAMExchangeText.buy("") else EOSRAMExchangeText.sell("")
				title.textColor = if (position == 0) Spectrum.green else Spectrum.lightRed
			}
		} else if (holder is TradingHolder) {
			(holder.itemView as? TradingCell)?.apply {
				if (position <= buyList.size ) {
					val model = buyList[position - 1]
					val maxValue = buyList.maxBy { it.quantity }?.quantity ?: 0.0
					setData(model.account, model.quantity, maxValue, Color.parseColor("#0F1CC881"))
					onClick {
						hold(model)
					}
				} else {
					val model = sellList[position - buyList.size - 2]
					val maxValue = sellList.maxBy { it.quantity }?.quantity ?: 0.0
					setData(model.account, model.quantity, maxValue, Color.parseColor("#0FFF6464"))
					onClick {
						hold(model)
					}
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
			textSize = fontSize(12)
			typeface = GoldStoneFont.black(context)
			layoutParams = RelativeLayout.LayoutParams(
				wrapContent,
				wrapContent
			).apply {
				alignParentRight()
			}
		}
		title.setMargins<RelativeLayout.LayoutParams> {
			topMargin = 8.uiPX()
			bottomMargin = 8.uiPX()
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
		layoutParams = ViewGroup.LayoutParams(matchParent, 22.uiPX())
		backgroundView = view {
			layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
		}
		name = textView {
			textColor = GrayScale.midGray
			textSize = fontSize(12)
			typeface = GoldStoneFont.heavy(context)
			layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent).apply {
				centerVertically()
			}
		}
		name.setMargins<RelativeLayout.LayoutParams> { leftMargin = 5.uiPX() }
		transactionAmount = textView {
			textColor = GrayScale.midGray
			textSize = fontSize(12)
			typeface = GoldStoneFont.heavy(context)
			layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent).apply {
				alignParentRight()
				centerVertically()
			}
		}
		transactionAmount.setMargins<RelativeLayout.LayoutParams> { rightMargin = 5.uiPX() }
	}
	
	fun setData(accountName: String, quantity: Double, maxValue:Double, backgroundColor: Int) {
		percent = if (maxValue == 0.0) 0f else (quantity / maxValue).toFloat()
		name.text = accountName
		transactionAmount.text = if (quantity > 10000)
			BigDecimal(quantity / 1000.0).setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString() + "k"
		else BigDecimal(quantity).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString()
		backgroundView.setMargins<RelativeLayout.LayoutParams> {
			leftMargin = (viewWidth * (1 - percent)).toInt()
		}
		backgroundView.backgroundColor = backgroundColor
	}
	
	
}










