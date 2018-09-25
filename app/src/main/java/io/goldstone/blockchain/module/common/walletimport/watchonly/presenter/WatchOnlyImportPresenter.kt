package io.goldstone.blockchain.module.common.walletimport.watchonly.presenter

import android.support.annotation.UiThread
import android.widget.EditText
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.ethereum.Address
import io.goldstone.blockchain.crypto.ethereum.isValid
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
import io.goldstone.blockchain.crypto.multichain.AddressType
import io.goldstone.blockchain.crypto.multichain.ChainAddresses
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.AddressCommissionModel
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.EOSAccountInfo
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.EOSDefaultAllChainName
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.common.walletimport.watchonly.view.WatchOnlyImportFragment

/**
 * @date 23/03/2018 2:16 AM
 * @author KaySaith
 */
class WatchOnlyImportPresenter(
	override val fragment: WatchOnlyImportFragment
) : BasePresenter<WatchOnlyImportFragment>() {

	private var currentETHSeriesAddress = ""
	private var currentBTCAddress = ""
	private var currentBTCTestAddress = ""
	private var currentETCAddress = ""
	private var currentLTCAddress = ""
	private var currentBCHAddress = ""
	// EOS 可能导入的三种情况
	private var eosMainnetAccountName = ""
	private var eosTestnetAccountName = ""
	private var currentEOSAddress = ""

	fun importWatchOnlyWallet(
		addressType: String,
		addressInput: EditText,
		nameInput: EditText,
		@UiThread callback: (GoldStoneError) -> Unit
	) {
		// 默认去除所有的空格
		val address = addressInput.text.toString().replace(" ", "")
		when (addressType) {
			AddressType.ETHSeries.value ->
				if (!Address(address).isValid()) {
					callback(AccountError.InvalidAddress)
					return
				}
			AddressType.BTC.value ->
				if (!BTCUtils.isValidMainnetAddress(address)) {
					callback(AccountError.InvalidAddress)
					return
				}
			AddressType.LTC.value ->
				if (!LTCWalletUtils.isValidAddress(address)) {
					callback(AccountError.InvalidAddress)
					return
				}
			AddressType.EOS.value ->
				if (!EOSWalletUtils.isValidAddress(address) && !EOSAccount(address).isValid()) {
					callback(AccountError.InvalidAddress)
					return
				}
			AddressType.EOSJungle.value ->
				if (!EOSWalletUtils.isValidAddress(address) && !EOSAccount(address).isValid()) {
					callback(AccountError.InvalidAddress)
					return
				}
			AddressType.BCH.value ->
				if (!BCHWalletUtils.isValidAddress(address)) {
					callback(AccountError.InvalidAddress)
					return
				}
			else -> if (!BTCUtils.isValidTestnetAddress(address)) {
				callback(AccountError.InvalidAddress)
				return
			}
		}
		val name = if (nameInput.text.toString().isEmpty()) nameInput.hint.toString()
		else nameInput.text.toString()
		// 准备对应的地址
		setAddressByChainType(address, addressType)
		// 通过用导入的地址查找钱包的行为判断是否已经存在此钱包地址
		WalletTable.getWalletByAddress(address) { it ->
			if (it.isNull()) WalletTable(
				name,
				currentETHSeriesAddress,
				currentBTCTestAddress,
				currentBTCAddress,
				currentETCAddress,
				currentLTCAddress,
				currentBCHAddress,
				getUnEmptyEOSAccountValue(), // 如果导入的是一个 `AccountName` 的钱包, 那么同时把这个 `AccountName` 作为 `CurrentEOSAddress` 录入, 以此来对接大逻辑
				EOSDefaultAllChainName(eosMainnetAccountName, eosTestnetAccountName),
				if (getUnEmptyEOSAccountValue().isEmpty()) listOf()
				else listOf(
					EOSAccountInfo(eosMainnetAccountName, ChainID.EOS.id),
					EOSAccountInfo(eosTestnetAccountName, ChainID.EOSTest.id)
				)
			).insertWatchOnlyWallet { thisWallet ->
				CreateWalletPresenter.generateMyTokenInfo(
					ChainAddresses(
						currentETHSeriesAddress,
						currentETCAddress,
						currentBTCAddress,
						currentBTCTestAddress,
						currentLTCAddress,
						currentBCHAddress,
						getUnEmptyEOSAccountValue()
					)
				) { error ->
					if (error.isNone()) thisWallet.registerPushByAddress(callback)
					else callback(error)
				}
			} else callback(AccountError.ExistAddress)
		}
	}

	private fun WalletTable.registerPushByAddress(callback: (GoldStoneError) -> Unit) {
		listOf(
			Pair(currentBTCAddress, ChainType.BTC),
			Pair(currentLTCAddress, ChainType.LTC),
			Pair(currentBCHAddress, ChainType.BCH),
			Pair(currentBTCTestAddress, ChainType.AllTest),
			Pair(currentETCAddress, ChainType.ETC),
			Pair(currentETHSeriesAddress, ChainType.ETH),
			Pair(getUnEmptyEOSAccountValue(), ChainType.EOS)
		).first {
			it.first.isNotEmpty()
		}.apply {
			XinGePushReceiver.registerSingleAddress(
				AddressCommissionModel(first, second.id, 1, id))
			callback(AccountError.None)
		}
	}

	private fun getUnEmptyEOSAccountValue(): String {
		return listOf(
			currentEOSAddress,
			eosTestnetAccountName,
			eosMainnetAccountName
		).firstOrNull { it.isNotEmpty() } ?: ""
	}

	private fun setAddressByChainType(address: String, addressType: String) {
		when (addressType) {
			AddressType.ETHSeries.value -> {
				currentETHSeriesAddress = address
				currentETCAddress = address
			}
			AddressType.BTC.value -> {
				currentBTCAddress = address
				Config.updateIsTestEnvironment(false)
			}
			AddressType.LTC.value -> {
				currentLTCAddress = address
				Config.updateIsTestEnvironment(false)
			}
			AddressType.BCH.value -> {
				currentBCHAddress = address
				Config.updateIsTestEnvironment(false)
			}
			AddressType.EOS.value -> {
				if (EOSWalletUtils.isValidAddress(address)) currentEOSAddress = address
				else if (EOSAccount(address).isValid()) {
					eosMainnetAccountName = address
					Config.updateCurrentEOSName(address)
				}
				Config.updateIsTestEnvironment(false)
			}
			AddressType.EOSJungle.value -> {
				if (EOSWalletUtils.isValidAddress(address)) currentEOSAddress = address
				else if (EOSAccount(address).isValid()) {
					eosTestnetAccountName = address
					Config.updateCurrentEOSName(address)
					Config.updateEOSCurrentChain(ChainID.eosTest)
				}
				Config.updateIsTestEnvironment(true)
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