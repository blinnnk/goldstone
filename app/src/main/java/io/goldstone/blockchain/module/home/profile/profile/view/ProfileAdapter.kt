package io.goldstone.blockchain.module.home.profile.profile.view

import android.content.Context
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.keyboardHeightListener
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.module.home.profile.profile.model.ProfileModel
import org.jetbrains.anko.matchParent

/**
 * @date 25/03/2018 10:54 PM
 * @author KaySaith
 */
class ProfileAdapter(
	override val dataSet: ArrayList<ProfileModel>,
	private val callback: (ProfileCell, Int) -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<ProfileModel, View, ProfileCell, View>() {
	
	private var hasHiddenSoftNavigationBar = false
	override fun generateFooter(context: Context) =
		View(context).apply {
			val barHeight =
				if (
					(!hasHiddenSoftNavigationBar && !KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK))
					|| Config.isNotchScreen()
				) {
					60.uiPX()
				} else 10.uiPX()
			layoutParams = LinearLayout.LayoutParams(matchParent, barHeight)
		}
	
	override fun generateHeader(context: Context) = View(context).apply {
		/**
		 * 判断不同手机的不同 `Navigation` 的状态决定 `Footer` 的补贴高度
		 * 主要是, `Samsung S8, S9` 的 `Navigation` 状态判断
		 */
		keyboardHeightListener {
			if (it < 0) {
				hasHiddenSoftNavigationBar = true
			}
		}
		layoutParams = LinearLayout.LayoutParams(matchParent, 90.uiPX())
	}
	
	override fun generateCell(context: Context) = ProfileCell(context)
	
	override fun ProfileCell.bindCell(data: ProfileModel, position: Int) {
		model = data
		callback(this@bindCell, position)
	}
}