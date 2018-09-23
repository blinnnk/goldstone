package io.goldstone.blockchain.module.home.wallet.walletdetail.presenter

import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.FixTextLength
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.component.overlay.ContentScrollOverlayView
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.passcode.view.PasscodeFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.presenter.TokenDetailOverlayPresenter
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.findIsItExist
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
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

	var lockGettingChainModelsThread = false

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		updateData()
		updateUnreadCount()
		fragment.getMainActivity()?.backEvent = null
	}

	override fun updateData() {
		fragment.showMiniLoadingView()
		// 先初始化空数组再更新列表
		if (fragment.asyncData.isNull()) {
			fragment.asyncData = arrayListOf()
			fragment.updateHeaderValue()
		}
		// Check the info of wallet currency list
		WalletDetailCellModel.apply {
			// 显示本地的 `Token` 据
			getLocalModels { models, myTokens ->
				updateUIByData(models)
				// 这个页面检查的比较频繁所以在这里通过 `Boolean` 对线程的开启状态标记
				if (!lockGettingChainModelsThread) {
					// 再检查链上的最新价格和数量
					lockGettingChainModelsThread = getChainModels(myTokens) { model, error ->
						lockGettingChainModelsThread = false
						if (error.isNone() && !model.isNull()) {
							updateUIByData(model!!)
							fragment.removeMiniLoadingView()
						} else fragment.context.alert(error.message)
					}
				} else fragment.removeMiniLoadingView()
			}
		}
	}

	fun setQuickTransferEvent(isShowAddress: Boolean) {
		// Check current wallet is watch only or not
		WalletTable.checkIsWatchOnlyAndHasBackupOrElse(
			fragment.context!!,
			{
				// Click Dialog Confirm Button Event
				TokenDetailOverlayPresenter.showMnemonicBackupFragment(fragment)
			}
		) {
			MyTokenTable.getCurrentChainDefaultAndMyTokens { myTokens, defaultTokens ->
				// Jump directly if there is only one type of token
				if (myTokens.size == 1) defaultTokens.find {
					it.contract.equals(myTokens.first().contract, true)
				}?.let {
					isShowAddress isTrue {
						TokenSelectionRecyclerView.showTransferAddressFragment(fragment.context, it)
					} otherwise {
						TokenSelectionRecyclerView.showDepositFragment(fragment.context, it)
					}
				} else fragment.showSelectionListOverlayView(
					myTokens,
					defaultTokens,
					isShowAddress
				)
			}
		}
	}

	/**
	 * 每次后台到前台更新首页的 `token` 信息, 除了第一次初始化加载的时候
	 */
	override fun onFragmentResume() {
		if (!fragment.asyncData.isNullOrEmpty()) {
			updateData()
		}
		showPinCodeFragment()
		updateUnreadCount()
		fragment.getMainActivity()?.backEvent = null
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
				) {
					putString(
						ArgumentKey.walletSettingsTitle, WalletSettingsText.walletSettings
					)
				}
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

	fun updateUnreadCount() {
		doAsync {
			AppConfigTable.getAppConfig { config ->
				NotificationTable.getAllNotifications { notifications ->
					config?.apply {
						/**
						 * 时间戳, 如果本地一条通知记录都没有, 那么传入设备创建的时间, 就是 `GoldStone ID` 的最后 `13` 位
						 * 如果本地有数据获取最后一条的创建时间作为请求时间
						 */
						val time = if (notifications.isEmpty()) goldStoneID.substring(
							goldStoneID.length - System.currentTimeMillis().toString().length, goldStoneID.length
						).toLong()
						else notifications.maxBy { it.createTime }?.createTime.orElse(0)
						GoldStoneAPI.getUnreadCount(goldStoneID, time) { unreadCount ->
							uiThread {
								if (unreadCount.isNotEmpty() && unreadCount.toIntOrZero() > 0) {
									fragment.setNotificationUnreadCount(unreadCount)
								} else {
									fragment.recoveryNotifyButtonStyle()
								}
							}
						}
					}
				}
			}
		}
	}

	private fun WalletDetailFragment.showSelectionListOverlayView(
		myTokens: List<MyTokenTable>,
		defaultTokens: List<DefaultTokenTable>,
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
						defaultTokens.filter { default ->
							myTokens.any { it.contract.equals(default.contract, true) }
						}.let { it ->
							val data = it.sortedByDescending { it.weight }.toArrayList()
							val tokenList = TokenSelectionRecyclerView(context)
							tokenList.into(this)
							tokenList.setAdapter(data, isShowAddress)
						}
					}
				}
				// 重置回退栈首先关闭悬浮层
				recoveryBackEvent()
			}
		}
	}

	private fun updateUIByData(data: List<WalletDetailCellModel>) {
		if (data.isNotEmpty()) {
			load {
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
			}
		} else {
			diffAndUpdateAdapterData<WalletDetailAdapter>(arrayListOf())
			fragment.updateHeaderValue()
		}
	}

	private fun showPinCodeFragment() {
		fragment.activity?.supportFragmentManager?.findFragmentByTag(
			FragmentTag.pinCode
		).isNull() isTrue {
			AppConfigTable.getAppConfig {
				it?.showPincode?.isTrue {
					fragment.activity?.addFragmentAndSetArguments<PasscodeFragment>(
						ContainerID.main,
						FragmentTag.pinCode
					)
				}
			}
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

	private fun generateHeaderModel(
		hold: (WalletDetailHeaderModel) -> Unit
	) {
		val totalBalance = fragment.asyncData?.sumByDouble { it.currency }
		// Once the calculation is finished then update `WalletTable`
		Config.updateCurrentBalance(totalBalance.orElse(0.0))
		WalletTable.getCurrentWallet {
			val subtitle = getAddressDescription()
			WalletDetailHeaderModel(
				null,
				Config.getCurrentName(),
				if (subtitle.equals(WalletText.multiChainWallet, true)) {
					object : FixTextLength() {
						override var text = WalletText.multiChainWallet
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


