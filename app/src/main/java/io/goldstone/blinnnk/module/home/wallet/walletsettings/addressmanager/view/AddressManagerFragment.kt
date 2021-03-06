package io.goldstone.blinnnk.module.home.wallet.walletsettings.addressmanager.view

import android.content.Context
import android.support.annotation.WorkerThread
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.ViewGroup
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import io.goldstone.blinnnk.common.base.basefragment.BaseFragment
import io.goldstone.blinnnk.common.component.overlay.Dashboard
import io.goldstone.blinnnk.common.component.title.AttentionTextView
import io.goldstone.blinnnk.common.error.AccountError
import io.goldstone.blinnnk.common.language.*
import io.goldstone.blinnnk.common.sharedpreference.SharedValue
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.alert
import io.goldstone.blinnnk.common.utils.getMainActivity
import io.goldstone.blinnnk.common.utils.safeShowError
import io.goldstone.blinnnk.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blinnnk.crypto.keystore.verifyKeystorePasswordByWalletID
import io.goldstone.blinnnk.crypto.multichain.*
import io.goldstone.blinnnk.kernel.commontable.MyTokenTable
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.Bip44Address
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import io.goldstone.blinnnk.module.home.profile.contacts.contractinput.model.ContactModel
import io.goldstone.blinnnk.module.home.wallet.walletsettings.addressmanager.event.DefaultAddressUpdateEvent
import io.goldstone.blinnnk.module.home.wallet.walletsettings.addressmanager.model.GridIconTitleModel
import io.goldstone.blinnnk.module.home.wallet.walletsettings.addressmanager.presenter.AddressManagerPresenter
import io.goldstone.blinnnk.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bitcoinj.params.MainNetParams
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.toast

/**
 * @date 2018/7/11 12:44 AM
 * @author KaySaith
 */
class AddressManagerFragment : BaseFragment<AddressManagerPresenter>() {

	override val pageTitle: String = WalletSettingsText.viewAddresses

	override fun onStart() {
		super.onStart()
		EventBus.getDefault().register(this)
	}

	override fun onStop() {
		super.onStop()
		EventBus.getDefault().unregister(this)
	}

	// Event Bus
	@Subscribe(threadMode = ThreadMode.POSTING)
	fun updateAddressEvent(updateEvent: DefaultAddressUpdateEvent) {
		WalletTable.getCurrent(Dispatchers.Main) {
			when {
				updateEvent.chainType.isETH() -> setEthereumAddressesModel(this)
				updateEvent.chainType.isETC() -> setEthereumClassicAddressesModel(this)
				updateEvent.chainType.isBTC() -> setBitcoinAddressesModel(this)
				updateEvent.chainType.isBCH() -> setBitcoinCashAddressesModel(this)
				updateEvent.chainType.isLTC() -> setLitecoinAddressesModel(this)
				updateEvent.chainType.isEOS() -> setEOSAddressesModel(this)
				updateEvent.chainType.isAllTest() -> setBTCSeriesTestAddressesModel(this)
			}
		}
	}

	private val currentMultiChainAddressesView by lazy {
		AddressesListView(context!!, 7) { cell, data, wallet, isDefault ->
			val chainType = data.getChainType()
			// EOS 需要在默认地址中显示激活状态
			if (chainType.isEOS()) {
				presenter.showEOSPublickeyDescription(cell, data.address, wallet)
			}
			cell.moreButton.onClick {
				showCellMoreDashboard(data, chainType, !isDefault)
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
		if (SharedWallet.getCurrentWalletType().isBIP44()) setAddButtonEvent()
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, matchParent)
				topPadding = 20.uiPX()
				showAddresses(this)
			}
		}
	}

	private fun showAddresses(parent: ViewGroup) {
		WalletTable.getCurrent(Dispatchers.Main) {
			currentMultiChainAddressesView.into(parent)
			setMultiChainAddresses(this)
			if (ethAddresses.isNotEmpty()) {
				// ETHSeries List
				ethSeriesView.into(parent)
				setEthereumAddressesModel(this)
				// ETC List
				etcAddressesView.into(parent)
				setEthereumClassicAddressesModel(this)
				// BTC List
				btcAddressesView.into(parent)
				// `比特币` 的主网测试网地址根据环境显示不同的数据
				// EOS List
				eosAddressesView.into(parent)
				setEOSAddressesModel(this)
				if (!SharedValue.isTestEnvironment()) {
					setBitcoinAddressesModel(this)
					// 因为比特币系列分叉币的测试地址是公用的, 在测试环境下不额外显示分叉币的地址.
					// BCH List
					bchAddressesView.into(parent)
					setBitcoinCashAddressesModel(this)
					// LTC List
					ltcAddressesView.into(parent)
					setLitecoinAddressesModel(this)
				} else {
					setBTCSeriesTestAddressesModel(this)
					setBitcoinCashAddressesModel(this)
					setLitecoinAddressesModel(this)
				}
			} else {
				attentionView.into(parent)
			}
		}
	}

	private fun setMultiChainAddresses(wallet: WalletTable) {
		currentMultiChainAddressesView.setTitle(WalletSettingsText.currentMultiChainAddresses)
		currentMultiChainAddressesView.currentWallet = wallet
		currentMultiChainAddressesView.model =
			if (SharedValue.isTestEnvironment()) wallet.getCurrentTestnetBip44Addresses()
			else wallet.getCurrentMainnetBip44Addresses()
	}

	fun setEthereumAddressesModel(wallet: WalletTable) {
		ethSeriesView.checkAllEvent = presenter.showAllETHSeriesAddresses()
		ethSeriesView.setTitle(WalletSettingsText.ethereumSeriesAddress)
		ethSeriesView.currentWallet = wallet
		ethSeriesView.model = wallet.ethAddresses
	}

	fun setBitcoinCashAddressesModel(wallet: WalletTable) {
		val address = if (SharedValue.isTestEnvironment()) wallet.btcSeriesTestAddresses
		else wallet.bchAddresses
		bchAddressesView.checkAllEvent = presenter.showAllBCHAddresses()
		bchAddressesView.setTitle(WalletSettingsText.bitcoinCashAddress)
		bchAddressesView.currentWallet = wallet
		bchAddressesView.model = address
	}

	fun setEthereumClassicAddressesModel(wallet: WalletTable) {
		etcAddressesView.checkAllEvent = presenter.showAllETCAddresses()
		etcAddressesView.setTitle(WalletSettingsText.ethereumClassicAddress)
		etcAddressesView.currentWallet = wallet
		etcAddressesView.model = wallet.etcAddresses
	}

	// 测试网络环境下的测试地址是公用的所以这里要额外处理 `Title` 显示
	fun setBitcoinAddressesModel(wallet: WalletTable) {
		val title = WalletSettingsText.bitcoinAddress()
		val addresses = wallet.btcAddresses
		btcAddressesView.checkAllEvent = presenter.showAllBTCAddresses()
		btcAddressesView.setTitle(title)
		btcAddressesView.currentWallet = wallet
		btcAddressesView.model = addresses
	}

	fun setBTCSeriesTestAddressesModel(wallet: WalletTable) {
		val title = "${CoinSymbol.btc}/${CoinSymbol.ltc}/${CoinSymbol.bch} ${WalletSettingsText.testAddress}"
		val addresses = wallet.btcSeriesTestAddresses
		btcAddressesView.checkAllEvent = presenter.showAllBTCSeriesTestAddresses()
		btcAddressesView.setTitle(title)
		btcAddressesView.currentWallet = wallet
		btcAddressesView.model = addresses
	}

	fun setLitecoinAddressesModel(wallet: WalletTable) {
		val address =
			if (SharedValue.isTestEnvironment()) wallet.btcSeriesTestAddresses
			else wallet.ltcAddresses
		ltcAddressesView.checkAllEvent = presenter.showAllLTCAddresses()
		ltcAddressesView.setTitle(WalletSettingsText.litecoinAddress)
		ltcAddressesView.currentWallet = wallet
		ltcAddressesView.model = address
	}

	fun setEOSAddressesModel(wallet: WalletTable) {
		eosAddressesView.checkAllEvent = presenter.showAllEOSAddresses()
		eosAddressesView.setTitle(WalletSettingsText.eosAddress)
		eosAddressesView.currentWallet = wallet
		eosAddressesView.model = wallet.eosAddresses
	}

	fun setAddButtonEvent() {
		getParentFragment<WalletSettingsFragment> {
			showAddButton(true, false) {
				showCreatorDashboard()
			}
		}
	}

	private fun showCreatorDashboard() {
		Dashboard(context!!) {
			showGrid(
				WalletText.createNewAddress,
				GridIconTitleAdapter(GridIconTitleModel.getModels()) {
					verifyMultiChainWalletPassword(context!!) { password, error ->
						if (!password.isNullOrEmpty() && error.isNone() && it.chainType.isNotNull()) {
							createChildAddressByButtonTitle(it.chainType) { addresses ->
								launchUI {
									updateUI(addresses, it.chainType)
									toast(CommonText.succeed)
								}
							}
						} else safeShowError(error)
					}
					dismiss()
				}
			)
		}
	}

	private fun createChildAddressByButtonTitle(
		chainType: ChainType,
		@WorkerThread hold: (addresses: List<Bip44Address>) -> Unit
	) {
		when {
			chainType.isETH() ->
				AddressManagerPresenter.createETHSeriesAddress(hold)
			chainType.isETC() ->
				AddressManagerPresenter.createETCAddress(hold)
			chainType.isEOS() ->
				AddressManagerPresenter.createEOSAddress(hold)
			SharedValue.isTestEnvironment() && chainType.isBTCSeries() ->
				AddressManagerPresenter.createBTCTestAddress(hold)
			chainType.isLTC() ->
				AddressManagerPresenter.createLTCAddress(hold)
			chainType.isBCH() ->
				AddressManagerPresenter.createBCHAddress(hold)
			chainType.isBTC() ->
				AddressManagerPresenter.createBTCAddress(hold)
		}
	}

	private fun updateUI(addresses: List<Bip44Address>, chainType: ChainType) {
		when {
			chainType.isETH() -> ethSeriesView.model = addresses
			chainType.isETC() -> etcAddressesView.model = addresses
			chainType.isEOS() -> eosAddressesView.model = addresses
			SharedValue.isTestEnvironment() && chainType.isBTCSeries() ->
				btcAddressesView.model = addresses
			chainType.isLTC() -> ltcAddressesView.model = addresses
			chainType.isBCH() -> bchAddressesView.model = addresses
			chainType.isBTC() -> btcAddressesView.model = addresses
		}
	}

	private fun showCellMoreDashboard(
		address: Bip44Address,
		coinType: ChainType,
		hasDefaultCell: Boolean
	) {
		getParentContainer()?.showMoreDashboard(
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
							coinType.isBCH() -> setBitcoinCashAddressesModel(wallet)
							coinType.isBTC() -> setBitcoinAddressesModel(wallet)
							coinType.isAllTest() -> setBTCSeriesTestAddressesModel(wallet)
						}

						// 更换默认地址后需要刷新总的默认地址目录
						setMultiChainAddresses(wallet)
						toast(CommonText.succeed)
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
					AddressManagerPresenter.showKeystoreExportFragment(address.address, coinType, this)
				}
			},
			convertBCHAddressToLegacy = {
				val legacyAddress = BCHWalletUtils.formattedToLegacy(address.address, MainNetParams.get())
				context.alert(legacyAddress)
				context?.clickToCopy(legacyAddress)
			}
		)
	}

	private fun updateWalletDetail() {
		getMainActivity()?.getWalletDetailFragment()?.presenter?.start()
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<WalletSettingsFragment> {
			presenter.popFragmentFrom<AddressManagerFragment>()
		}
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
						EOSAccountText.chooseFromMultipleEosAccounts,
						namesInMyTokenTable
					) { _, index ->
						hold(namesInMyTokenTable[index])
					}
				}
			}
		}

		fun verifyMultiChainWalletPassword(
			context: Context,
			@WorkerThread hold: (password: String?, error: AccountError) -> Unit
		) {
			Dashboard(context) {
				showAlertView(
					WalletSettingsText.createSubAccount,
					WalletSettingsText.createSubAccountIntro,
					!SharedWallet.isWatchOnlyWallet(),
					{}
				) { passwordInput ->
					GlobalScope.launch(Dispatchers.Default) {
						val password = passwordInput?.text.toString()
						context.verifyKeystorePasswordByWalletID(
							password,
							SharedWallet.getCurrentWalletID()
						) {
							if (it) hold(password, AccountError.None)
							else hold(null, AccountError.WrongPassword)
						}
					}
				}
			}
		}

		fun ViewGroup.showMoreDashboard(
			hasDefaultCell: Boolean = true,
			isCashAddress: Boolean,
			setDefaultAddressEvent: () -> Unit,
			qrCellClickEvent: () -> Unit,
			exportPrivateKey: () -> Unit,
			keystoreCellClickEvent: () -> Unit,
			convertBCHAddressToLegacy: () -> Unit
		) {
			val data =
				GridIconTitleModel.getMenuModels(hasDefaultCell, isCashAddress)
			Dashboard(context) {
				showGrid(
					WalletText.moreOperations,
					GridIconTitleAdapter(data) {
						when (it.name) {
							WalletText.setDefaultAddress -> setDefaultAddressEvent()
							WalletText.qrCode -> qrCellClickEvent()
							WalletSettingsText.exportPrivateKey -> exportPrivateKey()
							WalletSettingsText.exportKeystore -> keystoreCellClickEvent()
							WalletText.getBCHLegacyAddress -> convertBCHAddressToLegacy()
						}
						dismiss()
					}
				)
			}
		}
	}
}

