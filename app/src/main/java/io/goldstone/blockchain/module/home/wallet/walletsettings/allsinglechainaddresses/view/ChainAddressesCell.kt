package io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.cell.GraySquareCellWithButtons
import io.goldstone.blockchain.common.component.cell.GraySquareCellWithButtons.Companion
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.Bip44Address
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * @date 2018/7/16 8:28 PM
 * @author KaySaith
 */
class ChainAddressesCell(context: Context) : LinearLayout(context) {

	var model: Bip44Address? by observing(null) {
		cell.setTitle("${model?.index}.")
		cell.setSubtitle(CryptoUtils.scaleMiddleAddress(model?.address.orEmpty()))
	}
	val cell = GraySquareCellWithButtons(context)

	init {
		orientation = VERTICAL
		gravity = Gravity.CENTER_HORIZONTAL
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
		cell.into(this)
		cell.updateStyle(Companion.CellType.Normal)
	}
}