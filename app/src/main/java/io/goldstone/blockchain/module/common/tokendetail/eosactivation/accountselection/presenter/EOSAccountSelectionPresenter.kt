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
					it.chainID.equals(Config.getEOSCurrentChain(), true)
				}
			var actors = listOf<AccountActor>()
			object : ConcurrentAsyncCombine() {
				override var asyncCount: Int = currentChainNames.size
				override fun concurrentJobs() {
					currentChainNames.forEach { account ->
						EOSAccountTable.getAccountByName(account.name, false) { localAccount ->
							// 本地为空的话从网络获取数据
							if (localAccount.isNull()) {
								EOSAPI.getAccountInfoByName(
									account.name,
									{
										completeMark()
										LogUtil.error("getAccountInfoByName", it)
									}
								) { eosAccount ->
									// 插入数据库
									EOSAccountTable.preventDuplicateInsert(eosAccount)
									actors += getAccountActorByPublicKey(eosAccount, account.name)
									completeMark()
								}
							} else {
								actors += getAccountActorByPublicKey(localAccount!!, account.name)
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
		var actors = listOf<AccountActor>()
		account.permissions.forEach { permission ->
			permission.requiredAuthorization.getKeys().forEach {
				if (it.publicKey == Config.getCurrentEOSAddress()) {
					actors += AccountActor(name, EOSActor.getActorByValue(permission.permissionName)!!, it.weight)
				}
			}
		}
		return actors
	}
}