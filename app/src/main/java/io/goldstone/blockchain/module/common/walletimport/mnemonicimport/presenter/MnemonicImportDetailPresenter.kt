package io.goldstone.blockchain.module.common.walletimport.mnemonicimport.presenter

import android.widget.EditText
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.getWalletByMnemonic
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.mnemonicimport.view.MnemonicImportDetailFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.presenter.WalletImportPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment

/**
 * @date 23/03/2018 1:46 AM
 * @author KaySaith
 */
class MnemonicImportDetailPresenter(
	override val fragment: MnemonicImportDetailFragment
) : BasePresenter<MnemonicImportDetailFragment>() {
	
	fun importWalletByMnemonic(
		pathInput: EditText,
		mnemonicInput: EditText,
		repeatPasswordInput: EditText,
		hintInput: EditText,
		isAgree: Boolean,
		nameInput: EditText,
		callback: () -> Unit
	) {
		if (pathInput.text.isNotEmpty() && !isVaildPath(pathInput.text.toString())) {
			fragment.context?.alert(ImportWalletText.pathAlert)
			callback()
			return
		}
		
		mnemonicInput.text.isEmpty() isTrue {
			fragment.context?.alert(ImportWalletText.mnemonicAlert)
			callback()
			return
		}
		val pathValue =
			if (pathInput.text.isEmpty()) "m/44'/60'/0'/0/0"
			else pathInput.text.toString()
		
		CreateWalletPresenter.checkInputValue(
			nameInput.text.toString(),
			repeatPasswordInput.text.toString(),
			isAgree,
			fragment.context,
			failedCallback = { callback() }) { passwordValue, walletName ->
			val mnemonicContent =
				mnemonicInput
					.text.toString()
					.replaceWithPattern()
					.replace("\n", " ")
					.removeStartAndEndValue(" ")
			
			Mnemonic.validateMnemonic(mnemonicContent) isFalse {
				fragment.context?.alert(ImportWalletText.mnemonicAlert)
				callback()
			} otherwise {
				importWallet(
					mnemonicContent,
					pathValue,
					passwordValue,
					walletName,
					hintInput.text?.toString(),
					callback
				)
			}
		}
	}
	
	private fun importWallet(
		mnemonic: String,
		pathValue: String,
		password: String,
		name: String,
		hint: String? = null,
		callback: () -> Unit
	) {
		fragment.context?.getWalletByMnemonic(
			mnemonic,
			pathValue,
			password
		) { address ->
			address?.let {
				WalletImportPresenter.insertWalletToDatabase(
					fragment, it, name, hint, callback
				)
			}
		}
	}
	
	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		setRootChildFragmentBackEvent<WalletImportFragment>(fragment)
	}
	
	private fun isVaildPath(path: String): Boolean {
		// 最小 3 位数字
		if (path.length < 3) return false
		// 格式化无用信息
		val formatPath = path.replace("\n", "").replace(" ", "")
		// 校验前两位强制内容
		return if (formatPath.substring(0, 2).equals("m/", true)) {
			val pathNumber = formatPath.substring(1, formatPath.length).replace("/", "").replace("'", "")
			// 检验剩余部分是否全部为数字
			!pathNumber.toIntOrNull().isNull()
		} else {
			false
		}
	}
}