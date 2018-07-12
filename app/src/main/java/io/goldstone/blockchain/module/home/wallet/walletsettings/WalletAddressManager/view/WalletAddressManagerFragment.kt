package io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.view

import android.support.v4.app.Fragment
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.isNull
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.MiniOverlay
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.getViewAbsolutelyPositionInScreen
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.common.value.WalletText
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
	
	private val ethereumAddress by lazy {
		EthereumSeriesAddress(context!!) {
			onClick {
				showCellMoreDashboard(getViewAbsolutelyPositionInScreen()[1].toFloat())
				preventDuplicateClicks()
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
				ethereumAddress.into(this)
			}
		}
	}
	
	fun setEthereumAddressModel(model: List<String>) {
		ethereumAddress.model = model
	}
	
	private fun showChildAddressCreatorDashboard() {
		getParentFragment<WalletSettingsFragment> {
			overlayView.header.showAddButton(true, false) {
				getMainActivity()?.getMainContainer()?.apply {
					if (findViewById<MiniOverlay>(ElementID.miniOverlay).isNull()) {
						creatorDashBoard = MiniOverlay(context) { cell, title ->
							cell.onClick {
								if (title.equals(WalletSettingsText.newEthereumAddress, true)) {
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
	
	private fun createNewEthereumChildAddress() {
	
	}
	
	private fun showCellMoreDashboard(top: Float) {
		getMainActivity()?.getMainContainer()?.apply {
			if (findViewById<MiniOverlay>(ElementID.miniOverlay).isNull()) {
				creatorDashBoard = MiniOverlay(context) { cell, title ->
					cell.onClick {
						when (title) {
							WalletText.setDefaultAddress -> {
							}
							
							WalletText.showQRCode -> presenter.showQRCodeFragment()
							WalletSettingsText.exportPrivateKey -> presenter.showPrivateKeyExportFragment()
							WalletSettingsText.exportKeystore -> presenter.showKeystoreExportFragment()
						}
						cell.preventDuplicateClicks()
					}
				}
				creatorDashBoard?.model = presenter.getCellDashboardMenu()
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

