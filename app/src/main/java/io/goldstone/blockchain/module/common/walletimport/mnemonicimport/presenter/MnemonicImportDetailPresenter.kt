package io.goldstone.blockchain.module.common.walletimport.mnemonicimport.presenter

import android.widget.EditText
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.removeStartAndEndValue
import io.goldstone.blockchain.common.utils.replaceWithPattern
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.crypto.big39.MnemonicWordList
import io.goldstone.blockchain.crypto.getWalletByMnemonic
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.mnemonicimport.view.MnemonicImportDetailFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.presenter.WalletImportPresenter

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
		passwordInput: EditText,
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
		val pathValue = if (pathInput.text.isEmpty()) "m/44'/60'/0'/0/0" else pathInput.text.toString()
		CreateWalletPresenter.checkInputValue(nameInput.text.toString(), passwordInput.text.toString(),
			repeatPasswordInput.text.toString(), isAgree, fragment.context,
			failedCallback = { callback() }) { passwordValue, walletName ->
			val mnemonicContent =
				mnemonicInput.text.toString().replaceWithPattern().replace("\n", " ")
					.removeStartAndEndValue(" ")

			isValidMnemonic(mnemonicContent) {
				it isFalse {
					fragment.context?.alert(ImportWalletText.mnemonicAlert)
					callback()
				} otherwise {
					importWallet(
						mnemonicContent, pathValue, passwordValue, walletName, hintInput.text?.toString(),
						callback
					)
				}
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
			mnemonic, pathValue, password
		) { address ->
			address?.let {
				WalletImportPresenter.insertWalletToDatabase(
					fragment, it, name, hint, callback
				)
			}
		}
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

	private fun isValidMnemonic(
		mnemonic: String,
		hold: (Boolean) -> Unit
	) {
		val inputMnemonicSize = mnemonic.split(" ").size
		if (inputMnemonicSize < 12) {
			fragment.context?.alert(ImportWalletText.mnemonicLengthAlert)
			hold(false)
			return
		} else {
			var errorCode = 1
			object : ConcurrentAsyncCombine() {
				override var asyncCount: Int = inputMnemonicSize
				override fun concurrentJobs() {
					mnemonic.split(" ").forEach { inputMnemonic ->
						MnemonicWordList.any {
							it == inputMnemonic
						} isTrue {
							errorCode *= 1
							completeMark()
						} otherwise {
							errorCode *= 0
							completeMark()
						}
					}
				}

				override fun mergeCallBack() {
					hold(errorCode == 1)
				}
			}.start()
		}
	}

}