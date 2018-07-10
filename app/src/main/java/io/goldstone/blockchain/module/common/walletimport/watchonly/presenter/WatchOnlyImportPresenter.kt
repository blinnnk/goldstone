package io.goldstone.blockchain.module.common.walletimport.watchonly.presenter

import android.widget.EditText
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.crypto.Address
import io.goldstone.blockchain.crypto.isValid
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.common.walletimport.watchonly.view.WatchOnlyImportFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity

/**
 * @date 23/03/2018 2:16 AM
 * @author KaySaith
 */
class WatchOnlyImportPresenter(
	override val fragment: WatchOnlyImportFragment
) : BasePresenter<WatchOnlyImportFragment>() {
	
	fun importWatchOnlyWallet(
		addressInput: EditText,
		nameInput: EditText,
		callback: () -> Unit
	) {
		// 默认去除所有的空格
		val address = Address(addressInput.text.toString().replace(" ", ""))
		if (!address.isValid()) {
			fragment.context?.alert(ImportWalletText.addressFromatAlert)
			callback()
			return
		}
		val name = if (nameInput.text.toString().isEmpty()) nameInput.hint.toString()
		else nameInput.text.toString()
		
		WalletTable.getWalletByAddress(address.hex) {
			it.isNull() isTrue {
				WalletTable.insert(
					WalletTable(
						0,
						name = name,
						currentEthSeriesAddress = address.hex,
						isUsing = true,
						isWatchOnly = true,
						hasBackUpMnemonic = true
					)
				) {
					CreateWalletPresenter.generateMyTokenInfo(
						address.hex,
						{
							LogUtil.error(this.javaClass.simpleName)
							callback()
						}
					) {
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
	
	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		setRootChildFragmentBackEvent<WalletImportFragment>(fragment)
	}
}