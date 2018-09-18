package io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.view

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.ViewGroup
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AttentionTextView
import io.goldstone.blockchain.common.component.overlay.MiniOverlay
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.keystore.verifyKeystorePassword
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.MultiChainType
import io.goldstone.blockchain.crypto.multichain.isEOS
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.model.ContactModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.presenter.AddressManagerPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.bitcoinj.params.MainNetParams
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast

/**
 * @date 2018/7/11 12:44 AM
 * @author KaySaith
 */
class AddressManagerFragment : BaseFragment<AddressManagerPresenter>() {

	private val currentMultiChainAddressesView by lazy {
		AddressesListView(context!!, 7) { cell, data, wallet, isDefault ->
			val chainType = ChainType.getChainTypeBySymbol(data.second)
			// EOS 需要在默认地址中显示激活状态
			if (CoinSymbol(data.second).isEOS()) {
				presenter.showEOSPublickeyDescription(cell, data.first, wallet)
			}
			cell.moreButton.onClick {
				showCellMoreDashboard(
					cell.moreButton.getViewAbsolutelyPositionInScreen()[1].toFloat(),
					data.first,
					chainType,
					!isDefault
				)
				cell.moreButton.preventDuplicateClicks()
			}
		}
	}
	private val attentionView by lazy {
		AttentionTextView(context!!).apply {
			topPadding = 20.uiPX()
			text = ImportWalletText.notBip44WalletAttention
			isCenter()
		}
	}
	private val ethSeriesView by lazy {
		AddressesListView(context!!, 3) { cell, data, _, isDefault ->
			cell.moreButton.onClick {
				showCellMoreDashboard(
					cell.moreButton.getViewAbsolutelyPositionInScreen()[1].toFloat(),
					data.first,
					MultiChainType.ETH.id,
					!isDefault
				)
				cell.moreButton.preventDuplicateClicks()
			}
		}
	}
	private val etcAddressesView by lazy {
		AddressesListView(context!!, 3) { cell, data, _, isDefault ->
			cell.moreButton.onClick {
				showCellMoreDashboard(
					cell.moreButton.getViewAbsolutelyPositionInScreen()[1].toFloat(),
					data.first,
					MultiChainType.ETC.id,
					!isDefault
				)
				cell.moreButton.preventDuplicateClicks()
			}
		}
	}
	private val btcAddressesView by lazy {
		AddressesListView(context!!, 3) { cell, data, _, isDefault ->
			cell.moreButton.onClick {
				showCellMoreDashboard(
					cell.moreButton.getViewAbsolutelyPositionInScreen()[1].toFloat(),
					data.first,
					MultiChainType.BTC.id,
					!isDefault
				)
				cell.moreButton.preventDuplicateClicks()
			}
		}
	}
	private val ltcAddressesView by lazy {
		AddressesListView(context!!, 3) { cell, data, _, isDefault ->
			cell.moreButton.onClick {
				showCellMoreDashboard(
					cell.moreButton.getViewAbsolutelyPositionInScreen()[1].toFloat(),
					data.first,
					MultiChainType.LTC.id,
					!isDefault
				)
				cell.moreButton.preventDuplicateClicks()
			}
		}
	}

	private val eosAddressesView by lazy {
		AddressesListView(context!!, 3) { cell, data, wallet, isDefault ->
			presenter.showEOSPublickeyDescription(cell, data.first, wallet)
			cell.moreButton.onClick {
				showCellMoreDashboard(
					cell.moreButton.getViewAbsolutelyPositionInScreen()[1].toFloat(),
					data.first,
					MultiChainType.EOS.id,
					!isDefault
				)
				cell.moreButton.preventDuplicateClicks()
			}
		}
	}

	private val bchAddressesView by lazy {
		AddressesListView(context!!, 3) { cell, data, _, isDefault ->
			cell.moreButton.onClick {
				showCellMoreDashboard(
					cell.moreButton.getViewAbsolutelyPositionInScreen()[1].toFloat(),
					data.first,
					MultiChainType.BCH.id,
					!isDefault
				)
				cell.moreButton.preventDuplicateClicks()
			}
		}
	}
	override val presenter = AddressManagerPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		if (Config.getCurrentWalletType().isBIP44()) showCreatorDashboard()
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout parent@{
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, matchParent)
				topPadding = 20.uiPX()
				// 不为空才显示 `bip44` 规则的子地址界面
				WalletTable.getCurrentWallet {
					currentMultiChainAddressesView.into(this@parent)
					currentMultiChainAddressesView.currentWallet = this
					presenter.getMultiChainAddresses(this)
					if (ethAddresses.isNotEmpty()) {
						// ETHSeries List
						ethSeriesView.into(this@parent)
						ethSeriesView.currentWallet = this
						ethSeriesView.checkAllEvent = presenter.showAllETHSeriesAddresses()
						presenter.getEthereumAddresses(this)
						// ETC List
						etcAddressesView.into(this@parent)
						etcAddressesView.currentWallet = this
						etcAddressesView.checkAllEvent = presenter.showAllETCAddresses()
						presenter.getEthereumClassicAddresses(this)
						// BTC List
						btcAddressesView.into(this@parent)
						btcAddressesView.currentWallet = this
						btcAddressesView.checkAllEvent = presenter.showAllBTCAddresses()
						// `比特币` 的主网测试网地址根据环境显示不同的数据
						// EOS List
						eosAddressesView.into(this@parent)
						eosAddressesView.currentWallet = this
						eosAddressesView.checkAllEvent = presenter.showAllEOSAddresses()
						presenter.getEOSAddresses(this)
						if (!Config.isTestEnvironment()) {
							presenter.getBitcoinAddresses(this)
							// 因为比特币系列分叉币的测试地址是公用的, 在测试环境下不额外显示分叉币的地址.
							// BCH List
							bchAddressesView.into(this@parent)
							bchAddressesView.currentWallet = this
							bchAddressesView.checkAllEvent = presenter.showAllBCHAddresses()
							presenter.getBitcoinCashAddresses(this)
							// LTC List
							ltcAddressesView.into(this@parent)
							ltcAddressesView.currentWallet = this
							ltcAddressesView.checkAllEvent = presenter.showAllLTCAddresses()
							presenter.getLitecoinAddresses(this)
						} else {
							presenter.getBitcoinTestAddresses(this)
							presenter.getBitcoinCashTestAddresses(this)
							presenter.getLitecoinTestAddresses(this)
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
		ethSeriesView.setTitle(WalletSettingsText.ethereumSeriesAddress)
		ethSeriesView.model = model
	}

	fun setBitcoinCashAddressesModel(model: List<Pair<String, String>>) {
		bchAddressesView.setTitle(WalletSettingsText.bitcoinCashAddress)
		bchAddressesView.model = model
	}

	fun setEthereumClassicAddressesModel(model: List<Pair<String, String>>) {
		etcAddressesView.setTitle(WalletSettingsText.ethereumClassicAddress)
		etcAddressesView.model = model
	}

	// 测试网络环境下的测试地址是公用的所以这里要额外处理 `Title` 显示
	fun setBitcoinAddressesModel(model: List<Pair<String, String>>) {
		val title = if (Config.isTestEnvironment()) {
			"${CoinSymbol.btc()}/${CoinSymbol.ltc}/${CoinSymbol.bch} Test Addresses"
		} else {
			WalletSettingsText.bitcoinAddress(Config.getYingYongBaoInReviewStatus())
		}
		btcAddressesView.setTitle(title)
		btcAddressesView.model = model
	}

	fun setLitecoinAddressesModel(model: List<Pair<String, String>>) {
		ltcAddressesView.setTitle(WalletSettingsText.litecoinAddress)
		ltcAddressesView.model = model
	}

	fun setEOSAddressesModel(model: List<Pair<String, String>>) {
		eosAddressesView.setTitle(WalletSettingsText.eosAddress)
		eosAddressesView.model = model
	}

	fun showCreatorDashboard() {
		getParentFragment<WalletSettingsFragment> {
			showAddButton(true, false) {
				getMainActivity()?.getMainContainer()?.apply {
					if (findViewById<MiniOverlay>(ElementID.miniOverlay).isNull()) {
						val creatorDashBoard = MiniOverlay(context) { cell, title ->
							cell.onClick { _ ->
								this@getParentFragment.context?.apply {
									verifyMultiChainWalletPassword(this) {
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
				WalletSettingsText.newETHAndERCAddress ->
					AddressManagerPresenter.createETHAndERCAddress(this, password) {
						ethSeriesView.model = it
					}

				WalletSettingsText.newETCAddress ->
					AddressManagerPresenter.createETCAddress(this, password) {
						etcAddressesView.model = it
					}

				WalletSettingsText.newEOSAddress ->
					AddressManagerPresenter.createEOSAddress(this, password) {
						eosAddressesView.model = it
					}

				WalletSettingsText.newLTCAddress -> {
					if (Config.isTestEnvironment()) {
						AddressManagerPresenter.createBTCTestAddress(this, password) {
							btcAddressesView.model = it
						}
					} else {
						AddressManagerPresenter.createLTCAddress(this, password) {
							ltcAddressesView.model = it
						}
					}
				}

				WalletSettingsText.newBCHAddress -> {
					if (Config.isTestEnvironment()) {
						AddressManagerPresenter.createBTCTestAddress(this, password) {
							btcAddressesView.model = it
						}
					} else {
						AddressManagerPresenter.createBCHAddress(this, password) {
							bchAddressesView.model = it
						}
					}
				}

				WalletSettingsText.newBTCAddress -> {
					if (Config.isTestEnvironment()) {
						AddressManagerPresenter.createBTCTestAddress(this, password) {
							btcAddressesView.model = it
						}
					} else {
						AddressManagerPresenter.createBTCAddress(this, password) {
							btcAddressesView.model = it
						}
					}
				}
			}
		}
	}

	private fun showCellMoreDashboard(top: Float, address: String, coinType: Int, hasDefaultCell: Boolean) {
		AddressManagerFragment.showMoreDashboard(
			getParentContainer(),
			top,
			hasDefaultCell,
			BCHWalletUtils.isNewCashAddress(address),
			setDefaultAddressEvent = {
				ChainType(coinType).updateCurrentAddress(address) { isSwitchEOSAddress ->
					// 如果是更改了 `EOS` 的默认地址, 那么跳回首页重新走检测 `AccountName` 流程
					if (isSwitchEOSAddress) showSwitchEOSAddressAlertAndJump(context)
					// 更新默认地址后同时更新首页的列表
					else {
						updateWalletDetail()
						WalletTable.getCurrentWallet {
							when (coinType) {
								MultiChainType.ETH.id -> presenter.getEthereumAddresses(this)
								MultiChainType.ETC.id -> presenter.getEthereumClassicAddresses(this)
								MultiChainType.EOS.id -> presenter.getEOSAddresses(this)
								MultiChainType.LTC.id -> {
									if (Config.isTestEnvironment()) presenter.getLitecoinTestAddresses(this)
									else presenter.getLitecoinAddresses(this)
								}
								MultiChainType.BTC.id -> {
									if (Config.isTestEnvironment()) presenter.getBitcoinTestAddresses(this)
									else presenter.getBitcoinAddresses(this)
								}
							}
						}
						toast(CommonText.succeed)
						AddressManagerFragment.removeDashboard(context)
					}
				}
			},
			qrCellClickEvent = {
				getParentFragment<WalletSettingsFragment> {
					// `BCH` 需要在二维码页面下转换 `CashAddress` 和 `Legacy` 所以需要标记 `BCH Symbol`
					val symbol = if (ChainType(coinType).isBCH()) CoinSymbol.bch else ""
					AddressManagerPresenter
						.showQRCodeFragment(ContactModel(address, symbol), this)
				}
			},
			exportPrivateKey = {
				getParentFragment<WalletSettingsFragment> {
					AddressManagerPresenter.showPrivateKeyExportFragment(address, coinType, this)
				}
			},
			keystoreCellClickEvent = {
				getParentFragment<WalletSettingsFragment> {
					AddressManagerPresenter.showKeystoreExportFragment(address, this)
				}
			},
			convertBCHAddressToLegacy = {
				val legacyAddress = BCHWalletUtils.formattedToLegacy(address, MainNetParams.get())
				context.alert(legacyAddress)
				context?.clickToCopy(legacyAddress)
				AddressManagerFragment.removeDashboard(context)
			}
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

		fun showSwitchEOSAddressAlertAndJump(context: Context?) {
			context?.showAlertView(
				"Switching EOS Address",
				"eos account depends on eos public key, if you switch default address we will re-check the account name for you",
				false
			) {
				(context as? Activity)?.jump<SplashActivity>()
			}
		}

		fun verifyMultiChainWalletPassword(
			context: Context,
			callback: (password: String) -> Unit
		) {
			context.showAlertView(
				WalletSettingsText.createSubAccount,
				WalletSettingsText.createSubAccountIntro,
				!Config.getCurrentIsWatchOnlyOrNot()
			) { passwordInput ->
				val password = passwordInput?.text.toString()
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
			hasDefaultCell: Boolean = true,
			isCashAddress: Boolean,
			setDefaultAddressEvent: () -> Unit,
			qrCellClickEvent: () -> Unit,
			exportPrivateKey: () -> Unit,
			keystoreCellClickEvent: () -> Unit,
			convertBCHAddressToLegacy: () -> Unit
		) {
			container?.apply {
				if (findViewById<MiniOverlay>(ElementID.miniOverlay).isNull()) {
					val creatorDashBoard = MiniOverlay(context) { cell, title ->
						cell.onClick {
							when (title) {
								WalletText.setDefaultAddress -> setDefaultAddressEvent()
								WalletText.showQRCode -> qrCellClickEvent()
								WalletSettingsText.exportPrivateKey -> exportPrivateKey()
								WalletSettingsText.exportKeystore -> keystoreCellClickEvent()
								WalletText.getBCHLegacyAddress -> convertBCHAddressToLegacy()
							}
							cell.preventDuplicateClicks()
						}
					}
					creatorDashBoard.model =
						AddressManagerPresenter.getCellDashboardMenu(hasDefaultCell, isCashAddress)
					creatorDashBoard.into(this)
					// 防止超出屏幕便捷的尺寸弥补
					val overHeightSize =
						ScreenSize.fullHeight - creatorDashBoard.getOverlayHeight() - top
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

