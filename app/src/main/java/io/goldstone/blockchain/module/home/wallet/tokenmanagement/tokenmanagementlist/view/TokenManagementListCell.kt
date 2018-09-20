package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.orFalse
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.component.button.SquareIcon
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent

/**
 * @date 25/03/2018 5:12 PM
 * @author KaySaith
 */
open class TokenManagementListCell(context: Context) : BaseCell(context) {

	var model: DefaultTokenTable? by observing(null) {
		model?.apply {
			// 显示默认图判断
			when {
				iconUrl.isBlank() -> icon.image.imageResource = R.drawable.default_token
				TokenContract(contract).isETH() -> icon.image.imageResource = R.drawable.eth_icon
				TokenContract(contract).isETC() -> icon.image.imageResource = R.drawable.etc_icon
				TokenContract(contract).isLTC() -> icon.image.imageResource = R.drawable.ltc_icon
				TokenContract(contract).isBCH() -> icon.image.imageResource = R.drawable.bch_icon
				TokenContract(contract).isEOS() -> icon.image.imageResource = R.drawable.eos_icon
				TokenContract(contract).isBTC() ->
					icon.image.imageResource =
						if (Config.getYingYongBaoInReviewStatus()) R.drawable.default_token
						else R.drawable.btc_icon
				else -> icon.image.glideImage(iconUrl)
			}
			tokenInfo.title.text = CoinSymbol.updateSymbolIfInReview(symbol)
			tokenInfo.subtitle.text = CoinSymbol.updateNameIfInReview(name)
			switch.isChecked = model?.isUsed.orFalse()
		}
	}
	var searchModel: QuotationSelectionTable? by observing(null) {
		searchModel?.apply {
			tokenInfo.title.text = infoTitle
			tokenInfo.subtitle.text = name
			switch.isChecked = isSelecting
		}
	}
	val switch by lazy { HoneyBaseSwitch(context) }
	private val tokenInfo by lazy { TwoLineTitles(context) }
	protected val icon by lazy { SquareIcon(context, SquareIcon.Companion.Style.Big) }

	init {
		hasArrow = false
		setHorizontalPadding()
		this.addView(icon.apply {
			setGrayStyle()
			y += 10.uiPX()
		})

		this.addView(tokenInfo.apply {
			setBlackTitles()
			x += 10.uiPX()
		})

		this.addView(switch.apply {
			layoutParams = RelativeLayout.LayoutParams(50.uiPX(), matchParent)
			setThemColor(Spectrum.green, Spectrum.lightGreen)
		})

		tokenInfo.apply {
			setCenterInVertical()
			x += 40.uiPX()
		}

		switch.apply {
			setCenterInVertical()
			setAlignParentRight()
		}

		setGrayStyle()
	}

	fun showArrow() {
		removeView(switch)
		hasArrow = true
	}

	fun hideIcon() {
		icon.visibility = View.GONE
		tokenInfo.x = 0f
	}
}