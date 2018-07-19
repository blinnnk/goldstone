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
import com.google.zxing.integration.android.IntentIntegrator
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.presenter.AddressSelectionPresenter
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.presenter.QRCodePresenter
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.textColor

/**
 * @date 28/03/2018 9:24 AM
 * @author KaySaith
 */
class AddressSelectionFragment : BaseRecyclerFragment<AddressSelectionPresenter, ContactTable>() {
	
	private val buttonHeight = 50.uiPX()
	private var viewHeight = 0
	private var keyboardHeight = 0
	private val confirmButton by lazy {
		TextView(context).apply {
			text = CommonText.confirm
			typeface = GoldStoneFont.heavy(context)
			layoutParams = RelativeLayout.LayoutParams(matchParent, buttonHeight)
			textSize = fontSize(14)
			textColor = GrayScale.midGray
			gravity = Gravity.CENTER
			setAlignParentBottom()
			addTouchRippleAnimation(GrayScale.whiteGray, Spectrum.blue, RippleMode.Square)
		}
	}
	override val presenter = AddressSelectionPresenter(this)
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<ContactTable>?
	) {
		recyclerView.adapter = AddressSelectionAdapter(asyncData.orEmptyArray()) {
			clickEvent = Runnable {
				presenter.showPaymentPrepareFragment(model.ethERCAndETCAddress)
			}
		}
	}
	
	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(view, savedInstanceState)
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
		if (hidden) {
			setScanButtonStatus(false)
		} else {
			setScanButtonStatus {
				QRCodePresenter.scanQRCode(this)
			}
		}
	}
	
	/**
	 * 扫描二维码后接受信息用的函数
	 */
	override fun onActivityResult(
		requestCode: Int,
		resultCode: Int,
		data: Intent?
	) {
		super.onActivityResult(requestCode, resultCode, data)
		if (data.isNull()) return
		val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
		intentResult?.let {
			presenter.showPaymentPrepareFragmentByQRCode(it.contents)
		}
	}
	
	fun updateHeaderViewStatus() {
		recyclerView.getItemAtAdapterPosition<AddressSelectionHeaderView>(0) {
			it?.setFocusStatus()
			it?.getInputStatus { _, address ->
				if (!address.isNullOrBlank()) {
					confirmButton.apply {
						textColor = Spectrum.white
						addTouchRippleAnimation(Spectrum.green, Spectrum.yellow, RippleMode.Square)
						onClick {
							presenter.showPaymentPrepareFragment(address.orEmpty())
							preventDuplicateClicks()
						}
					}
				} else {
					confirmButton.apply {
						textColor = GrayScale.midGray
						addTouchRippleAnimation(GrayScale.whiteGray, Spectrum.blue, RippleMode.Square)
					}
				}
			}
		}
	}
	
	private fun setScanButtonStatus(
		isShow: Boolean = true,
		callback: () -> Unit = {}
	) {
		getParentFragment<TokenDetailOverlayFragment> {
			overlayView.header.showScanButton(isShow, isFromQuickTransfer) {
				callback()
			}
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
}