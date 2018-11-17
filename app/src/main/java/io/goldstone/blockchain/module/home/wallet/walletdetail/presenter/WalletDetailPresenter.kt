package io.goldstone.blockchain.module.home.wallet.walletdetail.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.isNull
import com.blinnnk.extension.toArrayList
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.ConcurrentJobs
import com.blinnnk.util.FixTextLength
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.crypto.multichain.getAddress
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.MyTokenWithDefaultTable
import io.goldstone.blockchain.module.home.wallet.walletdetail.contract.WalletDetailContract
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailHeaderModel
import kotlinx.coroutines.Dispatchers

/**
 * @date 23/03/2018 3:45 PM
 * @author KaySaith
 */
class WalletDetailPresenter(
	private val detailView: WalletDetailContract.GSView
) : WalletDetailContract.GSPresenter {

	private var lockGettingChainModelsThread = false

	override fun start() {
		detailView.showLoading(true)
		updateData()
		if (NetworkUtil.hasNetwork(GoldStoneAPI.context)) {
			updateUnreadCount()
		}
	}

	private fun updateData() {
		// 先初始化空数组再更新列表
		if (detailView.asyncData.isNull()) {
			detailView.asyncData = arrayListOf()
			generateHeaderModel {
				detailView.setHeaderData(it)
			}
		}
		// 显示本地的 `Token` 据
		MyTokenWithDefaultTable.getMyDefaultTokens { models ->
			launchUI {
				updateUIByData(models)
				// 这个页面检查的比较频繁所以在这里通过 `Boolean` 对线程的开启状态标记
				if (!lockGettingChainModelsThread) {
					// 再检查链上的最新价格和数量
					lockGettingChainModelsThread = true
					models.getChainModels { chainModels, error ->
						detailView.showLoading(false)
						lockGettingChainModelsThread = false
						updateUIByData(chainModels)
						if (error.hasError()) {
							if (error.isChainError()) detailView.showChainError()
							else detailView.showError(error)
						}
					}
				} else detailView.showLoading(false)
			}
		}
	}

	// Check current wallet is watch only or not
	// Jump directly if there is only one type of token
	// Or Show Dialog Alert
	override fun showTransferDashboard(isAddress: Boolean) {
		if (SharedWallet.isWatchOnlyWallet()) detailView.showError(Throwable(AlertText.watchOnly))
		else WalletTable.getCurrent(Dispatchers.Main) {
			if (hasBackUpMnemonic) MyTokenWithDefaultTable.getMyDefaultTokens {
				launchUI {
					if (it.size == 1) {
						if (isAddress) detailView.showAddressSelectionFragment(it.first())
						else detailView.showDepositFragment(it.first())
					} else detailView.showSelectionDashboard(it, isAddress)
				}
			} else detailView.showMnemonicBackUpDialog()
		}
	}

	/**
	 * 时间戳, 如果本地一条通知记录都没有, 那么传入设备创建的时间,
	 * 就是 `GoldStone ID` 的最后 `13` 位 如果本地有数据获取最后一条
	 * 的创建时间作为请求时间
	 */
	private fun updateUnreadCount() {
		val goldStoneID = SharedWallet.getGoldStoneID()
		NotificationTable.getAllNotifications { notifications ->
			val time =
				if (notifications.isEmpty()) goldStoneID.substring(
					goldStoneID.length - System.currentTimeMillis().toString().length,
					goldStoneID.length
				).toLong()
				else notifications.maxBy { it.createTime }?.createTime ?: 0L
			GoldStoneAPI.getUnreadCount(goldStoneID, time) { unreadCount, error ->
				if (error.isNone() && unreadCount != null) launchUI {
					detailView.setUnreadCount(unreadCount)
				} else detailView.showError(error)
			}
		}
	}

	private fun List<WalletDetailCellModel>.getChainModels(
		@UiThread hold: (data: List<WalletDetailCellModel>, error: GoldStoneError) -> Unit
	) {
		var balanceError = GoldStoneError.None
		// 没有网络直接返回
		if (!NetworkUtil.hasNetwork(GoldStoneAPI.context)) hold(this, GoldStoneError.None)
		else {
			object : ConcurrentJobs() {
				override var asyncCount: Int = size
				override fun doChildJob(index: Int) {
					// 链上查余额
					val ownerName = get(index).contract.getAddress(true)
					MyTokenTable.getBalanceByContract(get(index).contract, ownerName) { balance, error ->
						// 更新数据的余额信息
						if (balance.isNotNull() && error.isNone()) {
							MyTokenTable.dao.updateBalanceByContract(
								balance,
								get(index).contract.contract,
								get(index).contract.symbol,
								ownerName
							)
							get(index).count = balance
						} else balanceError = error
						completeMark()
					}
				}

				override fun mergeCallBack() {
					launchUI {
						hold(this@getChainModels, balanceError)
					}
				}
			}.start()
		}
	}

	private fun updateUIByData(data: List<WalletDetailCellModel>) {
		if (data.isNotEmpty()) load {
			/** 先按照资产情况排序, 资产为零的按照权重排序 */
			val hasPrice =
				data.asSequence().filter { it.price * it.count != 0.0 }
					.sortedByDescending { it.count * it.price }.toList()
			val hasBalance =
				data.asSequence().filter { it.count != 0.0 && it.price == 0.0 }
					.sortedByDescending { it.count }.toList()
			val others =
				data.asSequence().filter { it.count == 0.0 }
					.sortedByDescending { it.weight }.toList()
			hasPrice.asSequence().plus(hasBalance).plus(others).toList().toArrayList()
		} then { listData ->
			detailView.updateAdapterData(listData)
			generateHeaderModel {
				detailView.setHeaderData(it)
			}
		} else {
			detailView.updateAdapterData(arrayListOf())
			generateHeaderModel {
				detailView.setHeaderData(it)
			}
		}
	}

	private fun generateHeaderModel(@UiThread hold: (WalletDetailHeaderModel) -> Unit) {
		val totalBalance = detailView.asyncData?.sumByDouble {
			if (it.currency == 0.0) it.price * it.count else it.currency
		}
		// Once the calculation is finished then update `WalletTable`
		if (totalBalance != null) SharedWallet.updateCurrentBalance(totalBalance)
		WalletTable.getCurrent(Dispatchers.Main) {
			val subtitle = getAddressDescription()
			WalletDetailHeaderModel(
				null,
				SharedWallet.getCurrentName(),
				if (
					subtitle.equals(WalletText.multiChainWallet, true) ||
					subtitle.equals(WalletText.bip44MultiChain, true)
				) {
					object : FixTextLength() {
						override var text = subtitle
						override val maxWidth = 90.uiPX().toFloat()
						override val textSize: Float = 12.uiPX().toFloat()
					}.getFixString()
				} else CryptoUtils.scaleMiddleAddress(subtitle, 5),
				balance.toString()
			).let(hold)
		}
	}
}


