package io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter

import android.content.Context
import android.os.Bundle
import android.support.annotation.UiThread
import android.widget.EditText
import com.blinnnk.extension.getParentFragment
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.*
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.language.CreateWalletText
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.UIUtils.generateDefaultName
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.WebUrl
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.multichain.ChainAddresses
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.DefaultPath
import io.goldstone.blockchain.crypto.multichain.GenerateMultiChainWallet
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.EOSDefaultAllChainName
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.view.CreateWalletFragment
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.view.MnemonicBackupFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.presenter.WalletImportPresenter
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 22/03/2018 2:46 AM
 * @author KaySaith
 */
class CreateWalletPresenter(
	override val fragment: CreateWalletFragment
) : BasePresenter<CreateWalletFragment>() {

	private var nameText = ""
	private var passwordText = ""
	private var repeatPasswordText = ""

	fun showAgreementFragment() {
		val argument = Bundle().apply {
			putString(ArgumentKey.webViewUrl, WebUrl.terms)
		}
		showTargetFragment<WebViewFragment, WalletGenerationFragment>(
			CreateWalletText.agreement,
			CreateWalletText.mnemonicBackUp,
			argument
		)
	}

	fun generateWalletWith(
		isAgree: Boolean,
		hintInput: EditText,
		callback: (GoldStoneError) -> Unit
	) {
		checkInputValue(
			nameText,
			passwordText,
			repeatPasswordText,
			isAgree,
			callback
		) { password, walletName ->
			fragment.context?.generateWalletWith(
				password,
				walletName,
				hintInput.text?.toString(),
				callback
			)
		}
	}

	fun updateConfirmButtonStyle(
		nameInput: RoundInput,
		passwordInput: RoundInput,
		repeatPasswordInput: RoundInput,
		confirmButton: RoundButton
	) {
		nameInput.afterTextChanged = Runnable {
			nameText = nameInput.getContent()
			setConfirmButtonStyle(confirmButton)
		}
		passwordInput.afterTextChanged = Runnable {
			showPasswordSafeLevel(passwordInput)
			passwordText = passwordInput.getContent()
			setConfirmButtonStyle(confirmButton)
		}
		repeatPasswordInput.afterTextChanged = Runnable {
			repeatPasswordText = repeatPasswordInput.getContent()
			setConfirmButtonStyle(confirmButton)
		}
	}

	private fun setConfirmButtonStyle(confirmButton: RoundButton) {
		if (passwordText.length * repeatPasswordText.length != 0) {
			confirmButton.setBlueStyle(20.uiPX())
		} else {
			confirmButton.setGrayStyle(20.uiPX())
		}
	}

	private fun Context.generateWalletWith(
		password: String,
		name: String,
		hint: String? = null,
		callback: (GoldStoneError) -> Unit
	) {
		doAsync {
			GenerateMultiChainWallet.create(
				this@generateWalletWith,
				password
			) { multiChainAddresses, mnemonic ->
				// 将基础的不存在安全问题的信息插入数据库
				WalletTable(
					0,
					name,
					multiChainAddresses.ethAddress,
					multiChainAddresses.etcAddress,
					multiChainAddresses.btcAddress,
					multiChainAddresses.btcSeriesTestAddress,
					multiChainAddresses.ltcAddress,
					multiChainAddresses.bchAddress,
					multiChainAddresses.eosAddress,
					EOSDefaultAllChainName(multiChainAddresses.eosAddress, multiChainAddresses.eosAddress),
					ethAddresses = WalletImportPresenter.childAddressValue(
						multiChainAddresses.ethAddress,
						WalletImportPresenter.getAddressIndexFromPath(DefaultPath.ethPath)
					),
					etcAddresses = WalletImportPresenter.childAddressValue(
						multiChainAddresses.etcAddress,
						WalletImportPresenter.getAddressIndexFromPath(DefaultPath.ethPath)
					),
					btcAddresses = WalletImportPresenter.childAddressValue(
						multiChainAddresses.btcAddress,
						WalletImportPresenter.getAddressIndexFromPath(DefaultPath.ethPath)
					),
					btcSeriesTestAddresses = WalletImportPresenter.childAddressValue(
						multiChainAddresses.btcSeriesTestAddress,
						WalletImportPresenter.getAddressIndexFromPath(DefaultPath.ethPath)
					),
					ltcAddresses = WalletImportPresenter.childAddressValue(
						multiChainAddresses.ltcAddress,
						WalletImportPresenter.getAddressIndexFromPath(DefaultPath.ltcPath)
					),
					bchAddresses = WalletImportPresenter.childAddressValue(
						multiChainAddresses.bchAddress,
						WalletImportPresenter.getAddressIndexFromPath(DefaultPath.bchPath)
					),
					eosAddresses = WalletImportPresenter.childAddressValue(
						multiChainAddresses.eosAddress,
						WalletImportPresenter.getAddressIndexFromPath(DefaultPath.eosPath)
					),
					eosAccountNames = listOf(),
					ethPath = DefaultPath.ethPath,
					btcPath = DefaultPath.btcPath,
					etcPath = DefaultPath.etcPath,
					btcTestPath = DefaultPath.testPath,
					bchPath = DefaultPath.bchPath,
					ltcPath = DefaultPath.ltcPath,
					eosPath = DefaultPath.eosPath,
					hint = hint,
					isUsing = true
				).insertWatchOnlyWallet { wallet ->
					generateMyTokenInfo(multiChainAddresses) {
						// 传递数据到下一个 `Fragment`
						val arguments = Bundle().apply {
							putString(ArgumentKey.mnemonicCode, mnemonic)
						}
						// 防止用户跳过助记词, 把使用 `RSA` 加密后的助记词存入数据库
						saveEncryptMnemonic(mnemonic, multiChainAddresses.ethAddress) {
							fragment.context?.runOnUiThread {
								showMnemonicBackupFragment(arguments)
								callback(GoldStoneError.None)
							}
						}
					}
					XinGePushReceiver.registerAddressesForPush(wallet)
				}
			}
		}
	}

	private fun saveEncryptMnemonic(
		mnemonic: String?,
		address: String,
		callback: () -> Unit
	) {
		mnemonic?.let {
			WalletTable.saveEncryptMnemonicIfUserSkip(
				JavaKeystoreUtil().encryptData(it),
				address
			) {
				callback()
			}
		}
	}

	private fun showMnemonicBackupFragment(arguments: Bundle) {
		// 创建钱包一旦成功跳转到 `备份助记词` 界面就不准许再回到创建的界面防止重复创建账号
		// 所以这里使用了覆盖 `Fragment` 的方法
		fragment.getParentFragment<WalletGenerationFragment> {
			replaceFragmentAndSetArgument<MnemonicBackupFragment>(ContainerID.content) {
				putAll(arguments)
			}
			headerTitle = CreateWalletText.mnemonicBackUp
		}
	}

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		setRootChildFragmentBackEvent<WalletGenerationFragment>(fragment)
	}

	companion object {

		fun showPasswordSafeLevel(passwordInput: RoundInput) {
			passwordInput.apply {
				val password = text.toString()
				password.checkPasswordInRules { SafeLevel, _ ->
					context?.runOnUiThread {
						setAlertStyle(SafeLevel)
					}
				}
			}
		}

		/**
		 * 拉取 `GoldStone` 默认显示的 `Token` 清单插入数据库
		 */
		fun generateMyTokenInfo(addresses: ChainAddresses, @UiThread callback: (RequestError) -> Unit) {
			doAsync {
				// 首先从本地查找数据
				GoldStoneDataBase.database.defaultTokenDao().getAllTokens().apply {
					// 本地没有数据从服务器获取数据
					if (isEmpty()) GoldStoneAPI.getDefaultTokens(callback) { serverTokens ->
						serverTokens.completeAddressInfo(addresses, callback)
					} else completeAddressInfo(addresses, callback)
				}
			}
		}

		fun checkInputValue(
			name: String,
			password: String,
			repeatPassword: String,
			isAgree: Boolean,
			errorCallback: (GoldStoneError) -> Unit,
			@UiThread callback: (password: String, walletName: String) -> Unit
		) {
			if (password.isEmpty()) errorCallback(AccountError.EmptyRepeatPassword)
			else if (!isAgree) errorCallback(AccountError.AgreeTerms)
			else if (password != repeatPassword) errorCallback(AccountError.DifferentRepeatPassword)
			else {
				val walletName = if (name.isEmpty()) generateDefaultName() else name
				val reason = ReasonText().apply {
					passwordCount = CreateWalletText.passwordCount
					illegalSymbol = CreateWalletText.illegalSymbol
					tooMuchSameValue = CreateWalletText.tooMuchSame
					bothNumberAndLetter = CreateWalletText.bothNumberAndLetter
					capitalAndLowercase = CreateWalletText.bothNumberAndLetter
					weak = CreateWalletText.safetyLevelWeak
					strong = CreateWalletText.safetyLevelStrong
					normal = CreateWalletText.safetyLevelNormal
					high = CreateWalletText.safetyLevelHigh
				}
				doAsync {
					password.checkPasswordInRules(reason) { _, reasons ->
						GoldStoneAPI.context.runOnUiThread {
							if (reasons == UnsafeReasons.None) callback(password, walletName)
							else errorCallback(AccountError.PasswordFormatted(reasons.info))
						}
					}
				}
			}
		}

		private fun List<DefaultTokenTable>.completeAddressInfo(
			currentAddresses: ChainAddresses,
			@UiThread callback: (RequestError) -> Unit
		) {
			// 初始的时候显示后台要求标记为 `force show` 的 `Token`
			// 新创建的钱包, 没有网络的情况下的导入钱包, 都直接插入账目为 `0.0` 的数据
			filter {
				TinyNumberUtils.isTrue(it.forceShow)
			}.insertNewAccount(currentAddresses) {
				callback(RequestError.None)
			}
		}

		private fun List<DefaultTokenTable>.insertNewAccount(
			currentAddresses: ChainAddresses,
			@UiThread callback: () -> Unit
		) {
			object : ConcurrentAsyncCombine() {
				override var asyncCount: Int = size
				override fun concurrentJobs() {
					forEach { defaults ->
						when (defaults.chainID) {
							ChainID.ethMain,
							ChainID.ropsten,
							ChainID.kovan,
							ChainID.rinkeby -> {
								if (currentAddresses.ethAddress.isNotEmpty()) {
									MyTokenTable(defaults, currentAddresses.ethAddress).insert()
								}
							}

							ChainID.etcMain, ChainID.etcTest -> {
								if (currentAddresses.etcAddress.isNotEmpty()) {
									MyTokenTable(defaults, currentAddresses.etcAddress).insert()
								}
							}

							ChainID.eosMain, ChainID.eosTest -> {
								if (currentAddresses.eosAddress.isNotEmpty()) {
									if (EOSWalletUtils.isValidAddress(currentAddresses.eosAddress))
										MyTokenTable(defaults, currentAddresses.eosAddress).insert()
									else if (EOSAccount(currentAddresses.eosAddress).isValid()) {
										// 这种情况通常是观察钱包的特殊情况, 有 `AccountName` 没有公钥的导入情况
										MyTokenTable(defaults, currentAddresses.eosAddress, "").insert()
									}
								}
							}

							ChainID.btcMain -> {
								if (currentAddresses.btcAddress.isNotEmpty()) {
									MyTokenTable(defaults, currentAddresses.btcAddress).insert()
								}
							}

							ChainID.btcTest, ChainID.ltcTest, ChainID.bchTest -> {
								if (currentAddresses.btcSeriesTestAddress.isNotEmpty()) {
									MyTokenTable(defaults, currentAddresses.btcSeriesTestAddress).insert()
								}
							}
							ChainID.ltcMain -> {
								if (currentAddresses.ltcAddress.isNotEmpty()) {
									MyTokenTable(defaults, currentAddresses.ltcAddress).insert()
								}
							}

							ChainID.bchMain -> {
								if (currentAddresses.bchAddress.isNotEmpty()) {
									MyTokenTable(defaults, currentAddresses.bchAddress).insert()
								}
							}
						}
						completeMark()
					}
				}

				override fun mergeCallBack() = callback()
			}.start()
		}
	}
}