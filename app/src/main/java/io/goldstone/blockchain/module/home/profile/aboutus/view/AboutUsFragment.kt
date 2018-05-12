package io.goldstone.blockchain.module.home.profile.aboutus.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.Size
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.CircleButton
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.profile.aboutus.presenter.AboutUsPresenter
import org.jetbrains.anko.*

/**
 * @date 2018/5/12 7:50 PM
 * @author KaySaith
 */

class AboutUsFragment : BaseFragment<AboutUsPresenter>() {

	private val lineSpace = 40.uiPX()
	private lateinit var introTextView: TextView
	private lateinit var productIntro: TextView

	fun setIntroContent(content: String) {
		introTextView.text = content
	}

	fun setProductIntroContent(content: String) {
		productIntro.text = content
	}

	override val presenter = AboutUsPresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			relativeLayout {
				imageView {
					y -= 5.uiPX()
					imageResource = R.drawable.about_us_background
					layoutParams = RelativeLayout.LayoutParams(
						matchParent, 160.uiPX()
					)
					scaleType = ImageView.ScaleType.CENTER_INSIDE
				}

				imageView {
					imageResource = R.drawable.logo
				}.lparams {
					centerHorizontally()
					topMargin = 80.uiPX()
					width = 120.uiPX()
					height = 120.uiPX()
				}

				verticalLayout {
					UnderLineTitle(context).apply {
						text = "GOLD STONE"
					}.into(this)

					introTextView = textView {
						textSize = 4.uiPX().toFloat() + 2f
						textColor = GrayScale.gray
						gravity = Gravity.CENTER_HORIZONTAL
					}.lparams {
						width = ScreenSize.widthWithPadding
						height = wrapContent
						leftMargin = PaddingSize.device
					}

					linearLayout {
						val iconSize = 80.uiPX()
						val iconViewWidth = 100.uiPX()
						val leftMarginSize = (ScreenSize.widthWithPadding - iconViewWidth * 2) / 2 - 20.uiPX()
						CircleButton(context).apply {
							setStyleParameter(
								Size(iconViewWidth, 120.uiPX()), iconSize, Spectrum.softGreen, Spectrum.green
							)
							setTitleStyle(4.uiPX().toFloat(), GrayScale.black, GoldStoneFont.black(context))
							title = "SAFE"
							src = R.drawable.safe_icon
						}.apply {
							setMargins<LinearLayout.LayoutParams> { leftMargin = leftMarginSize }
						}.into(this)

						CircleButton(context).apply {
							setStyleParameter(
								Size(iconViewWidth, 120.uiPX()), iconSize, Spectrum.softGreen, Spectrum.green
							)
							setTitleStyle(4.uiPX().toFloat(), GrayScale.black, GoldStoneFont.black(context))
							title = "SPEED"
							src = R.drawable.speed_icon
						}.apply {
							setMargins<LinearLayout.LayoutParams> { leftMargin = 40.uiPX() }
						}.into(this)
					}.lparams {
						leftMargin = PaddingSize.device
						topMargin = lineSpace
					}

					productIntro = textView {
						textSize = 4.uiPX().toFloat() + 2f
						textColor = GrayScale.gray
						gravity = Gravity.CENTER_HORIZONTAL
					}.lparams {
						width = ScreenSize.widthWithPadding
						height = wrapContent
						leftMargin = PaddingSize.device
						topMargin = lineSpace
					}

					// Investors's Logos
					UnderLineTitle(context).apply {
						text = "INVESTORS"
						setMargins<LinearLayout.LayoutParams> { topMargin = lineSpace }
					}.into(this)

					imageView {
						imageResource = R.drawable.sequoia_capital_icon
						layoutParams = LinearLayout.LayoutParams(matchParent, 80.uiPX())
					}

					imageView {
						imageResource = R.drawable.tencent_icon
						layoutParams = LinearLayout.LayoutParams(matchParent, 70.uiPX())
					}

					imageView {
						imageResource = R.drawable.sinovation_ventures_icon
						layoutParams = LinearLayout.LayoutParams(matchParent, 90.uiPX())
					}

					imageView {
						imageResource = R.drawable.zhen_fund_icon
					}.lparams {
						topMargin = 30.uiPX()
						width = matchParent
						height = 65.uiPX()
					}

				}.lparams {
					width = matchParent
					height = wrapContent
					topMargin = 220.uiPX()
					bottomPadding = 50.uiPX()
				}
			}
		}
	}
}

class UnderLineTitle(context: Context) : View(context) {

	var text: String by observing("Title") {
		invalidate()
	}

	private val fontSize = 14.uiPX().toFloat()

	private val paint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		color = GrayScale.black
		textSize = fontSize
		typeface = GoldStoneFont.black(context)
	}

	private val linePaint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		color = GrayScale.black
	}

	init {
		layoutParams = LinearLayout.LayoutParams(
			matchParent, 50.uiPX()
		)
	}

	@SuppressLint("DrawAllocation")
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)

		val marginLeft = (width - paint.measureText(text)) / 2f

		canvas?.drawText(
			text, marginLeft, fontSize + 2.uiPX().toFloat(), paint
		)

		val rectF = RectF(
			marginLeft, fontSize + 12.uiPX(), width - marginLeft, fontSize + 14.uiPX().toFloat()
		)
		canvas?.drawRect(
			rectF, linePaint
		)
	}

}