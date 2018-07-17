package io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.view

import android.support.v4.app.Fragment
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AttentionTextView
import io.goldstone.blockchain.common.component.MiniOverlay
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.getViewAbsolutelyPositionInScreen
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.verifyKeystorePassword
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.presenter.AddressManagerPresneter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast

/**
 * @date 2018/7/11 12:44 AM
 * @author KaySaith
 */
class AddressManagerFragment : BaseFragment<AddressManagerPresneter>() {
	
	private val currentMultiChainAddresses by lazy {
		AddressesListView(context!!) { moreButton, address, isDefault, title ->
			val chainTYpe = when (title) {
				CryptoSymbol.eth -> ChainType.ETH.id
				CryptoSymbol.etc -> ChainType.ETC.id
				CryptoSymbol.btc -> ChainType.BTC.id
				CryptoSymbol.erc -> ChainType.ETH.id
				else -> ChainType.BTCTest.id
			}
			moreButton.onClick {
				showCellMoreDashboard(
					moreButton.getViewAbsolutelyPositionInScreen()[1].toFloat(),
					address,
					chainTYpe,
					!isDefault
				)
				moreButton.preventDuplicateClicks()
			}
		}
	}
	private val attentionView by lazy {
		AttentionTextView(context!!).apply {
			text = ImportWalletText.notBip44WalletAttention
			setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
			isCenter()
		}
	}
	private val ethAndERCAddresses by lazy {
		AddressesListView(context!!) { moreButton, address, isDefault, _ ->
			moreButton.onClick {
				showCellMoreDashboard(
					moreButton.getViewAbsolutelyPositionInScreen()[1].toFloat(),
					address,
					ChainType.ETH.id,
					!isDefault
				)
				moreButton.preventDuplicateClicks()
			}
		}
	}
	private val etcAddresses by lazy {
		AddressesListView(context!!) { moreButton, address, isDefault, _ ->
			moreButton.onClick {
				showCellMoreDashboard(
					moreButton.getViewAbsolutelyPositionInScreen()[1].toFloat(),
					address,
					ChainType.ETC.id,
					!isDefault
				)
				moreButton.preventDuplicateClicks()
			}
		}
	}
	private val btcAddresses by lazy {
		AddressesListView(context!!) { moreButton, address, isDefault, _ ->
			moreButton.onClick {
				showCellMoreDashboard(
					moreButton.getViewAbsolutelyPositionInScreen()[1].toFloat(),
					address,
					ChainType.BTC.id,
					!isDefault
				)
				moreButton.preventDuplicateClicks()
			}
		}
	}
	override val presenter = AddressManagerPresneter(this)
	
	override fun AnkoContext<Fragment>.initView() {
		showChildAddressCreatorDashboard()
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams {
					leftPadding = PaddingSize.device
					topPadding = 20.uiPX()
				}
				currentMultiChainAddresses.into(this)
				// 不为空才显示 `bip44` 规则的子地址界面
				WalletTable.getCurrentWallet {
					if (it?.ethAddresses?.isNotEmpty() == true) {
						ethAndERCAddresses.into(this)
						etcAddresses.into(this)
						btcAddresses.into(this)
						ethAndERCAddresses.checkAllEvent = presenter.showAllETHAndERCAddresses()
						etcAddresses.checkAllEvent = presenter.showAllETCAddresses()
						btcAddresses.checkAllEvent = presenter.showAllBTCAddresses()
						presenter.getEthereumAddresses()
						presenter.getEthereumClassicAddresses()
						presenter.getBitcoinAddresses()
					} else {
						attentionView.into(this)
					}
				}
			}
		}
	}
	
	fun setMultiChainAddresses(model: List<Pair<String, String>>) {
		currentMultiChainAddresses.setTitle(WalletSettingsText.currentMultiChainAddresses)
		currentMultiChainAddresses.model = model
	}
	
	fun setEthereumAddressesModel(model: List<Pair<String, String>>) {
		ethAndERCAddresses.setTitle(WalletSettingsText.ethereumSeriesAddress)
		ethAndERCAddresses.model = model
	}
	
	fun setEthereumClassicAddressesModel(model: List<Pair<String, String>>) {
		etcAddresses.setTitle(WalletSettingsText.ethereumClassicAddress)
		etcAddresses.model = model
	}
	
	fun setBitcoinAddressesModel(model: List<Pair<String, String>>) {
		btcAddresses.setTitle(WalletSettingsText.bitcoinAddress)
		btcAddresses.model = model
	}
	
	fun showChildAddressCreatorDashboard() {
		getParentFragment<WalletSettingsFragment> {
			overlayView.header.showAddButton(true, false) {
				getMainActivity()?.getMainContainer()?.apply {
					if (findViewById<MiniOverlay>(ElementID.miniOverlay).isNull()) {
						val creatorDashBoard = MiniOverlay(context) { cell, title ->
							cell.onClick {
								AddressManagerFragment.verifyPassword(this@getParentFragment) {
									createChildAddressByButtonTitle(title, it)
								}
								cell.preventDuplicateClicks()
							}
						}
						creatorDashBoard.model =
							this@AddressManagerFragment.presenter.getAddressCreatorMenu()
						creatorDashBoard.into(this)
						creatorDashBoard.setTopRight()
					}
				}
			}
		}
	}
	
	private fun createChildAddressByButtonTitle(title: String, password: String) {
		when (title) {
			WalletSettingsText.newETHAndERCAddress -> {
				AddressManagerPresneter.createETHAndERCAddress(context, password) {
					ethAndERCAddresses.model = it
				}
			}
			
			WalletSettingsText.newETCAddress -> {
				AddressManagerPresneter.createETCAddress(context, password) {
					etcAddresses.model = it
				}
			}
			
			WalletSettingsText.newBTCAddress -> {
				AddressManagerPresneter.createBTCAddress(context, password) {
					btcAddresses.model = it
				}
			}
		}
	}
	
	private fun showCellMoreDashboard(
		top: Float,
		address: String,
		coinType: Int,
		hasDefaultCell: Boolean
	) {
		val isBTC = ChainType.BTC.id == coinType
		AddressManagerFragment.showMoreDashboard(
			this,
			top,
			isBTC,
			hasDefaultCell,
			setDefaultAddressEvent = {
				AddressManagerPresneter.setDefaultAddress(coinType, address) {
					when (coinType) {
						ChainType.ETH.id -> presenter.getEthereumAddresses()
						ChainType.ETC.id -> presenter.getEthereumClassicAddresses()
						ChainType.BTC.id -> presenter.getBitcoinAddresses()
						ChainType.BTCTest.id -> presenter.getBitcoinAddresses()
					}
					toast(CommonText.succeed)
					AddressManagerFragment.removeDashboard(this)
				}
			},
			qrCellClickEvent = { presenter.showQRCodeFragment(address) },
			exportPrivateKey = { presenter.showPrivateKeyExportFragment(address) },
			keystoreCellClickEvent = { presenter.showKeystoreExportFragment(address) },
			exportBTCPrivateKey = { presenter.showBTCPrivateKeyExportFragment(address) }
		)
	}
	
	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<WalletSettingsFragment> {
			presenter.showWalletSettingListFragment()
		}
	}
	
	companion object {
		
		fun verifyPassword(
			fragment: Fragment,
			callback: (password: String) -> Unit
		) {
			fragment.context?.showAlertView(
				WalletSettingsText.deleteInfoTitle,
				WalletSettingsText.deleteInfoSubtitle,
				!Config.getCurrentIsWatchOnlyOrNot()
			) {
				val password = it?.text.toString()
				fragment.context?.verifyKeystorePassword(password) {
					if (it) {
						callback(password)
					} else {
						fragment.context.alert(CommonText.wrongPassword)
					}
				}
			}
			AddressManagerFragment.removeDashboard(fragment)
		}
		
		fun showMoreDashboard(
			fragment: Fragment,
			top: Float,
			isBTC: Boolean = false,
			hasDefaultCell: Boolean = true,
			setDefaultAddressEvent: () -> Unit,
			qrCellClickEvent: () -> Unit,
			exportBTCPrivateKey: () -> Unit,
			exportPrivateKey: () -> Unit,
			keystoreCellClickEvent: () -> Unit
		) {
			fragment.getMainActivity()?.getMainContainer()?.apply {
				if (findViewById<MiniOverlay>(ElementID.miniOverlay).isNull()) {
					val creatorDashBoard = MiniOverlay(context) { cell, title ->
						cell.onClick {
							when (title) {
								WalletText.setDefaultAddress -> setDefaultAddressEvent()
								WalletText.showQRCode -> qrCellClickEvent()
								
								WalletSettingsText.exportPrivateKey -> {
									if (isBTC) exportBTCPrivateKey()
									else exportPrivateKey()
								}
								
								WalletSettingsText.exportKeystore -> keystoreCellClickEvent()
							}
							cell.preventDuplicateClicks()
						}
					}
					creatorDashBoard.model =
						AddressManagerPresneter.getCellDashboardMenu(isBTC, hasDefaultCell)
					creatorDashBoard.into(this)
					creatorDashBoard.setTopValue(top)
				}
			}
		}
		
		fun removeDashboard(fragment: Fragment) {
			fragment.getMainActivity()?.getMainContainer()?.apply {
				findViewById<MiniOverlay>(ElementID.miniOverlay)?.removeSelf()
			}
		}
	}
}

