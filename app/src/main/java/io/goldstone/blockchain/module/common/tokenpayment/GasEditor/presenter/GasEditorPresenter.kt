package io.goldstone.blockchain.module.common.tokenpayment.gaseditor.presenter

import android.support.v4.app.Fragment
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.view.GasEditorFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.insertCustomBTCSatoshi
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.insertCustomGasData
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import java.io.Serializable

/**
 * @date 2018/5/8 3:22 PM
 * @author KaySaith
 */
data class GasFee(var gasLimit: Long, val gasPrice: Long) : Serializable

class GasEditorPresenter(
	override val fragment: GasEditorFragment
) : BasePresenter<GasEditorFragment>() {

	fun confirmGasCustom(gasPrice: Long, gasLimit: Long) {
		if (gasPrice <= 0 || gasLimit <= 0) {
			fragment.context?.alert(AlertText.gasEditorEmpty)
			return
		}

		if (gasLimit < fragment.getGasSize() ?: CryptoValue.ethMinGasLimit) {
			fragment.context?.alert(
				"${AlertText.gasLimitValue} ${fragment.getGasSize() ?: CryptoValue.ethMinGasLimit}"
			)
			return
		}

		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			childFragmentManager.fragments.forEach {
				it.updateGasSelectionList(gasLimit, gasPrice)
				presenter.popFragmentFrom<GasEditorFragment>()
				headerTitle = TokenDetailText.paymentValue
			}
		}
	}

	private fun Fragment.updateGasSelectionList(gasLimit: Long, gasPrice: Long) {
		if (fragment.isBTCSeries) {
			if (this is GasSelectionFragment) {
				MinerFeeType.Custom.satoshi = gasPrice
				arguments?.putSerializable(
					ArgumentKey.gasEditor,
					GasFee(gasLimit, gasPrice)
				)
				presenter.insertCustomBTCSatoshi()
			}
		} else {
			if (this is GasSelectionFragment) {
				MinerFeeType.Custom.value = gasPrice
				arguments?.putSerializable(
					ArgumentKey.gasEditor,
					GasFee(gasLimit, gasPrice)
				)
				presenter.insertCustomGasData()
			}
		}
	}

	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		fragment.activity?.let { SoftKeyboard.hide(it) }
	}
}