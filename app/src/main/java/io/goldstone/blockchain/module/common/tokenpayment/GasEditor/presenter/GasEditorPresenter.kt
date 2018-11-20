package io.goldstone.blockchain.module.common.tokenpayment.gaseditor.presenter

import android.support.v4.app.Fragment
import com.blinnnk.extension.suffix
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.view.GasEditorFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import java.io.Serializable

/**
 * @date 2018/5/8 3:22 PM
 * @author KaySaith
 */
data class GasFee(
	var gasLimit: Long,
	val gasPrice: Long,
	val type: MinerFeeType
) : Serializable {
	fun getUsedAmount(): Long {
		return gasLimit * gasPrice
	}

	companion object {
		const val recommendPrice = 30L
	}
}

class GasEditorPresenter(
	override val fragment: GasEditorFragment
) : BasePresenter<GasEditorFragment>() {

	fun confirmGasCustom(gasPrice: Long, gasLimit: Long) {
		if (gasPrice <= 0 || gasLimit <= 0) {
			fragment.safeShowError(Throwable(AlertText.gasEditorEmpty))
		} else if (gasLimit < fragment.getGasSize() ?: CryptoValue.ethMinGasLimit) {
			fragment.safeShowError(
				Throwable(
					AlertText.gasLimitValue suffix "${fragment.getGasSize() ?: CryptoValue.ethMinGasLimit}")
			)
		} else fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			childFragmentManager.fragments.forEach {
				it.updateGasSelectionList(GasFee(gasLimit, gasPrice, MinerFeeType.Custom))
				presenter.popFragmentFrom<GasEditorFragment>()
			}
		}
	}

	private fun Fragment.updateGasSelectionList(fee: GasFee) {
		if (this is GasSelectionFragment) {
			arguments?.putSerializable(ArgumentKey.gasEditor, fee)
			arguments?.putSerializable(ArgumentKey.gasEditor, fee)
			presenter.addCustomFeeCell()
		}
	}

	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		fragment.activity?.let { SoftKeyboard.hide(it) }
	}
}