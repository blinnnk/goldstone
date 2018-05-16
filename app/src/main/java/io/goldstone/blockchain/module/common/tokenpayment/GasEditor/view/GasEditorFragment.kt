package io.goldstone.blockchain.module.common.tokenpayment.gaseditor.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.presenter.GasEditorPresenter
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.MinerFeeType
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 2018/5/8 3:22 PM
 * @author KaySaith
 */

class GasEditorFragment : BaseFragment<GasEditorPresenter>() {

	val minLimit by lazy { arguments?.getLong(ArgumentKey.gasLimit) }

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
				title = TransactionText.gasPrice
			}.into(this)

			gasLimitInput.apply {
				setNumberInput()
				hint = minLimit.toString()
				setMargins<LinearLayout.LayoutParams> { topMargin = 15.uiPX() }
				title = TransactionText.gasLimit
			}.into(this)

			speedLevelBar.apply {
				setMargins<RelativeLayout.LayoutParams> { topMargin = 30.uiPX() }
			}.into(this)

			confirmButton.apply {
				text = CommonText.confirm
				setBlueStyle(20.uiPX())
			}.click {
				presenter.confirmGasCustom(gasPrice, gasLimit)
			}.into(this)

			setProcessValue()
		}
	}

	private var fast = MinerFeeType.Fast.value * 21000
	private val currentValue: (gasPrice: Long, gasLimit: Long) -> Double = { gasPrice, gasLimit ->
		(gasPrice * gasLimit) / fast.toDouble()
	}

	private var gasPrice: Long by observing(0L) {
		speedLevelBar.setProgressValue(currentValue(gasPrice, gasLimit))
	}

	private var gasLimit: Long by observing(0L) {
		speedLevelBar.setProgressValue(currentValue(gasPrice, gasLimit))
	}

	private fun setProcessValue() {
		gasPriceInput.let { price ->
			price.afterTextChanged = Runnable {
				price.getContent {
					gasPrice = if (it.isEmpty()) 0L
					else it.toLong()
				}
			}
		}
		gasLimitInput.let { limit ->
			limit.afterTextChanged = Runnable {
				limit.getContent {
					gasLimit = if (it.isEmpty()) 0L
					else it.toLong()
				}
			}
		}
	}

}