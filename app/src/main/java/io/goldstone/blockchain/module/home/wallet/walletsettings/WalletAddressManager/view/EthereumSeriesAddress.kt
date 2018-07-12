package io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.GraySqualCellWithButtons
import io.goldstone.blockchain.common.component.TopBottomLineCell
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.verticalLayout

@SuppressLint("ViewConstructor")
/**
 * @date 2018/7/11 12:46 AM
 * @author KaySaith
 */
class EthereumSeriesAddress(
	context: Context,
	private val hold: ImageView.() -> Unit
) : TopBottomLineCell(context) {
	
	var checkAllEvent: Runnable? = null
	var model: List<String>? by observing(null) {
		model?.apply {
			layoutParams.height += size * 45.uiPX() - 25.uiPX()
			verticalLayout {
				forEachIndexed { index, address ->
					GraySqualCellWithButtons(context)
						.apply cell@{
							copyButton.onClick {
								context.clickToCopy(address)
								copyButton.preventDuplicateClicks()
							}
							hold(moreButton)
							setTitle("${index + 1}.")
							setSubtitle(CryptoUtils.scaleMiddleAddress(address))
						}
						.into(this@EthereumSeriesAddress)
				}
				y -= 10.uiPX()
			}.setAlignParentBottom()
		}
	}
	
	init {
		LinearLayout.LayoutParams(matchParent, 0)
		title.text = "Ethereum Series Address"
		showButton("Check All (8)") {
			checkAllEvent?.run()
		}
	}
}