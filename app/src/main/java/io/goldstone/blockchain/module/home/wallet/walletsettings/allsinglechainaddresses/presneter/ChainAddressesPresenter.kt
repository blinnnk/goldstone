package io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.presneter

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.component.cell.GraySqualCellWithButtons
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getChildFragment
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.getViewAbsolutelyPositionInScreen
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view.ChainAddressesAdapter
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view.ChainAddressesFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view.ChainAddressesHeaderView
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.presenter.AddressManagerPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.view.AddressManagerFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.bitcoinj.params.MainNetParams
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
			updateAddAddressEvent()
		}
	}

	fun showMoreDashboard(
		cell: GraySqualCellWithButtons,
		address: String,
		coinType: Int,
		hasDefaultCell: Boolean = true
	) {
		AddressManagerFragment.showMoreDashboard(
			fragment.wrapper,
			cell.getViewAbsolutelyPositionInScreen()[1].toFloat(),
			hasDefaultCell,
			BCHWalletUtils.isNewCashAddress(address),
			setDefaultAddressEvent = {
				AddressManagerPresenter.setDefaultAddress(coinType, address) {
					// 更新钱包默认地址, 同时更新首页的数据
					updateWalletDetail()
					updateData()
					AddressManagerFragment.removeDashboard(fragment.context)
					updateDefaultStyle(coinType)
					fragment.toast(CommonText.succeed)
				}
			},
			qrCellClickEvent = { showQRCode(address) },
			keystoreCellClickEvent = {
				showKeystoreExportFragment(address)
			},
			exportPrivateKey = {
				showPrivateKeyExportFragment(address, coinType)
			},
			convertBCHAddressToLegacy = {
				val legacyAddress = BCHWalletUtils.formattedToLegacy(address, MainNetParams.get())
				fragment.context.alert(legacyAddress)
				fragment.context?.clickToCopy(legacyAddress)
				AddressManagerFragment.removeDashboard(fragment.context)
			}
		)
	}

	fun updateAddAddressEvent() {
		fragment.getParentFragment<WalletSettingsFragment> {
			showAddButton(true, false) {
				context?.apply {
					AddressManagerFragment.verifyMultiChainWalletPassword(this) { password ->
						when (fragment.coinType) {
							ChainType.ETH.id ->
								AddressManagerPresenter.createETHAndERCAddress(this, password) {
									updateAddressManagerDataBy(ChainType.ETH.id, it)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
								}
							ChainType.ETC.id ->
								AddressManagerPresenter.createETCAddress(this, password) {
									updateAddressManagerDataBy(ChainType.ETC.id, it)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
								}

							ChainType.LTC.id ->
								AddressManagerPresenter.createLTCAddress(this, password) {
									updateAddressManagerDataBy(ChainType.LTC.id, it)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
								}

							ChainType.BCH.id ->
								AddressManagerPresenter.createBCHAddress(this, password) {
									updateAddressManagerDataBy(ChainType.BCH.id, it)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
								}

							ChainType.BTC.id -> {
								if (Config.isTestEnvironment()) {
									AddressManagerPresenter.createBTCTestAddress(this, password) {
										updateAddressManagerDataBy(ChainType.AllTest.id, it)
										diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
									}
								} else {
									AddressManagerPresenter.createBTCAddress(this, password) {
										updateAddressManagerDataBy(ChainType.BTC.id, it)
										diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private fun updateWalletDetail() {
		fragment.getMainActivity()?.getWalletDetailFragment()?.presenter?.updateData()
	}

	private fun updateAddressManagerDataBy(
		coinType: Int,
		data: ArrayList<Pair<String, String>>
	) {
		fragment.parentFragment?.getChildFragment<AddressManagerFragment>()?.apply {
			when (coinType) {
				ChainType.ETH.id -> setEthereumAddressesModel(data)
				ChainType.ETC.id -> setEthereumClassicAddressesModel(data)
				ChainType.BTC.id -> setBitcoinAddressesModel(data)
				ChainType.BCH.id -> setBitcoinCashAddressesModel(data)
				ChainType.LTC.id -> setLitecoinAddressesModel(data)
				ChainType.AllTest.id -> setBitcoinAddressesModel(data)
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
					ChainType.LTC.id -> it.presenter.getLitecoinAddresses()
					ChainType.BCH.id -> it.presenter.getBitcoinCashAddresses()
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
		// 这个页面不限时 `Header` 上的加号按钮
		fragment.getParentFragment<WalletSettingsFragment> {
			AddressManagerPresenter.showQRCodeFragment(address, this)
		}
	}

	private fun showPrivateKeyExportFragment(address: String, coinType: Int) {
		fragment.getParentFragment<WalletSettingsFragment> {
			AddressManagerPresenter.showPrivateKeyExportFragment(address, coinType, this)
		}
	}

	private fun showKeystoreExportFragment(address: String) {
		fragment.getParentFragment<WalletSettingsFragment> {
			AddressManagerPresenter.showKeystoreExportFragment(address, this)
		}
	}

	override fun updateData() {
		// 用户在这个界面更新 默认地址的时候会再次调用这个方法，所以方法内包含更新当前地址的方法.
		WalletTable.getCurrentWallet {
			when (fragment.coinType) {
				ChainType.ETH.id -> {
					fragment.asyncData =
						AddressManagerPresenter.convertToChildAddresses(ethAddresses).toArrayList()
					AddressManagerPresenter.getCurrentAddressIndexByChainType(ChainType.ETH.id) {
						setDefaultAddress(it, currentETHAndERCAddress, ChainType.ETH.id)
						Config.updateCurrentEthereumAddress(currentETHAndERCAddress)
					}
				}

				ChainType.ETC.id -> {
					fragment.asyncData =
						AddressManagerPresenter.convertToChildAddresses(etcAddresses).toArrayList()
					AddressManagerPresenter.getCurrentAddressIndexByChainType(ChainType.ETC.id) {
						setDefaultAddress(it, currentETCAddress, ChainType.ETC.id)
						Config.updateCurrentETCAddress(currentETCAddress)
					}
				}

				ChainType.LTC.id -> {
					fragment.asyncData =
						AddressManagerPresenter.convertToChildAddresses(ltcAddresses).toArrayList()
					AddressManagerPresenter.getCurrentAddressIndexByChainType(ChainType.LTC.id) {
						setDefaultAddress(it, currentLTCAddress, ChainType.LTC.id)
						Config.updateCurrentLTCAddress(currentLTCAddress)
					}
				}

				ChainType.BCH.id -> {
					fragment.asyncData =
						AddressManagerPresenter.convertToChildAddresses(bchAddresses).toArrayList()
					AddressManagerPresenter.getCurrentAddressIndexByChainType(ChainType.BCH.id) {
						setDefaultAddress(it, currentBCHAddress, ChainType.BCH.id)
						Config.updateCurrentLTCAddress(currentBCHAddress)
					}
				}

				ChainType.BTC.id -> {
					val address =
						if (Config.isTestEnvironment()) btcSeriesTestAddresses else btcAddresses
					val currentAddress =
						if (Config.isTestEnvironment()) currentBTCSeriesTestAddress else currentBTCAddress
					fragment.asyncData =
						AddressManagerPresenter.convertToChildAddresses(address).toArrayList()
					AddressManagerPresenter.getCurrentAddressIndexByChainType(ChainType.BTC.id) {
						setDefaultAddress(it, currentAddress, ChainType.BTC.id)
						if (Config.isTestEnvironment())
							Config.updateCurrentBTCSeriesTestAddress(currentBTCSeriesTestAddress)
						else Config.updateCurrentBTCAddress(currentBTCAddress)
					}
				}
			}
		}
	}

	private fun setDefaultAddress(index: String, address: String, chainType: Int) {
		fragment.recyclerView.getItemAtAdapterPosition<ChainAddressesHeaderView>(0) {
			it.setDefaultAddress(index, address, chainType) {
				showMoreDashboard(this, address, chainType, false)
			}
		}
	}
}