package io.goldstone.blockchain.module.home.profile.securitysetting.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.*
import com.blinnnk.extension.centerInVertical
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import org.jetbrains.anko.*

/**
 * @date 11/27/2018
 * @author yangLiHai
 */
@SuppressLint("ViewConstructor")
class SecuritySwitchView(context: Context): RelativeLayout(context) {
	private lateinit var switchLayout: View
	private val switch by lazy { Switch(context) }
	private val twoLineTitles by lazy { TwoLineTitles(context) }
	
	init {
		layoutParams = LayoutParams(matchParent, 50.uiPX())
		leftPadding = PaddingSize.device
		rightPadding = PaddingSize.device
		twoLineTitles.apply {
			layoutParams = LayoutParams(wrapContent, wrapContent)
			centerInVertical()
			setBlackTitles()
		}.into(this)
		
		relativeLayout {
			layoutParams = LayoutParams(50.uiPX(), matchParent).apply {
				alignParentRight()
			}
			switch.apply {
				isChecked = false
				layoutParams = LayoutParams(50.uiPX(), matchParent).apply {
					centerInVertical()
				}
			}.into(this)
			switchLayout = view {
				layoutParams = LayoutParams(matchParent, matchParent)
			}
		}
		view {
			backgroundColor = GrayScale.midGray
			layoutParams = LayoutParams(matchParent,1).apply {
				alignParentBottom()
			}
		}
		
		
	}
	
	fun setOnclick(callback: (Switch) -> Unit) {
		switchLayout.click {
			callback(switch)
		}
	}
	
	fun setSwitchStatus(isChecked: Boolean) {
		switch.isChecked = isChecked
	}
	
	fun getSwitchCheckedStatus(): Boolean {
		return switch.isChecked
	}
	
	fun setTitle(title: String, subTitle: String) {
		this.twoLineTitles.title.text = title
		this.twoLineTitles.subtitle.text = subTitle
	}
}