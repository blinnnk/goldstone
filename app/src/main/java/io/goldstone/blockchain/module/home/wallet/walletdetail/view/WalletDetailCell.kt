package io.goldstone.blockchain.module.home.wallet.walletdetail.view

import android.annotation.SuppressLint
import android.content.Context
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.component.SquareIcon
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.formatCount
import io.goldstone.blockchain.crypto.formatCurrency
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.imageResource

@SuppressLint("SetTextI18n")
/**
 * @date 23/03/2018 6:19 PM
 * @author KaySaith
 */
class WalletDetailCell(context: Context) : BaseCell(context) {
	
	var model: WalletDetailCellModel? by observing(null) {
		model?.apply {
			if (iconUrl.isBlank()) {
				icon.image.imageResource = R.drawable.default_token
			} else {
				if (symbol.equals(CryptoSymbol.eth, true)) {
					icon.image.imageResource = R.drawable.eth_icon
				} else {
					// 获取指定尺寸的图片
					icon.image.glideImage("$iconUrl?imageView2/1/w/120/h/120")
				}
			}
			tokenInfo.title.text = symbol
			tokenInfo.subtitle.text = name
			valueInfo.title.text = count.formatCount()
			valueInfo.subtitle.text = "≈ " + currency.formatCurrency() +
				" (${GoldStoneApp.getCurrencyCode()})"
		}
	}
	private val icon by lazy { SquareIcon(context, SquareIcon.Companion.Style.Big) }
	private val tokenInfo by lazy { TwoLineTitles(context) }
	private val valueInfo by lazy { TwoLineTitles(context) }
	
	init {
		icon.into(this)
		tokenInfo.into(this)
		valueInfo.into(this)
		
		tokenInfo.apply {
			title.typeface = GoldStoneFont.heavy(context)
			setCenterInVertical()
			x += 50.uiPX()
			y += 2.uiPX()
		}
		
		icon.setCenterInVertical()
		
		valueInfo.apply {
			title.typeface = GoldStoneFont.heavy(context)
			setAlignParentRight()
			setCenterInVertical()
			x -= 30.uiPX()
			isFloatRight = true
			y += 2.uiPX()
		}
	}
}