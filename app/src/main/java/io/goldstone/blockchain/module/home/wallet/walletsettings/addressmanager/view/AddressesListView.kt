package io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.orZero
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.cell.GraySquareCellWithButtons
import io.goldstone.blockchain.common.component.cell.GraySquareCellWithButtons.Companion
import io.goldstone.blockchain.common.component.cell.TopBottomLineCell
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.Bip44Address
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
	maxCount: Int = 4,
	private val hold: (
		cell: GraySquareCellWithButtons,
		data: Bip44Address,
		wallet: WalletTable?,
		isDefault: Boolean
	) -> Unit
) : TopBottomLineCell(context) {

	private val cellLayout = verticalLayout {
		lparams(matchParent, matchParent)
	}
	var checkAllEvent: Runnable? = null
	var currentWallet: WalletTable? = null
	var model: List<Bip44Address>? by observing(null) {
		cellLayout.removeAllViewsInLayout()
		model?.apply {
			val currentAddresses by lazy { currentWallet?.getCurrentAddresses() }
			if (isNotEmpty() && firstOrNull()?.index == -1) hideButton()
			else updateButtonTitle("${CommonText.checkAll} (${model?.size})")
			// 计算最大显示个数
			val limitCount = if (model?.size.orZero() > maxCount) maxCount else model?.size.orZero()
			// 动态计算 `View` 的总高
			layoutParams.height = limitCount * 50.uiPX() + 60.uiPX()
			requestLayout()
			val isMultiChain = distinctBy { it.chainType }.size > 1
			reversed().forEachIndexed { index, data ->
				var isDefault = false
				// 默认最多显示 `4` 条地址
				if (index >= maxCount) return@forEachIndexed
				GraySquareCellWithButtons(context).apply cell@{
					// 如果列表中有默认地址那么更改样式
					if (currentAddresses?.any { it.equals(data.address, true) } == true) {
						isDefault = true
						updateStyle(Companion.CellType.Default)
					} else updateStyle(Companion.CellType.Normal)

					copyButton.onClick {
						context.clickToCopy(data.address)
						copyButton.preventDuplicateClicks()
					}
					hold(this, data, currentWallet, isDefault)
					val title = when {
						isMultiChain -> data.getChainType().getSymbol().symbol
						data.index == -1 -> ""
						else -> "${data.index}"
					}
					setTitle(title.orEmpty())
					setSubtitle(CryptoUtils.scaleMiddleAddress(data.address, 12))
				}.into(cellLayout)
			}
		}
	}

	init {
		setHorizontalPadding(PaddingSize.device.toFloat())
		cellLayout.gravity = Gravity.CENTER_HORIZONTAL
		showTopLine = true
		layoutParams = LinearLayout.LayoutParams(matchParent, 0)
		showButton(CommonText.checkAll, PaddingSize.device) {
			checkAllEvent?.run()
		}
		cellLayout.setAlignParentBottom()
	}
}