package io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.view

import android.support.v4.app.Fragment
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.isNull
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.MiniOverlay
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.getViewAbsolutelyPositionInScreen
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.verifyKeystorePassword
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.presenter.WalletAddressManagerPresneter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 2018/7/11 12:44 AM
 * @author KaySaith
 */
class WalletAddressManagerFragment : BaseFragment<WalletAddressManagerPresneter>() {
	
	private val ethereumAddresses by lazy {
		AddressesListView(context!!) { cell, address ->
			cell.onClick {
				showCellMoreDashboard(cell.getViewAbsolutelyPositionInScreen()[1].toFloat(), address)
				cell.preventDuplicateClicks()
			}
		}
	}
	private val ethereumClassicAddresses by lazy {
		AddressesListView(context!!) { cell, address ->
			cell.onClick {
				showCellMoreDashboard(cell.getViewAbsolutelyPositionInScreen()[1].toFloat(), address)
				cell.preventDuplicateClicks()
			}
		}
	}
	private val bitcoinAddresses by lazy {
		AddressesListView(context!!) { cell, address ->
			cell.onClick {
				showCellMoreDashboard(
					cell.getViewAbsolutelyPositionInScreen()[1].toFloat(),
					address,
					true
				)
				cell.preventDuplicateClicks()
			}
		}
	}
	var creatorDashBoard: MiniOverlay? = null
	override val presenter = WalletAddressManagerPresneter(this)
	
	override fun AnkoContext<Fragment>.initView() {
		showChildAddressCreatorDashboard()
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams {
					leftPadding = PaddingSize.device
					topPadding = 20.uiPX()
				}
				ethereumAddresses.into(this)
				ethereumClassicAddresses.into(this)
				bitcoinAddresses.into(this)
			}
		}
	}
	
	fun setEthereumAddressesModel(model: List<String>) {
		ethereumAddresses.setTitle(WalletSettingsText.ethereumSeriesAddress)
		ethereumAddresses.model = model
	}
	
	fun setEthereumClassicAddressesModel(model: List<String>) {
		ethereumClassicAddresses.setTitle(WalletSettingsText.ethereumClassicAddress)
		ethereumClassicAddresses.model = model
	}
	
	fun setBitcoinAddressesModel(model: List<String>) {
		bitcoinAddresses.setTitle(WalletSettingsText.bitcoinAddress)
		bitcoinAddresses.model = model
	}
	
	private fun showChildAddressCreatorDashboard() {
		getParentFragment<WalletSettingsFragment> {
			overlayView.header.showAddButton(true, false) {
				getMainActivity()?.getMainContainer()?.apply {
					if (findViewById<MiniOverlay>(ElementID.miniOverlay).isNull()) {
						creatorDashBoard = MiniOverlay(context) { cell, title ->
							cell.onClick {
								verifyPassword {
									createChildAddressByButtonTitle(title, it)
								}
								cell.preventDuplicateClicks()
							}
						}
						creatorDashBoard?.model =
							this@WalletAddressManagerFragment.presenter.getAddressCreatorMenu()
						creatorDashBoard?.into(this)
						creatorDashBoard?.setTopRight()
					}
				}
			}
		}
	}
	
	private fun verifyPassword(callback: (password: String) -> Unit) {
		context?.showAlertView(
			WalletSettingsText.deleteInfoTitle,
			WalletSettingsText.deleteInfoSubtitle,
			!Config.getCurrentIsWatchOnlyOrNot()
		) {
			val password = it?.text.toString()
			context?.verifyKeystorePassword(password) {
				if (it) {
					callback(password)
				} else {
					context.alert(CommonText.wrongPassword)
				}
			}
		}
		creatorDashBoard?.removeSelf()
	}
	
	private fun createChildAddressByButtonTitle(title: String, password: String) {
		when (title) {
			WalletSettingsText.newETHAndERCAddress -> {
				presenter.createNewETHAndERCChildAddress(password) {
					ethereumAddresses.model = it
				}
			}
			
			WalletSettingsText.newETCAddress -> {
				presenter.createNewETCChildAddress(password) {
					ethereumClassicAddresses.model = it
				}
			}
			
			WalletSettingsText.newBTCAddress -> {
				presenter.createNewBTCChildAddress(password) {
					bitcoinAddresses.model = it
				}
			}
		}
	}
	
	private fun showCellMoreDashboard(top: Float, address: String, isBTC: Boolean = false) {
		getMainActivity()?.getMainContainer()?.apply {
			if (findViewById<MiniOverlay>(ElementID.miniOverlay).isNull()) {
				creatorDashBoard = MiniOverlay(context) { cell, title ->
					cell.onClick {
						when (title) {
							WalletText.setDefaultAddress -> {
							}
							
							WalletText.showQRCode -> presenter.showQRCodeFragment(address)
							
							WalletSettingsText.exportPrivateKey -> {
								if (isBTC)  presenter.showBTCPrivateKeyExportFragment(address)
								else presenter.showPrivateKeyExportFragment(address)
							}
							
							WalletSettingsText.exportKeystore -> presenter.showKeystoreExportFragment(address)
						}
						cell.preventDuplicateClicks()
					}
				}
				creatorDashBoard?.model = presenter.getCellDashboardMenu(isBTC)
				creatorDashBoard?.into(this)
				creatorDashBoard?.setTopValue(top)
			}
		}
	}
	
	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<WalletSettingsFragment> {
			presenter.showWalletSettingListFragment()
		}
	}
}

