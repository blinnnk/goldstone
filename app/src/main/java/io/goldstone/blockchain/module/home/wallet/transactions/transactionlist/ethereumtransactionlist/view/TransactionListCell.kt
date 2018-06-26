package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.view

import android.annotation.SuppressLint
import android.content.Context
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baseInfocell.BaseValueCell
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
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
					src = R.drawable.error_icon
					iconColor = Spectrum.red
					count?.title?.textColor = Spectrum.lightRed
				} else {
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
				title.text = CryptoUtils.scaleTo22(it.addressName)
				subtitle.text = it.addressInfo
			}
			
			count?.apply {
				title.text = (if (it.isReceived) "+" else "-") + it.count.formatCount()
				subtitle.text = it.symbol
			}
		}
	}
	
	init {
		layoutParams.height = 65.uiPX()
		setGrayStyle()
		setValueStyle(true)
	}
}