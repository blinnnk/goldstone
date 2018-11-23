package io.goldstone.blockchain.module.common.tokendetail.tokenasset.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.alignParentRight
import com.blinnnk.extension.centerInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.DelegateBandWidthInfo
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/11/22
 */
class DelegateBandwidthCell(context: Context) : RelativeLayout(context) {

	var model: DelegateBandWidthInfo? by observing(null) {
		model?.apply {
			removeAllViewsInLayout()
			verticalLayout {
				lparams(matchParent, matchParent)
				textView {
					bottomPadding = 5.uiPX()
					text = toName
					textSize = fontSize(18)
					textColor = Spectrum.blue
					typeface = GoldStoneFont.heavy(context)
				}
				val data = listOf(Pair("CPU WEIGHT", cpuWeight), Pair("NET WEIGHT", netWeight))
				data.forEach {
					relativeLayout {
						topPadding = 5.uiPX()
						textView {
							text = it.first
							layoutParams = RelativeLayout.LayoutParams(matchParent, wrapContent)
							textSize = fontSize(12)
							typeface = GoldStoneFont.black(context)
							textColor = GrayScale.midGray
							centerInVertical()
						}
						val hasValue = it.second.substringBefore(" ").toDoubleOrNull()
						textView {
							rightPadding = 35.uiPX()
							text = it.second
							gravity = Gravity.END
							layoutParams = RelativeLayout.LayoutParams(matchParent, wrapContent)
							textSize = fontSize(14)
							typeface = GoldStoneFont.black(context)
							textColor = if (hasValue == 0.0) GrayScale.midGray else GrayScale.black
							centerInVertical()
						}
					}
				}
			}
			imageView {
				imageResource = R.drawable.arrow_icon
				layoutParams = RelativeLayout.LayoutParams(35.uiPX(), 35.uiPX())
				scaleType = ImageView.ScaleType.CENTER_INSIDE
				setColorFilter(GrayScale.midGray)
				alignParentRight()
				centerInVertical()
				y = 15.uiPX().toFloat()
			}
		}
	}

	init {
		setWillNotDraw(false)
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
		setPadding(20.uiPX(), 10.uiPX(), 20.uiPX(), 10.uiPX())
	}

	private val paint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		color = GrayScale.gray
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		canvas?.drawLine(20.uiPX().toFloat(), height.toFloat(), width - 20.uiPX().toFloat(), height.toFloat(), paint)
	}
}