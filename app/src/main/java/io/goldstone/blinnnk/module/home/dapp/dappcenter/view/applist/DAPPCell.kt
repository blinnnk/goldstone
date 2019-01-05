package io.goldstone.blinnnk.module.home.dapp.dappcenter.view.applist

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.component.title.TwoLineTitles
import io.goldstone.blinnnk.common.component.title.twoLineTitles
import io.goldstone.blinnnk.common.utils.glideImage
import io.goldstone.blinnnk.common.value.CornerSize
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.fontSize
import io.goldstone.blinnnk.module.home.dapp.dappcenter.model.DAPPTable
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/12/02
 */
class DAPPCell(context: Context) : RelativeLayout(context) {

	var model: DAPPTable? by observing(null) {
		model?.apply {
			appIcon.glideImage(icon)
			titles.title.text = title
			titles.subtitle.text = description
			tagContainer.removeAllViewsInLayout()
			getTagList().forEach {
				val subTag = Tag(context)
				subTag.text = it
				subTag.into(tagContainer)
				subTag.setMargins<LinearLayout.LayoutParams> { leftMargin = 5.uiPX() }
			}
		}
	}

	private val appIcon = ImageView(context)
	private lateinit var titles: TwoLineTitles
	private lateinit var tagContainer: LinearLayout

	init {
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
		setWillNotDraw(false)
		gravity = Gravity.CENTER_VERTICAL
		padding = 10.uiPX()
		appIcon.apply {
			scaleType = ImageView.ScaleType.CENTER_CROP
			addCorner(CornerSize.normal.toInt(), GrayScale.whiteGray)
			elevation = 3.uiPX().toFloat()
			layoutParams = RelativeLayout.LayoutParams(60.uiPX(), 60.uiPX())
		}.into(this)
		appIcon.centerInVertical()
		appIcon.setMargins<RelativeLayout.LayoutParams> {
			margin = 5.uiPX()
		}
		verticalLayout {
			leftPadding = 70.uiPX()
			rightPadding = 30.uiPX()
			lparams(matchParent, wrapContent)
			titles = twoLineTitles {
				setSubtitleLineCount(2)
				leftPadding = 10.uiPX()
				setBlackTitles(
					lineSpace = 1.uiPX(),
					subtitleSize = fontSize(12)
				)
			}
			tagContainer = linearLayout {
				lparams(matchParent, wrapContent)
			}
		}.centerInVertical()
		imageView {
			imageResource = R.drawable.arrow_icon
			scaleType = ImageView.ScaleType.CENTER_INSIDE
			layoutParams = RelativeLayout.LayoutParams(30.uiPX(), matchParent)
			setColorFilter(GrayScale.midGray)
		}.apply {
			alignParentRight()
			centerInVertical()
		}
	}

	private val paint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		color = GrayScale.midGray
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		canvas?.drawLine(
			90.uiPX().toFloat(),
			height.toFloat(),
			width - 20.uiPX().toFloat(),
			height.toFloat(),
			paint
		)
	}

	inner class Tag(context: Context) : TextView(context) {
		init {
			gravity = Gravity.CENTER
			layoutParams = LinearLayout.LayoutParams(wrapContent, 24.uiPX())
			setPadding(8.uiPX(), 1.uiPX(), 8.uiPX(), 1.uiPX())
			textSize = fontSize(11)
			textColor = GrayScale.midGray
			addCorner(12.uiPX(), GrayScale.whiteGray)
		}
	}
}