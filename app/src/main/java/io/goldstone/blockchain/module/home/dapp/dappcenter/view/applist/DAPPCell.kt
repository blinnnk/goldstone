package io.goldstone.blockchain.module.home.dapp.dappcenter.view.applist

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPModel
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent


/**
 * @author KaySaith
 * @date  2018/12/02
 */
class DAPPCell(context: Context) : LinearLayout(context) {

	var model: DAPPModel? by observing(null) {
		model?.apply {
			appIcon.glideImage(src)
			titles.title.text = title
			titles.subtitle.text = description
		}
	}

	private val appIcon = ImageView(context)
	private val titles = TwoLineTitles(context)

	init {
		layoutParams = LinearLayout.LayoutParams(matchParent, 78.uiPX())
		setWillNotDraw(false)
		gravity = Gravity.CENTER_VERTICAL
		setPadding(10.uiPX(), 10.uiPX(), 10.uiPX(), 0.uiPX())
		appIcon.apply {
			scaleType = ImageView.ScaleType.CENTER_CROP
			addCorner(CornerSize.default.toInt(), GrayScale.whiteGray)
			elevation = 3.uiPX().toFloat()
			layoutParams = LinearLayout.LayoutParams(60.uiPX(), 60.uiPX())
		}.into(this)
		appIcon.setMargins<LinearLayout.LayoutParams> {
			margin = 6.uiPX()
		}

		titles.apply {
			leftPadding = 10.uiPX()
			setBlackTitles(subtitleSize = fontSize(11))
		}.into(this)
	}

	private val paint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		color = GrayScale.midGray
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		canvas?.drawLine(
			85.uiPX().toFloat(),
			height.toFloat(),
			width - 20.uiPX().toFloat(),
			height.toFloat(),
			paint
		)
	}
}