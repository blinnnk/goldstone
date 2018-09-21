package io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.presenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.jump
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.accountregister.AccountActor
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.view.EOSAccountSelectionFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity


/**
 * @author KaySaith
 * @date  2018/09/11
 */
class EOSAccountSelectionPresenter(
	override val fragment: EOSAccountSelectionFragment
) : BasePresenter<EOSAccountSelectionFragment>() {

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		showAvailableNames()
	}

	fun setEOSDefaultName(name: String) {
		/**
		 * 1. 更新 `WalletTable` 里面的 `CurrentEOSAccountName`
		 * 2. 更新 `MyTokenTable` 里面的 `OwnerName` 的名字
		 */
		WalletTable.updateEOSDefaultName(name) {
			fragment.activity?.jump<SplashActivity>()
		}
	}

	private fun showAvailableNames() {
		fragment.showLoadingView(true)
		WalletTable.getCurrentWallet {
			val currentChainNames =
				eosAccountNames.filter {
					it.chainID.equals(Config.getEOSCurrentChain().id, true)
				}
			val actors = arrayListOf<AccountActor>()
			object : ConcurrentAsyncCombine() {
				override var asyncCount: Int = currentChainNames.size
				override fun concurrentJobs() {
					currentChainNames.forEach { account ->
						EOSAccountTable.getAccountByName(account.name, false) { localAccount ->
							// 本地为空的话从网络获取数据
							if (localAccount.isNull()) EOSAPI.getAccountInfo(
								account.name,
								{
									completeMark()
									LogUtil.error("getAccountInfo", it)
								}
							) { eosAccount ->
								actors.addAll(getAccountActorByPublicKey(eosAccount, account.name))
								// 插入数据库
								EOSAccountTable.preventDuplicateInsert(eosAccount)
								completeMark()
							} else {
								actors.addAll(getAccountActorByPublicKey(localAccount!!, account.name))
								completeMark()
							}
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

	private fun getAccountActorByPublicKey(
		account: EOSAccountTable,
		name: String
	): List<AccountActor> {
		return account.permissions.asSequence().map { permission ->
			permission.requiredAuthorization.getKeys().asSequence().filter {
				it.publicKey == Config.getCurrentEOSAddress()
			}.map {
				AccountActor(name, EOSActor.getActorByValue(permission.permissionName)!!, it.weight)
			}.toList()
		}.toList().flatten()
	}
}