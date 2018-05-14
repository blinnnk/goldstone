package io.goldstone.blockchain.module.common.walletimport.walletimport.presenter

import android.support.v4.app.Fragment
import android.util.Log
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity

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

	companion object {
		fun insertWalletToDatabase(fragment: Fragment, address: String, name: String, hint: String?, callback: () -> Unit) {
			WalletTable.getWalletByAddress(address) {
				it.isNull() isTrue {
					// 在数据库记录钱包信息
					WalletTable.insertAddress(address, name, hint) {
						// 创建钱包并获取默认的 `token` 信息
						CreateWalletPresenter.generateMyTokenInfo(address, false, {
							Log.e("ERROR", "get default token from has error")
							callback()
						}) {
							fragment.activity?.jump<SplashActivity>()
							callback()
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
}