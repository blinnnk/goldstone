package io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.jump
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.accountregister.AccountActor
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.view.EOSAccountSelectionFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.EOSAccountInfo
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import org.jetbrains.anko.runOnUiThread


/**
 * @author KaySaith
 * @date  2018/09/11
 */
class EOSAccountSelectionPresenter(
	override val fragment: EOSAccountSelectionFragment
) : BasePresenter<EOSAccountSelectionFragment>() {

	/**
	 * 1. 更新 `WalletTable` 里面的 `CurrentEOSAccountName`
	 * 2. 更新 `MyTokenTable` 里面的 `OwnerName` 的名字
	 */
	fun setEOSDefaultName(name: String) {
		WalletTable.updateEOSDefaultName(name) {
			fragment.activity?.jump<SplashActivity>()
		}
	}

	private fun getNewAccountNameFromChain(
		errorCallback: (RequestError) -> Unit,
		@UiThread hold: (newAccount: List<EOSAccountInfo>) -> Unit
	) {
		WalletTable.getCurrentWallet {
			// 获取本地数据库里面的此公钥地址对应的用户名字
			val currentChainNames =
				eosAccountNames.filter {
					it.chainID.equals(SharedChain.getEOSCurrent().id, true)
				}
			EOSAPI.getAccountNameByPublicKey(
				SharedAddress.getCurrentEOS(),
				{
					errorCallback(it)
					// 如果请求出错的话, 仍要保持不阻碍用户的模式, 那么暂时通过只显示
					// 本地数据库里面存储的 `AccountNames` 作为结果
					hold(currentChainNames)
				},
				hold = hold
			)
		}
	}

	fun showAvailableNames(errorCallback: (RequestError) -> Unit) {
		fragment.showLoadingView(true)
		// 从链上重新拉取一次该公钥的对应的 AccountNames,
		getNewAccountNameFromChain(errorCallback) { allAccountNames ->
			EOSAccountTable.getAccountsByNames(
				allAccountNames.map { it.name },
				false
			) { localAccounts ->
				val actors = arrayListOf<AccountActor>()
				// 本地现存的数据整理到 `Actors``
				actors.addAll(
					localAccounts.map { localAccount ->
						getAccountActorByPublicKey(localAccount, localAccount.name)
					}.flatten()
				)
				// 删选出本地不存在的链上 `AccountName`
				val notInLocalAccount =
					allAccountNames.filterNot { chainAccount ->
						localAccounts.any {
							it.name.equals(chainAccount.name, true) &&
								it.chainID.equals(chainAccount.chainID, true)
						}
					}
				if (notInLocalAccount.isEmpty()) {
					fragment.context?.runOnUiThread {
						fragment.setAccountNameList(actors)
						fragment.showLoadingView(false)
					}
				} else object : ConcurrentAsyncCombine() {
					override var asyncCount: Int = notInLocalAccount.size
					override fun concurrentJobs() {
						notInLocalAccount.forEach { account ->
							// 本地为空的话从网络获取数据
							EOSAPI.getAccountInfo(
								EOSAccount(account.name)
							) { eosAccount, error ->
								if (!eosAccount.isNull() && error.isNone()) {
									actors.addAll(getAccountActorByPublicKey(eosAccount!!, account.name))
									// 插入数据库
									EOSAccountTable.preventDuplicateInsert(eosAccount)
								}
								completeMark()
							}
						}
					}

					override fun mergeCallBack() {
						fragment.setAccountNameList(actors)
						fragment.showLoadingView(false)
					}
				}.start()
			}
		}
	}

	private fun getAccountActorByPublicKey(
		account: EOSAccountTable,
		name: String
	): List<AccountActor> {
		return account.permissions.asSequence().map { permission ->
			permission.requiredAuthorization.getKeys().asSequence().filter {
				it.publicKey == SharedAddress.getCurrentEOS()
			}.map {
				AccountActor(name, EOSActor.getActorByValue(permission.permissionName)!!, it.weight)
			}.toList()
		}.toList().flatten()
	}
}