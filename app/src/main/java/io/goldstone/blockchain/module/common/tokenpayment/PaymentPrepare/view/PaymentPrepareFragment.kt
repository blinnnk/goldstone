package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.view

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.animation.updateAlphaAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.GraySqualCell
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.TopBottomLineCell
import io.goldstone.blockchain.common.component.ValueInputView
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter.PaymentPreparePresenter
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 2018/5/15 10:18 PM
 * @author KaySaith
 */

class PaymentPrepareFragment : BaseFragment<PaymentPreparePresenter>() {

	val address by lazy { arguments?.getString(ArgumentKey.paymentAddress) }
	val count by lazy { arguments?.getDouble(ArgumentKey.paymentCount).orElse(0.0) }
	private val inputView by lazy { ValueInputView(context!!) }
	private val sendInfo by lazy { GraySqualCell(context!!) }
	private val from by lazy { GraySqualCell(context!!) }
	private val memo by lazy { GraySqualCell(context!!) }
	private val price by lazy { GraySqualCell(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	private var memoInputView: MemoInputView? = null
	private var memoData: String = ""

	private lateinit var container: RelativeLayout

	override val presenter = PaymentPreparePresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			isVerticalScrollBarEnabled = false
			lparams(matchParent, matchParent)
			container = relativeLayout {
				lparams(matchParent, matchParent)
				verticalLayout {
					gravity = Gravity.CENTER_HORIZONTAL
					lparams(matchParent, matchParent)
					addView(inputView.apply {
						if (count > 0) {
							setInputValue(count)
						}
					})

					TopBottomLineCell(context).apply {
						layoutParams =
							LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 150.uiPX()).apply {
								topMargin = 10.uiPX()
							}
						setTitle(PrepareTransferText.accountInfo)

						sendInfo.apply {
							setTitle(PrepareTransferText.send)
							setSubtitle(CryptoUtils.scaleMiddleAddress(address?.toUpperCase().orEmpty()))
						}.into(this)

						from.apply {
							setTitle(PrepareTransferText.from)
							setSubtitle(CryptoUtils.scaleMiddleAddress(WalletTable.current.address.toUpperCase()))
						}.into(this)

					}.into(this)

					TopBottomLineCell(context).apply {
						layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 100.uiPX())
						setTitle(PrepareTransferText.memoInformation)
						memo.apply {
							setTitle(PrepareTransferText.memo)
							setSubtitle(PrepareTransferText.addAMemo)
							showArrow()
							addTouchRippleAnimation(GrayScale.whiteGray, Spectrum.green, RippleMode.Square)
						}.click {
							container.showMemoInputView {
								if (it.isNotEmpty()) {
									memoData = it
									memo.setSubtitle(it)
								} else {
									memo.setSubtitle(PrepareTransferText.addAMemo)
								}
							}
						}.into(this)
					}.into(this)

					TopBottomLineCell(context).apply {
						layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 100.uiPX())
						setTitle(PrepareTransferText.currentPrice)
						price.apply {
							setTitle(PrepareTransferText.price)
						}.into(this)
					}.into(this)

					confirmButton.apply {
						setGrayStyle(20.uiPX())
						text = CommonText.next.toUpperCase()
						setMargins<LinearLayout.LayoutParams> {
							bottomMargin = 30.uiPX()
						}
					}.click {
						it.showLoadingStatus()
						presenter.goToGasEditorFragment {
							it.showLoadingStatus(false)
						}
					}.into(this)
					
					// 扫描二维码进入后的样式判断
					if (count > 0) {
						confirmButton.setBlueStyle(20.uiPX())
					}
				}
			}
		}
	}

	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(view, savedInstanceState)
		updateValueTotalPrice()
		resetBackButtonEvent()
	}

	override fun onResume() {
		super.onResume()
		inputView.setFoucs()
	}

	fun getMemoContent(): String {
		return memoData
	}

	fun getTransferCount(): Double {
		return if (inputView.getValue().isEmpty()) 0.0 else inputView.getValue().toDouble()
	}

	private fun resetBackButtonEvent() {
		/** 从下一个页面返回后通过显示隐藏监听重设回退按钮的事件 */
		getParentFragment<TokenDetailOverlayFragment>()?.apply {
			overlayView.header.showBackButton(true) {
				if (memoInputView.isNull()) {
					presenter.setValueHeader(token)
					presenter.popFragmentFrom<PaymentPrepareFragment>()
					this@PaymentPrepareFragment.presenter.recoveryFragmentHeight()
				} else {
					removeMemoInputView()
				}
			}
		}
	}

	private fun ViewGroup.showMemoInputView(hold: (String) -> Unit) {
		if (memoInputView.isNull()) {
			memoInputView = MemoInputView(context).apply {
				updateConfirmButtonEvent { button ->
					button.onClick {
						hold(getMemoContent())
						removeMemoInputView()
						button.preventDuplicateClicks()
					}
				}
			}
			memoInputView?.into(this)
		}
	}

	private fun removeMemoInputView() {
		memoInputView?.updateAlphaAnimation(0f) {
			container.removeView(memoInputView)
			memoInputView = null
		}
	}

	private fun updateValueTotalPrice() {
		val price =
			getParentFragment<TokenDetailOverlayFragment>()?.token?.price ?: 0.0
		inputView.inputTextListener {
			inputView.updateCurrencyValue(price)
			if (it.isNotEmpty()) {
				confirmButton.setBlueStyle(20.uiPX())
			} else {
				confirmButton.setGrayStyle(20.uiPX())
			}
			confirmButton.setMargins<LinearLayout.LayoutParams> {
				bottomMargin = 30.uiPX()
			}
		}
	}

	fun setSymbolAndPrice(
		symbol: String,
		price: String
	) {
		this.inputView.setHeaderSymbol(symbol)
		this.price.setSubtitle(price)
	}

	override fun setBackEvent(
		activity: MainActivity,
		parent: Fragment?
	) {
		getParentFragment<TokenDetailOverlayFragment>()?.let {
			presenter.backEvent(it)
		}
	}

}