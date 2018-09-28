package io.goldstone.blockchain.module.common.walletimport.mnemonicimport.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.multichain.ChainPath
import io.goldstone.blockchain.crypto.multichain.GenerateMultiChainWallet
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
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
		multiChainPath: ChainPath,
		mnemonic: String,
		password: String,
		repeatPassword: String,
		passwordHint: String,
		isAgree: Boolean,
		name: String,
		callback: (GoldStoneError) -> Unit
	) {
		if (mnemonic.isEmpty()) {
			callback(AccountError.InvalidMnemonic)
		} else if (!isValidPath(multiChainPath).isNone()) callback(AccountError.InvalidBip44Path)
		else CreateWalletPresenter.checkInputValue(
			name,
			password,
			repeatPassword,
			isAgree,
			callback
		) { passwordValue, walletName ->
			val mnemonicContent =
				mnemonic.replaceWithPattern()
					.replace("\n", " ")
					.removeStartAndEndValue(" ")

			Mnemonic.validateMnemonic(mnemonicContent) isFalse {
				callback(AccountError.InvalidMnemonic)
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

	private fun isValidPath(multiChainPath: ChainPath): AccountError {
		return if (multiChainPath.ethPath.isNotEmpty() && !isValidBIP44Path(multiChainPath.ethPath)) {
			AccountError.InvalidBip44Path
		} else if (multiChainPath.btcPath.isNotEmpty() && !isValidBIP44Path(multiChainPath.btcPath)) {
			AccountError.InvalidBip44Path
		} else if (multiChainPath.testPath.isNotEmpty() && !isValidBIP44Path(multiChainPath.testPath)) {
			AccountError.InvalidBip44Path
		} else if (multiChainPath.ltcPath.isNotEmpty() && !isValidBIP44Path(multiChainPath.ltcPath)) {
			AccountError.InvalidBip44Path
		} else if (multiChainPath.etcPath.isNotEmpty() && !isValidBIP44Path(multiChainPath.etcPath)) {
			AccountError.InvalidBip44Path
		} else AccountError.None
	}

	private fun importWallet(
		mnemonic: String,
		multiChainPath: ChainPath,
		password: String,
		name: String,
		hint: String? = null,
		callback: (GoldStoneError) -> Unit
	) {
		// 加密 `Mnemonic` 后存入数据库, 用于用户创建子账号的时候使用
		val encryptMnemonic = JavaKeystoreUtil().encryptData(mnemonic)
		WalletTable.getAll {
			val isExistent = any {
				try {
					JavaKeystoreUtil()
						.decryptData(it.encryptMnemonic.orEmpty())
						.equals(mnemonic, true)
				} catch (error: Exception) {
					LogUtil.error("decrypt Data", error)
					false
				}
			}
			if (isExistent) {
				callback(AccountError.ExistAddress)
			} else {
				GenerateMultiChainWallet.import(
					fragment.context!!,
					mnemonic,
					password,
					multiChainPath
				) { multiChainAddresses ->
					// 如果地址已经存在则会返回空的多链地址 `Model`
					WalletImportPresenter.insertWalletToDatabase(
						multiChainAddresses,
						name,
						encryptMnemonic,
						multiChainPath,
						hint,
						callback
					)
				}
			}
		}
	}

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		setRootChildFragmentBackEvent<WalletImportFragment>(fragment)
	}

	private fun isValidBIP44Path(path: String): Boolean {
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