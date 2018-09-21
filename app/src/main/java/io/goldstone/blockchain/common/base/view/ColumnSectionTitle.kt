package io.goldstone.blockchain.common.base.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.GridLayout
import android.widget.LinearLayout
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.matchParent
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
	fun showTitles(titles: List<Pair<String, String>>) {
		removeAllViews()
		columnCount = titles.size
		titles.forEachIndexed { index, pair ->
			this.addView(
				TwoLineTitles(context).apply {
					layoutParams = LinearLayout.LayoutParams(titleWidth, matchParent)
					isCenter = true
					id = index
					setBlackTitles(fontSize(12))
					title.text = pair.first
					subtitle.text = pair.second
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