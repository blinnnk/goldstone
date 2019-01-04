package io.goldstone.blockchain.common.base.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.GridLayout
import android.widget.LinearLayout
import com.blinnnk.model.MutablePair
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent


/**
 * @author KaySaith
 * @date  2018/09/21
 */
@SuppressLint("ViewConstructor")
open class ColumnSectionTitle(context: Context) : GridLayout(context) {

	init {
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
		rowCount = 1
		bottomPadding = 10.uiPX()
	}

	private val titleWidth = ScreenSize.widthWithPadding / 3
	fun showTitles(titles: List<MutablePair<String, String>>) {
		removeAllViews()
		columnCount = titles.size
		titles.forEachIndexed { index, pair ->
			this.addView(
				TwoLineTitles(context).apply {
					layoutParams = LinearLayout.LayoutParams(titleWidth, matchParent)
					isCenter = true
					id = index
					title.apply {
						textSize = fontSize(14)
						text = pair.left
						textColor = GrayScale.black
						typeface = GoldStoneFont.black(context)
					}
					subtitle.apply {
						textSize = fontSize(16)
						text = pair.right
						textColor = GrayScale.midGray
						typeface = GoldStoneFont.black(context)
					}
				}
			)
		}
	}

	fun updateValues(index: Int, value: String) {
		findViewById<TwoLineTitles>(index)?.apply {
			subtitle.text = value
		}
	}

}