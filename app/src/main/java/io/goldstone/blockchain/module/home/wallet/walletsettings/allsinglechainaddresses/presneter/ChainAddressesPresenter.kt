package io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.presneter

import android.os.Bundle
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orZero
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.component.GraySqualCellWithButtons
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.getViewAbsolutelyPositionInScreen
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view.ChainAddressesAdapter
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view.ChainAddressesFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view.ChainAddressesHeaderView
import io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.view.KeystoreExportFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view.PrivateKeyExportFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view.QRCodeFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.presenter.AddressManagerPresneter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.view.AddressManagerFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.support.v4.toast

/**
 * @date 2018/7/16 6:07 PM
 * @author KaySaith
 */
class ChainAddressesPresenter(
	override val fragment: ChainAddressesFragment
) : BaseRecyclerPresenter<ChainAddressesFragment, Pair<String, String>>() {
	
	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		fragment.getParentFragment<WalletSettingsFragment> {
			overlayView.header.showCloseButton(false)
			overlayView.header.showBackButton(true) {
				headerTitle = WalletSettingsText.viewAddresses
				presenter.popFragmentFrom<ChainAddressesFragment>()
			}
		}
	}
	
	fun showMoreDashboard(
		cell: GraySqualCellWithButtons,
		address: String,
		coinType: Int,
		hasDefaultCell: Boolean = true
	) {
		val isBTC = coinType == ChainType.BTC.id
		AddressManagerFragment.showMoreDashboard(
			fragment.wrapper,
			cell.getViewAbsolutelyPositionInScreen()[1].toFloat(),
			isBTC,
			hasDefaultCell,
			setDefaultAddressEvent = {
				AddressManagerPresneter.setDefaultAddress(coinType, address) {
					// 更新钱包默认地址, 同时更新首页的数据
					updateWalletDetail()
					updateData()
					AddressManagerFragment.removeDashboard(fragment)
					updateDefaultStyle(coinType)
					fragment.toast(CommonText.succeed)
				}
			},
			qrCellClickEvent = { showQRCode(address) },
			exportBTCPrivateKey = {
				showPrivateKeyExportFragment(address, isBTC)
			},
			keystoreCellClickEvent = {
				showKeystoreExportFragment(address)
			},
			exportPrivateKey = {
				showPrivateKeyExportFragment(address, isBTC)
			}
		)
	}
	
	fun updateAddAddressEvent() {
		fragment.getParentFragment<WalletSettingsFragment> {
			overlayView.header.showAddButton(true, false) {
				AddressManagerFragment.verifyPassword(this) {
					when (fragment.coinType) {
						ChainType.ETH.id -> AddressManagerPresneter.createETHAndERCAddress(context, it) {
							updateAddressManagerDataBy(ChainType.ETH.id, it)
							diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
						}
						ChainType.ETC.id -> AddressManagerPresneter.createETCAddress(context, it) {
							updateAddressManagerDataBy(ChainType.ETC.id, it)
							diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
						}
						ChainType.BTC.id -> AddressManagerPresneter.createBTCAddress(context, it) {
							updateAddressManagerDataBy(ChainType.BTC.id, it)
							diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
						}
					}
				}
			}
		}
	}
	
	private fun updateWalletDetail() {
		fragment.getMainActivity()?.getWalletDetailFragment()?.presenter?.updateData()
	}
	
	private fun updateAddressManagerDataBy(coinType: Int, data: ArrayList<Pair<String, String>>) {
		fragment.parentFragment?.childFragmentManager?.fragments?.find {
			it is AddressManagerFragment
		}?.let {
			if (it is AddressManagerFragment) {
				when (coinType) {
					ChainType.ETH.id -> it.setEthereumAddressesModel(data)
					ChainType.ETC.id -> it.setEthereumClassicAddressesModel(data)
					ChainType.BTC.id -> it.setBitcoinAddressesModel(data)
				}
			}
		}
	}
	
	private fun updateDefaultStyle(coinType: Int) {
		fragment.parentFragment?.childFragmentManager?.fragments?.find {
			it is AddressManagerFragment
		}?.let {
			if (it is AddressManagerFragment) {
				when (coinType) {
					ChainType.ETH.id -> it.presenter.getEthereumAddresses()
					ChainType.ETC.id -> it.presenter.getEthereumClassicAddresses()
					
					ChainType.BTC.id -> {
						if (Config.isTestEnvironment()) {
							it.presenter.getBitcoinTestAddresses()
						} else {
							it.presenter.getBitcoinAddresses()
						}
					}
				}
			}
		}
	}
	
	private fun showQRCode(address: String) {
		AddressManagerFragment.removeDashboard(fragment)
		fragment.getParentFragment<WalletSettingsFragment> {
			presenter.showTargetFragment<QRCodeFragment>(
				WalletText.showQRCode,
				getFragmentTitleBy(fragment.coinType.orZero()),
				Bundle().apply { putString(ArgumentKey.address, address) }
			)
		}
	}
	
	private fun showPrivateKeyExportFragment(address: String, isBTC: Boolean) {
		AddressManagerFragment.removeDashboard(fragment)
		fragment.getParentFragment<WalletSettingsFragment> {
			presenter.showTargetFragment<PrivateKeyExportFragment>(
				WalletSettingsText.exportPrivateKey,
				getFragmentTitleBy(fragment.coinType.orZero()),
				Bundle().apply {
					putString(ArgumentKey.address, address)
					if (isBTC) putBoolean(ArgumentKey.isBTCAddress, isBTC)
				}
			)
		}
	}
	
	private fun showKeystoreExportFragment(address: String) {
		AddressManagerFragment.removeDashboard(fragment)
		fragment.getParentFragment<WalletSettingsFragment> {
			presenter.showTargetFragment<KeystoreExportFragment>(
				WalletSettingsText.exportKeystore,
				getFragmentTitleBy(fragment.coinType.orZero()),
				Bundle().apply {
					putString(ArgumentKey.address, address)
				}
			)
		}
	}
	
	private fun getFragmentTitleBy(coinType: Int): String {
		return when (coinType) {
			ChainType.ETH.id -> WalletSettingsText.allETHAndERCAddresses
			ChainType.ETC.id -> WalletSettingsText.allETCAddresses
			ChainType.BTC.id -> WalletSettingsText.allBtCAddresses
			else ->
				WalletSettingsText.allBtCTestAddresses
		}
	}
	
	override fun updateData() {
		WalletTable.getCurrentWallet {
			it?.apply {
				when (fragment.coinType) {
					ChainType.ETH.id -> {
						fragment.asyncData =
							AddressManagerPresneter.convertToChildAddresses(ethAddresses).toArrayList()
						AddressManagerPresneter.getCurrentAddressIndexByChainType(ChainType.ETH.id) {
							setDefaultAddress(it, currentETHAndERCAddress, ChainType.ETH.id)
							Config.updateCurrentEthereumAddress(currentETHAndERCAddress)
						}
					}
					
					ChainType.ETC.id -> {
						fragment.asyncData =
							AddressManagerPresneter.convertToChildAddresses(etcAddresses).toArrayList()
						AddressManagerPresneter.getCurrentAddressIndexByChainType(ChainType.ETC.id) {
							setDefaultAddress(it, currentETCAddress, ChainType.ETC.id)
							Config.updateCurrentETCAddress(currentETCAddress)
						}
					}
					
					ChainType.BTC.id -> {
						val address = if (Config.isTestEnvironment()) btcTestAddresses else btcAddresses
						val currentAddress =
							if (Config.isTestEnvironment()) currentBTCTestAddress else currentBTCAddress
						fragment.asyncData =
							AddressManagerPresneter.convertToChildAddresses(address).toArrayList()
						AddressManagerPresneter.getCurrentAddressIndexByChainType(ChainType.BTC.id) {
							setDefaultAddress(it, currentAddress, ChainType.BTC.id)
							if (Config.isTestEnvironment())
								Config.updateCurrentBTCTestAddress(btcTestAddresses)
							else Config.updateCurrentBTCAddress(btcAddresses)
						}
					}
				}
			}
		}
	}
	
	private fun setDefaultAddress(index: String, address: String, chainType: Int) {
		fragment.recyclerView.getItemAtAdapterPosition<ChainAddressesHeaderView>(0) {
			it?.setDefaultAddress(index, address, chainType) {
				showMoreDashboard(this, address, chainType, false)
			}
		}
	}
}