package io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.UnsafeReasons
import com.blinnnk.util.checkPasswordInRules
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ChainID
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.common.value.WebUrl
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.JavaKeystoreUtil
import io.goldstone.blockchain.crypto.generateWallet
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.view.CreateWalletFragment
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.view.MnemonicBackupFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TinyNumber
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
			CreateWalletText.agreement, CreateWalletText.mnemonicBackUp, argument
		)
	}
	
	fun generateWalletWith(
		isAgree: Boolean,
		hintInput: EditText,
		callback: () -> Unit
	) {
		checkInputValue(
			nameText, passwordText, repeatPasswordText, isAgree, fragment.context, callback
		) { password, walletName ->
			fragment.context?.generateWalletWith(
				password, walletName, hintInput.text?.toString(), callback
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
			nameInput.getContent { nameText = it }
			setConfirmButtonStyle(confirmButton)
		}
		passwordInput.afterTextChanged = Runnable {
			passwordInput.getContent { passwordText = it }
			setConfirmButtonStyle(confirmButton)
		}
		repeatPasswordInput.afterTextChanged = Runnable {
			repeatPasswordInput.getContent { repeatPasswordText = it }
			setConfirmButtonStyle(confirmButton)
		}
	}
	
	private fun setConfirmButtonStyle(confirmButton: RoundButton) {
		if (nameText.count() * passwordText.count() * repeatPasswordText.count() != 0) {
			confirmButton.setBlueStyle(20.uiPX())
		} else {
			confirmButton.setGrayStyle(20.uiPX())
		}
	}
	
	private fun Context.generateWalletWith(
		password: String,
		name: String,
		hint: String? = null,
		callback: () -> Unit
	) {
		doAsync {
			generateWallet(password) { mnemonicCode, address ->
				// 将基础的不存在安全问题的信息插入数据库
				WalletTable.insert(WalletTable(0, name, address, true, hint)) {
					generateMyTokenInfo(address, true, {
						LogUtil.error("generateWalletWith")
					}) {
						// 传递数据到下一个 `Fragment`
						val arguments = Bundle().apply {
							putString(ArgumentKey.mnemonicCode, mnemonicCode)
						}
						// 防止用户跳过助记词, 会在完成助记词后清除记录的加密数据
						saveEncryptMnemonic(mnemonicCode, address) {
							fragment.context?.runOnUiThread {
								showMnemonicBackupFragment(arguments)
								callback()
							}
						}
					}
					
					XinGePushReceiver.registerWalletAddressForPush()
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
				JavaKeystoreUtil().encryptData(it), address
			) {
				callback()
			}
		}
	}
	
	private fun showMnemonicBackupFragment(arguments: Bundle) {
		showTargetFragment<MnemonicBackupFragment, WalletGenerationFragment>(
			CreateWalletText.mnemonicBackUp, CreateWalletText.create, arguments, false
		)
	}
	
	companion object {
		/**
		 * 拉取 `GoldStone` 默认显示的 `Token` 清单插入数据库
		 */
		fun generateMyTokenInfo(
			ownerAddress: String,
			isNewAccount: Boolean = false,
			errorCallback: () -> Unit,
			callback: () -> Unit
		) {
			// 首先从本地查找数据
			DefaultTokenTable.getTokens { localTokens ->
				localTokens.isEmpty() isTrue {
					// 本地没有数据从服务器获取数据
					GoldStoneAPI.getDefaultTokens(errorCallback) { serverTokens ->
						serverTokens.completeAddressInfo(ownerAddress, isNewAccount, callback)
					}
				} otherwise {
					localTokens.completeAddressInfo(ownerAddress, isNewAccount, callback)
				}
			}
		}
		
		fun checkInputValue(
			name: String,
			password: String,
			repeatPassword: String,
			isAgree: Boolean,
			context: Context?,
			failedCallback: () -> Unit = {},
			callback: (password: String, walletName: String) -> Unit
		) {
			isAgree isFalse {
				context?.alert(CreateWalletText.agreeRemind)
				failedCallback()
				return
			}
			
			if (password != repeatPassword) {
				context?.alert(CreateWalletText.repeatPassword)
				failedCallback()
				return
			}
			val walletName = if (name.isEmpty()) "Wallet" else name
			doAsync {
				password.checkPasswordInRules { _, reasons ->
					context?.apply {
						runOnUiThread {
							if (reasons == UnsafeReasons.None) {
								callback(password, walletName)
							} else {
								alert(reasons.info)
								failedCallback()
							}
						}
					}
				}
			}
		}
		
		private fun ArrayList<DefaultTokenTable>.completeAddressInfo(
			ownerAddress: String,
			isNewAccount: Boolean,
			callback: () -> Unit
		) {
			filter {
				// 初始的时候显示后台要求标记为 `force show` 的 `Token`
				it.forceShow == TinyNumber.True.value
			}.apply {
				/**
				 * 新创建的钱包, 没有网络的情况下的导入钱包, 都直接插入账目为 `0.0` 的数据
				 **/
				if (isNewAccount || !NetworkUtil.hasNetwork(GoldStoneAPI.context)) {
					insertNewAccount(ownerAddress, callback)
				} else {
					checkAddressBalanceThenInsert(ownerAddress, this, callback)
				}
			}
		}
		
		private fun List<DefaultTokenTable>.insertNewAccount(
			address: String,
			callback: () -> Unit
		) {
			object : ConcurrentAsyncCombine() {
				override var asyncCount: Int = size
				override fun concurrentJobs() {
					forEach {
						ChainID.getAllChainID().forEach { chainID ->
							MyTokenTable.insert(
								MyTokenTable(0, address, it.symbol, 0.0, it.contract, chainID)
							)
						}
						completeMark()
					}
				}
				
				override fun mergeCallBack() =
					callback()
			}.start()
		}
		
		private fun checkAddressBalanceThenInsert(
			address: String,
			data: List<DefaultTokenTable>,
			callback: () -> Unit
		) {
			// 不是新建账号就检查余额
			object : ConcurrentAsyncCombine() {
				override var asyncCount: Int = data.size
				override fun concurrentJobs() {
					data.forEach { tokenInfo ->
						// 获取选中的 `Symbol` 的 `Token` 对应 `WalletAddress` 的 `Balance`
						if (tokenInfo.symbol.equals(CryptoSymbol.eth, true)) {
							GoldStoneEthCall.getEthBalance(address) {
								insertMyTokenBalanceByChainID(address, tokenInfo, it)
								completeMark()
							}
						} else {
							GoldStoneEthCall
								.getTokenBalanceWithContract(tokenInfo.contract, address) {
									insertMyTokenBalanceByChainID(address, tokenInfo, it)
									completeMark()
								}
						}
					}
				}
				
				override fun mergeCallBack() {
					callback()
				}
			}.start()
		}
		
		private fun insertMyTokenBalanceByChainID(
			address: String,
			tokenInfo: DefaultTokenTable,
			balance: Double
		) {
			ChainID.getAllChainID().forEach { chainID ->
				val currentBalance = if (GoldStoneApp.currentChain == chainID) balance else 0.0
				MyTokenTable
					.insert(
						MyTokenTable(
							0,
							address,
							tokenInfo.symbol,
							currentBalance,
							tokenInfo.contract,
							chainID
						)
					)
			}
		}
	}
}