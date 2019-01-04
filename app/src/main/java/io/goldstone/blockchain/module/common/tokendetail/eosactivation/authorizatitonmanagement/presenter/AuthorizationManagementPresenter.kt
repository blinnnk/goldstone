package io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.presenter

import android.os.Handler
import android.os.Looper
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orFalse
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.load
import com.blinnnk.util.observing
import com.blinnnk.util.then
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.language.EOSAccountText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.thread.launchDefault
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.eos.accountregister.ActorKey
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.authorizationeditor.EOSAuthorizationEditor
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.contract.AuthorizationManagementContract
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.model.AuthorizationManagementModel
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.model.AuthorizationObserverModel
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.model.AuthorizationType
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import kotlinx.coroutines.Runnable
import org.json.JSONObject


/**
 * @author KaySaith
 * @date  2018/12/26
 */
class AuthorizationManagementPresenter(
	private val view: AuthorizationManagementContract.GSView
) : AuthorizationManagementContract.GSPresenter {

	private val account = SharedAddress.getCurrentEOSAccount()
	private val chainID = SharedChain.getEOSCurrent().chainID

	override fun start() {
		setAuthList()
	}

	/**
	 * 更新权限的机制, 需要每次更新都带入全部的值, 所以需要新增 `PublicKey` 的时候
	 * 首先需要查询到已经支持的全部的 `Key` 再合并即将新增的 Key 最后统统 `Update`
	 * @Important
	 * updateAuth 要对需要更新的 PublicKey 进行全维度排序 数字, 大写字母, 小写字母
	 * 排序后再更新不然会报错.
	 * @See
	 * https://github.com/EOSIO/eos/issues/4040
	 * @Description
	 *  [newPublicKey] 和 [oldPublicKey] 是 Edit 的时候用来替换比对用的. 如果 [actionType] 不是
	 *  `AuthorizationType.Edit` 那么 `OldPublicKey` 为 `空`
	 */
	override fun updateAuthorization(
		newPublicKey: String,
		oldPublicKey: String,
		permission: EOSActor,
		privateKey: EOSPrivateKey,
		actionType: AuthorizationType,
		hold: (response: EOSResponse?, error: GoldStoneError) -> Unit
	) {
		launchDefault {
			val basicPermission =
				EOSAccountTable.getValidPermission(account, chainID, permission.isOwner())
			if (basicPermission.isNull())
				hold(null, GoldStoneError(EOSAccountText.permissionDenied))
			else {
				val existedPermissions =
					EOSAccountTable.getPermissions(account, chainID)
				val existedPublicKeys = existedPermissions.find {
					it.permissionName.equals(permission.value, true)
				}?.requiredAuthorization?.publicKeys
				val totalKeys = when {
					existedPublicKeys.isNullOrEmpty() -> listOf(ActorKey(newPublicKey, 1))
					actionType == AuthorizationType.Add ->
						existedPublicKeys.map { ActorKey(JSONObject(it)) }.plus(ActorKey(newPublicKey, 1))
					actionType == AuthorizationType.Edit -> {
						// 第一种是存在的账户修改新的账户, 第二种是挪移权限把 `Active` 存在的账户 挪到 `Owner` 不存在的下面
						if (existedPublicKeys.any { it.contains(newPublicKey) }) existedPublicKeys.map {
							ActorKey(JSONObject(it.replace(oldPublicKey, newPublicKey)))
						} else existedPublicKeys.map {
							ActorKey(JSONObject(it))
						}.plus(ActorKey(newPublicKey, 1))
					}
					else -> existedPublicKeys.map { ActorKey(JSONObject(it)) }.minus(ActorKey(newPublicKey, 1))
				}
				EOSAuthorizationEditor(
					chainID,
					totalKeys.sortedBy { it.publicKey },
					if (permission.isOwner()) EOSActor.Empty else EOSActor.Owner,
					permission,
					1,
					EOSAuthorization(account.name, basicPermission)
				).send(
					privateKey,
					chaiURL = SharedChain.getEOSCurrent().getURL(),
					hold = hold
				)
			}
		}
	}

	override fun showAlertBeforeDeleteLastKey(willDeletePublicKey: String, callback: (Boolean) -> Unit) {
		launchDefault {
			val existedPermissions =
				EOSAccountTable.getPermissions(account, chainID)
			val totalMyPublicKeys = existedPermissions.map {
				it.requiredAuthorization.publicKeys
			}.flatten().map {
				ActorKey(JSONObject(it))
			}.filter {
				it.publicKey.equals(SharedAddress.getCurrentEOS(), true)
			}
			launchUI {
				callback(totalMyPublicKeys.size == 1 && totalMyPublicKeys.first().publicKey == willDeletePublicKey)
			}
		}
	}

	override fun deleteAccount(callback: () -> Unit) = launchDefault {
		EOSAccountTable.dao.deleteByNameAndChainID(account.name, chainID.id)
		WalletTable.dao.getWalletByAddress(SharedAddress.getCurrentEOS())?.apply {
			eosAccountNames.filterNot {
				it.name.equals(account.name, true) && it.chainID == chainID.id
			}.let { accountInfo ->
				val currentChainAccount = accountInfo.find { it.chainID == chainID.id }
				val insteadAccount = if (currentChainAccount.isNotNull()) currentChainAccount.name else SharedAddress.getCurrentEOS()
				WalletTable.dao.updateCurrentEOSAccountNames(accountInfo)
				WalletTable.updateEOSDefaultName(insteadAccount, callback)
			}
		}
	}

	override fun removeObserver() {
		handler.removeCallbacks(checkPermission)
	}

	override fun checkIsExistedOrElse(
		targetPublickey: String,
		targetActor: EOSActor,
		callback: (Boolean) -> Unit
	) = launchDefault {
		EOSAccountTable.getPermissions(account, chainID).find {
			EOSActor.getActorByValue(it.permissionName) == targetActor
		}?.requiredAuthorization?.publicKeys?.any { jsonObject ->
			ActorKey(JSONObject(jsonObject)).publicKey == targetPublickey
		}.orFalse().let(callback)
	}

	private fun setAuthList() {
		load {
			EOSAccountTable.dao.getAccount(account.name, chainID.id)
		} then { accountTable ->
			accountTable?.permissions?.map { permission ->
				val actorKey =
					permission.requiredAuthorization.publicKeys.map { ActorKey(JSONObject(it)) }
				val eosActor = EOSActor.getActorByValue(permission.permissionName)
				actorKey.map { AuthorizationManagementModel(it, eosActor) }
			}?.flatten()?.toArrayList()?.apply {
				view.updateData(this)
			}
		}
	}

	/**
	 * 权限操作后, 本地需要即时更新列表. 但是 有可能本次操作还没有被 块 收录
	 * 所以这里增加轮循操作. 一旦符合目标就更新界面.
	 */

	private var observerModel: AuthorizationObserverModel? by observing(null) {
		handler.postDelayed(checkPermission, 2000)
	}

	override fun refreshAuthList(observerModel: AuthorizationObserverModel) {
		if (observerModel.publickey.isNotEmpty()) this.observerModel = observerModel
	}

	private val handler = Handler(Looper.getMainLooper())
	private val checkPermission: Runnable = Runnable {
		observerModel?.let {
			checkPermissionIsExistedInChain(it)
		}
	}

	private fun checkPermissionIsExistedInChain(observerModel: AuthorizationObserverModel) {
		EOSAPI.getAccountInfo(account) { accountInfo, error ->
			if (accountInfo.isNotNull() && error.isNone()) {
				accountInfo.permissions.map {
					val hasTargetKeyInChain = it.requiredAuthorization.publicKeys.map { jsonKey ->
						ActorKey(JSONObject(jsonKey))
					}.any { actorKey ->
						actorKey.publicKey.equals(observerModel.publickey, true)
					}
					if (hasTargetKeyInChain == !observerModel.isDelete) {
						EOSAccountTable.dao.insert(accountInfo)
						launchUI {
							removeObserver()
							setAuthList()
						}
					} else launchUI {
						removeObserver()
						handler.postDelayed(checkPermission, 2000)
					}
				}
			} else launchUI {
				removeObserver()
				view.showError(error)
			}
		}
	}
}