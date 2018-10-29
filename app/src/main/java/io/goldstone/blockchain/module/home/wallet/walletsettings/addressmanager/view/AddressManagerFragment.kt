package io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.view

import android.content.Context
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.ViewGroup
import com.blinnnk.animation.scale
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.overlay.MiniOverlay
import io.goldstone.blockchain.common.component.title.AttentionTextView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.keystore.verifyKeystorePassword
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.Bip44Address
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.model.ContactModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.presenter.AddressManagerPresenter
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

	override val pageTitle: String = WalletSettingsText.viewAddresses
	private val currentMultiChainAddressesView by lazy {
		AddressesListView(context!!, 7) { cell, data, wallet, isDefault ->
			val chainType = data.getChainType()
			// EOS 需要在默认地址中显示激活状态
			if (chainType.isEOS()) {
				presenter.showEOSPublickeyDescription(cell, data.address, wallet)
			}
			cell.moreButton.onClick {
				showCellMoreDashboard(
					cell.moreButton.getViewAbsolutelyPositionInScreen()[1].toFloat(),
					data,
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
					data,
					ChainType.ETH,
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
					data,
					ChainType.ETC,
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
					data,
					if (SharedValue.isTestEnvironment()) ChainType.AllTest else ChainType.BTC,
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
					data,
					ChainType.LTC,
					!isDefault
				)
				cell.moreButton.preventDuplicateClicks()
			}
		}
	}

	private val eosAddressesView by lazy {
		AddressesListView(context!!, 3) { cell, data, wallet, isDefault ->
			presenter.showEOSPublickeyDescription(cell, data.address, wallet)
			cell.moreButton.onClick {
				showCellMoreDashboard(
					cell.moreButton.getViewAbsolutelyPositionInScreen()[1].toFloat(),
					data,
					ChainType.EOS,
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
					data,
					ChainType.BCH,
					!isDefault
				)
				cell.moreButton.preventDuplicateClicks()
			}
		}
	}
	override val presenter = AddressManagerPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		if (SharedWallet.getCurrentWalletType().isBIP44()) showCreatorDashboard()
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout parent@{
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, matchParent)
				topPadding = 20.uiPX()
				// 不为空才显示 `bip44` 规则的子地址界面
				WalletTable.getCurrentWallet {
					currentMultiChainAddressesView.into(this@parent)
					setMultiChainAddresses(this)
					if (ethAddresses.isNotEmpty()) {
						// ETHSeries List
						ethSeriesView.into(this@parent)
						setEthereumAddressesModel(this)
						// ETC List
						etcAddressesView.into(this@parent)
						setEthereumClassicAddressesModel(this)
						// BTC List
						btcAddressesView.into(this@parent)
						// `比特币` 的主网测试网地址根据环境显示不同的数据
						// EOS List
						eosAddressesView.into(this@parent)
						setEOSAddressesModel(this)
						if (!SharedValue.isTestEnvironment()) {
							setBitcoinAddressesModel(this)
							// 因为比特币系列分叉币的测试地址是公用的, 在测试环境下不额外显示分叉币的地址.
							// BCH List
							bchAddressesView.into(this@parent)
							setBitcoinCashAddressesModel(this)
							// LTC List
							ltcAddressesView.into(this@parent)
							setLitecoinAddressesModel(this)
						} else {
							setBTCSeriesTestAddressesModel(this)
							setBitcoinCashAddressesModel(this)
							setLitecoinAddressesModel(this)
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

	private fun setMultiChainAddresses(wallet: WalletTable) {
		currentMultiChainAddressesView.setTitle(WalletSettingsText.currentMultiChainAddresses)
		currentMultiChainAddressesView.currentWallet = wallet
		currentMultiChainAddressesView.model =
			if (SharedValue.isTestEnvironment()) wallet.getCurrentTestnetBip44Addresses()
			else wallet.getCurrentMainnetBip44Addresses()
	}

	fun setEthereumAddressesModel(wallet: WalletTable) {
		setMultiChainAddresses(wallet)
		ethSeriesView.checkAllEvent = presenter.showAllETHSeriesAddresses()
		ethSeriesView.setTitle(WalletSettingsText.ethereumSeriesAddress)
		ethSeriesView.currentWallet = wallet
		ethSeriesView.model = wallet.ethAddresses
	}

	fun setBitcoinCashAddressesModel(wallet: WalletTable) {
		val address = if (SharedValue.isTestEnvironment()) wallet.btcSeriesTestAddresses
		else wallet.bchAddresses
		setMultiChainAddresses(wallet)
		bchAddressesView.checkAllEvent = presenter.showAllBCHAddresses()
		bchAddressesView.setTitle(WalletSettingsText.bitcoinCashAddress)
		bchAddressesView.currentWallet = wallet
		bchAddressesView.model = address
	}

	fun setEthereumClassicAddressesModel(wallet: WalletTable) {
		setMultiChainAddresses(wallet)
		etcAddressesView.checkAllEvent = presenter.showAllETCAddresses()
		etcAddressesView.setTitle(WalletSettingsText.ethereumClassicAddress)
		etcAddressesView.currentWallet = wallet
		etcAddressesView.model = wallet.etcAddresses
	}

	// 测试网络环境下的测试地址是公用的所以这里要额外处理 `Title` 显示
	fun setBitcoinAddressesModel(wallet: WalletTable) {
		val title = WalletSettingsText.bitcoinAddress(SharedWallet.getYingYongBaoInReviewStatus())
		val addresses = wallet.btcAddresses
		setMultiChainAddresses(wallet)
		btcAddressesView.checkAllEvent = presenter.showAllBTCAddresses()
		btcAddressesView.setTitle(title)
		btcAddressesView.currentWallet = wallet
		btcAddressesView.model = addresses
	}

	fun setBTCSeriesTestAddressesModel(wallet: WalletTable) {
		val title = "${CoinSymbol.btc()}/${CoinSymbol.ltc}/${CoinSymbol.bch} ${WalletSettingsText.testAddress}"
		val addresses = wallet.btcSeriesTestAddresses
		setMultiChainAddresses(wallet)
		btcAddressesView.checkAllEvent = presenter.showAllBTCSeriesTestAddresses()
		btcAddressesView.setTitle(title)
		btcAddressesView.currentWallet = wallet
		btcAddressesView.model = addresses
	}

	fun setLitecoinAddressesModel(wallet: WalletTable) {
		val address = if (SharedValue.isTestEnvironment()) wallet.btcSeriesTestAddresses
		else wallet.ltcAddresses
		setMultiChainAddresses(wallet)
		ltcAddressesView.checkAllEvent = presenter.showAllLTCAddresses()
		ltcAddressesView.setTitle(WalletSettingsText.litecoinAddress)
		ltcAddressesView.currentWallet = wallet
		ltcAddressesView.model = address
	}

	fun setEOSAddressesModel(wallet: WalletTable) {
		setMultiChainAddresses(wallet)
		eosAddressesView.checkAllEvent = presenter.showAllEOSAddresses()
		eosAddressesView.setTitle(WalletSettingsText.eosAddress)
		eosAddressesView.currentWallet = wallet
		eosAddressesView.model = wallet.eosAddresses
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
				WalletSettingsText.newETHSeriesAddress ->
					AddressManagerPresenter.createETHSeriesAddress(this, password) {
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
					if (SharedValue.isTestEnvironment()) AddressManagerPresenter.createBTCTestAddress(this, password) {
						btcAddressesView.model = it
					} else AddressManagerPresenter.createLTCAddress(this, password) {
						ltcAddressesView.model = it
					}
				}

				WalletSettingsText.newBCHAddress -> {
					if (SharedValue.isTestEnvironment()) AddressManagerPresenter.createBTCTestAddress(this, password) {
						btcAddressesView.model = it
					} else AddressManagerPresenter.createBCHAddress(this, password) {
						bchAddressesView.model = it
					}
				}

				WalletSettingsText.newBTCAddress -> {
					if (SharedValue.isTestEnvironment()) AddressManagerPresenter.createBTCTestAddress(this, password) {
						btcAddressesView.model = it
					} else AddressManagerPresenter.createBTCAddress(this, password) {
						btcAddressesView.model = it
					}
				}
			}
		}
	}

	private fun showCellMoreDashboard(
		top: Float,
		address: Bip44Address,
		coinType: ChainType,
		hasDefaultCell: Boolean
	) {
		showMoreDashboard(
			getParentContainer(),
			top,
			hasDefaultCell,
			BCHWalletUtils.isNewCashAddress(address.address),
			setDefaultAddressEvent = {
				// 因为在下面多种判断下调用同一套逻辑, 这是一个方法内的复用函数
				fun update(address: Bip44Address, eosAccountName: String) {
					coinType.updateCurrentAddress(address, eosAccountName) { wallet ->
						updateWalletDetail()
						when {
							coinType.isETH() -> setEthereumAddressesModel(wallet)
							coinType.isETC() -> setEthereumClassicAddressesModel(wallet)
							coinType.isEOS() -> setEOSAddressesModel(wallet)
							coinType.isLTC() -> setLitecoinAddressesModel(wallet)
							coinType.isBTC() -> setBitcoinAddressesModel(wallet)
							coinType.isAllTest() -> setBTCSeriesTestAddressesModel(wallet)
						}
						toast(CommonText.succeed)
						removeDashboard(context)
					}
				}
				// `EOS` 和其他链的切换默认地址的逻辑不同
				if (coinType.isEOS()) switchEOSDefaultAddress(context, address.address) { accountName ->
					update(address, accountName)
				} else update(address, address.address)
			},
			qrCellClickEvent = {
				getParentFragment<WalletSettingsFragment> {
					// `BCH` 需要在二维码页面下转换 `CashAddress` 和 `Legacy` 所以需要标记 `BCH Symbol`
					val symbol = if (coinType.isBCH()) CoinSymbol.bch else ""
					AddressManagerPresenter.showQRCodeFragment(ContactModel(address.address, symbol), this)
				}
			},
			exportPrivateKey = {
				getParentFragment<WalletSettingsFragment> {
					AddressManagerPresenter.showPrivateKeyExportFragment(address.address, coinType, this)
				}
			},
			keystoreCellClickEvent = {
				getParentFragment<WalletSettingsFragment> {
					AddressManagerPresenter.showKeystoreExportFragment(address.address, this)
				}
			},
			convertBCHAddressToLegacy = {
				val legacyAddress = BCHWalletUtils.formattedToLegacy(address.address, MainNetParams.get())
				context.alert(legacyAddress)
				context?.clickToCopy(legacyAddress)
				removeDashboard(context)
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

		/**
		 * `EOS` 更改默认地址的逻辑与其他的链比较复杂一些, 首先一个新的默认地址在 `MyToken` 里面可能对应多个
		 * `AccountName` 的记录, 也可能没有. 所以单纯的切换地址是无法做到首页资产的重新查找.
		 * 如果切换了新的地址那么会执行以下步骤.
		 * 1. 去 MyToken 表里面查找这个公钥名下的隶属与不同 `AccountName` 的资产
		 * 2. 如果没有那么意味着是一个本地未记录激活过的账号那么直接走常规逻辑
		 * 3. 如果是有且只有 `1个` 对应的 `AccountName` 那么直接把这个 `AccountName` 作为默认地址
		 *    对应的资产 `AccountName`
		 * 4. 如果有多个 `AccountName` 那么就展示明细让用户选择默认地址对应的默认 `AccountName` 是哪个
		 */
		fun switchEOSDefaultAddress(
			context: Context?,
			newDefaultAddress: String,
			hold: (String) -> Unit
		) {
			MyTokenTable.getEOSAccountNamesByAddress(newDefaultAddress) { namesInMyTokenTable ->
				when {
					namesInMyTokenTable.size == 1 -> hold(namesInMyTokenTable.first())
					namesInMyTokenTable.isEmpty() -> hold(newDefaultAddress)
					else -> context?.selector(
						"Multiple Account Name Be Detected On This Account",
						namesInMyTokenTable
					) { _, index ->
						hold(namesInMyTokenTable[index])
					}
				}
			}
		}

		fun verifyMultiChainWalletPassword(
			context: Context,
			callback: (password: String) -> Unit
		) {
			context.showAlertView(
				WalletSettingsText.createSubAccount,
				WalletSettingsText.createSubAccountIntro,
				!SharedWallet.isWatchOnlyWallet()
			) { passwordInput ->
				val password = passwordInput?.text.toString()
				context.verifyKeystorePassword(
					password,
					SharedAddress.getCurrentBTC(),
					true
				) {
					if (it) callback(password)
					else context.alert(CommonText.wrongPassword)
				}
			}
			removeDashboard(context)
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
								WalletText.qrCode -> qrCellClickEvent()
								WalletSettingsText.exportPrivateKey -> exportPrivateKey()
								WalletSettingsText.exportKeystore -> keystoreCellClickEvent()
								WalletText.getBCHLegacyAddress -> convertBCHAddressToLegacy()
							}
							cell.preventDuplicateClicks()
						}
					}
					creatorDashBoard.scale(2, false, 60)
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

