package io.goldstone.blockchain.module.common.tokenpayment.addressselection.view

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import com.google.zxing.integration.android.IntentIntegrator
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.multichain.QRCode
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.contract.AddressSelectionContract
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.presenter.AddressSelectionPresenter
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.view.PaymentDetailFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.presenter.QRCodePresenter
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.textColor
import org.jetbrains.anko.yesButton

/**
 * @date 28/03/2018 9:24 AM
 * @author KaySaith
 */
class AddressSelectionFragment : GSRecyclerFragment<ContactTable>(), AddressSelectionContract.GSView {

	override val pageTitle: String = TokenDetailText.address
	private val token by lazy {
		getParentFragment<TokenDetailOverlayFragment>()?.token
	}
	private val buttonHeight = 50.uiPX()
	private var viewHeight = 0
	private var keyboardHeight = 0
	private val confirmButton by lazy { generateConfirmButton() }
	private var headerView: AddressSelectionHeaderView? = null
	override lateinit var presenter: AddressSelectionContract.GSPresenter

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<ContactTable>?
	) {
		recyclerView.adapter = AddressSelectionAdapter(
			asyncData.orEmptyArray(),
			{
				presenter.showPaymentDetail(it.defaultAddress, 0.0)
			}
		) {
			headerView = this
		}
	}

	override fun showAddresses(data: ArrayList<ContactTable>) {
		updateAdapterData<AddressSelectionAdapter>(data)
		updateInputStatus()
	}

	override fun goToPaymentDetailFragment(
		address: String,
		count: Double,
		token: WalletDetailCellModel
	) {
		getParentFragment<TokenDetailOverlayFragment>()?.apply {
			hideChildFragment(this@AddressSelectionFragment)
			addFragmentAndSetArgument<PaymentDetailFragment>(
				ContainerID.content,
				Pair(ArgumentKey.paymentAddress, address),
				Pair(ArgumentKey.paymentCount, count),
				Pair(ArgumentKey.tokenModel, token)
			)
			showBackButton(true) {
				presenter.popFragmentFrom<PaymentDetailFragment>()
			}
		}
	}

	override fun goToPaymentDetailWithExistedCheckedDialog(
		addresses: List<String>,
		toAddress: String,
		count: Double,
		token: WalletDetailCellModel
	) {
		if (addresses.any { it.equals(toAddress, true) }) {
			showAlert(
				TokenDetailText.transferToLocalWalletAlertDescription,
				TokenDetailText.transferToLocalWalletAlertTitle
			) {
				goToPaymentDetailFragment(toAddress, count, token)
			}
		} else goToPaymentDetailFragment(toAddress, count, token)
	}

	private fun showAlert(title: String, subtitle: String, callback: () -> Unit) {
		alert(title, subtitle) {
			yesButton { callback() }
			noButton { }
		}.show()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		asyncData = arrayListOf()
		token?.let {
			presenter = AddressSelectionPresenter(it, this)
			presenter.start()
		}
		wrapper.addView(confirmButton)
		setScanButtonStatus {
			QRCodePresenter.scanQRCode(this)
		}
		wrapper.keyboardHeightListener {
			if (keyboardHeight != it) {
				viewHeight = ScreenSize.heightWithOutHeader - it
				confirmButton.y = viewHeight - buttonHeight * 1f
				keyboardHeight = it
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		setScanButtonStatus(false)
	}

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		if (hidden) setScanButtonStatus(false)
		else {
			getParentFragment<TokenDetailOverlayFragment>()?.apply {
				if (!isFromQuickTransfer) {
					showBackButton(true) {
						presenter.popFragmentFrom<AddressSelectionFragment>()
					}
				}
			}
			setScanButtonStatus {
				QRCodePresenter.scanQRCode(this)
			}
		}
	}

	// 扫描二维码后接受信息用的函数
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		// `LG` 手机在 `Scan` 回到当前 `Activity` 后键盘监听不到变化. 这里强行归位确认按钮
		confirmButton.y = ScreenSize.heightWithOutHeader - buttonHeight * 1f
		if (data.isNull()) return
		val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
		intentResult?.let {
			presenter.showPaymentDetailByQRCode(QRCode(it.contents))
		}
	}

	override fun showError(error: Throwable) {
		safeShowError(error)
	}

	private fun updateInputStatus() {
		headerView?.apply {
			getInputStatus { _, address ->
				if (!address.isNullOrBlank()) {
					confirmButton.apply {
						textColor = Spectrum.white
						addTouchRippleAnimation(Spectrum.green, Spectrum.yellow, RippleMode.Square)
						click {
							presenter.showPaymentDetail(address.replace(" ", "").orEmpty(), 0.0)
						}
					}
				} else confirmButton.apply {
					textColor = GrayScale.midGray
					addTouchRippleAnimation(GrayScale.whiteGray, Spectrum.blue, RippleMode.Square)
				}
			}
			setFocusStatus()
			recyclerView.scrollToPosition(0)
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		getParentFragment<TokenDetailOverlayFragment> {
			if (isFromQuickTransfer) {
				presenter.removeSelfFromActivity()
			} else {
				headerTitle = TokenDetailText.tokenDetail
				presenter.popFragmentFrom<AddressSelectionFragment>()
			}
		}
	}

	private fun setScanButtonStatus(isShow: Boolean = true, callback: () -> Unit = {}) {
		getParentFragment<TokenDetailOverlayFragment> {
			showScanButton(isShow, isFromQuickTransfer) {
				callback()
			}
		}
	}

	private fun generateConfirmButton(): TextView {
		return TextView(context).apply {
			text = CommonText.confirm
			typeface = GoldStoneFont.heavy(context)
			layoutParams = RelativeLayout.LayoutParams(matchParent, buttonHeight)
			textSize = fontSize(14)
			textColor = GrayScale.midGray
			gravity = Gravity.CENTER
			alignParentBottom()
			addTouchRippleAnimation(GrayScale.whiteGray, Spectrum.blue, RippleMode.Square)
		}
	}
}