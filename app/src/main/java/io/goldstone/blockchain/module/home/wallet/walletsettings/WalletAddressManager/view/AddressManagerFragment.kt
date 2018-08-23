package io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.view

import android.content.Context
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
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
import io.goldstone.blockchain.common.utils.getViewAbsolutelyPositionInScreen
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.verifyKeystorePassword
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
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
		AddressesListView(context!!, 6) { moreButton, address, isDefault, title ->
			val chainType = when (title) {
				CryptoSymbol.eth -> ChainType.ETH.id
				CryptoSymbol.etc -> ChainType.ETC.id
				CryptoSymbol.erc -> ChainType.ETH.id
				CryptoSymbol.ltc -> ChainType.LTC.id
				CryptoSymbol.bch -> ChainType.BCH.id
				else -> ChainType.BTC.id
			}
			moreButton.onClick {
				showCellMoreDashboard(
					moreButton.getViewAbsolutelyPositionInScreen()[1].toFloat(),
					address,
					chainType,
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
		AddressesListView(context!!, 3) { moreButton, address, isDefault, _ ->
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
		AddressesListView(context!!, 3) { moreButton, address, isDefault, _ ->
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
		AddressesListView(context!!, 3) { moreButton, address, isDefault, _ ->
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
	private val ltcAddressesView by lazy {
		AddressesListView(context!!, 3) { moreButton, address, isDefault, _ ->
			moreButton.onClick {
				showCellMoreDashboard(
					moreButton.getViewAbsolutelyPositionInScreen()[1].toFloat(),
					address,
					ChainType.LTC.id,
					!isDefault
				)
				moreButton.preventDuplicateClicks()
			}
		}
	}

	private val bchAddressesView by lazy {
		AddressesListView(context!!, 3) { moreButton, address, isDefault, _ ->
			moreButton.onClick {
				showCellMoreDashboard(
					moreButton.getViewAbsolutelyPositionInScreen()[1].toFloat(),
					address,
					ChainType.BCH.id,
					!isDefault
				)
				moreButton.preventDuplicateClicks()
			}
		}
	}
	override val presenter = AddressManagerPresenter(this)

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
						if (!Config.isTestEnvironment()) {
							// 因为比特币系列分叉币的测试地址是公用的, 在测试环境下不额外显示分叉币的地址.
							bchAddressesView.into(this@parent)
							ltcAddressesView.into(this@parent)
							ltcAddressesView.checkAllEvent = presenter.showAllLTCAddresses()
							bchAddressesView.checkAllEvent = presenter.showAllBCHAddresses()
						}
						btcAddressesView.checkAllEvent = presenter.showAllBTCAddresses()
						presenter.getEthereumAddresses()
						presenter.getEthereumClassicAddresses()
						// `比特币` 的主网测试网地址根据环境显示不同的数据
						if (Config.isTestEnvironment()) presenter.getBitcoinTestAddresses()
						else presenter.getBitcoinAddresses()
						// `Litecoin` 的主网测试网地址根据环境显示不同的数据
						if (Config.isTestEnvironment()) presenter.getLitecoinTestAddresses()
						else presenter.getLitecoinAddresses()
						// `Litecoin` 的主网测试网地址根据环境显示不同的数据
						if (Config.isTestEnvironment()) presenter.getBitcoinCashTestAddresses()
						else presenter.getBitcoinCashAddresses()
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

	fun setBitcoinCashAddressesModel(model: List<Pair<String, String>>) {
		bchAddressesView.setTitle(WalletSettingsText.bitcoinCashcoinAddress)
		bchAddressesView.model = model
	}

	fun setEthereumClassicAddressesModel(model: List<Pair<String, String>>) {
		etcAddressesView.setTitle(WalletSettingsText.ethereumClassicAddress)
		etcAddressesView.model = model
	}

	// 测试网络环境下的测试地址是公用的所以这里要额外处理 `Title` 显示
	fun setBitcoinAddressesModel(model: List<Pair<String, String>>) {
		val title = if (Config.isTestEnvironment()) {
			"${CryptoSymbol.btc()}/${CryptoSymbol.ltc}/${CryptoSymbol.bch} Test Addresses"
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
						ethAndERCAddressesView.model = it
					}

				WalletSettingsText.newETCAddress ->
					AddressManagerPresenter.createETCAddress(this, password) {
						etcAddressesView.model = it
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

	private fun showCellMoreDashboard(
		top: Float,
		address: String,
		coinType: Int,
		hasDefaultCell: Boolean
	) {
		AddressManagerFragment.showMoreDashboard(
			getParentContainer(),
			top,
			hasDefaultCell,
			BCHWalletUtils.isNewCashAddress(address),
			setDefaultAddressEvent = {
				AddressManagerPresenter.setDefaultAddress(coinType, address) {
					// 更新默认地址后同时更新首页的列表
					updateWalletDetail()
					when (coinType) {
						ChainType.ETH.id -> presenter.getEthereumAddresses()
						ChainType.ETC.id -> presenter.getEthereumClassicAddresses()
						ChainType.LTC.id -> {
							if (Config.isTestEnvironment())
								presenter.getLitecoinTestAddresses()
							else presenter.getLitecoinAddresses()
						}
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
			qrCellClickEvent = {
				getParentFragment<WalletSettingsFragment> {
					// `BCH` 需要在二维码页面下转换 `CashAddress` 和 `Legacy` 所以需要标记 `BCH Symbol`
					val symbol = if (coinType == ChainType.BCH.id) CryptoSymbol.bch else ""
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

