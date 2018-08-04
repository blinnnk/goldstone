package io.goldstone.blockchain.module.common.walletimport.mnemonicimport.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.crypto.GenerateMultiChainWallet
import io.goldstone.blockchain.crypto.MultiChainPath
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
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
		multiChainPath: MultiChainPath,
		mnemonic: String,
		password: String,
		repeatPassword: String,
		passwordHint: String,
		isAgree: Boolean,
		name: String,
		callback: (Boolean) -> Unit
	) {
		mnemonic.isEmpty() isTrue {
			fragment.context?.alert(ImportWalletText.mnemonicAlert)
			callback(false)
			return
		}
		
		if (!isValidPath(multiChainPath)) return
		
		CreateWalletPresenter.checkInputValue(
			name,
			password,
			repeatPassword,
			isAgree,
			fragment.context,
			failedCallback = { callback(false) }
		) { passwordValue, walletName ->
			val mnemonicContent =
				mnemonic
					.replaceWithPattern()
					.replace("\n", " ")
					.removeStartAndEndValue(" ")
			
			Mnemonic.validateMnemonic(mnemonicContent) isFalse {
				fragment.context?.alert(ImportWalletText.mnemonicAlert)
				callback(false)
			} otherwise {
				importWallet(
					mnemonicContent,
					multiChainPath,
					passwordValue,
					walletName,
					passwordHint,
					callback
				)
			}
		}
	}
	
	private fun isValidPath(multiChainPath: MultiChainPath): Boolean {
		return if (multiChainPath.ethPath.isNotEmpty() && !isVaildPath(multiChainPath.ethPath)) {
			fragment.context?.alert(ImportWalletText.pathAlert)
			false
		} else if (multiChainPath.btcPath.isNotEmpty() && !isVaildPath(multiChainPath.btcPath)) {
			fragment.context?.alert(ImportWalletText.pathAlert)
			false
		} else if (multiChainPath.btcTestPath.isNotEmpty() && !isVaildPath(multiChainPath.btcTestPath)) {
			fragment.context?.alert(ImportWalletText.pathAlert)
			false
		} else if (multiChainPath.etcPath.isNotEmpty() && !isVaildPath(multiChainPath.etcPath)) {
			fragment.context?.alert(ImportWalletText.pathAlert)
			false
		} else true
	}
	
	private fun importWallet(
		mnemonic: String,
		multiChainPath: MultiChainPath,
		password: String,
		name: String,
		hint: String? = null,
		callback: (Boolean) -> Unit
	) {
		// 加密 `Mnemonic` 后存入数据库, 用于用户创建子账号的时候使用
		val encryptMnemonic = JavaKeystoreUtil().encryptData(mnemonic)
		GenerateMultiChainWallet.import(
			fragment.context!!,
			mnemonic,
			password,
			multiChainPath
		) { multiChainAddresses ->
			// 本地若存有当前多链钱包则直接跳出逻辑
			if (multiChainAddresses.btcAddress.isEmpty() || multiChainAddresses.ethAddress.isEmpty()) {
				fragment.context.alert(ImportWalletText.existAddress)
				callback(false)
				return@import
			}
			WalletImportPresenter.insertWalletToDatabase(
				fragment.context,
				multiChainAddresses,
				name,
				encryptMnemonic,
				multiChainPath,
				hint = hint,
				callback = callback
			)
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