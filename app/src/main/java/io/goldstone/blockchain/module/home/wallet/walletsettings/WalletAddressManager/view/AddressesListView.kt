package io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.GraySqualCellWithButtons
import io.goldstone.blockchain.common.component.TopBottomLineCell
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.verticalLayout

@SuppressLint("ViewConstructor")
/**
 * @date 2018/7/11 12:46 AM
 * @author KaySaith
 */
class AddressesListView(
	context: Context,
	private val hold: (cell: ImageView, address: String) -> Unit
) : TopBottomLineCell(context) {
	
	private val cellLayout = verticalLayout {
		lparams(matchParent, matchParent)
	}
	private val maxCount = 5
	var checkAllEvent: Runnable? = null
	var model: List<Pair<String, String>>? by observing(null) {
		cellLayout.removeAllViewsInLayout()
		model?.apply {
			val limitCount = if (model?.size.orZero() > maxCount) maxCount else model?.size.orZero()
			layoutParams.height = limitCount * 50.uiPX() + 50.uiPX()
			requestLayout()
			WalletTable.getCurrentWallet {
				val currentAddresses =
					if (!it.isNull()) listOf(
						it!!.currentBTCAddress,
						it.currentBTCTestAddress,
						it.currentETCAddress,
						it.currentETHAndERCAddress
					) else listOf()
				reversed().forEachIndexed { index, data ->
					// 默认最多显示 `5` 条地址
					if (index >= maxCount) return@forEachIndexed
					GraySqualCellWithButtons(context).apply cell@{
						// 如果列表中有默认地址那么更改样式
						if (currentAddresses.any { it.equals(data.first, true) })
							updateBackgroundColor()
						copyButton.onClick {
							context.clickToCopy(data.first)
							copyButton.preventDuplicateClicks()
						}
						hold(moreButton, data.first)
						setTitle("${data.second}.")
						setSubtitle(CryptoUtils.scaleMiddleAddress(data.first))
					}.into(cellLayout)
				}
			}
			updateButtonTitle("Check All (${model?.size})")
		}
	}
	
	init {
		showTopLine = true
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 0)
		showButton("Check All Addresses") {
			checkAllEvent?.run()
		}
		cellLayout.setAlignParentBottom()
	}
}