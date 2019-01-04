package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.base.gsfragment.GSFragment
import io.goldstone.blockchain.common.component.button.RadiusButton
import io.goldstone.blockchain.common.component.overlay.TopMiniLoadingView
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.multichain.isBTCSeries
import io.goldstone.blockchain.crypto.multichain.isEOSSeries
import io.goldstone.blockchain.crypto.multichain.isETC
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokendetail.tokeninfo.presenter.TokenInfoPresenter
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.model.ContactModel
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.contract.TransactionDetailContract
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionProgressModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter.TransactionDetailPresenter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI


/**
 * @author KaySaith
 * @date  2018/11/07
 */
class TransactionDetailFragment : GSFragment(), TransactionDetailContract.GSView {

	override val pageTitle: String get() = TransactionText.detail
	private lateinit var headerView: TransactionDetailHeaderView
	private lateinit var progressCard: TransactionProgressCardView
	private lateinit var memoCard: TransactionCardView
	private lateinit var addressCard: TransactionAddressCardView
	private lateinit var infoCard: TransactionCardView
	private lateinit var loadingView: TopMiniLoadingView

	private val data by lazy {
		arguments?.get(ArgumentKey.transactionFromList) as? TransactionListModel
			?: arguments?.get(ArgumentKey.transactionDetail) as? ReceiptModel
			?: arguments?.get(ArgumentKey.notificationTransaction) as? NotificationModel
	}

	override lateinit var presenter: TransactionDetailContract.GSPresenter

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return generateLayout {
			progressCard = TransactionProgressCardView(context)
			progressCard.layoutParams = RelativeLayout.LayoutParams(ScreenSize.card, wrapContent)
			addView(progressCard)

			memoCard = TransactionCardView(context)
			memoCard.visibility = View.GONE
			memoCard.layoutParams = RelativeLayout.LayoutParams(ScreenSize.card, wrapContent)
			addView(memoCard)

			addressCard = TransactionAddressCardView(context) { address ->
				data?.symbol?.let { showContactEditor(address, it) }
			}
			addressCard.layoutParams = RelativeLayout.LayoutParams(ScreenSize.card, wrapContent)
			addView(addressCard)

			infoCard = TransactionCardView(context)
			infoCard.layoutParams = RelativeLayout.LayoutParams(ScreenSize.card, wrapContent)
			addView(infoCard)
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		data?.let {
			presenter = TransactionDetailPresenter(it, this)
			presenter.start()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		presenter.removeObserver()
	}

	override fun showLoading(status: Boolean) {
		loadingView.visibility = if (status) View.VISIBLE else View.GONE
	}

	override fun showErrorAlert(error: Throwable) {
		ErrorDisplayManager(error).show(context)
	}

	override fun showProgress(model: TransactionProgressModel?) {
		if (model == null) progressCard.visibility = View.GONE
		else progressCard.model = model
	}

	override fun showMemo(memo: TransactionDetailModel) {
		memoCard.visibility = View.VISIBLE
		memoCard.model = listOf(memo)
	}

	override fun showTransactionAddresses(vararg model: TransactionDetailModel) {
		addressCard.model = model.toList()
	}

	override fun showError(error: Throwable) {
		ErrorDisplayManager(error).show(context)
	}

	override fun showTransactionInformation(vararg info: TransactionDetailModel) {
		infoCard.model = info.toList()
		infoCard.addContent {
			data?.apply {
				TokenInfoPresenter.getExplorerInfo(data?.contract).forEachIndexed { index, explorer ->
					val url =
						TransactionListModel.generateTransactionURL(
							hash,
							contract,
							EOSAccount(fromAddress).isValid(false)
						)
					val exploreButton = RadiusButton(context).apply {
						setTitle(TokenDetailText.checkDetail suffix explorer.name, false)
						setIcon(explorer.icon)
						onClick {
							showExplorerWebFragment(url[index])
							preventDuplicateClicks()
						}
					}
					exploreButton.into(this@addContent)
					exploreButton.setMargins<LinearLayout.LayoutParams> {
						topMargin = 10.uiPX()
					}
				}
			}
		}
	}

	override fun showHeaderData(model: TransactionHeaderModel) {
		headerView.setIconStyle(model)
	}

	override fun showContactEditor(address: String, symbol: String) {
		parentFragment?.apply {
			when (this) {
				is TokenDetailOverlayFragment -> presenter.removeSelfFromActivity()
				is NotificationFragment -> presenter.removeSelfFromActivity()
			}
		}
		getMainActivity()?.apply {
			addFragmentAndSetArguments<ProfileOverlayFragment>(ContainerID.main) {
				putString(ArgumentKey.profileTitle, ProfileText.contactsInput)
				putSerializable(ArgumentKey.addressModel, ContactModel(address, symbol))
			}
		}
	}

	private fun showExplorerWebFragment(url: String) {
		data?.apply {
			val webTitle = when {
				contract.isETC() -> TransactionText.gasTracker
				contract.isBTCSeries() || contract.isEOSSeries() ->
					TransactionText.transactionWeb
				else -> TransactionText.etherScanTransaction
			}
			val argument = Bundle().apply {
				putString(ArgumentKey.webViewUrl, url)
				putString(ArgumentKey.webViewName, webTitle)
			}
			parentFragment?.apply {
				when (this) {
					is TokenDetailOverlayFragment -> presenter.showTargetFragment<WebViewFragment>(argument)
					is NotificationFragment -> presenter.showTargetFragment<WebViewFragment>(argument)
				}
			}
		}
	}

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		if (!hidden) {
			getParentFragment<BaseOverlayFragment<*>>()?.showBackButton(true) {
				backEvent()
			}
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		backEvent()
	}

	private fun backEvent() {
		// 如果是转账进入的那么不允许再回退到燃气设置界面了. 只允许关闭
		val parent = parentFragment
		when (parent) {
			is TokenDetailOverlayFragment ->
				if (data is ReceiptModel) parent.presenter.removeSelfFromActivity()
				else parent.presenter.popFragmentFrom<TransactionDetailFragment>()
			is NotificationFragment ->
				parent.presenter.popFragmentFrom<TransactionDetailFragment>()
		}
	}

	private fun generateLayout(hold: LinearLayout.() -> Unit): View {
		return UI {
			scrollView {
				lparams(matchParent, matchParent)
				backgroundColor = GrayScale.whiteGray
				relativeLayout {
					loadingView = TopMiniLoadingView(context).apply {
						visibility = View.GONE
						z = 1f
					}
					addView(loadingView)
					lparams(matchParent, wrapContent)
					headerView = TransactionDetailHeaderView(context)
					addView(headerView)
					verticalLayout {
						gravity = Gravity.CENTER_HORIZONTAL
						hold(this)
					}.apply {
						val params =
							RelativeLayout.LayoutParams(matchParent, matchParent)
						params.topMargin = 200.uiPX()
						layoutParams = params
					}
				}
			}
		}.view
	}
}