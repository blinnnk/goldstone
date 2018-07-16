package io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.presneter

import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view.ChainAddressesHeaderView
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view.SingleChainAddressesFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.presenter.WalletAddressManagerPresneter

/**
 * @date 2018/7/16 6:07 PM
 * @author KaySaith
 */
class SingleChainAddressesPresenter(
	override val fragment: SingleChainAddressesFragment
) : BaseRecyclerPresenter<SingleChainAddressesFragment, Pair<String, String>>() {
	
	private val coinType by lazy {
		fragment.arguments?.getInt(ArgumentKey.coinType)
	}
	
	override fun updateData() {
		WalletTable.getCurrentWallet {
			it?.apply {
				when (coinType) {
					ChainType.ETH.id -> {
						fragment.asyncData =
							WalletAddressManagerPresneter.convertToChildAddresses(ethAddresses).toArrayList()
						WalletAddressManagerPresneter.getCurrentAddressIndexByChainType(ChainType.ETH.id) {
							setDefaultAddress(it, currentETHAndERCAddress, ChainType.ETH.id)
						}
					}
					
					ChainType.ETC.id -> {
						fragment.asyncData =
							WalletAddressManagerPresneter.convertToChildAddresses(etcAddresses).toArrayList()
						WalletAddressManagerPresneter.getCurrentAddressIndexByChainType(ChainType.ETH.id) {
							setDefaultAddress(it, currentETCAddress, ChainType.ETC.id)
						}
					}
					
					ChainType.BTC.id -> {
						fragment.asyncData =
							WalletAddressManagerPresneter.convertToChildAddresses(btcAddresses).toArrayList()
						WalletAddressManagerPresneter.getCurrentAddressIndexByChainType(ChainType.ETH.id) {
							setDefaultAddress(it, currentBTCAddress, ChainType.BTC.id)
						}
					}
					
					else -> {
						fragment.asyncData =
							WalletAddressManagerPresneter.convertToChildAddresses(btcTestAddresses).toArrayList()
						WalletAddressManagerPresneter.getCurrentAddressIndexByChainType(ChainType.ETH.id) {
							setDefaultAddress(it, currentBTCTestAddress, ChainType.BTCTest.id)
						}
					}
				}
			}
		}
	}
	
	private fun setDefaultAddress(index: String, address: String, chainType: Int) {
		fragment.recyclerView.getItemAtAdapterPosition<ChainAddressesHeaderView>(0) {
			it?.setDefaultAddress(index, address, chainType)
		}
	}
}