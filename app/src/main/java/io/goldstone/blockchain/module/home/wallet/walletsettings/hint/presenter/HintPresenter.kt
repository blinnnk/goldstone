package io.goldstone.blockchain.module.home.wallet.walletsettings.hint.presenter

import android.widget.EditText
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.hint.view.HintFragment

/**
 * @date 24/04/2018 10:54 AM
 * @author KaySaith
 */
class HintPresenter(
	override val fragment: HintFragment
) : BasePresenter<HintFragment>() {

	fun updateHint(hintInput: EditText) {
		hintInput.text?.toString()?.let {
			it.isNotEmpty() isTrue {
				WalletTable.updateHint(it) {
					fragment.context?.alert(CommonText.succeed)
				}
			} otherwise {
				fragment.context?.alert(WalletSettingsText.hintAlert)
			}
		}
	}

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		// 初始化高度
		updateHeight(250.uiPX())
	}
}