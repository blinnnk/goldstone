package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.imageResource

@SuppressLint("ViewConstructor")
/**
 * @date 23/03/2018 11:11 PM
 * @author KaySaith
 */
class SquareIcon(
	context: Context,
	val style: SquareIcon.Companion.Style = SquareIcon.Companion.Style.Small
) : LinearLayout(context) {
	
	var src: Int by observing(0) {
		image.imageResource = src
	}
	val image by lazy { ImageView(context) }
	private val iconSize = when (style) {
		SquareIcon.Companion.Style.Small -> 30.uiPX()
		SquareIcon.Companion.Style.Big -> 40.uiPX()
	}
	private val imageSize = when (style) {
		SquareIcon.Companion.Style.Small -> 24.uiPX()
		SquareIcon.Companion.Style.Big -> 32.uiPX()
	}
	private val cornerSize = when (style) {
		SquareIcon.Companion.Style.Small -> CornerSize.default
		SquareIcon.Companion.Style.Big -> 20.uiPX().toFloat()
	}
	
	init {
		setWillNotDraw(false)
		
		layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
		image.apply {
			layoutParams = LinearLayout.LayoutParams(imageSize, imageSize).apply {
				topMargin = UIUtils.subtractThenHalf(iconSize, imageSize)
				leftMargin = UIUtils.subtractThenHalf(iconSize, imageSize)
			}
			scaleType = ImageView.ScaleType.CENTER_CROP
			setColorFilter(Spectrum.white)
		}.into(this)
	}
	
	private val paint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		color = Spectrum.opacity2White
	}
	
	@SuppressLint("DrawAllocation")
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		val rectF = RectF(0f, 0f, iconSize.toFloat(), iconSize.toFloat())
		canvas?.drawRoundRect(rectF, cornerSize, cornerSize, paint)
	}
	
	fun setGrayStyle() {
		paint.color = GrayScale.Opacity2Black
		invalidate()
	}
	
	companion object {
		enum class Style {
			Big, Small
		}
	}
}