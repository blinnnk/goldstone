package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.animation.updateAlphaAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.ValueInputView
import io.goldstone.blockchain.common.component.WalletEditText
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.cell.GraySquareCell
import io.goldstone.blockchain.common.component.cell.TopBottomLineCell
import io.goldstone.blockchain.common.component.overlay.DashboardOverlay
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.PrepareTransferText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.crypto.multichain.CryptoSymbol
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.MultiChainUtils
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter.PaymentPreparePresenter
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter.isValidAddressOrElse
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter.isValidLTCAddressOrElse
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 2018/5/15 10:18 PM
 * @author KaySaith
 */
class PaymentPrepareFragment : BaseFragment<PaymentPreparePresenter>() {

	val address by lazy {
		arguments?.getString(ArgumentKey.paymentAddress)
	}
	val count by lazy {
		arguments?.getDouble(ArgumentKey.paymentCount).orElse(0.0)
	}
	val rootFragment by lazy {
		getParentFragment<TokenDetailOverlayFragment>()
	}
	private val inputView by lazy { ValueInputView(context!!) }
	private val sendInfo by lazy { GraySquareCell(context!!) }
	private val from by lazy { GraySquareCell(context!!) }
	private val memo by lazy { GraySquareCell(context!!) }
	private val customChangeAddressCell by lazy { GraySquareCell(context!!) }
	private val price by lazy { GraySquareCell(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	private var memoInputView: MemoInputView? = null
	private var memoData: String = ""
	private lateinit var changeAddress: String
	override val presenter = PaymentPreparePresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		changeAddress = MultiChainUtils.getAddressBySymbol(rootFragment?.token?.symbol)
		scrollView {
			isVerticalScrollBarEnabled = false
			lparams(matchParent, matchParent)
			verticalLayout {
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, matchParent)

				inputView.into(this)

				showAccountInfo()
				// `BTCSeries` 于 ETH, ERC20, ETC 显示不同的配置信息
				if (CryptoSymbol.isBTCSeriesSymbol(rootFragment?.token?.symbol))
					showCustomChangeAddressCell()
				else showMemoCell()

				showUnitPrice()

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
					inputView.setInputValue(count)
					confirmButton.setBlueStyle(20.uiPX())
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

	fun setSymbolAndPrice(symbol: String, price: String) {
		this.inputView.setHeaderSymbol(symbol)
		this.price.setSubtitle(price)
	}

	fun updateChangeAddress(address: String) {
		customChangeAddressCell.setSubtitle(address)
	}

	fun getChangeAddress(): String {
		return changeAddress
	}

	override fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
		if (memoInputView.isNull()) {
			getParentFragment<TokenDetailOverlayFragment>()?.let {
				presenter.backEvent(it)
			}
		} else {
			removeMemoInputView()
		}
	}

	private fun LinearLayout.showMemoCell() {
		TopBottomLineCell(context).apply {
			layoutParams = LinearLayout.LayoutParams(matchParent, 100.uiPX())
			setHorizontalPadding(PaddingSize.device.toFloat())
			setTitle(PrepareTransferText.memoInformation)
			memo.apply {
				setTitle(PrepareTransferText.memo)
				setSubtitle(CryptoUtils.scaleTo32(PrepareTransferText.addAMemo))
				showArrow()
			}.click {
				getParentContainer()?.showMemoInputView { content ->
					if (content.isNotEmpty()) {
						memoData = content
						memo.setSubtitle(content)
					} else {
						memo.setSubtitle(PrepareTransferText.addAMemo)
					}
				}
			}.into(this)
		}.into(this)
	}

	private fun LinearLayout.showCustomChangeAddressCell() {
		TopBottomLineCell(context).apply {
			layoutParams = LinearLayout.LayoutParams(matchParent, 100.uiPX())
			setHorizontalPadding(PaddingSize.device.toFloat())
			setTitle(PrepareTransferText.customChangeAddress)
			customChangeAddressCell.apply {
				setTitle(PrepareTransferText.changeAddress)
				setSubtitle(CryptoUtils.scaleTo16(changeAddress))
				showArrow()
			}.click {
				showCustomChangeAddressOverlay()
			}.into(this)
		}.into(this)
	}

	private fun showCustomChangeAddressOverlay() {
		getParentContainer()?.apply {
			val addressInput = WalletEditText(context)
			DashboardOverlay(context) {
				textView {
					text = PrepareTransferText.customChangeAddress
					textSize = fontSize(16)
					textColor = GrayScale.black
					typeface = GoldStoneFont.black(context)
					gravity = Gravity.CENTER_HORIZONTAL
				}
				addressInput.apply {
					setMargins<LinearLayout.LayoutParams> {
						width = ScreenSize.widthWithPadding - 40.uiPX()
						topMargin = 10.uiPX()
					}
					hint = changeAddress
				}
				addressInput.into(this)
			}.apply {
				confirmEvent = Runnable {
					val customAddress = addressInput.text?.toString().orEmpty()
					if (presenter.getToken()?.symbol.equals(CryptoSymbol.btc(), true)) {
						presenter.isValidAddressOrElse(customAddress) isTrue {
							// 更新默认的自定义找零地址
							changeAddress = customAddress
						}
					} else {
						presenter.isValidLTCAddressOrElse(customAddress) isTrue {
							// 更新默认的自定义找零地址
							changeAddress = customAddress
						}
					}
				}
			}.into(this)
		}
	}

	private fun LinearLayout.showAccountInfo() {
		TopBottomLineCell(context).apply {
			layoutParams =
				LinearLayout.LayoutParams(matchParent, 150.uiPX()).apply {
					topMargin = 10.uiPX()
				}
			setHorizontalPadding(PaddingSize.device.toFloat())
			setTitle(PrepareTransferText.accountInfo)

			sendInfo.apply {
				setTitle(PrepareTransferText.send)
				setSubtitle(CryptoUtils.scaleMiddleAddress(address.orEmpty()))
			}.into(this)

			from.apply {
				setTitle(PrepareTransferText.from)
			}.into(this)

			setFromAddress()
		}.into(this)
	}

	private fun LinearLayout.showUnitPrice() {
		TopBottomLineCell(context).apply {
			layoutParams = LinearLayout.LayoutParams(matchParent, 100.uiPX())
			setHorizontalPadding(PaddingSize.device.toFloat())
			setTitle(PrepareTransferText.currentPrice)
			price.apply {
				setTitle(PrepareTransferText.price)
			}.into(this)
		}.into(this)
	}

	private fun setFromAddress() {
		from.setSubtitle(
			CryptoUtils.scaleMiddleAddress(
				MultiChainUtils.getAddressBySymbol(presenter.getToken()?.symbol)
			)
		)
	}

	private fun ViewGroup.showMemoInputView(hold: (String) -> Unit) {
		if (memoInputView.isNull()) {
			// 禁止上下滚动
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
			getParentContainer()?.removeView(memoInputView)
			memoInputView = null
		}
	}

	private fun updateValueTotalPrice() {
		val price = rootFragment?.token?.price ?: 0.0
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

	private fun resetBackButtonEvent() {
		// 从下一个页面返回后通过显示隐藏监听重设回退按钮的事件
		rootFragment?.apply {
			overlayView.header.showBackButton(true) {
				if (memoInputView.isNull()) {
					setValueHeader(token)
					presenter.popFragmentFrom<PaymentPrepareFragment>()
				} else {
					removeMemoInputView()
				}
			}
		}
	}
}