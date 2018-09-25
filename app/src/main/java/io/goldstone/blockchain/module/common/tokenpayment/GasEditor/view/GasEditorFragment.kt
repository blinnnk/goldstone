package io.goldstone.blockchain.module.common.tokenpayment.gaseditor.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.presenter.GasEditorPresenter
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.MinerFeeType
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 2018/5/8 3:22 PM
 * @author KaySaith
 */
class GasEditorFragment : BaseFragment<GasEditorPresenter>() {

	override val pageTitle: String = TokenDetailText.customGas
	val getGasSize: () -> Long? = {
		arguments?.getLong(ArgumentKey.gasSize)
	}
	val isBTCSeries by lazy {
		arguments?.getBoolean(ArgumentKey.isBTCSeries).orFalse()
	}
	private val gasPriceInput by lazy { RoundInput(context!!) }
	private val gasLimitInput by lazy { RoundInput(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	private val speedLevelBar by lazy { GasSpeedLevelBar(context!!) }
	override val presenter = GasEditorPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
			gasPriceInput.apply {
				setNumberInput()
				setMargins<LinearLayout.LayoutParams> { topMargin = 50.uiPX() }
				title = if (isBTCSeries) TransactionText.satoshiValue else TransactionText.gasPrice
			}.into(this)
			// 只有 `ETH ERC20 or ETC` 才有 `GasLimit` 的概念
			if (!isBTCSeries) {
				gasLimitInput.apply {
					setNumberInput()
					setText(getGasSize().toString())
					setMargins<LinearLayout.LayoutParams> { topMargin = 15.uiPX() }
					title = TransactionText.gasLimit
				}.into(this)
			}

			speedLevelBar.apply {
				setMargins<RelativeLayout.LayoutParams> { topMargin = 30.uiPX() }
			}.into(this)

			confirmButton.apply {
				text = CommonText.confirm
				setBlueStyle(20.uiPX())
			}.click {
				presenter.confirmGasCustom(gasPrice, dataSize)
			}.into(this)
			setProcessValue()
		}
	}

	override fun onStart() {
		super.onStart()
		val defaultPrice =
			if (isBTCSeries) MinerFeeType.Recommend.satoshi else MinerFeeType.Recommend.value
		gasPriceInput.setText(defaultPrice.toString())
		gasPrice = defaultPrice
		getGasSize()?.let { dataSize = it }
	}

	private val currentValue: (
		gasPrice: Long,
		gasSize: Long
	) -> Double = { gasPrice, gasSize ->
		val fast = MinerFeeType.Fast.value * gasSize
		val btcFast = MinerFeeType.Fast.satoshi * gasSize
		(gasPrice * gasSize) / (if (isBTCSeries) btcFast else fast).toDouble()
	}
	private var gasPrice: Long by observing(0L) {
		speedLevelBar.setProgressValue(currentValue(gasPrice, dataSize))
	}
	private var dataSize: Long by observing(0L) {
		speedLevelBar.setProgressValue(currentValue(gasPrice, dataSize))
	}

	private fun setProcessValue() {
		gasPriceInput.let { price ->
			price.afterTextChanged = Runnable {
				gasPrice = if (price.getContent().isEmpty()) 0L else price.getContent().toLong()
			}
		}
		if (isBTCSeries) {
			dataSize = getGasSize().orElse(0L)
		} else {
			gasLimitInput.let { limit ->
				limit.afterTextChanged = Runnable {
					dataSize = if (limit.getContent().isEmpty()) getGasSize().orElse(0)
					else limit.getContent().toLong()
				}
			}
		}
	}

	override fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
		getParentFragment<TokenDetailOverlayFragment> {
			headerTitle = TokenDetailText.customGas
			presenter.popFragmentFrom<GasEditorFragment>()
		}
	}
}