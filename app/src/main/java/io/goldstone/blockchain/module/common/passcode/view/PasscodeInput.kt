package io.goldstone.blockchain.module.common.passcode.view

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.animation.scale
import com.blinnnk.animation.setValueAnimatorOfFloat
import com.blinnnk.extension.addCircleBorder
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.extension.setCenterInHorizontal
import com.blinnnk.uikit.FloatAnimationObject
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.language.PincodeText
import io.goldstone.blockchain.common.value.BorderSize
import io.goldstone.blockchain.common.value.Count
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * @date 23/04/2018 12:03 PM
 * @author KaySaith
 */

class PasscodeInput(context: Context) : RelativeLayout(context) {

	private val titles by lazy { TwoLineTitles(context) }
	private val codeSize = 30.uiPX()
	private val codeSpace = 20.uiPX()
	private var inputLayout: LinearLayout

	private val codeWidth = (codeSize + codeSpace) * Count.pinCode - codeSpace
	private val centerLeft = (ScreenSize.Width - codeWidth) / 2f

	init {

		layoutParams = LinearLayout.LayoutParams(matchParent, 200.uiPX())

		titles.apply {
			layoutParams = RelativeLayout.LayoutParams((ScreenSize.Width * 0.8f).toInt(), 200.uiPX())
			setBigWhiteStyle()
			titles.title.text = PincodeText.enterPincode
			titles.subtitle.text = PincodeText.enterPincodeDescription
			isCenter = true
		}.into(this)
		titles.setCenterInHorizontal()

		inputLayout = linearLayout {
			layoutParams = LinearLayout.LayoutParams(codeWidth, wrapContent)
			y += 100.uiPX()
			x = centerLeft
			(0 until Count.pinCode).forEach {
				View(context).apply {
					id = it
					layoutParams = LinearLayout.LayoutParams(codeSize, codeSize)
					addCircleBorder(codeSize, BorderSize.bold.toInt(), Spectrum.white)
					x = codeSpace * it.toFloat()
				}.into(this)
			}
		}
	}

	fun setEnteredStyle(index: Int) {
		findViewById<View>(index + 1)?.apply {
			addCorner(codeSize, Color.TRANSPARENT)
			addCircleBorder(codeSize, BorderSize.bold.toInt(), Spectrum.white)
		}
		findViewById<View>(index)?.apply {
			addCorner(codeSize, Spectrum.white)
			scale(0)
		}
	}

	fun recoveryStyle() {
		(0 until Count.pinCode).forEach {
			findViewById<View>(it)?.apply {
				addCorner(codeSize, Color.TRANSPARENT)
				addCircleBorder(codeSize, BorderSize.bold.toInt(), Spectrum.white)
			}
		}
	}

	fun swipe() {
		val leftValue = centerLeft - 30f
		val rightValue = centerLeft + 30f
		inputLayout.apply {
			setValueAnimatorOfFloat(FloatAnimationObject.X, centerLeft, leftValue, 100L) {
				setValueAnimatorOfFloat(FloatAnimationObject.X, leftValue, rightValue, 100L) {
					setValueAnimatorOfFloat(FloatAnimationObject.X, rightValue, centerLeft, 100L)
				}
			}
		}
	}
}