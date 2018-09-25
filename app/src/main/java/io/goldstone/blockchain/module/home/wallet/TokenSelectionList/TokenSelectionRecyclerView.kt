package io.goldstone.blockchain.module.home.wallet.tokenselectionlist

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.component.overlay.ContentScrollOverlayView
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.home.view.findIsItExist
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.wrapContent

/**
 * @date 2018/6/7 3:29 AM
 * @author KaySaith
 */
class TokenSelectionRecyclerView(context: Context) : BaseRecyclerView(context) {

	init {
		backgroundColor = Color.RED
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
	}

	fun setAdapter(data: ArrayList<WalletDetailCellModel>, isShowAddressList: Boolean = true) {
		adapter = TokenSelectionAdapter(data) {
			model?.let { token ->
				onClick {
					if (isShowAddressList)
						showTransferAddressFragment(this@TokenSelectionRecyclerView.context, token)
					else showDepositFragment(this@TokenSelectionRecyclerView.context, token)
					preventDuplicateClicks()
				}
			}
		}
	}

	companion object {

		fun showTransferAddressFragment(context: Context?, token: WalletDetailCellModel) {
			// 显示 `ContentOverlay`
			(context as? MainActivity)?.showTokenDetailOverlayFragment(token) {
				putBoolean(ArgumentKey.fromQuickTransfer, true)
			}
		}

		fun showDepositFragment(context: Context?, token: WalletDetailCellModel) {
			// 显示 `ContentOverlay`
			(context as? MainActivity)?.showTokenDetailOverlayFragment(token) {
				putBoolean(ArgumentKey.fromQuickTransfer, true)
			}
		}


		private fun MainActivity.showTokenDetailOverlayFragment(
			model: WalletDetailCellModel,
			putArgument: Bundle.() -> Unit
		) {
			getMainContainer()?.apply {
				findViewById<ContentScrollOverlayView>(ElementID.contentScrollview)?.let {
					removeView(it)
				}
			}
			findIsItExist(FragmentTag.tokenDetail) isFalse {
				addFragmentAndSetArguments<TokenDetailOverlayFragment>(
					ContainerID.main,
					FragmentTag.tokenDetail
				) {
					putSerializable(ArgumentKey.tokenDetail, model)
					putArgument(this)
				}
			}
		}
	}
}