package io.goldstone.blockchain.module.home.wallet.walletdetail.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.FixTextLength
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.component.overlay.ContentScrollOverlayView
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.crypto.multichain.getAddress
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.presenter.TokenDetailOverlayPresenter
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.findIsItExist
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.MyTokenWithDefaultTable
import io.goldstone.blockchain.module.home.wallet.tokenselectionlist.TokenSelectionRecyclerView
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailHeaderModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailAdapter
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailHeaderView
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.topPadding
import org.jetbrains.anko.uiThread

/**
 * @date 23/03/2018 3:45 PM
 * @author KaySaith
 */
class WalletDetailPresenter(
	override val fragment: WalletDetailFragment
) : BaseRecyclerPresenter<WalletDetailFragment, WalletDetailCellModel>() {

	private var lockGettingChainModelsThread = false
	// 把这个数据存在内存里面一份, 在打开快捷面板的时候可以复用这个数据
	private var detailModels: List<WalletDetailCellModel>? = null

	override fun updateData() {
		fragment.showMiniLoadingView()
		// 先初始化空数组再更新列表
		if (fragment.asyncData.isNull()) {
			fragment.asyncData = arrayListOf()
			fragment.updateHeaderValue()
		}
		// 显示本地的 `Token` 据
		MyTokenWithDefaultTable.getMyDefaultTokens(true) { models ->
			// 把数据存在内存里面
			detailModels = models
			updateUIByData(models)
			// 这个页面检查的比较频繁所以在这里通过 `Boolean` 对线程的开启状态标记
			if (!lockGettingChainModelsThread) {
				// 再检查链上的最新价格和数量
				fragment.removeMiniLoadingView()
				models.getChainModels { chainModels, error ->
					// 更新内存的数据
					detailModels = chainModels
					updateUIByData(chainModels)
					if (!error.isNone()) fragment.context.alert(error.message)
				}
			} else fragment.removeMiniLoadingView()
		}
	}

	fun setQuickTransferEvent(isShowAddress: Boolean) {
		// Check current wallet is watch only or not
		WalletTable.isAvailableWallet(
			fragment.context!!,
			// Click Dialog Confirm Button Event
			{ TokenDetailOverlayPresenter.showMnemonicBackupFragment(fragment) }
		) {

			fun getQuickDashboardData(data: List<WalletDetailCellModel>) {
				// Jump directly if there is only one type of token
				if (data.size == 1) {
					if (isShowAddress)
						TokenSelectionRecyclerView.showTransferAddressFragment(fragment.context, data.first())
					else TokenSelectionRecyclerView.showDepositFragment(fragment.context, data.first())
				} else fragment.showSelectionListOverlayView(data, isShowAddress)
			}

			if (detailModels.isNull())
				MyTokenWithDefaultTable.getMyDefaultTokens(true) {
					getQuickDashboardData(it)
				} else getQuickDashboardData(detailModels!!)
		}
	}

	fun showNotificationListFragment() {
		XinGePushReceiver.clearAppIconReDot()
		fragment.activity?.apply {
			findIsItExist(FragmentTag.notification) isFalse {
				addFragment<NotificationFragment>(ContainerID.main, FragmentTag.notification)
			}
		}
	}

	fun showTokenManagementFragment() {
		fragment.activity?.apply {
			findIsItExist(FragmentTag.tokenManagement) isFalse {
				addFragment<TokenManagementFragment>(ContainerID.main, FragmentTag.tokenManagement)
			}
		}
	}

	fun showWalletSettingsFragment() {
		fragment.activity?.apply {
			findIsItExist(FragmentTag.walletSettings) isFalse {
				addFragmentAndSetArguments<WalletSettingsFragment>(
					ContainerID.main, FragmentTag.walletSettings
				) { putString(ArgumentKey.walletSettingsTitle, WalletSettingsText.walletSettings) }
			}
		}
	}

	fun showMyTokenDetailFragment(model: WalletDetailCellModel) {
		fragment.activity?.apply {
			findIsItExist(FragmentTag.tokenDetail) isFalse {
				addFragmentAndSetArguments<TokenDetailOverlayFragment>(ContainerID.main, FragmentTag.tokenDetail) {
					putSerializable(ArgumentKey.tokenDetail, model)
				}
			}
		}
	}

	fun updateUnreadCount(@UiThread hold: (unreadCount: Int?, error: GoldStoneError) -> Unit) {
		if (!NetworkUtil.hasNetwork()) return
		doAsync {
			val goldStoneID = SharedWallet.getGoldStoneID()
			NotificationTable.getAllNotifications { notifications ->
				/**
				 * 时间戳, 如果本地一条通知记录都没有, 那么传入设备创建的时间, 就是 `GoldStone ID` 的最后 `13` 位
				 * 如果本地有数据获取最后一条的创建时间作为请求时间
				 */
				val time = if (notifications.isEmpty()) goldStoneID.substring(
					goldStoneID.length - System.currentTimeMillis().toString().length, goldStoneID.length
				).toLong()
				else notifications.maxBy { it.createTime }?.createTime.orElse(0)
				GoldStoneAPI.getUnreadCount(
					goldStoneID,
					time,
					{ hold(null, it) }
				) { unreadCount ->
					uiThread {
						hold(unreadCount.toIntOrNull().orZero(), GoldStoneError.None)
					}
				}

			}
		}
	}

	private fun List<WalletDetailCellModel>.getChainModels(hold: (List<WalletDetailCellModel>, GoldStoneError) -> Unit) {
		var balanceError = GoldStoneError.None
		// 没有网络直接返回
		if (!NetworkUtil.hasNetwork(GoldStoneAPI.context)) hold(this, GoldStoneError.None)
		else {
			lockGettingChainModelsThread = true
			object : ConcurrentAsyncCombine() {
				override var asyncCount: Int = size
				override fun concurrentJobs() {
					forEach { model ->
						// 链上查余额
						val ownerName = model.contract.getAddress(true)
						MyTokenTable.getBalanceByContract(
							model.contract,
							ownerName
						) { balance, error ->
							// 更新数据的余额信息
							if (!balance.isNull() && error.isNone()) {
								MyTokenTable.updateBalanceByContract(
									balance!!,
									ownerName,
									model.contract
								)
								model.count = balance
							} else balanceError = error
							completeMark()
						}
					}
				}

				override fun mergeCallBack() = hold(this@getChainModels, balanceError)

			}.start()
		}
	}

	private fun WalletDetailFragment.showSelectionListOverlayView(
		tokens: List<WalletDetailCellModel>,
		isShowAddress: Boolean
	) {
		// Prepare token list and show content scroll overlay view
		getMainActivity()?.getMainContainer()?.apply {
			if (findViewById<ContentScrollOverlayView>(ElementID.contentScrollview).isNull()) {
				val overlay = ContentScrollOverlayView(context)
				overlay.into(this)
				overlay.apply {
					setTitle(TransactionText.tokenSelection)
					addContent {
						topPadding = 10.uiPX()
						val data =
							tokens.sortedByDescending { it.weight }.toArrayList()
						val tokenList = TokenSelectionRecyclerView(context)
						tokenList.into(this)
						tokenList.setAdapter(data, isShowAddress)
					}
				}
				// 重置回退栈首先关闭悬浮层
				recoveryBackEvent()
			}
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
		} then {
			diffAndUpdateAdapterData<WalletDetailAdapter>(it)
			fragment.updateHeaderValue()
		} else {
			diffAndUpdateAdapterData<WalletDetailAdapter>(arrayListOf())
			fragment.updateHeaderValue()
		}
	}

	private fun WalletDetailFragment.updateHeaderValue() {
		try {
			recyclerView.getItemAtAdapterPosition<WalletDetailHeaderView>(0) {
				generateHeaderModel { model ->
					it.model = model
				}
			}
		} catch (error: Exception) {
			LogUtil.error("WalletDetail updateHeaderValue", error)
		}
	}

	private fun generateHeaderModel(hold: (WalletDetailHeaderModel) -> Unit) {
		var totalBalance = fragment.asyncData?.sumByDouble { it.currency }
		// Once the calculation is finished then update `WalletTable`
		if (!totalBalance.isNull()) SharedWallet.updateCurrentBalance(totalBalance!!)
		else totalBalance = SharedWallet.getCurrentBalance()
		WalletTable.getCurrentWallet {
			val subtitle = getAddressDescription()
			WalletDetailHeaderModel(
				null,
				SharedWallet.getCurrentName(),
				if (subtitle.equals(WalletText.multiChainWallet, true) || subtitle.equals(WalletText.bip44MultiChain, true)) {
					object : FixTextLength() {
						override var text = subtitle
						override val maxWidth = 90.uiPX().toFloat()
						override val textSize: Float = 12.uiPX().toFloat()
					}.getFixString()
				} else {
					CryptoUtils.scaleMiddleAddress(subtitle, 5)
				},
				totalBalance.toString()
			).let(hold)
		}
	}
}


