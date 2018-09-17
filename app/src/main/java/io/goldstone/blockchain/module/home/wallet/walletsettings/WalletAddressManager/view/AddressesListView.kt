package io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.cell.GraySquareCellWithButtons
import io.goldstone.blockchain.common.component.cell.GraySquareCellWithButtons.Companion
import io.goldstone.blockchain.common.component.cell.TopBottomLineCell
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.crypto.utils.CryptoUtils
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
		isDefault: Boolean
	) -> Unit
) : TopBottomLineCell(context) {

	private val cellLayout = verticalLayout {
		lparams(matchParent, matchParent)
	}
	var checkAllEvent: Runnable? = null
	var currentAddresses: List<String>? = null
	var model: List<Pair<String, String>>? by observing(null) {
		cellLayout.removeAllViewsInLayout()
		model?.apply {
			// 如果是当前使用的多链那么 　`data.second`` 会是对应的链的缩写用此判断做缩进
			if (isNotEmpty() && firstOrNull()?.second?.toIntOrNull().isNull()) hideButton()
			else updateButtonTitle("${CommonText.checkAll} (${model?.size})")
			// 计算最大显示个数
			val limitCount = if (model?.size.orZero() > maxCount) maxCount else model?.size.orZero()
			// 动态计算 `View` 的总高
			layoutParams.height = limitCount * 50.uiPX() + 60.uiPX()
			requestLayout()
			reversed().forEachIndexed { index, data ->
				var isDefault = false
				// 默认最多显示 `4` 条地址
				if (index >= maxCount) return@forEachIndexed
				GraySquareCellWithButtons(context).apply cell@{
					// 如果列表中有默认地址那么更改样式
					if (currentAddresses?.any { it.equals(data.first, true) } == true) {
						isDefault = true
						updateStyle(Companion.CellType.Default)
					} else updateStyle(Companion.CellType.Normal)

					copyButton.onClick {
						context.clickToCopy(data.first)
						copyButton.preventDuplicateClicks()
					}
					hold(this, isDefault)
					setTitle("${data.second}.")
					setSubtitle(CryptoUtils.scaleMiddleAddress(data.first, 12))
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