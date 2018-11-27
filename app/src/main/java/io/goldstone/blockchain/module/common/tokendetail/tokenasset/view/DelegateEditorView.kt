package io.goldstone.blockchain.module.common.tokendetail.tokenasset.view

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.crypto.utils.toEOSUnit
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.math.BigInteger

/**
 * @author KaySaith
 * @date  2018/11/22
 */
class DelegateEditorView(context: Context) : LinearLayout(context) {

	var closeEvent: Runnable? = null
	var confirmEvent: Runnable? = null

	private val titleView = TextView(context)
	private val cpuInput = RoundInput(context)
	private val netInput = RoundInput(context)
	private val passwordInput = RoundInput(context)
	private val confirmButton = RoundButton(context)

	init {
		padding = 20.uiPX()
		orientation = LinearLayout.VERTICAL
		relativeLayout {
			titleView.apply {
				setPadding(10.uiPX(), 0, 10.uiPX(), 20.uiPX())
				layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
				textSize = fontSize(20)
				typeface = GoldStoneFont.heavy(context)
				textColor = GrayScale.black
			}.into(this)
			imageView {
				imageResource = R.drawable.close_icon
				y = (-2).uiPX().toFloat()
				layoutParams = RelativeLayout.LayoutParams(30.uiPX(), 30.uiPX())
				scaleType = ImageView.ScaleType.CENTER_INSIDE
				setColorFilter(GrayScale.midGray)
				onClick {
					closeEvent?.run()
					preventDuplicateClicks()
				}
			}.alignParentRight()
		}

		val inputWidth = ScreenSize.overlayContentWidth - 40.uiPX()
		cpuInput.apply {
			setNumberInput()
			setText("0.0")
			layoutParams = LinearLayout.LayoutParams(inputWidth, wrapContent)
			title = "CPU Amount"
		}.into(this)

		netInput.apply {
			setText("0.0")
			setNumberInput()
			layoutParams = LinearLayout.LayoutParams(inputWidth, wrapContent)
			title = "NET Amount"
		}.into(this)

		passwordInput.apply {
			setPasswordInput()
			layoutParams = LinearLayout.LayoutParams(inputWidth, wrapContent)
			title = "Password"
		}.into(this)

		confirmButton.apply {
			layoutParams = LinearLayout.LayoutParams(inputWidth, 45.uiPX())
			setCardBackgroundColor(Spectrum.blue)
			text = CommonText.confirm
			onClick {
				confirmEvent?.run()
				preventDuplicateClicks()
			}
		}.into(this)
		confirmButton.setMargins<LinearLayout.LayoutParams> {
			topMargin = 5.uiPX()
		}
	}

	fun setTitle(text: String) {
		titleView.text = text
	}

	fun getCPUAMount(): BigInteger =
		cpuInput.getContent().toDoubleOrZero().toEOSUnit()

	fun getNetAmount(): BigInteger =
		netInput.getContent().toDoubleOrZero().toEOSUnit()

	fun getPassword(): String = passwordInput.getContent()

	fun showLoading(status: Boolean) {
		confirmButton.showLoadingStatus(status)
	}

}