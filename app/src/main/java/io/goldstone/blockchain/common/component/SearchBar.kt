package io.goldstone.blockchain.common.component

import android.content.Context
import android.graphics.PorterDuff
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/12/01
 */
class SearchBar(context: Context) : GSCard(context) {
	private lateinit var input: EditText

	init {
		relativeLayout {
			lparams(matchParent, 45.uiPX())
			imageView {
				imageResource = R.drawable.search_icon
				scaleType = ImageView.ScaleType.CENTER_INSIDE
				layoutParams = RelativeLayout.LayoutParams(45.uiPX(), matchParent)
				setColorFilter(GrayScale.midGray)
			}
			input = editText {
				layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
				leftPadding = 45.uiPX()
				hint = "Search what you want to use"
				backgroundTintMode = PorterDuff.Mode.CLEAR
				hintTextColor = GrayScale.midGray
				textSize = fontSize(14)
			}
		}
	}
}