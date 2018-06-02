package io.goldstone.blockchain.module.common.walletimport.walletimport.presenter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletaddingmethod.view.WalletAddingMethodFragment

/**
 * @date 23/03/2018 12:55 AM
 * @author KaySaith
 */
class WalletImportPresenter(
	override val fragment: WalletImportFragment
) : BaseOverlayPresenter<WalletImportFragment>() {
	
	fun onClickMenuBarItem() {
		fragment.apply {
			menuBar.clickEvent = Runnable {
				menuBar.onClickItem {
					viewPager.setSelectedStyle(id, menuBar)
					viewPager.setCurrentItem(id, true)
				}
			}
		}
	}
	
	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		recoveryBackEventInMainActivity(fragment.activity)
	}
	
	companion object {
		
		fun recoveryBackEventInMainActivity(activity: FragmentActivity?) {
			// 因为创建的 `Fragment` 高度复用导致了回退栈的耦合判断
			if (activity is MainActivity) {
				val currentFragment =
					activity.supportFragmentManager.findFragmentByTag(FragmentTag.walletManagement)
						?.childFragmentManager?.fragments?.last()
				if (currentFragment is WalletAddingMethodFragment) {
					currentFragment.recoveryBackEvent()
				}
			}
		}
		
		fun insertWalletToDatabase(
			fragment: Fragment,
			address: String,
			name: String,
			hint: String?,
			callback: () -> Unit
		) {
			WalletTable.getWalletByAddress(address) {
				it.isNull() isTrue {
					// 在数据库记录钱包信息
					WalletTable.insert(WalletTable(
						0,
						name,
						address,
						true,
						hint,
						false,
						0.0,
						null,
						true
					)) {
						// 创建钱包并获取默认的 `token` 信息
						CreateWalletPresenter.generateMyTokenInfo(address, {
							LogUtil.error("insertWalletToDatabase")
							callback()
						}) {
							fragment.activity?.jump<SplashActivity>()
						}
						// 注册钱包地址用于发送 `Push`
						XinGePushReceiver.registerWalletAddressForPush()
					}
				} otherwise {
					fragment.context?.alert(ImportWalletText.existAddress)
					callback()
				}
			}
		}
	}
	
	fun showWebViewFragment(url: String, title: String) {
		fragment.headerTitle = title
		
		hideChildFragments()
		
		fragment.addFragmentAndSetArgument<WebViewFragment>(ContainerID.content) {
			putString(ArgumentKey.webViewUrl, url)
		}
		
		fragment.overlayView.header.apply {
			showCloseButton(false)
			showBackButton(true) {
				removeWebFragment()
				showChildFragments()
				showBackButton(false)
				showCloseButton(true)
			}
		}
	}
	
	private fun hideChildFragments() {
		fragment.childFragmentManager.apply {
			fragments.forEach {
				beginTransaction().hide(it).commit()
			}
		}
	}
	
	private fun showChildFragments() {
		fragment.childFragmentManager.apply {
			fragments.forEach {
				beginTransaction().show(it).commit()
			}
		}
	}
	
	private fun removeWebFragment() {
		fragment.childFragmentManager.apply {
			fragments?.find {
				it is WebViewFragment
			}?.let {
				beginTransaction().remove(it).commit()
			}
		}
	}
}