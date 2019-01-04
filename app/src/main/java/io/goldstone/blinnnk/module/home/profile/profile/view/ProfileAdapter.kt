package io.goldstone.blinnnk.module.home.profile.profile.view

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.module.home.profile.profile.model.ProfileModel
import org.jetbrains.anko.matchParent

/**
 * @date 25/03/2018 10:54 PM
 * @author KaySaith
 */
class ProfileAdapter(
	override val dataSet: ArrayList<ProfileModel>,
	private val callback: (ProfileCell, Int) -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<ProfileModel, View, ProfileCell, View>() {

	override fun generateFooter(context: Context) =
		View(context).apply {
			layoutParams = LinearLayout.LayoutParams(matchParent, 60.uiPX())
		}

	override fun generateHeader(context: Context) = View(context).apply {
		layoutParams = LinearLayout.LayoutParams(matchParent, 80.uiPX())
	}

	override fun generateCell(context: Context) = ProfileCell(context)

	override fun ProfileCell.bindCell(data: ProfileModel, position: Int) {
		model = data
		callback(this@bindCell, position)
	}
}