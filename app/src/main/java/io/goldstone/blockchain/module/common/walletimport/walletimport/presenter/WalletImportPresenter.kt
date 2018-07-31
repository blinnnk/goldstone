package io.goldstone.blockchain.module.common.walletimport.walletimport.presenter

import android.content.Context
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
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
		
		// 非 `Bip44` 钱包, 本地没有 `Path index` 返回 `-1` 进行标记
		fun childAddressValue(address: String, index: Int): String {
			return if (index == -1) ""
			else "$address|$index"
		}
		
		fun getAddressIndexFromPath(path: String): Int {
			return if (path.isEmpty()) -1
			else path.substringAfterLast("/").toInt()
		}
		
		fun insertWalletToDatabase(
			context: Context?,
			multiChainAddresses: MultiChainAddresses,
			name: String,
			encryptMnemonic: String,
			multiChainPath: MultiChainPath,
			hint: String?,
			callback: (Boolean) -> Unit
		) {
			// 不为空的地址进行
			val currentAddress =
				arrayListOf(
					multiChainAddresses.etcAddress,
					multiChainAddresses.etcAddress,
					multiChainAddresses.btcAddress,
					multiChainAddresses.btcTestAddress
				).find { it.isNotEmpty() }.orEmpty()
			
			WalletTable.getWalletByAddress(currentAddress) {
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
							multiChainAddresses,
							{
								LogUtil.error("insertWalletToDatabase")
								callback(false)
							},
							callback
						)
						// 注册钱包地址用于发送 `Push`
						XinGePushReceiver.registerAddressesForPush()
					}
				} otherwise {
					context?.alert(ImportWalletText.existAddress)
					callback(false)
				}
			}
		}
	}
}