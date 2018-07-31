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
	private val hold: (
		moreButton: ImageView,
		address: String,
		isDefault: Boolean,
		title: String
	) -> Unit
) : TopBottomLineCell(context) {
	
	private val cellLayout = verticalLayout {
		lparams(matchParent, matchParent)
	}

	private val maxCount = 4
	var checkAllEvent: Runnable? = null
	var model: List<Pair<String, String>>? by observing(null) {
		cellLayout.removeAllViewsInLayout()
		model?.apply {
			var halfSize = 13
			// 如果是当前使用的多链那么 　`data.second`` 会是对应的链的缩写用此判断做缩进
			if (this[0].second.toIntOrNull().isNull()) {
				halfSize = 11
				hideButton()
			} else {
				updateButtonTitle("Check All (${model?.size})")
			}
			// 最多只显示 `4` 个链下地址
			val limitCount = if (model?.size.orZero() > maxCount) maxCount else model?.size.orZero()
			layoutParams.height = limitCount * 50.uiPX() + 50.uiPX()
			requestLayout()
			WalletTable.getCurrentAddresses { currentAddresses ->
				reversed().forEachIndexed { index, data ->
					var isDefault = false
					// 默认最多显示 `4` 条地址
					if (index >= maxCount) return@forEachIndexed
					GraySqualCellWithButtons(context).apply cell@{
						// 如果列表中有默认地址那么更改样式
						if (currentAddresses.any { it.equals(data.first, true) }) {
							isDefault = true
							updateBackgroundColor()
						}
						
						copyButton.onClick {
							context.clickToCopy(data.first)
							copyButton.preventDuplicateClicks()
						}
						hold(moreButton, data.first, isDefault, data.second)
						setTitle("${data.second}.")
						
						setSubtitle(CryptoUtils.scaleMiddleAddress(data.first, halfSize))
					}.into(cellLayout)
				}
			}
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