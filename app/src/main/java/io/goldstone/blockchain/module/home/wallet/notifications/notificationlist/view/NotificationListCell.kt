package io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view

import android.content.Context
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.HoneyDateUtil
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basecell.BaseValueCell
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.DateAndTimeText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.toMillisecond
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationType
import org.jetbrains.anko.textColor

/**
 * @date 25/03/2018 1:49 AM
 * @author KaySaith
 */
class NotificationListCell(context: Context) : BaseValueCell(context) {

	var model: NotificationTable? by observing(null) {
		info.apply {
			title.text = model?.title?.scaleTo(16)
		}
		date.text =
			HoneyDateUtil.getSinceTime(
				model?.createTime?.toMillisecond().orElse(0),
				DateAndTimeText.getDateText()
			)
		WalletTable.getAll {
			when (model?.type) {
				NotificationType.Transaction.code -> {
					if (NotificationTable.getReceiveStatus(model?.extra.orEmpty()).orFalse()) {
						setIconColor(Spectrum.green)
						setIconResource(R.drawable.receive_icon)
						info.subtitle.text = (CommonText.from + " " + model?.content.orEmpty()).scaleTo(22)
					} else {
						setIconColor(GrayScale.midGray)
						setIconResource(R.drawable.send_icon)
						info.subtitle.text = (CommonText.to + " " + model?.content.orEmpty()).scaleTo(22)
					}
				}

				NotificationType.System.code -> {
					setIconColor(Spectrum.green)
					setIconResource(R.drawable.system_notification_icon)
					info.subtitle.text = model?.content?.scaleTo(22)
				}
			}
		}
	}
	private val date by lazy { TextView(context) }

	init {
		date.apply {
			textColor = GrayScale.midGray
			textSize = fontSize(10)
			typeface = GoldStoneFont.book(context)
			x -= 30.uiPX()
		}.into(this)
		date.apply {
			setCenterInVertical()
			setAlignParentRight()
		}
		setGrayStyle()
	}
}