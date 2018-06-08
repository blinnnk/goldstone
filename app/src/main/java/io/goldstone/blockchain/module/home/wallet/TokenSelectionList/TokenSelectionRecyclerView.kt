package io.goldstone.blockchain.module.home.wallet.tokenselectionlist

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.orElse
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.component.ContentScrollOverlayView
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.crypto.formatCount
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.home.view.findIsItExist
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
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
	
	fun setAdapter(data: ArrayList<DefaultTokenTable>, isShowAddressList: Boolean = true) {
		adapter = TokenSelectionAdapter(data) {
			model?.let { token ->
				onClick {
					if (isShowAddressList) {
						showTransferAddressFragment(token)
					} else {
						showDepositFragment(token)
					}
					preventDuplicateClicks()
				}
			}
		}
	}
	
	private fun showDepositFragment(token: DefaultTokenTable) {
		prepareContentOverlay(token) {
			putBoolean(ArgumentKey.fromQuickDeposit, true)
		}
	}
	
	private fun showTransferAddressFragment(token: DefaultTokenTable) {
		prepareContentOverlay(token) {
			putBoolean(ArgumentKey.fromQuickTransfer, true)
		}
	}
	
	private fun prepareContentOverlay(
		token: DefaultTokenTable,
		putArgument: Bundle.() -> Unit
	) {
		MyTokenTable.getCurrentChainTokenBalanceByContract(token.contract) {
			// 准备数据
			val model = WalletDetailCellModel(token, it.orElse(0.0), true)
			// 显示 `ContentOverlay`
			(context as? MainActivity)?.apply {
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
}