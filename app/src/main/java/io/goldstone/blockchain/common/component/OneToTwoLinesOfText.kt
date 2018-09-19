package io.goldstone.blockchain.common.component

import android.content.Context
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.uikit.uiPX
import com.github.mmin18.widget.RealtimeBlurView
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textView

open class OneToTwoLinesOfText(context: Context) : RelativeLayout(context) {

	private val time by lazy { TextView(context) }
	private val name by lazy { TextView(context) }
	private val purchaseAndSaleQuantity by lazy { TextView(context) }
	private val price by lazy { TextView(context) }

	init {
		layoutParams = RelativeLayout.LayoutParams(
			matchParent,
			60.uiPX()
		).apply {
			topMargin = 5.uiPX()
			bottomMargin = 5.uiPX()
		}
		time.apply {
			text = "时间"
		}.into(this)

		name.apply {
			text = "姓名"
			x += 150.uiPX()
		}.into(this)

		purchaseAndSaleQuantity.into(this)
		purchaseAndSaleQuantity.apply {
			text = "买卖数量"
			setAlignParentRight()
		}

		price.into(this)
		price.apply {
			text = "价格"
			y += 20.uiPX()
			setAlignParentRight()
		}
	}

	fun setText(
		timeContent: String,
		nameContent: String,
		purchaseAndSaleQuantityContent: String,
		priceContent: String
	) {
		time.text = timeContent
		name.text = nameContent
		purchaseAndSaleQuantity.text = purchaseAndSaleQuantityContent
		price.text = priceContent
	}
}