package io.goldstone.blockchain.module.common.walletimport.walletimport.presenter

import android.support.v4.app.Fragment
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.crypto.MultiChainAddresses
import io.goldstone.blockchain.crypto.MultiChainPath
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
		
		fun childAddressValue(address: String, index: Int): String {
			return "$address|$index"
		}
		
		fun getAddressIndexFromPath(path: String): Int {
			return path.substringAfterLast("/").toInt()
		}
		
		fun insertWalletToDatabase(
			fragment: Fragment,
			multiChainAddresses: MultiChainAddresses,
			name: String,
			encryptMnemonic: String,
			multiChainPath: MultiChainPath,
			hint: String?,
			callback: () -> Unit
		) {
			WalletTable.getWalletByAddress(multiChainAddresses.ethAddress) {
				it.isNull() isTrue {
					// 在数据库记录钱包信息
					WalletTable.insert(
						WalletTable(
							0,
							name,
							currentETHAndERCAddress = multiChainAddresses.ethAddress,
							currentETCAddress = multiChainAddresses.etcAddress,
							currentBTCAddress = multiChainAddresses.btcAddress,
							currentBTCTestAddress = multiChainAddresses.btcTestAddress,
							isUsing = true,
							hint = hint,
							isWatchOnly = false,
							balance = 0.0,
							encryptMnemonic = encryptMnemonic,
							hasBackUpMnemonic = true,
							ethAddresses = childAddressValue(
								multiChainAddresses.ethAddress,
								getAddressIndexFromPath(multiChainPath.ethPath)
							),
							etcAddresses = childAddressValue(
								multiChainAddresses.etcAddress,
								getAddressIndexFromPath(multiChainPath.etcPath)
							),
							btcAddresses = childAddressValue(
								multiChainAddresses.btcAddress,
								getAddressIndexFromPath(multiChainPath.btcPath)
							),
							btcTestAddresses = childAddressValue(
								multiChainAddresses.btcTestAddress,
								getAddressIndexFromPath(multiChainPath.btcTestPath)
							),
							ethPath = multiChainPath.ethPath,
							btcPath = multiChainPath.btcPath,
							etcPath = multiChainPath.etcPath,
							btcTestPath = multiChainPath.btcTestPath
						)
					) {
						// 创建钱包并获取默认的 `token` 信息
						CreateWalletPresenter.generateMyTokenInfo(
							multiChainAddresses.ethAddress,
							{
								LogUtil.error("insertWalletToDatabase")
								callback()
							}
						) {
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
}