package io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter

import android.content.Context
import android.os.Bundle
import android.support.annotation.WorkerThread
import com.blinnnk.util.ConcurrentJobs
import com.blinnnk.util.ReasonText
import com.blinnnk.util.UnsafeReasons
import com.blinnnk.util.checkPasswordInRules
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.language.CreateWalletText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.UIUtils.generateDefaultName
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.WebUrl
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.multichain.ChainAddresses
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.DefaultPath
import io.goldstone.blockchain.crypto.multichain.GenerateMultiChainWallet
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.EOSDefaultAllChainName
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.view.CreateWalletFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.jetbrains.anko.runOnUiThread

/**
 * @date 22/03/2018 2:46 AM
 * @author KaySaith
 */
class CreateWalletPresenter(
	override val fragment: CreateWalletFragment
) : BasePresenter<CreateWalletFragment>() {

	fun showAgreementFragment() {
		val argument = Bundle().apply {
			putString(ArgumentKey.webViewUrl, WebUrl.terms)
			putString(ArgumentKey.webViewName, CreateWalletText.agreement)
		}
		showTargetFragment<WebViewFragment, WalletGenerationFragment>(argument)
	}

	@WorkerThread
	fun generateWalletWith(
		context: Context,
		password: String,
		name: String,
		hint: String? = null
	) {
		GenerateMultiChainWallet.create(context, password) { multiChainAddresses, mnemonic ->
			// 将基础的不存在安全问题的信息插入数据库
			WalletTable(
				0,
				SharedWallet.getMaxWalletID() + 1,
				name,
				multiChainAddresses.eth.address,
				multiChainAddresses.etc.address,
				multiChainAddresses.btc.address,
				multiChainAddresses.btcSeriesTest.address,
				multiChainAddresses.ltc.address,
				multiChainAddresses.bch.address,
				multiChainAddresses.eos.address,
				EOSDefaultAllChainName(multiChainAddresses.eos.address, multiChainAddresses.eos.address, multiChainAddresses.eos.address),
				ethAddresses = listOf(multiChainAddresses.eth),
				etcAddresses = listOf(multiChainAddresses.etc),
				btcAddresses = listOf(multiChainAddresses.btc),
				btcSeriesTestAddresses = listOf(multiChainAddresses.btcSeriesTest
				),
				ltcAddresses = listOf(multiChainAddresses.ltc),
				bchAddresses = listOf(multiChainAddresses.bch),
				eosAddresses = listOf(multiChainAddresses.eos),
				eosAccountNames = listOf(),
				ethPath = DefaultPath.ethPath,
				btcPath = DefaultPath.btcPath,
				etcPath = DefaultPath.etcPath,
				btcTestPath = DefaultPath.testPath,
				bchPath = DefaultPath.bchPath,
				ltcPath = DefaultPath.ltcPath,
				eosPath = DefaultPath.eosPath,
				hint = hint,
				isUsing = true,
				// 防止用户跳过助记词, 把使用 `RSA` 加密后的助记词存入数据库
				encryptMnemonic = JavaKeystoreUtil().encryptData(mnemonic)
			) insert { wallet ->
				// 本地没有数据从服务器获取数据
				insertNewAccount(multiChainAddresses) {
					// 传递数据到下一个 `Fragment`
					val arguments = Bundle().apply {
						putString(ArgumentKey.mnemonicCode, mnemonic)
					}
					launchUI {
						fragment.showMnemonicBackupFragment(arguments)
					}
				}
				XinGePushReceiver.registerAddressesForPush(wallet)
			}
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

		@WorkerThread
		fun checkInputValue(
			name: String,
			password: String,
			repeatPassword: String,
			isAgree: Boolean,
			callback: (password: String?, walletName: String?, error: AccountError) -> Unit
		) {
			if (password.isEmpty())
				callback(null, null, AccountError.EmptyRepeatPassword)
			else if (!isAgree)
				callback(null, null, AccountError.AgreeTerms)
			else if (password != repeatPassword)
				callback(null, null, AccountError.DifferentRepeatPassword)
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
				password.checkPasswordInRules(reason) { _, reasons ->
					if (reasons == UnsafeReasons.None) callback(password, walletName, AccountError.None)
					else callback(
						null,
						null,
						AccountError.PasswordFormatted(reasons.info)
					)
				}
			}
		}

		fun insertNewAccount(addresses: ChainAddresses, @WorkerThread callback: () -> Unit) {
			val localTokens = DefaultTokenTable.dao.getForceShow()
			object : ConcurrentJobs() {
				override var asyncCount: Int = localTokens.size
				override fun doChildJob(index: Int) {
					when (localTokens[index].chainID) {
						ChainID.ethMain,
						ChainID.ropsten,
						ChainID.kovan,
						ChainID.rinkeby -> {
							if (addresses.eth.isNotEmpty())
								MyTokenTable.dao.insert(MyTokenTable(localTokens[index], addresses.eth.address))
						}

						ChainID.etcMain, ChainID.etcTest -> {
							if (addresses.etc.isNotEmpty())
								MyTokenTable.dao.insert(MyTokenTable(localTokens[index], addresses.etc.address))
						}

						ChainID.eosMain,
						ChainID.eosJungle,
						ChainID.eosKylin -> {
							if (addresses.eos.isNotEmpty())
								if (EOSWalletUtils.isValidAddress(addresses.eos.address)) {
									MyTokenTable.dao.insert(MyTokenTable(localTokens[index], addresses.eos.address))
								} else if (EOSAccount(addresses.eos.address).isValid(false)) {
									// 这种情况通常是观察钱包的特殊情况, 有 `AccountName` 没有公钥的导入情况
									MyTokenTable.dao.insert(MyTokenTable(localTokens[index], addresses.eos.address, addresses.eos.address))
								}
						}

						ChainID.btcMain -> {
							if (addresses.btc.isNotEmpty())
								MyTokenTable.dao.insert(MyTokenTable(localTokens[index], addresses.btc.address))
						}

						ChainID.btcTest, ChainID.ltcTest, ChainID.bchTest -> {
							if (addresses.btcSeriesTest.isNotEmpty())
								MyTokenTable.dao.insert(MyTokenTable(localTokens[index], addresses.btcSeriesTest.address))
						}
						ChainID.ltcMain -> {
							if (addresses.ltc.isNotEmpty())
								MyTokenTable.dao.insert(MyTokenTable(localTokens[index], addresses.ltc.address))
						}

						ChainID.bchMain -> {
							if (addresses.bch.isNotEmpty())
								MyTokenTable.dao.insert(MyTokenTable(localTokens[index], addresses.bch.address))
						}
					}
					completeMark()
				}

				override fun mergeCallBack() = callback()
			}.start()
		}
	}
}