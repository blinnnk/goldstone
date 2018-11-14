package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.component.button.SquareIcon
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent

/**
 * @date 25/03/2018 5:12 PM
 * @author KaySaith
 */
open class TokenManagementListCell(context: Context) : BaseCell(context) {

	var model: WalletDetailCellModel? by observing(null) {
		model?.apply {
			// 显示默认图判断
			when {
				iconUrl.isBlank() -> icon.image.imageResource = R.drawable.default_token
				contract.isETH() -> icon.image.imageResource = R.drawable.eth_icon
				contract.isETC() -> icon.image.imageResource = R.drawable.etc_icon
				contract.isLTC() -> icon.image.imageResource = R.drawable.ltc_icon
				contract.isBCH() -> icon.image.imageResource = R.drawable.bch_icon
				contract.isEOS() -> icon.image.imageResource = R.drawable.eos_icon
				contract.isBTC() ->
					icon.image.imageResource =
						if (SharedWallet.getYingYongBaoInReviewStatus()) R.drawable.default_token
						else R.drawable.btc_icon
				else -> icon.image.glideImage(iconUrl)
			}
			tokenInfo.title.text = CoinSymbol.updateSymbolIfInReview(symbol)
			tokenInfo.subtitle.text = CoinSymbol.updateNameIfInReview(if (tokenName.isEmpty()) symbol.symbol else tokenName)
		}
	}
	var quotationSearchModel: QuotationSelectionTable? by observing(null) {
		quotationSearchModel?.apply {
			tokenInfo.title.text = infoTitle
			tokenInfo.subtitle.text = name
			switch.isChecked = isSelecting
		}
	}

	var tokenSearchModel: DefaultTokenTable? by observing(null) {
		tokenSearchModel?.apply {
			// 显示默认图判断
			when {
				iconUrl.isBlank() -> icon.image.imageResource = R.drawable.default_token
				TokenContract(this).isETH() -> icon.image.imageResource = R.drawable.eth_icon
				TokenContract(this).isETC() -> icon.image.imageResource = R.drawable.etc_icon
				TokenContract(this).isLTC() -> icon.image.imageResource = R.drawable.ltc_icon
				TokenContract(this).isBCH() -> icon.image.imageResource = R.drawable.bch_icon
				TokenContract(this).isEOS() -> icon.image.imageResource = R.drawable.eos_icon
				TokenContract(this).isBTC() ->
					icon.image.imageResource =
						if (SharedWallet.getYingYongBaoInReviewStatus()) R.drawable.default_token
						else R.drawable.btc_icon
				else -> icon.image.glideImage(iconUrl)
			}
			tokenInfo.title.text = CoinSymbol.updateSymbolIfInReview(CoinSymbol(symbol))
			tokenInfo.subtitle.text = CoinSymbol.updateNameIfInReview(if (name.isEmpty()) symbol else name)
			switch.isChecked = isUsed
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