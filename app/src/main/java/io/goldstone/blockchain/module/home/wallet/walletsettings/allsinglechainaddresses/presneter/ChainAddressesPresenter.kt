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
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.Bip44Address
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.model.ContactModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.presenter.AddressManagerPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.view.AddressManagerFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.view.AddressManagerFragment.Companion.showMoreDashboard
import io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.view.AddressManagerFragment.Companion.switchEOSDefaultAddress
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view.ChainAddressesAdapter
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view.ChainAddressesFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view.ChainAddressesHeaderView
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.bitcoinj.params.MainNetParams
import org.jetbrains.anko.support.v4.toast

/**
 * @date 2018/7/16 6:07 PM
 * @author KaySaith
 */
class ChainAddressesPresenter(
	override val fragment: ChainAddressesFragment
) : BaseRecyclerPresenter<ChainAddressesFragment, Bip44Address>() {

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
		bip44Address: Bip44Address,
		hasDefaultCell: Boolean = true
	) {
		val coinType = bip44Address.getChainType()
		showMoreDashboard(
			fragment.wrapper,
			cell.getViewAbsolutelyPositionInScreen()[1].toFloat(),
			hasDefaultCell,
			BCHWalletUtils.isNewCashAddress(bip44Address.address),
			setDefaultAddressEvent = {
				fun update(bip44Address: Bip44Address, eosAccountName: String) {
					coinType.updateCurrentAddress(bip44Address, eosAccountName) { _ ->
						// 更新钱包默认地址, 同时更新首页的数据
						updateWalletDetail()
						updateData()
						AddressManagerFragment.removeDashboard(fragment.context)
						fragment.toast(CommonText.succeed)
					}
				}
				// `EOS` 和其他链的切换默认地址的逻辑不同
				if (coinType.isEOS()) switchEOSDefaultAddress(fragment.context, bip44Address.address) { accountName ->
					update(bip44Address, accountName)
				} else update(bip44Address, bip44Address.address)
			},
			qrCellClickEvent = {
				val symbol = if (coinType.isBCH()) CoinSymbol.bch else ""
				showQRCode(ContactModel(bip44Address.address, symbol))
			},
			keystoreCellClickEvent = {
				showKeystoreExportFragment(bip44Address.address)
			},
			exportPrivateKey = {
				showPrivateKeyExportFragment(bip44Address.address, coinType)
			},
			convertBCHAddressToLegacy = {
				val legacyAddress = BCHWalletUtils.formattedToLegacy(bip44Address.address, MainNetParams.get())
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
								AddressManagerPresenter.createETHSeriesAddress(this, password) {
									updateAddressManagerDataBy(ChainType.ETH)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it.toArrayList())
								}
							fragment.coinType.isETC() ->
								AddressManagerPresenter.createETCAddress(this, password) {
									updateAddressManagerDataBy(ChainType.ETC)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it.toArrayList())
								}

							fragment.coinType.isLTC() ->
								AddressManagerPresenter.createLTCAddress(this, password) {
									updateAddressManagerDataBy(ChainType.LTC)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it.toArrayList())
								}

							fragment.coinType.isEOS() ->
								AddressManagerPresenter.createEOSAddress(this, password) {
									updateAddressManagerDataBy(ChainType.EOS)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it.toArrayList())
								}

							fragment.coinType.isBCH() ->
								AddressManagerPresenter.createBCHAddress(this, password) {
									updateAddressManagerDataBy(ChainType.BCH)
									diffAndUpdateAdapterData<ChainAddressesAdapter>(it.toArrayList())
								}

							fragment.coinType.isBTC() -> {
								if (SharedValue.isTestEnvironment()) {
									AddressManagerPresenter.createBTCTestAddress(this, password) {
										updateAddressManagerDataBy(ChainType.AllTest)
										diffAndUpdateAdapterData<ChainAddressesAdapter>(it.toArrayList())
									}
								} else {
									AddressManagerPresenter.createBTCAddress(this, password) {
										updateAddressManagerDataBy(ChainType.BTC)
										diffAndUpdateAdapterData<ChainAddressesAdapter>(it.toArrayList())
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

	private fun updateAddressManagerDataBy(chainType: ChainType) {
		fragment.parentFragment?.getChildFragment<AddressManagerFragment>()?.apply {
			WalletTable.getCurrentWallet {
				when {
					chainType.isETH() -> setEthereumAddressesModel(this)
					chainType.isETC() -> setEthereumClassicAddressesModel(this)
					chainType.isBTC() -> setBitcoinAddressesModel(this)
					chainType.isBCH() -> setBitcoinCashAddressesModel(this)
					chainType.isLTC() -> setLitecoinAddressesModel(this)
					chainType.isEOS() -> setEOSAddressesModel(this)
					chainType.isAllTest() -> setBitcoinAddressesModel(this)
				}
			}
		}
	}

	private fun updateDefaultStyle(walletTable: WalletTable) {
		fragment.parentFragment?.getChildFragment<AddressManagerFragment>()?.apply {
			walletTable.apply {
				when {
					fragment.coinType.isETH() -> setEthereumAddressesModel(this)
					fragment.coinType.isETC() -> setEthereumClassicAddressesModel(this)
					fragment.coinType.isLTC() -> setLitecoinAddressesModel(this)
					fragment.coinType.isBCH() -> setBitcoinCashAddressesModel(this)
					fragment.coinType.isEOS() -> setEOSAddressesModel(this)
					fragment.coinType.isBTC() -> setBitcoinAddressesModel(this)
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
					fragment.asyncData = ethAddresses.toArrayList()
					setDefaultAddress(getCurrentBip44Address(ChainType.ETH))
				}

				fragment.coinType.isETC() -> {
					fragment.asyncData = etcAddresses.toArrayList()
					setDefaultAddress(getCurrentBip44Address(ChainType.ETC))
				}

				fragment.coinType.isLTC() -> {
					fragment.asyncData = ltcAddresses.toArrayList()
					setDefaultAddress(getCurrentBip44Address(ChainType.LTC))
				}

				fragment.coinType.isEOS() -> {
					fragment.asyncData = eosAddresses.toArrayList()
					setDefaultAddress(getCurrentBip44Address(ChainType.EOS))
				}

				fragment.coinType.isBCH() -> {
					fragment.asyncData = bchAddresses.toArrayList()
					setDefaultAddress(getCurrentBip44Address(ChainType.BCH))
				}

				fragment.coinType.isBTC() -> {
					val addresses = btcAddresses
					fragment.asyncData = addresses.toArrayList()
					setDefaultAddress(getCurrentBip44Address(ChainType.BTC))
				}

				fragment.coinType.isAllTest() -> {
					val addresses = btcSeriesTestAddresses
					fragment.asyncData = addresses.toArrayList()
					setDefaultAddress(getCurrentBip44Address(ChainType.AllTest))
				}
			}

			updateDefaultStyle(this)
		}
	}

	private fun setDefaultAddress(bip44Address: Bip44Address) {
		fragment.recyclerView.getItemAtAdapterPosition<ChainAddressesHeaderView>(0) {
			it.setDefaultAddress(bip44Address) {
				showMoreDashboard(this, bip44Address, false)
			}
		}
	}
}