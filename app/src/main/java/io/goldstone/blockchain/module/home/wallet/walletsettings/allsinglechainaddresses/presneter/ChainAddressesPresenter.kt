package io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.presneter

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.getChildFragment
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.Bip44Address
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.model.ContactModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.event.DefaultAddressUpdateEvent
import io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.presenter.AddressManagerPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.view.AddressManagerFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.view.AddressManagerFragment.Companion.showMoreDashboard
import io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.view.AddressManagerFragment.Companion.switchEOSDefaultAddress
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view.ChainAddressesAdapter
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view.ChainAddressesFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import kotlinx.coroutines.Dispatchers
import org.bitcoinj.params.MainNetParams
import org.greenrobot.eventbus.EventBus
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
			showBackButton(true) {
				presenter.popFragmentFrom<ChainAddressesFragment>(false)
			}
			setAddAddressEvent()
		}
	}

	fun showMoreDashboard(bip44Address: Bip44Address, hasDefaultCell: Boolean = true) {
		val coinType = bip44Address.getChainType()
		fragment.wrapper.showMoreDashboard(
			hasDefaultCell,
			BCHWalletUtils.isNewCashAddress(bip44Address.address),
			setDefaultAddressEvent = {
				fun update(bip44Address: Bip44Address, eosAccountName: String) {
					coinType.updateCurrentAddress(bip44Address, eosAccountName) {
						fragment.setDefaultAddress(bip44Address)
						// 更新钱包默认地址, 同时更新首页的数据
						updateWalletDetail()
						// 更新 `AddressManager` 界面的默认地址显示
						EventBus.getDefault().post(DefaultAddressUpdateEvent(coinType))
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
			}
		)
	}

	fun setAddAddressEvent() = fragment.getParentFragment<WalletSettingsFragment> {
		showAddButton(true, false) {
			context?.apply {
				createEvent(this) { addresses, error ->
					if (error.hasError()) safeShowError(error)
					else fragment.coinType?.apply {
						updateAddressManagerDataBy(this)
						addresses?.apply {
							launchUI {
								diffAndUpdateAdapterData<ChainAddressesAdapter>(toArrayList())
							}
						}
					}
				}
			}
		}
	}

	@WorkerThread
	private fun createEvent(context: Context, hold: (addresses: List<Bip44Address>?, error: AccountError) -> Unit) {
		AddressManagerFragment.verifyMultiChainWalletPassword(context) { password, verifyError ->
			if (password.isNullOrEmpty() || verifyError.hasError()) hold(null, verifyError)
			else when {
				fragment.coinType.isETH() ->
					AddressManagerPresenter.createETHSeriesAddress(context, password, hold)
				fragment.coinType.isETC() ->
					AddressManagerPresenter.createETCAddress(context, password, hold)
				fragment.coinType.isLTC() ->
					AddressManagerPresenter.createLTCAddress(context, password, hold)
				fragment.coinType.isEOS() ->
					AddressManagerPresenter.createEOSAddress(context, password, hold)
				fragment.coinType.isBCH() ->
					AddressManagerPresenter.createBCHAddress(context, password, hold)
				fragment.coinType.isBTC() ->
					AddressManagerPresenter.createBTCAddress(context, password, hold)
				fragment.coinType.isAllTest() ->
					AddressManagerPresenter.createBTCTestAddress(context, password, hold)
			}
		}
	}

	private fun updateWalletDetail() {
		fragment.getMainActivity()?.getWalletDetailFragment()?.presenter?.start()
	}

	private fun updateAddressManagerDataBy(chainType: ChainType) {
		fragment.parentFragment?.getChildFragment<AddressManagerFragment>()?.apply {
			WalletTable.getCurrent(Dispatchers.Main) {
				when {
					chainType.isETH() -> setEthereumAddressesModel(this)
					chainType.isETC() -> setEthereumClassicAddressesModel(this)
					chainType.isBTC() -> setBitcoinAddressesModel(this)
					chainType.isBCH() -> setBitcoinCashAddressesModel(this)
					chainType.isLTC() -> setLitecoinAddressesModel(this)
					chainType.isEOS() -> setEOSAddressesModel(this)
					chainType.isAllTest() -> setBTCSeriesTestAddressesModel(this)
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
					fragment.coinType.isAllTest() -> setBTCSeriesTestAddressesModel(this)
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

	fun setAddresses() {
		WalletTable.getCurrent(Dispatchers.Main) {
			when {
				fragment.coinType.isETH() -> {
					diffAndUpdateAdapterData<ChainAddressesAdapter>(ethAddresses.toArrayList())
					fragment.setDefaultAddress(getCurrentBip44Address(ChainType.ETH))
				}

				fragment.coinType.isETC() -> {
					diffAndUpdateAdapterData<ChainAddressesAdapter>(etcAddresses.toArrayList())
					fragment.setDefaultAddress(getCurrentBip44Address(ChainType.ETC))
				}

				fragment.coinType.isLTC() -> {
					diffAndUpdateAdapterData<ChainAddressesAdapter>(ltcAddresses.toArrayList())
					fragment.setDefaultAddress(getCurrentBip44Address(ChainType.LTC))
				}

				fragment.coinType.isEOS() -> {
					diffAndUpdateAdapterData<ChainAddressesAdapter>(eosAddresses.toArrayList())
					fragment.setDefaultAddress(getCurrentBip44Address(ChainType.EOS))
				}

				fragment.coinType.isBCH() -> {
					diffAndUpdateAdapterData<ChainAddressesAdapter>(bchAddresses.toArrayList())
					fragment.setDefaultAddress(getCurrentBip44Address(ChainType.BCH))
				}

				fragment.coinType.isBTC() -> {
					diffAndUpdateAdapterData<ChainAddressesAdapter>(btcAddresses.toArrayList())
					fragment.setDefaultAddress(getCurrentBip44Address(ChainType.BTC))
				}

				fragment.coinType.isAllTest() -> {
					diffAndUpdateAdapterData<ChainAddressesAdapter>(btcSeriesTestAddresses.toArrayList())
					fragment.setDefaultAddress(getCurrentBip44Address(ChainType.AllTest))
				}
			}

			updateDefaultStyle(this)
		}
	}
}