package io.goldstone.blockchain.module.common.walletimport.watchonly.presenter

import android.util.Log
import android.widget.EditText
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.watchonly.view.WatchOnlyImportFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import org.web3j.crypto.WalletUtils

/**
 * @date 23/03/2018 2:16 AM
 * @author KaySaith
 */

class WatchOnlyImportPresenter(
	override val fragment: WatchOnlyImportFragment
) : BasePresenter<WatchOnlyImportFragment>() {

	fun importWatchOnlyWallet(addressInput: EditText, nameInput: EditText, callback: () -> Unit) {
		// 默认去除所有的空格
		val address = addressInput.text.toString().replace(" ", "")
		if (!WalletUtils.isValidAddress(address)) {
			fragment.context?.alert("address isn't valid")
			callback()
			return
		}

		val name = if (nameInput.text.toString().isEmpty()) "Wallet"
		else nameInput.text.toString()

		WalletTable.getWalletByAddress(address) {
			it.isNull() isTrue {
				WalletTable.insert(WalletTable(0, name, address, true, null, true)) {
					CreateWalletPresenter.generateMyTokenInfo(address, false, {
						Log.e("ERROR", "server get default token error")
						callback()
					}) {
						fragment.activity?.jump<SplashActivity>()
						callback()
					}
					// 注册钱包地址用于发送 `Push`
					XinGePushReceiver.registerWalletAddressForPush()
				}
			} otherwise {
				fragment.context?.alert("There is already this account in gold stone")
				callback()
			}
		}
	}
}