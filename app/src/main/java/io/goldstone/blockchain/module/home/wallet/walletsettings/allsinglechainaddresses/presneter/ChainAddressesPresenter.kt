package io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.presneter

import com.blinnnk.extension.getChildFragment
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.getViewAbsolutelyPositionInScreen
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.component.cell.GraySquareCellWithButtons
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.model.ContactModel
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
		cell: GraySquareCellWithButtons,
		address: String,
		coinType: ChainType,
		hasDefaultCell: Boolean = true
	) {
		AddressManagerFragment.showMoreDashboard(
			fragment.wrapper,
			cell.getViewAbsolutelyPositionInScreen()[1].toFloat(),
			hasDefaultCell,
			BCHWalletUtils.isNewCashAddress(address),
			setDefaultAddressEvent = {
				coinType.updateCurrentAddress(address) { isSwitchEOSAddress ->
					if (isSwitchEOSAddress)
						AddressManagerFragment.showSwitchEOSAddressAlertAndJump(fragment.context)
					else {
						// 更新钱包默认地址, 同时更新首页的数据
						updateWalletDetail()
						updateData()
						AddressManagerFragment.removeDashboard(fragment.context)
						updateDefaultStyle(coinType)
						fragment.toast(CommonText.succeed)
					}
				}
			},
			qrCellClickEvent = {
				val symbol = if (coinType.isBCH()) CoinSymbol.bch else ""
				showQRCode(ContactModel(address, symbol))
			},
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
							ChainType.ETH ->
								AddressManagerPresenter.createETHAndERCAddress(this, password) {
									updateAddressManagerDataBy(ChainType.ETH, it)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
								}
							ChainType.ETC ->
								AddressManagerPresenter.createETCAddress(this, password) {
									updateAddressManagerDataBy(ChainType.ETC, it)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
								}

							ChainType.LTC ->
								AddressManagerPresenter.createLTCAddress(this, password) {
									updateAddressManagerDataBy(ChainType.LTC, it)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
								}

							ChainType.BCH ->
								AddressManagerPresenter.createBCHAddress(this, password) {
									updateAddressManagerDataBy(ChainType.BCH, it)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
								}

							ChainType.BTC -> {
								if (Config.isTestEnvironment()) {
									AddressManagerPresenter.createBTCTestAddress(this, password) {
										updateAddressManagerDataBy(ChainType.AllTest, it)
										diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
									}
								} else {
									AddressManagerPresenter.createBTCAddress(this, password) {
										updateAddressManagerDataBy(ChainType.BTC, it)
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
		chainType: ChainType,
		data: ArrayList<Pair<String, String>>
	) {
		fragment.parentFragment?.getChildFragment<AddressManagerFragment>()?.apply {
			when (chainType) {
				ChainType.ETH -> setEthereumAddressesModel(data)
				ChainType.ETC -> setEthereumClassicAddressesModel(data)
				ChainType.BTC -> setBitcoinAddressesModel(data)
				ChainType.BCH -> setBitcoinCashAddressesModel(data)
				ChainType.LTC -> setLitecoinAddressesModel(data)
				ChainType.AllTest -> setBitcoinAddressesModel(data)
			}
		}
	}

	private fun updateDefaultStyle(coinType: ChainType) {
		fragment.parentFragment?.childFragmentManager?.fragments?.find {
			it is AddressManagerFragment
		}?.let {
			if (it is AddressManagerFragment) {
				WalletTable.getCurrentWallet {
					when (coinType) {
						ChainType.ETH -> it.presenter.getEthereumAddresses(this)
						ChainType.ETC -> it.presenter.getEthereumClassicAddresses(this)
						ChainType.LTC -> it.presenter.getLitecoinAddresses(this)
						ChainType.BCH -> it.presenter.getBitcoinCashAddresses(this)
						ChainType.BTC -> {
							if (Config.isTestEnvironment()) {
								it.presenter.getBitcoinTestAddresses(this)
							} else {
								it.presenter.getBitcoinAddresses(this)
							}
						}
					}
				}
			}
		}
	}

	private fun showQRCode(addressModel: ContactModel) {
		// 这个页面不限时 `Header` 上的加号按钮
		fragment.getParentFragment<WalletSettingsFragment> {
			AddressManagerPresenter.showQRCodeFragment(
				addressModel,
				this
			)
		}
	}

	private fun showPrivateKeyExportFragment(address: String, coinType: ChainType) {
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
				ChainType.ETH -> {
					fragment.asyncData =
						AddressManagerPresenter.convertToChildAddresses(ethAddresses).toArrayList()
					AddressManagerPresenter.getCurrentAddressIndexByChainType(ChainType.ETH) {
						setDefaultAddress(it, currentETHAndERCAddress, ChainType.ETH)
						Config.updateCurrentEthereumAddress(currentETHAndERCAddress)
					}
				}

				ChainType.ETC -> {
					fragment.asyncData =
						AddressManagerPresenter.convertToChildAddresses(etcAddresses).toArrayList()
					AddressManagerPresenter.getCurrentAddressIndexByChainType(ChainType.ETC) {
						setDefaultAddress(it, currentETCAddress, ChainType.ETC)
						Config.updateCurrentETCAddress(currentETCAddress)
					}
				}

				ChainType.LTC -> {
					fragment.asyncData =
						AddressManagerPresenter.convertToChildAddresses(ltcAddresses).toArrayList()
					AddressManagerPresenter.getCurrentAddressIndexByChainType(ChainType.LTC) {
						setDefaultAddress(it, currentLTCAddress, ChainType.LTC)
						Config.updateCurrentLTCAddress(currentLTCAddress)
					}
				}

				ChainType.BCH -> {
					fragment.asyncData =
						AddressManagerPresenter.convertToChildAddresses(bchAddresses).toArrayList()
					AddressManagerPresenter.getCurrentAddressIndexByChainType(ChainType.BCH) {
						setDefaultAddress(it, currentBCHAddress, ChainType.BCH)
						Config.updateCurrentLTCAddress(currentBCHAddress)
					}
				}

				ChainType.BTC -> {
					val address =
						if (Config.isTestEnvironment()) btcSeriesTestAddresses else btcAddresses
					val currentAddress =
						if (Config.isTestEnvironment()) currentBTCSeriesTestAddress else currentBTCAddress
					fragment.asyncData =
						AddressManagerPresenter.convertToChildAddresses(address).toArrayList()
					AddressManagerPresenter.getCurrentAddressIndexByChainType(ChainType.BTC) {
						setDefaultAddress(it, currentAddress, ChainType.BTC)
						if (Config.isTestEnvironment())
							Config.updateCurrentBTCSeriesTestAddress(currentBTCSeriesTestAddress)
						else Config.updateCurrentBTCAddress(currentBTCAddress)
					}
				}
			}
		}
	}

	private fun setDefaultAddress(index: String, address: String, chainType: ChainType) {
		fragment.recyclerView.getItemAtAdapterPosition<ChainAddressesHeaderView>(0) {
			it.setDefaultAddress(index, address, chainType) {
				showMoreDashboard(this, address, chainType, false)
			}
		}
	}
}