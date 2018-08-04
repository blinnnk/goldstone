package io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.view

import android.content.Context
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AttentionTextView
import io.goldstone.blockchain.common.component.MiniOverlay
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.getViewAbsolutelyPositionInScreen
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
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
	
	private val currentMultiChainAddressesView by lazy {
		AddressesListView(context!!) { moreButton, address, isDefault, title ->
			val chainTYpe = when (title) {
				CryptoSymbol.eth -> ChainType.ETH.id
				CryptoSymbol.etc -> ChainType.ETC.id
				CryptoSymbol.erc -> ChainType.ETH.id
				else -> ChainType.BTC.id
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
	private val ethAndERCAddressesView by lazy {
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
	private val etcAddressesView by lazy {
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
	private val btcAddressesView by lazy {
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
		showCreatorDashboard()
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout parent@{
				gravity = Gravity.CENTER_HORIZONTAL
				lparams {
					width = matchParent
					height = matchParent
					topPadding = 20.uiPX()
				}
				currentMultiChainAddressesView.into(this)
				// 不为空才显示 `bip44` 规则的子地址界面
				WalletTable.getCurrentWallet {
					if (ethAddresses.isNotEmpty()) {
						ethAndERCAddressesView.into(this@parent)
						etcAddressesView.into(this@parent)
						btcAddressesView.into(this@parent)
						ethAndERCAddressesView.checkAllEvent = presenter.showAllETHAndERCAddresses()
						etcAddressesView.checkAllEvent = presenter.showAllETCAddresses()
						btcAddressesView.checkAllEvent = presenter.showAllBTCAddresses()
						presenter.getEthereumAddresses()
						presenter.getEthereumClassicAddresses()
						if (Config.isTestEnvironment()) {
							presenter.getBitcoinTestAddresses()
						} else {
							presenter.getBitcoinAddresses()
						}
					} else {
						hideAddButton()
						attentionView.into(this@parent)
					}
				}
			}
		}
	}
	
	private fun hideAddButton() {
		getParentFragment<WalletSettingsFragment>()?.showAddButton(false)
	}
	
	fun setMultiChainAddresses(model: List<Pair<String, String>>) {
		currentMultiChainAddressesView.setTitle(WalletSettingsText.currentMultiChainAddresses)
		currentMultiChainAddressesView.model = model
	}
	
	fun setEthereumAddressesModel(model: List<Pair<String, String>>) {
		ethAndERCAddressesView.setTitle(WalletSettingsText.ethereumSeriesAddress)
		ethAndERCAddressesView.model = model
	}
	
	fun setEthereumClassicAddressesModel(model: List<Pair<String, String>>) {
		etcAddressesView.setTitle(WalletSettingsText.ethereumClassicAddress)
		etcAddressesView.model = model
	}
	
	fun setBitcoinAddressesModel(model: List<Pair<String, String>>) {
		btcAddressesView.setTitle(WalletSettingsText.bitcoinAddress)
		btcAddressesView.model = model
	}
	
	fun showCreatorDashboard() {
		getParentFragment<WalletSettingsFragment> {
			showAddButton(true, false) {
				getMainActivity()?.getMainContainer()?.apply {
					if (findViewById<MiniOverlay>(ElementID.miniOverlay).isNull()) {
						val creatorDashBoard = MiniOverlay(context) { cell, title ->
							cell.onClick {
								this@getParentFragment.context?.apply {
									AddressManagerFragment.verifyMultiChainWalletPassword(this) {
										createChildAddressByButtonTitle(title, it)
									}
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
		context?.apply {
			when (title) {
				WalletSettingsText.newETHAndERCAddress -> {
					AddressManagerPresneter.createETHAndERCAddress(this, password) {
						ethAndERCAddressesView.model = it
					}
				}
				
				WalletSettingsText.newETCAddress -> {
					AddressManagerPresneter.createETCAddress(this, password) {
						etcAddressesView.model = it
					}
				}
				
				WalletSettingsText.newBTCAddress -> {
					if (Config.isTestEnvironment()) {
						AddressManagerPresneter.createBTCTestAddress(this, password) {
							btcAddressesView.model = it
						}
					} else {
						AddressManagerPresneter.createBTCAddress(this, password) {
							btcAddressesView.model = it
						}
					}
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
			getParentContainer(),
			top,
			isBTC,
			hasDefaultCell,
			setDefaultAddressEvent = {
				AddressManagerPresneter.setDefaultAddress(coinType, address) {
					// 更新默认地址后同时更新首页的列表
					updateWalletDetail()
					when (coinType) {
						ChainType.ETH.id -> presenter.getEthereumAddresses()
						ChainType.ETC.id -> presenter.getEthereumClassicAddresses()
						
						ChainType.BTC.id -> {
							if (Config.isTestEnvironment()) {
								presenter.getBitcoinTestAddresses()
							} else {
								presenter.getBitcoinAddresses()
							}
						}
					}
					toast(CommonText.succeed)
					AddressManagerFragment.removeDashboard(context)
				}
			},
			qrCellClickEvent = { presenter.showQRCodeFragment(address) },
			exportPrivateKey = {
				presenter.showPrivateKeyExportFragment(address)
			},
			exportBTCPrivateKey = {
				presenter.showBTCPrivateKeyExportFragment(address)
			},
			keystoreCellClickEvent = { presenter.showKeystoreExportFragment(address) }
		)
	}
	
	private fun updateWalletDetail() {
		getMainActivity()?.getWalletDetailFragment()?.presenter?.updateData()
	}
	
	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<WalletSettingsFragment> {
			presenter.showWalletSettingListFragment()
		}
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		removeDashboard(context)
	}
	
	companion object {
		fun verifyMultiChainWalletPassword(
			context: Context,
			callback: (password: String) -> Unit
		) {
			context.showAlertView(
				WalletSettingsText.deleteInfoTitle,
				WalletSettingsText.deleteInfoSubtitle,
				!Config.getCurrentIsWatchOnlyOrNot()
			) {
				val password = it?.text.toString()
				context.verifyKeystorePassword(
					password,
					Config.getCurrentBTCAddress(),
					true,
					false
				) {
					if (it) callback(password)
					else context.alert(CommonText.wrongPassword)
				}
			}
			AddressManagerFragment.removeDashboard(context)
		}
		
		fun showMoreDashboard(
			container: ViewGroup?,
			top: Float,
			isBTC: Boolean = false,
			hasDefaultCell: Boolean = true,
			setDefaultAddressEvent: () -> Unit,
			qrCellClickEvent: () -> Unit,
			exportBTCPrivateKey: () -> Unit,
			exportPrivateKey: () -> Unit,
			keystoreCellClickEvent: () -> Unit
		) {
			container?.apply {
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
						AddressManagerPresneter.getCellDashboardMenu(hasDefaultCell)
					creatorDashBoard.into(this)
					// 防止超出屏幕便捷的尺寸弥补
					val overHeightSize = ScreenSize.fullHeight - creatorDashBoard.getOverlayHeight() - top
					creatorDashBoard.setTopValue(top + if (overHeightSize < 0f) overHeightSize else 0f)
				}
			}
		}
		
		fun removeDashboard(context: Context?) {
			(context as? MainActivity)?.getMainContainer()?.apply {
				findViewById<MiniOverlay>(ElementID.miniOverlay)?.removeSelf()
			}
		}
	}
}

