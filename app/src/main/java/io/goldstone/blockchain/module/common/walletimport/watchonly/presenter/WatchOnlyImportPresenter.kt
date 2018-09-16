package io.goldstone.blockchain.module.common.walletimport.watchonly.presenter

import android.widget.EditText
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.ethereum.Address
import io.goldstone.blockchain.crypto.ethereum.isValid
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
import io.goldstone.blockchain.crypto.multichain.ChainAddresses
import io.goldstone.blockchain.crypto.multichain.MultiChainType
import io.goldstone.blockchain.crypto.multichain.PrivateKeyType
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.AddressCommissionModel
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.EOSDefaultAllChainName
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
	private var currentLTCAddress = ""
	private var currentBCHAddress = ""
	private var currentEOSAddress = ""

	fun importWatchOnlyWallet(
		addressType: String,
		addressInput: EditText,
		nameInput: EditText,
		callback: () -> Unit
	) {
		// 默认去除所有的空格
		val address = addressInput.text.toString().replace(" ", "")
		when (addressType) {
			PrivateKeyType.ETHERCAndETC.content -> {
				if (!Address(address).isValid()) {
					fragment.context?.alert(ImportWalletText.addressFormatAlert)
					callback()
					return
				}
			}

			PrivateKeyType.BTC.content -> {
				if (!BTCUtils.isValidMainnetAddress(address)) {
					fragment.context?.alert(ImportWalletText.addressFormatAlert)
					callback()
					return
				}
			}

			PrivateKeyType.LTC.content -> {
				if (!LTCWalletUtils.isValidAddress(address)) {
					fragment.context?.alert(ImportWalletText.addressFormatAlert)
					callback()
					return
				}
			}

			PrivateKeyType.EOS.content -> {
				if (!EOSWalletUtils.isValidAddress(address)) {
					fragment.context?.alert(ImportWalletText.addressFormatAlert)
					callback()
					return
				}
			}

			PrivateKeyType.BCH.content -> {
				if (!BCHWalletUtils.isValidAddress(address)) {
					fragment.context?.alert(ImportWalletText.addressFormatAlert)
					callback()
					return
				}
			}

			else -> {
				if (!BTCUtils.isValidTestnetAddress(address)) {
					fragment.context?.alert(ImportWalletText.addressFormatAlert)
					callback()
					return
				}
			}
		}
		val name = if (nameInput.text.toString().isEmpty()) nameInput.hint.toString()
		else nameInput.text.toString()
		// 准备对应的地址
		setAddressByChainType(address, addressType)
		WalletTable.getWalletByAddress(address) { it ->
			it.isNull() isTrue {
				WalletTable.insert(
					WalletTable(
						0,
						name = name,
						currentETHAndERCAddress = currentETHAndERCAddress,
						isUsing = true,
						isWatchOnly = true,
						hasBackUpMnemonic = true,
						currentBTCSeriesTestAddress = currentBTCTestAddress,
						currentBTCAddress = currentBTCAddress,
						currentETCAddress = currentETCAddress,
						currentLTCAddress = currentLTCAddress,
						currentBCHAddress = currentBCHAddress,
						currentEOSAddress = currentEOSAddress,
						currentEOSAccountName = EOSDefaultAllChainName(currentEOSAddress, currentEOSAddress),
						ethPath = "",
						etcPath = "",
						btcPath = "",
						bchPath = "",
						btcTestPath = "",
						ltcPath = "",
						eosPath = "",
						ethAddresses = "",
						etcAddresses = "",
						btcAddresses = "",
						bchAddresses = "",
						btcSeriesTestAddresses = "",
						ltcAddresses = "",
						eosAddresses = "",
						eosAccountNames = listOf()
					)
				) { thisWallet ->
					if (thisWallet.isNull()) return@insert
					CreateWalletPresenter.generateMyTokenInfo(
						ChainAddresses(
							currentETHAndERCAddress,
							currentETCAddress,
							currentBTCAddress,
							currentBTCTestAddress,
							currentLTCAddress,
							currentBCHAddress,
							currentEOSAddress
						),
						{
							LogUtil.error(this.javaClass.simpleName)
							callback()
						}
					) { _ ->
						callback()
						// 注册钱包地址用于发送 `Push`
						val addressPairs =
							listOf(
								Pair(currentBTCAddress, MultiChainType.BTC.id),
								Pair(currentLTCAddress, MultiChainType.LTC.id),
								Pair(currentBCHAddress, MultiChainType.BCH.id),
								Pair(currentBTCTestAddress, MultiChainType.AllTest.id),
								Pair(currentETCAddress, MultiChainType.ETC.id),
								Pair(currentETHAndERCAddress, MultiChainType.ETH.id),
								Pair(currentEOSAddress, MultiChainType.EOS.id)
							)
						val current = addressPairs.first { it.first.isNotEmpty() }
						XinGePushReceiver.registerSingleAddress(
							AddressCommissionModel(
								current.first,
								current.second,
								1,
								thisWallet!!.id
							))
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
			PrivateKeyType.ETHERCAndETC.content -> {
				currentETHAndERCAddress = address
				currentETCAddress = address
			}

			PrivateKeyType.BTC.content -> {
				currentBTCAddress = address
			}

			PrivateKeyType.LTC.content -> {
				currentLTCAddress = address
			}

			PrivateKeyType.BCH.content -> {
				currentBCHAddress = address
			}

			PrivateKeyType.EOS.content -> {
				currentEOSAddress = address
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