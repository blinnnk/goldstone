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
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.WalletSettingsText
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
	
	private val cellLayout = verticalLayout {
		lparams(matchParent, matchParent)
	}
	var checkAllEvent: Runnable? = null
	var model: List<String>? by observing(null) {
		cellLayout.removeAllViewsInLayout()
		model?.apply {
			layoutParams.height = size * 50.uiPX() + 50.uiPX()
			requestLayout()
			forEachIndexed { index, address ->
				GraySqualCellWithButtons(context).apply cell@{
					copyButton.onClick {
						context.clickToCopy(address)
						copyButton.preventDuplicateClicks()
					}
					hold(moreButton)
					setTitle("$index.")
					setSubtitle(CryptoUtils.scaleMiddleAddress(address))
				}.into(cellLayout)
				// 默认最多显示 `5` 条地址
				if (index >= 5) return@forEachIndexed
			}
			updateButtonTitle("Check All (${model?.size})")
		}
	}
	
	init {
		showTopLine = true
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 0)
		setTitle(WalletSettingsText.ethereumSeriesAddress)
		showButton("Check All Addresses") {
			checkAllEvent?.run()
		}
		cellLayout.setAlignParentBottom()
	}
}