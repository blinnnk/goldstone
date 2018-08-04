package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRadioCell
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

@SuppressLint("ViewConstructor")
/**
 * @date 2018/7/12 3:57 PM
 * @author KaySaith
 */
open class DashboardOverlay(
	context: Context,
	hold: LinearLayout.() -> Unit
) : RelativeLayout(context) {
	
	var confirmEvent: Runnable? = null
	private val confirmButton = RoundButton(context)
	private val container = scrollView {
		addCorner(CornerSize.small, Spectrum.white)
		elevation = ShadowSize.Overlay
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
		minimumHeight = 200.uiPX()
		verticalLayout {
			lparams(matchParent, matchParent)
			gravity = Gravity.CENTER_HORIZONTAL
			topPadding = 20.uiPX()
			bottomPadding = 20.uiPX()
			hold(this)
			confirmButton
				.click {
					confirmEvent?.run()
					removeSelf()
				}
				.into(this)
			confirmButton.text = CommonText.confirm
			confirmButton.setBlueStyle(20.uiPX(), ScreenSize.widthWithPadding - 40.uiPX())
		}
	}
	
	init {
		id = ElementID.dashboardOverlay
		backgroundColor = GrayScale.Opacity5Black
		layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
		container.setCenterInParent()
		isClickable = true
		onClick {
			removeSelf()
		}
	}
	
	private fun removeSelf() {
		(parent as? ViewGroup)?.removeView(this)
	}
}

abstract class RadioDashboard {
	
	abstract val cellContent: ArrayList<String>
	abstract var defaultRadio: String
	abstract fun afterSelected()
	
	fun inTo(parent: ViewGroup?) {
		parent?.apply {
			DashboardOverlay(context) {
				// 取消所有选中样式的函数内方法
				fun recoveryRadioChecked() {
					(0 until cellContent.size).forEach {
						findViewById<BaseRadioCell>(it)?.checkedStatus = false
					}
				}
				// 加载视图
				cellContent.forEachIndexed { index, it ->
					BaseRadioCell(context).apply {
						id = index
						if (it.equals(defaultRadio, true)) {
							checkedStatus = true
						}
						setTitle(it)
						setGrayStyle()
					}.click {
						recoveryRadioChecked()
						defaultRadio = it.getTitle()
						afterSelected()
						it.checkedStatus = true
					}.into(this)
				}
			}.into(this)
		}
	}
}