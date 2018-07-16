package io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.presenter

import android.widget.EditText
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.crypto.getKeystoreFile
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.view.KeystoreExportFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.support.v4.toast

/**
 * @date 06/04/2018 1:46 AM
 * @author KaySaith
 */
class KeystoreExportPresenter(
	override val fragment: KeystoreExportFragment
) : BasePresenter<KeystoreExportFragment>() {
	
	private val address by lazy { fragment.arguments?.getString(ArgumentKey.address) }
	
	fun getKeystoreByAddress(
		passwordInput: EditText,
		hold: String.() -> Unit
	) {
		if (passwordInput.text?.toString().isNullOrBlank()) {
			fragment.toast(ImportWalletText.exportWrongPassword)
			hold("")
			return
		}
		
		fragment.activity?.apply {
			SoftKeyboard.hide(this)
		}
		address?.let {
			doAsync {
				fragment.context?.getKeystoreFile(
					it,
					passwordInput.text.toString(),
					{
						hold("")
					}
				) { it ->
					GoldStoneAPI.context.runOnUiThread {
						hold(it)
					}
				}
			}
		}
	}
}