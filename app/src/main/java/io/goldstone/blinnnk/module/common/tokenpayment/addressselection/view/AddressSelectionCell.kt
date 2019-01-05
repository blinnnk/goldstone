package io.goldstone.blinnnk.module.common.tokenpayment.addressselection.view

import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.module.home.profile.contacts.contracts.view.ContactsCell
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent

/**
 * @date 28/03/2018 9:25 AM
 * @author KaySaith
 */

class AddressSelectionCell(context: Context) : ContactsCell(context) {

	private val arrowIcon by lazy { ImageView(context) }

	init {
		arrowIcon.apply {
			imageResource = R.drawable.next_icon
			setColorFilter(GrayScale.lightGray)
			layoutParams = RelativeLayout.LayoutParams(30.uiPX(), matchParent)
			scaleType = ImageView.ScaleType.CENTER_INSIDE
			x = ScreenSize.widthWithPadding - 30.uiPX().toFloat()
		}.into(this)
	}
}