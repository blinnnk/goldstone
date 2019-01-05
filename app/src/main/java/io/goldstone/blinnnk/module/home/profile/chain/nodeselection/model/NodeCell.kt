package io.goldstone.blinnnk.module.home.profile.chain.nodeselection.model

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.component.title.TwoLineTitles
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.common.value.fontSize
import io.goldstone.blinnnk.crypto.multichain.CoinSymbol
import io.goldstone.blinnnk.crypto.multichain.CryptoName
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.topPadding
import org.jetbrains.anko.wrapContent

/**
 * @date 2018/6/20 9:04 PM
 * @author KaySaith
 */
class NodeCell(context: Context) : LinearLayout(context) {

	private val titles = TwoLineTitles(context)
	private val icon = ImageView(context)
	private val cellHeight = 42.uiPX()

	init {
		topPadding = 10.uiPX()
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
		icon.apply {
			layoutParams = LinearLayout.LayoutParams(cellHeight, cellHeight)
			setColorFilter(GrayScale.midGray)
		}.into(this)
		titles.apply {
			layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent)
			x += 10.uiPX()
			y += 4.uiPX()
			setBlackTitles(fontSize(16))
		}.into(this)
	}

	fun ethType(): NodeCell {
		icon.imageResource = R.drawable.eth_creator_icon
		titles.title.text = CoinSymbol.eth
		titles.subtitle.text = CryptoName.eth
		return this
	}

	fun btcType(): NodeCell {
		icon.imageResource = R.drawable.btc_creator_icon
		titles.title.text = CoinSymbol.btc
		titles.subtitle.text = CryptoName.btc
		return this
	}

	fun ltcType(): NodeCell {
		icon.imageResource = R.drawable.ltc_creator_icon
		titles.title.text = CoinSymbol.ltc
		titles.subtitle.text = CryptoName.ltc
		return this
	}

	fun bchType(): NodeCell {
		icon.imageResource = R.drawable.bch_creator_icon
		titles.title.text = CoinSymbol.bch
		titles.subtitle.text = CryptoName.bch
		return this
	}

	fun eosType(): NodeCell {
		icon.imageResource = R.drawable.eos_creator_icon
		titles.title.text = CoinSymbol.eos
		titles.subtitle.text = CryptoName.eos
		return this
	}

	fun etcType(): NodeCell {
		icon.imageResource = R.drawable.etc_creator_icon
		titles.title.text = CoinSymbol.etc
		titles.subtitle.text = CryptoName.etc
		return this
	}
}