package io.goldstone.blockchain.module.home.profile.chain.nodeselection.model

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.crypto.CryptoName
import io.goldstone.blockchain.crypto.CryptoSymbol
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * @date 2018/6/20 9:04 PM
 * @author KaySaith
 */
class NodeSelectionSectionCell(context: Context) : LinearLayout(context) {

	private val titles = TwoLineTitles(context)
	private val icon = ImageView(context)
	private val cellHeight = 50.uiPX()

	init {
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent).apply {
			topMargin = 20.uiPX()
		}
		icon
			.apply {
				layoutParams = LinearLayout.LayoutParams(cellHeight, cellHeight)
				addCorner(50.uiPX(), GrayScale.whiteGray)
				scaleType = ImageView.ScaleType.CENTER_INSIDE
			}
			.into(this)
		titles
			.apply {
				layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent)
				x += 20.uiPX()
				y += 4.uiPX()
				setBlackTitles(fontSize(18))
			}
			.into(this)
	}

	fun ethType(): NodeSelectionSectionCell {
		icon.imageResource = R.drawable.eth_icon
		titles.title.text = CryptoSymbol.eth
		titles.subtitle.text = CryptoName.eth
		return this
	}

	fun btcType(): NodeSelectionSectionCell {
		icon.imageResource = R.drawable.btc_icon
		titles.title.text = CryptoSymbol.btc()
		titles.subtitle.text = CryptoSymbol.updateNameIfInReview(CryptoName.btc)
		return this
	}

	fun ltcType(): NodeSelectionSectionCell {
		icon.imageResource = R.drawable.ltc_icon
		titles.title.text = CryptoSymbol.ltc
		titles.subtitle.text = CryptoName.ltc
		return this
	}

	fun bchType(): NodeSelectionSectionCell {
		icon.imageResource = R.drawable.bch_icon
		titles.title.text = CryptoSymbol.bch
		titles.subtitle.text = CryptoName.bch
		return this
	}

	fun etcType(): NodeSelectionSectionCell {
		icon.imageResource = R.drawable.etc_icon
		titles.title.text = CryptoSymbol.etc
		titles.subtitle.text = CryptoName.etc
		return this
	}
}