package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.view

import android.annotation.SuppressLint
import android.content.Context
import com.blinnnk.extension.scaleTo
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basecell.BaseValueCell
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.textColor

@SuppressLint("SetTextI18n")
/**
 * @date 24/03/2018 2:15 PM
 * @author KaySaith
 */
open class TransactionListCell(context: Context) : BaseValueCell(context) {
	var model: TransactionListModel? by observing(null) {
		model?.let {
			icon.apply {
				if (it.hasError || it.isFailed) {
					// 失败或错误的样式
					src = R.drawable.error_icon
					iconColor = Spectrum.red
					count?.title?.textColor = Spectrum.lightRed
				} else if (it.isFee) {
					// 燃气费开销的样式
					src = R.drawable.gas_used_icon
					iconColor = GrayScale.midGray
					count?.title?.textColor = GrayScale.midGray
				} else {
					// 接收或转出的样式
					if (it.isReceived) {
						src = R.drawable.receive_icon
						iconColor = Spectrum.green
						count?.title?.textColor = Spectrum.green
					} else {
						src =
							if (model?.isPending == true) R.drawable.pending_icon
							else R.drawable.send_icon
						iconColor =
							if (model?.isPending == true) Spectrum.darkBlue
							else GrayScale.midGray
						count?.title?.textColor = Spectrum.red
					}
				}
			}

			info.apply {
				title.text = it.addressName.scaleTo(14)
				subtitle.text =
					if (Config.getCurrentLanguageCode() == 0) it.addressInfo
					else it.addressInfo.scaleTo(26)
			}

			count?.apply {
				title.text =
					(if (it.isReceived) "+" else "-") +
					if (it.isFee) it.minerFee.substringBefore(" ") else it.count.formatCount()
				subtitle.text = if (it.isFee) {
					when {
						it.symbol.equals(CryptoSymbol.etc, true) -> CryptoSymbol.etc
						it.symbol.equals(CryptoSymbol.bch, true) -> CryptoSymbol.bch
						it.symbol.equals(CryptoSymbol.ltc, true) -> CryptoSymbol.ltc
						it.symbol.equals(CryptoSymbol.btc(), true) -> CryptoSymbol.btc()
						else -> CryptoSymbol.eth
					}
				} else it.symbol
			}
		}
	}

	init {
		layoutParams.height = 65.uiPX()
		setGrayStyle()
		setValueStyle(true)
	}
}