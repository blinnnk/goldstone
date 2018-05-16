package io.goldstone.blockchain.module.common.tokenpayment.gaseditor.presenter

import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.crypto.scaleToGwei
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.view.GasEditorFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import java.io.Serializable

/**
 * @date 2018/5/8 3:22 PM
 * @author KaySaith
 */

data class GasFee(
	var gasLimit: Long,
	val gasPrice: Long
) : Serializable

class GasEditorPresenter(
	override val fragment: GasEditorFragment
) : BasePresenter<GasEditorFragment>() {

	fun confirmGasCustom(gasPrice: Long, gasLimit: Long) {
		if (gasPrice <= 0 || gasLimit <= 0) {
			fragment.context?.alert("You have to set gas price or gas limit")
			return
		}

		if (gasLimit < fragment.minLimit ?: 21000) {
			fragment.context?.alert("Gas limit must more than ${fragment.minLimit ?: 21000}")
			return
		}

		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			childFragmentManager.fragments.forEach {
				if (it is GasSelectionFragment) {
					MinerFeeType.Custom.value = gasPrice
					it.arguments?.putSerializable(ArgumentKey.gasEditor, GasFee(gasLimit, gasPrice))
					it.presenter.insertCustomGasData()
				}
				presenter.popFragmentFrom<GasEditorFragment>()
				headerTitle = TokenDetailText.paymentValue
			}
		}
	}

	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		fragment.activity?.let { SoftKeyboard.hide(it) }
	}

}