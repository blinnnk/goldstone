package io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.jump
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.accountregister.AccountActor
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.view.EOSAccountSelectionFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.EOSAccountInfo
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity


/**
 * @author KaySaith
 * @date  2018/09/11
 */
class EOSAccountSelectionPresenter(
	override val fragment: EOSAccountSelectionFragment
) : BasePresenter<EOSAccountSelectionFragment>() {

	fun setEOSDefaultName(name: String) {
		/**
		 * 1. 更新 `WalletTable` 里面的 `CurrentEOSAccountName`
		 * 2. 更新 `MyTokenTable` 里面的 `OwnerName` 的名字
		 */
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
					it.chainID.equals(Config.getEOSCurrentChain().id, true)
				}
			EOSAPI.getAccountNameByPublicKey(
				Config.getCurrentEOSAddress(),
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
			val actors = arrayListOf<AccountActor>()
			object : ConcurrentAsyncCombine() {
				override var asyncCount: Int = allAccountNames.size
				override fun concurrentJobs() {
					allAccountNames.forEach { account ->
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