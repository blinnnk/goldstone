package io.goldstone.blockchain.module.home.wallet.walletdetail.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.common.passcode.view.PasscodeFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailAdapter
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailHeaderModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailHeaderView
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletmanagement.view.WalletManagementFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import okhttp3.*
import java.util.concurrent.TimeUnit

/**
 * @date 23/03/2018 3:45 PM
 * @author KaySaith
 */

class WalletDetailPresenter(
	override val fragment: WalletDetailFragment
) : BaseRecyclerPresenter<WalletDetailFragment, WalletDetailCellModel>() {

	fun updateAllTokensInWalletBy() {
		// Check the count of local wallets
		WalletTable.apply { getAll { walletCount = size } }
		// Check the info of wallet currency list
		WalletDetailCellModel.getModels { it ->
			val newData = it.sortedByDescending { it.currency }.toArrayList()
			fragment.asyncData.isNull() isTrue {
				fragment.asyncData = newData
			} otherwise {
				diffAndUpdateAdapterData<WalletDetailAdapter>(newData)
			}
			fragment.updateHeaderValue()
		}
	}

	/**
	 * 每次后台到前台更新首页的 `token` 信息
	 */
	override fun onFragmentResume() {
		CreateWalletPresenter.updateMyTokensValue {
			updateAllTokensInWalletBy()
		}
		showPinCodeFragment()
	}

	private fun showPinCodeFragment() {
		fragment.activity?.supportFragmentManager?.findFragmentByTag(FragmentTag.pinCode).isNull() isTrue {
			AppConfigTable.getAppConfig {
				it?.showPincode?.isTrue {
					fragment.activity?.addFragmentAndSetArguments<PasscodeFragment>(
						ContainerID.main, FragmentTag.pinCode
					) {
						// Send Argument
					}
				}
			}
		}
	}

	fun showTransactionsFragment() {
		fragment.activity?.addFragment<TransactionFragment>(ContainerID.main)
	}

	fun showWalletListFragment() {
		fragment.activity?.addFragment<WalletManagementFragment>(ContainerID.main)
	}

	fun showNotificationListFragment() {
		fragment.activity?.addFragment<NotificationFragment>(ContainerID.main)
	}

	fun showTokenManagementFragment() {
		fragment.activity?.addFragment<TokenManagementFragment>(ContainerID.main)
	}

	fun showWalletSettingsFragment() {
		fragment.activity?.addFragmentAndSetArguments<WalletSettingsFragment>(ContainerID.main) {
			putString(ArgumentKey.walletSettingsTitle, WalletSettingsText.walletSettings)
		}
	}

	fun showMyTokenDetailFragment(model: WalletDetailCellModel) {
		fragment.activity?.addFragmentAndSetArguments<TokenDetailOverlayFragment>(ContainerID.main) {
			putSerializable(ArgumentKey.tokenDetail, model)
		}
	}

	private fun WalletDetailFragment.updateHeaderValue() {
		val totalBalance = fragment.asyncData?.sumByDouble { it.currency }
		// Once the calculation is finished then update `WalletTable`
		WalletTable.current.balance = totalBalance
		recyclerView.getItemViewAtAdapterPosition<WalletDetailHeaderView>(0) {
			model = WalletDetailHeaderModel(
				null,
				WalletTable.current.name,
				CryptoUtils.scaleAddress(WalletTable.current.address),
				totalBalance.toString(),
				WalletTable.walletCount.orZero()
			)
		}
	}
}

fun getSocket(hold: WebSocket.() -> Unit) {
	val path = "ws://118.25.40.163:8088" // "ws://118.89.147.176:8001/ws"
	//创建WebSocket链接
	val client = OkHttpClient.Builder().retryOnConnectionFailure(true) // 允许失败重试
		.connectTimeout(8, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS)
		.writeTimeout(5, TimeUnit.SECONDS).build()

	val request = Request.Builder().url(path).build()

	client.newWebSocket(request, object : WebSocketListener() {
		override fun onOpen(webSocket: WebSocket, response: Response) {
			super.onOpen(webSocket, response)
			hold(webSocket)
		}

		override fun onMessage(webSocket: WebSocket, text: String) {
			super.onMessage(webSocket, text)
			System.out.println("______ $text")
		}

		override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
			super.onClosing(webSocket, code, reason)
			webSocket.close(1000, null)
		}

		override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
			super.onClosed(webSocket!!, code, reason!!)
		}

		override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
			super.onFailure(webSocket!!, t!!, response!!)
			webSocket.close(1000, null)
		}
	})
	client.dispatcher().executorService().shutdown()
}

