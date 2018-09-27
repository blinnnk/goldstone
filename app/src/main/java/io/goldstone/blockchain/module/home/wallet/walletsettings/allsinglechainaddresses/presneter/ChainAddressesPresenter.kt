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
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.multichain.*
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
						when {
							fragment.coinType.isETH() ->
								AddressManagerPresenter.createETHAndERCAddress(this, password) {
									updateAddressManagerDataBy(ChainType.ETH, it)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
								}
							fragment.coinType.isETC() ->
								AddressManagerPresenter.createETCAddress(this, password) {
									updateAddressManagerDataBy(ChainType.ETC, it)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
								}

							fragment.coinType.isLTC() ->
								AddressManagerPresenter.createLTCAddress(this, password) {
									updateAddressManagerDataBy(ChainType.LTC, it)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
								}

							fragment.coinType.isEOS() ->
								AddressManagerPresenter.createEOSAddress(this, password) {
									updateAddressManagerDataBy(ChainType.EOS, it)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
								}

							fragment.coinType.isBCH() ->
								AddressManagerPresenter.createBCHAddress(this, password) {
									updateAddressManagerDataBy(ChainType.BCH, it)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it)
								}

							fragment.coinType.isBTC() -> {
								if (SharedValue.isTestEnvironment()) {
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
			when {
				chainType.isETH() -> setEthereumAddressesModel(data)
				chainType.isETC() -> setEthereumClassicAddressesModel(data)
				chainType.isBTC() -> setBitcoinAddressesModel(data)
				chainType.isBCH() -> setBitcoinCashAddressesModel(data)
				chainType.isLTC() -> setLitecoinAddressesModel(data)
				chainType.isEOS() -> setEOSAddressesModel(data)
				chainType.isAllTest() -> setBitcoinAddressesModel(data)
			}
		}
	}

	private fun updateDefaultStyle(walletTable: WalletTable) {
		fragment.parentFragment?.getChildFragment<AddressManagerFragment>()?.apply {
			walletTable.apply {
				when {
					fragment.coinType.isETH() -> presenter.getEthereumAddresses(this)
					fragment.coinType.isETC() -> presenter.getEthereumClassicAddresses(this)
					fragment.coinType.isLTC() -> presenter.getLitecoinAddresses(this)
					fragment.coinType.isBCH() -> presenter.getBitcoinCashAddresses(this)
					fragment.coinType.isEOS() -> presenter.getEOSAddresses(this)
					fragment.coinType.isBTC() -> {
						if (SharedValue.isTestEnvironment()) presenter.getBitcoinTestAddresses(this)
						else presenter.getBitcoinAddresses(this)
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
			when {
				fragment.coinType.isETH() -> {
					fragment.asyncData =
						AddressManagerPresenter.convertToChildAddresses(ethAddresses).toArrayList()
					getAddressIndexByChainType(ChainType.ETH) {
						setDefaultAddress(it, currentETHSeriesAddress, ChainType.ETH)
					}
				}

				fragment.coinType.isETC() -> {
					fragment.asyncData =
						AddressManagerPresenter.convertToChildAddresses(etcAddresses).toArrayList()
					getAddressIndexByChainType(ChainType.ETC) {
						setDefaultAddress(it, currentETCAddress, ChainType.ETC)
					}
				}

				fragment.coinType.isLTC() -> {
					fragment.asyncData =
						AddressManagerPresenter.convertToChildAddresses(ltcAddresses).toArrayList()
					getAddressIndexByChainType(ChainType.LTC) {
						setDefaultAddress(it, currentLTCAddress, ChainType.LTC)
					}
				}

				fragment.coinType.isEOS() -> {
					fragment.asyncData =
						AddressManagerPresenter.convertToChildAddresses(eosAddresses).toArrayList()
					getAddressIndexByChainType(ChainType.EOS) {
						setDefaultAddress(it, currentEOSAddress, ChainType.EOS)
					}
				}

				fragment.coinType.isBCH() -> {
					fragment.asyncData =
						AddressManagerPresenter.convertToChildAddresses(bchAddresses).toArrayList()
					getAddressIndexByChainType(ChainType.BCH) {
						setDefaultAddress(it, currentBCHAddress, ChainType.BCH)
					}
				}

				fragment.coinType.isBTC() -> {
					val addresses =
						if (SharedValue.isTestEnvironment()) btcSeriesTestAddresses else btcAddresses
					val currentAddress =
						if (SharedValue.isTestEnvironment()) currentBTCSeriesTestAddress else currentBTCAddress
					fragment.asyncData =
						AddressManagerPresenter.convertToChildAddresses(addresses).toArrayList()
					getAddressIndexByChainType(ChainType.BTC) {
						setDefaultAddress(it, currentAddress, ChainType.BTC)
					}
				}
			}

			updateDefaultStyle(this)
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