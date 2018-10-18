package io.goldstone.blockchain.common.utils

import android.support.v4.app.Fragment
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.language.DialogText
import io.goldstone.blockchain.R

class ShowBackToHomeConfirmationDialog(private val fragment : Fragment) {
	fun showBackToHomeConfirmationDialog() {
		fragment.context?.let {
			GoldStoneDialog.show(it) {
				showButtons() {
					fragment.activity?.finish()
				}
				setImage(R.drawable.network_browken_banner)
				setContent(
					DialogText.title,DialogText.subtitle
				)
			}
		}
	}
}