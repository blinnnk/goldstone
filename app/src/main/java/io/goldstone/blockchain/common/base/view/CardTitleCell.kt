package io.goldstone.blockchain.common.base.view

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.textColor


/**
 * @author KaySaith
 * @date  2018/09/22
 */
open class CardTitleCell(context: Context) : GrayCardView(context) {

	private val titleView = TextView(context).apply {
		textSize = fontSize(14)
		typeface = GoldStoneFont.black(context)
		textColor = GrayScale.midGray
		layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
		gravity = Gravity.CENTER_VERTICAL
	}

	private val subtitleView = TextView(context).apply {
		visibility = View.GONE
		textSize = fontSize(12)
		typeface = GoldStoneFont.medium(context)
		textColor = GrayScale.midGray
		layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
		gravity = Gravity.CENTER_VERTICAL or Gravity.END
	}

	private val contentTextView = TextView(context).apply {
		visibility = View.GONE
		textSize = fontSize(14)
		typeface = GoldStoneFont.heavy(context)
		textColor = GrayScale.black
		layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
		gravity = Gravity.CENTER_VERTICAL
		setPadding(10.uiPX(), 5.uiPX(), 10.uiPX(), 0)
	}

init {
    getContainer().apply {
			relativeLayout {
				setPadding(10.uiPX(), 5.uiPX(), 10.uiPX(), 0)
				lparams(matchParent, 25.uiPX())
				titleView.into(this)
				subtitleView.into(this)
			}
			contentTextView.into(getContainer())
			bottomPadding = 10.uiPX()
		}
}

	fun setTitle(text: String) {
		titleView.text = text
	}

	fun setSubtitle(text: String) {
		subtitleView.visibility = View.VISIBLE
		subtitleView.text = text
	}

	fun setContent(text: CharSequence) {
		contentTextView.visibility = View.VISIBLE
		contentTextView.text = text
	}
}