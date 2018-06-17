package io.goldstone.blockchain.module.home.wallet.walletdetail.view

import android.content.Context
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.widget.LinearLayout
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.keyboardHeightListener
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.matchParent

/**
 * @date 23/03/2018 4:16 PM
 * @author KaySaith
 */
class WalletDetailAdapter(
	override val dataSet: ArrayList<WalletDetailCellModel>,
	private val holdCell: WalletDetailCell.() -> Unit,
	private val holdHeader: WalletDetailHeaderView.() -> Unit
) :
	HoneyBaseAdapterWithHeaderAndFooter<WalletDetailCellModel, WalletDetailHeaderView, WalletDetailCell, LinearLayout>() {
	override fun generateCell(context: Context) =
		WalletDetailCell(context)

	private var hasHiddenSoftNavigationBar = false
	override fun generateFooter(context: Context) =
		LinearLayout(context).apply {
			val barHeight =
				if (!hasHiddenSoftNavigationBar && !KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)) {
					60.uiPX()
				} else 10.uiPX()
			layoutParams = LinearLayout.LayoutParams(matchParent, barHeight)
		}

	override fun generateHeader(context: Context) =
		WalletDetailHeaderView(context).apply {
			/**
			 * 判断不同手机的不同 `Navigation` 的状态决定 `Footer` 的补贴高度
			 * 主要是, `Samsung S8, S9` 的 `Navigation` 状态判断
			 */
			keyboardHeightListener {
				if (it < 0) {
					hasHiddenSoftNavigationBar = true
				}
			}
			holdHeader(this)
		}

	override fun WalletDetailCell.bindCell(
		data: WalletDetailCellModel,
		position: Int
	) {
		model = data
		holdCell(this)
	}

}