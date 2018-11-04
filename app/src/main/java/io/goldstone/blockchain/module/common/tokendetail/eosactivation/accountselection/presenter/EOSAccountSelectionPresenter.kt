package io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.jump
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
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
		@UiThread hold: (newAccount: List<EOSAccountInfo>) -> Unit
	) {
		WalletTable.getCurrentWallet {
			// 获取本地数据库里面的此公钥地址对应的用户名字
			val currentChainNames =
				eosAccountNames.filter {
					it.chainID.equals(SharedChain.getEOSCurrent().chainID.id, true)
				}
			EOSAPI.getAccountNameByPublicKey(SharedAddress.getCurrentEOS()) { accounts, error ->
				if (!accounts.isNull() && error.isNone()) {
					hold(accounts!!)
				} else hold(currentChainNames)
			}
		}
	}

	fun showAvailableNames() {
		fragment.showLoadingView(true)
		// 从链上重新拉取一次该公钥的对应的 AccountNames,
		getNewAccountNameFromChain { allAccountNames ->
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
						val willRemoveData = actors - actors.distinctBy { it.name }
						val finalData = actors.filterNot { data ->
							willRemoveData.any { it.name.equals(data.name, true) } && data.permission.isActive()
						}
						fragment.setAccountNameList(finalData)
						fragment.showLoadingView(false)
					}
				} else object : ConcurrentAsyncCombine() {
					override var asyncCount: Int = notInLocalAccount.size
					override fun doChildTask(index: Int) {
						// 本地为空的话从网络获取数据
						EOSAPI.getAccountInfo(EOSAccount(notInLocalAccount[index].name)) { eosAccount, error ->
							if (eosAccount != null && error.isNone()) {
								actors.addAll(getAccountActorByPublicKey(eosAccount, notInLocalAccount[index].name))
								// 插入数据库
								EOSAccountTable.preventDuplicateInsert(eosAccount)
							}
							completeMark()
						}
					}

					override fun mergeCallBack() {
						// `Owner` 和 `Active` 同时存在的时候界面做去重显示
						val willRemoveData = actors - actors.distinctBy { it.name }
						val finalData = actors.filterNot { data ->
							willRemoveData.any { it.name.equals(data.name, true) } && data.permission.isActive()
						}
						fragment.setAccountNameList(finalData)
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