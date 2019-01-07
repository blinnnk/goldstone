package io.goldstone.blinnnk.module.common.tokendetail.tokenasset.view

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import com.blinnnk.util.getParentFragment
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.base.gsfragment.GSFragment
import io.goldstone.blinnnk.common.base.view.GrayCardView
import io.goldstone.blinnnk.common.component.ProcessType
import io.goldstone.blinnnk.common.component.ProgressView
import io.goldstone.blinnnk.common.component.button.titleIcon
import io.goldstone.blinnnk.common.component.cell.GraySquareCell
import io.goldstone.blinnnk.common.component.cell.graySquareCell
import io.goldstone.blinnnk.common.component.overlay.Dashboard
import io.goldstone.blinnnk.common.component.overlay.LoadingView
import io.goldstone.blinnnk.common.component.title.sessionTitle
import io.goldstone.blinnnk.common.language.*
import io.goldstone.blinnnk.common.sharedpreference.SharedAddress
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.common.utils.safeShowError
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.UMengEvent
import io.goldstone.blinnnk.crypto.eos.account.EOSAccount
import io.goldstone.blinnnk.crypto.eos.base.showDialog
import io.goldstone.blinnnk.crypto.multichain.CoinSymbol
import io.goldstone.blinnnk.crypto.multichain.TokenContract
import io.goldstone.blinnnk.crypto.multichain.getAddress
import io.goldstone.blinnnk.module.common.tokendetail.eosactivation.accountselection.view.EOSAccountSelectionFragment
import io.goldstone.blinnnk.module.common.tokendetail.eosactivation.authorizatitonmanagement.view.AuthorizationManagementFragment
import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.cputradingdetail.view.CPUTradingFragment
import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.nettradingdetail.view.NETTradingFragment
import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.ramtradingdetail.view.RAMTradingFragment
import io.goldstone.blinnnk.module.common.tokendetail.tokenasset.contract.TokenAssetContract
import io.goldstone.blinnnk.module.common.tokendetail.tokenasset.presenter.TokenAssetPresenter
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailcenter.view.TokenDetailCenterFragment
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blinnnk.module.common.tokendetail.tokeninfo.presenter.TokenInfoPresenter
import io.goldstone.blinnnk.module.common.tokendetail.tokeninfo.view.TokenInfoView
import io.goldstone.blinnnk.module.home.dapp.eosaccountregister.view.EOSAccountRegisterFragment
import io.goldstone.blinnnk.module.home.wallet.walletsettings.qrcodefragment.presenter.QRCodePresenter
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/10
 */

class TokenAssetFragment : GSFragment(), TokenAssetContract.GSView {

	override val pageTitle: String = "Asset"
	private val token by lazy {
		getParentFragment<TokenDetailCenterFragment>()?.token
	}
	private lateinit var tokenInfoView: TokenInfoView
	private lateinit var balanceCell: GraySquareCell
	private lateinit var refundsCell: GraySquareCell
	private lateinit var delegateBandWidthCell: GraySquareCell
	private lateinit var transactionCountCell: GraySquareCell
	private lateinit var authorizationCell: GraySquareCell
	private lateinit var accountAddress: GraySquareCell
	private lateinit var assetCard: GrayCardView
	private lateinit var ramAssetCell: ProgressView
	private lateinit var cpuAssetCell: ProgressView
	private lateinit var netAssetCell: ProgressView
	private var loadingView: LoadingView? = null

	override lateinit var presenter: TokenAssetContract.GSPresenter
	override fun onResume() {
		super.onResume()
		presenter = TokenAssetPresenter(this)
		presenter.start()
		token?.let { setAccountInfo(it.contract) }
	}

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		if (!hidden) presenter.start()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return UI {
			scrollView {
				lparams(matchParent, matchParent)
				verticalLayout {
					lparams(matchParent, wrapContent)
					bottomPadding = 20.uiPX()
					gravity = Gravity.CENTER_HORIZONTAL
					tokenInfoView = TokenInfoView(context)
					tokenInfoView.into(this)
					showAccountManagementCells()

					sessionTitle(TokenDetailText.assetTools)

					gridLayout {
						leftPadding = 10.uiPX()
						rightPadding = 10.uiPX()
						val iconSize = ScreenSize.widthWithPadding / 4
						listOf(
							Pair(R.drawable.cpu_icon, TokenDetailText.delegateCPU),
							Pair(R.drawable.net_icon, TokenDetailText.delegateNET),
							Pair(R.drawable.ram_icon, TokenDetailText.buySellRAM),
							Pair(R.drawable.permission_icon, TokenDetailText.permission)
						).forEach { info ->
							titleIcon {
								layoutParams = LinearLayout.LayoutParams(iconSize, wrapContent)
								setContent(info.first, info.second, Spectrum.blue)
							}.click {
								showTradingFragment(info.second)
								UMengEvent.add(context, UMengEvent.Click.TokenDetail.eosTools, info.second)
							}
						}
					}
					showTransactionCells()
					showAssetDashboard()
					sessionTitle {
						setTitle("Extension Tools")
					}
					graySquareCell {
						setTitle(TokenDetailText.accountRegister)
						showArrow()
					}.click {
						showTradingFragment(TokenDetailText.accountRegister)
						UMengEvent.add(context, UMengEvent.Click.TokenDetail.eosAccountRegister)

					}
				}
			}
		}.view
	}

	override fun setTransactionCount(count: Int) {
		transactionCountCell.setSubtitle(count.toString())
	}

	override fun showError(error: Throwable) {
		safeShowError(error)
	}

	private fun setAccountInfo(contract: TokenContract) {
		val info = TokenInfoPresenter.getDetailButtonInfo(contract)
		val code = QRCodePresenter.generateQRCode(contract.getAddress())
		val chainName = CoinSymbol.eos suffix TokenDetailText.chainType
		tokenInfoView.setData(code, chainName, CommonText.calculating, info.first) {
			TokenInfoPresenter.showThirdPartyAddressDetail(
				getGrandFather<TokenDetailOverlayFragment>(),
				info.second
			)
		}
		token?.apply {
			presenter.getLatestActivationDate(contract) {
				tokenInfoView.setLatestActivation(chainName, it)
			}
		}
	}

	private fun showPublicKeyAccountNames() {
		getGrandFather<TokenDetailOverlayFragment>()
			?.presenter?.showTargetFragment<EOSAccountSelectionFragment>(
			Bundle().apply {
				putString(
					ArgumentKey.defaultEOSAccountName,
					SharedAddress.getCurrentEOSAccount().name
				)
			},
			2
		)
	}

	private fun showTradingFragment(title: String) {
		if (SharedWallet.isWatchOnlyWallet()) {
			safeShowError(Throwable(AlertText.watchOnly))
			return
		}
		val parentPresenter =
			getGrandFather<TokenDetailOverlayFragment>()?.presenter
		when (title) {
			TokenDetailText.delegateCPU -> parentPresenter
				?.showTargetFragment<CPUTradingFragment>(Bundle(), 2)
			TokenDetailText.delegateNET -> parentPresenter
				?.showTargetFragment<NETTradingFragment>(Bundle(), 2)
			TokenDetailText.buySellRAM -> parentPresenter
				?.showTargetFragment<RAMTradingFragment>(Bundle(), 2)
			TokenDetailText.accountRegister -> parentPresenter
				?.showTargetFragment<EOSAccountRegisterFragment>(Bundle(), 2)
			TokenDetailText.permission -> parentPresenter?.
				showTargetFragment<AuthorizationManagementFragment>(Bundle(), 2)
		}
	}

	override fun setEOSBalance(balance: String) {
		balanceCell.setSubtitle(balance)
	}

	override fun setEOSRefunds(description: String) {
		refundsCell.setSubtitle(description)
	}

	override fun setEOSDelegateBandWidth(value: String) {
		delegateBandWidthCell.setSubtitle(value)
	}

	override fun setResourcesValue(
		ramAvailable: BigInteger,
		ramTotal: BigInteger,
		ramEOSCount: String,
		cpuAvailable: BigInteger,
		cpuTotal: BigInteger,
		cpuWeight: String,
		netAvailable: BigInteger,
		netTotal: BigInteger,
		netWeight: String
	) {
		ramAssetCell.setLeftValue(ramAvailable, TokenDetailText.available, ProcessType.Disk)
		ramAssetCell.setRightValue(ramTotal, TokenDetailText.total, ProcessType.Disk)
		ramAssetCell.setSubtitle(ramEOSCount)
		cpuAssetCell.setSubtitle(cpuWeight)
		cpuAssetCell.setLeftValue(
			cpuAvailable,
			TokenDetailText.available,
			ProcessType.Time
		)
		cpuAssetCell.setRightValue(
			cpuTotal,
			TokenDetailText.total,
			ProcessType.Time
		)
		netAssetCell.setSubtitle(netWeight)
		netAssetCell.setLeftValue(
			netAvailable,
			TokenDetailText.available,
			ProcessType.Disk
		)
		netAssetCell.setRightValue(
			netTotal,
			TokenDetailText.total,
			ProcessType.Disk
		)
	}

	private fun ViewGroup.showAccountManagementCells() {
		sessionTitle {
			setTitle(TokenDetailText.accountManagement)
		}
		authorizationCell = GraySquareCell(context).apply {
			showArrow()
			setTitle(EOSAccountText.authority)
			setSubtitle(SharedAddress.getCurrentEOSAccount().name)
			click {
				val type = SharedWallet.getCurrentWalletType()
				when {
					type.isEOSMainnet() || type.isEOSJungle() ->
						safeShowError(Throwable(WalletText.watchOnly))
					else -> showPublicKeyAccountNames()
				}
				UMengEvent.add(context, UMengEvent.Click.TokenDetail.eosAccountList)
			}
		}
		authorizationCell.into(this)

		accountAddress = GraySquareCell(context).apply {
			setTitle(EOSAccountText.publicKey)
			val address =
				if (SharedAddress.getCurrentEOS().isEmpty()) "Account Name Only"
				else SharedAddress.getCurrentEOS()
			setSubtitle(address)
			click {
				context?.clickToCopy(SharedAddress.getCurrentEOS())
				UMengEvent.add(context, UMengEvent.Click.TokenDetail.toCopyAddress, "EOS公钥")
			}
		}
		accountAddress.into(this)
	}

	private fun ViewGroup.showTransactionCells() {
		sessionTitle {
			setTitle(TokenDetailText.balance)
		}
		balanceCell = GraySquareCell(context).apply {
			setTitle(TokenDetailText.balance)
			setSubtitle(CommonText.calculating)
		}
		balanceCell.into(this)
		delegateBandWidthCell = GraySquareCell(context).apply {
			showArrow()
			setTitle(TokenDetailText.delband)
			setSubtitle(CommonText.calculating)
		}
		delegateBandWidthCell.click {
			showDelegateBandWidthDashboard()
			UMengEvent.add(context, UMengEvent.Click.TokenDetail.eosDelegateBandwidth)
		}.into(this)
		refundsCell = GraySquareCell(context).apply {
			setTitle(TokenDetailText.refunds)
			setSubtitle(CommonText.calculating)
		}
		refundsCell.into(this)

		transactionCountCell = GraySquareCell(context).apply {
			setTitle(TokenDetailText.transactionCount)
			setSubtitle(CommonText.calculating)
		}
		transactionCountCell.into(this)
	}

	private fun ViewGroup.showAssetDashboard() {
		sessionTitle {
			setTitle(TokenDetailText.resources)
		}
		assetCard = GrayCardView(context).apply {
			layoutParams = RelativeLayout.LayoutParams(ScreenSize.card, 255.uiPX())
		}
		assetCard.into(this)

		ramAssetCell = ProgressView(context).apply {
			setTitle(TokenDetailText.ram)
			setSubtitle(CommonText.calculating)
		}.click {
			showTradingFragment(TokenDetailText.buySellRAM)
			UMengEvent.add(context, UMengEvent.Click.TokenDetail.eosResourceProcessBar, "买卖RAM")
		}

		cpuAssetCell = ProgressView(context).apply {
			setTitle(TokenDetailText.cpu)
			setSubtitle(CommonText.calculating)
		}.click {
			showTradingFragment(TokenDetailText.delegateCPU)
			UMengEvent.add(context, UMengEvent.Click.TokenDetail.eosResourceProcessBar, "代理CPU")
		}

		netAssetCell = ProgressView(context).apply {
			setTitle(TokenDetailText.net)
			setSubtitle(CommonText.calculating)
		}.click {
			showTradingFragment(TokenDetailText.delegateNET)
			UMengEvent.add(context, UMengEvent.Click.TokenDetail.eosResourceProcessBar, "代理NET")
		}

		assetCard.addContent {
			ramAssetCell.into(this)
			cpuAssetCell.into(this)
			netAssetCell.into(this)
		}
	}

	override fun showCenterLoading(status: Boolean) = launchUI {
		if (loadingView.isNull()) loadingView = LoadingView(context!!)
		if (status) loadingView?.show() else loadingView?.remove()
	}

	private fun showDelegateBandWidthDashboard() {
		presenter.getDelegateBandWidthData {
			launchUI {
				Dashboard(context!!) {
					showList(
						TokenDetailText.delegateBandWidth,
						DelegateBandwidthAdapter(it) {
							if (SharedWallet.isWatchOnlyWallet()) {
								showError(Throwable(AlertText.watchOnly))
							} else showRefundBandwidthEditorDashboard(EOSAccount(toName))
						}
					)
				}
			}
		}
	}

	private fun Dashboard.showRefundBandwidthEditorDashboard(receiver: EOSAccount) {
		getDialog {
			cancelOnTouchOutside(false)
			setContentView(
				DelegateEditorView(context).apply {
					setTitle(TokenDetailText.delegateDetailTitle)
					closeEvent = Runnable { dismiss() }
					confirmEvent = Runnable {
						showLoading(true)
						presenter.redemptionBandwidth(
							getPassword(),
							receiver,
							getCPUAMount(),
							getNetAmount()
						) { response, error ->
							launchUI {
								if (response.isNotNull() && error.isNone()) launchUI {
									dismiss()
									showLoading(false)
									presenter.updateRefundInfo()
									response.showDialog(context)
								} else {
									showLoading(false)
									showError(error)
								}
							}
						}
					}
				},
				LinearLayout.LayoutParams(matchParent, wrapContent)
			)
		}
	}
}