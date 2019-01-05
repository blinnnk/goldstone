package io.goldstone.blinnnk.module.common.tokendetail.eosactivation.accountselection.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.jump
import com.blinnnk.util.ConcurrentAsyncCombine
import io.goldstone.blinnnk.common.base.basefragment.BasePresenter
import io.goldstone.blinnnk.common.sharedpreference.SharedAddress
import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.crypto.eos.account.EOSAccount
import io.goldstone.blinnnk.crypto.eos.accountregister.AccountActor
import io.goldstone.blinnnk.crypto.eos.accountregister.EOSActor
import io.goldstone.blinnnk.kernel.network.eos.EOSAPI
import io.goldstone.blinnnk.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blinnnk.module.common.tokendetail.eosactivation.accountselection.view.EOSAccountSelectionFragment
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.EOSAccountInfo
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blinnnk.module.entrance.splash.view.SplashActivity


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

	private fun getNewAccountNameFromChain(@WorkerThread hold: (newAccount: List<EOSAccountInfo>) -> Unit) {
		EOSAPI.getAccountNameByPublicKey(SharedAddress.getCurrentEOS()) { accounts, error ->
			val wallet = WalletTable.dao.findWhichIsUsing()
			if (accounts.isNotNull() && error.isNone()) hold(accounts)
			else wallet?.eosAccountNames?.filter {
				it.chainID.equals(SharedChain.getEOSCurrent().chainID.id, true) &&
					it.publicKey.equals(SharedAddress.getCurrentEOS(), true)
			}?.let(hold)
		}
	}

	fun showAvailableNames() {
		fragment.showLoadingView(true)
		// 从链上重新拉取一次该公钥的对应的 AccountNames,
		getNewAccountNameFromChain { allAccounts ->
			val localAccounts =
				EOSAccountTable.dao.getAccounts(allAccounts.map { it.name }, SharedAddress.getCurrentEOS())
			val actors = arrayListOf<AccountActor>().apply {
				addAll(
					localAccounts.map { localAccount ->
						getAccountActorByPublicKey(localAccount, localAccount.name)
					}.flatten()
				)
			}
			// 删选出本地不存在的链上 `AccountName`
			val notInLocalAccount =
				allAccounts.filterNot { chainAccount ->
					localAccounts.any {
						it.name.equals(chainAccount.name, true) &&
							it.chainID.equals(chainAccount.chainID, true)
					}
				}
			if (notInLocalAccount.isEmpty()) launchUI {
				fragment.setAccountNameList(getPermissions(actors))
				fragment.showLoadingView(false)
			} else object : ConcurrentAsyncCombine() {
				override var asyncCount: Int = notInLocalAccount.size
				override fun doChildTask(index: Int) {
					// 本地为空的话从网络获取数据
					EOSAPI.getAccountInfo(EOSAccount(notInLocalAccount[index].name)) { eosAccount, error ->
						if (eosAccount.isNotNull() && error.isNone()) {
							actors.addAll(getAccountActorByPublicKey(eosAccount, notInLocalAccount[index].name))
							// 插入数据库
							EOSAccountTable.dao.insert(eosAccount)
						}
						completeMark()
					}
				}

				override fun mergeCallBack() {

					fragment.setAccountNameList(getPermissions(actors))
					fragment.showLoadingView(false)
				}
			}.start()
		}
	}

	// `Owner` 和 `Active` 同时存在的时候界面做去重显示
	// Boolean is Single Permission
	private fun getPermissions(actors: List<AccountActor>): List<Pair<AccountActor, Boolean>> {
		val finalData =
			arrayListOf<Pair<AccountActor, Boolean>>()
		val actorMap = actors.groupBy {
			if (it.permission.isOwner()) EOSActor.Owner.value
			else EOSActor.Active.value
		}
		val owners = actorMap[EOSActor.Owner.value]
		val actives = actorMap[EOSActor.Active.value]
		when {
			owners.isNullOrEmpty() -> finalData.addAll(actives?.map { Pair(it, true) } ?: listOf())
			actives.isNullOrEmpty() -> finalData.addAll(owners.map { Pair(it, true) })
			else -> actors.forEach { actor ->
				if (
					owners.any { it.name.equals(actor.name, true) } &&
					actives.any { it.name.equals(actor.name, true) }
				) {
					finalData.add(Pair(actor, false))
				} else {
					finalData.add(Pair(actor, true))
				}
			}
		}
		return finalData.distinctBy { it.first.name }
	}

	private fun getAccountActorByPublicKey(
		account: EOSAccountTable,
		name: String
	): List<AccountActor> {
		return account.permissions.asSequence().map { permission ->
			permission.requiredAuthorization.getKeys().asSequence().filter {
				it.publicKey == SharedAddress.getCurrentEOS()
			}.map {
				AccountActor(name, EOSActor.getActorByValue(permission.permissionName), it.weight)
			}.toList()
		}.toList().flatten()
	}
}