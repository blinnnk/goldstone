package io.goldstone.blockchain.module.common.walletimport.watchonly.presenter

import android.widget.EditText
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.crypto.Address
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.bitcoin.MultiChainAddresses
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
	
	private var currentETHAndERCAddress = ""
	private var currentBTCAddress = ""
	private var currentBTCTestAddress = ""
	private var currentETCAddress = ""
	
	fun importWatchOnlyWallet(
		addressType: String,
		addressInput: EditText,
		nameInput: EditText,
		callback: () -> Unit
	) {
		// 默认去除所有的空格
		val address = addressInput.text.toString().replace(" ", "")
		when (addressType) {
			CryptoValue.PrivateKeyType.ETHERCAndETC.content -> {
				if (!Address(address).isValid()) {
					fragment.context?.alert(ImportWalletText.addressFromatAlert)
					callback()
					return
				}
			}
			
			CryptoValue.PrivateKeyType.BTC.content -> {
				if (!BTCUtils.isValidMainnetAddress(address)) {
					fragment.context?.alert(ImportWalletText.addressFromatAlert)
					callback()
					return
				}
			}
			
			else -> {
				if (!BTCUtils.isValidTestnetAddress(address)) {
					fragment.context?.alert(ImportWalletText.addressFromatAlert)
					callback()
					return
				}
			}
		}
		val name = if (nameInput.text.toString().isEmpty()) nameInput.hint.toString()
		else nameInput.text.toString()
		// 准备对应的地址
		setAddressByChainType(address, addressType)
		WalletTable.getWalletByAddress(address) {
			it.isNull() isTrue {
				WalletTable.insert(
					WalletTable(
						0,
						name = name,
						currentETHAndERCAddress = currentETHAndERCAddress,
						isUsing = true,
						isWatchOnly = true,
						hasBackUpMnemonic = true,
						currentBTCTestAddress = currentBTCTestAddress,
						currentBTCAddress = currentBTCAddress,
						currentETCAddress = currentETCAddress,
						ethPath = "",
						etcPath = "",
						btcPath = "",
						btcTestPath = "",
						ethAddresses = "",
						etcAddresses = "",
						btcAddresses = "",
						btcTestAddresses = ""
					)
				) {
					CreateWalletPresenter.generateMyTokenInfo(
						MultiChainAddresses(
							currentETHAndERCAddress,
							currentETCAddress,
							currentBTCAddress,
							currentBTCTestAddress
						),
						{
							LogUtil.error(this.javaClass.simpleName)
							callback()
						}
					) {
						callback()
						// 注册钱包地址用于发送 `Push`
						XinGePushReceiver.registerAddressesForPush()
						fragment.activity?.jump<SplashActivity>()
					}
				}
			} otherwise {
				fragment.context?.alert(ImportWalletText.existAddress)
				callback()
			}
		}
	}
	
	private fun setAddressByChainType(address: String, addressType: String) {
		when (addressType) {
			CryptoValue.PrivateKeyType.ETHERCAndETC.content -> {
				currentETHAndERCAddress = address
				currentETCAddress = address
			}
			
			CryptoValue.PrivateKeyType.BTC.content -> {
				currentBTCAddress = address
			}
			
			else -> {
				Config.updateIsTestEnvironment(true)
				currentBTCTestAddress = address
			}
		}
	}
	
	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		setRootChildFragmentBackEvent<WalletImportFragment>(fragment)
	}
}