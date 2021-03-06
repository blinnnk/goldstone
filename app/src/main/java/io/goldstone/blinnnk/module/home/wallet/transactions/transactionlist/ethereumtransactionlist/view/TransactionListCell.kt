package io.goldstone.blinnnk.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.view

import android.annotation.SuppressLint
import android.content.Context
import com.blinnnk.extension.scaleTo
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.base.basecell.BaseValueCell
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.crypto.multichain.getSymbol
import io.goldstone.blinnnk.crypto.utils.formatCount
import io.goldstone.blinnnk.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
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
				if (it.hasError) {
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
					if (SharedWallet.getCurrentLanguageCode() == 0) it.addressInfo.scaleTo(32)
					else it.addressInfo.scaleTo(26)
			}

			count?.apply {
				title.text =
					(if (it.isReceived) "+" else "-") +
					if (it.isFee) it.minerFee.substringBefore(" ").toDouble().formatCount() else it.count.formatCount()
				subtitle.text = if (it.isFee) {
					it.contract.getSymbol().symbol
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