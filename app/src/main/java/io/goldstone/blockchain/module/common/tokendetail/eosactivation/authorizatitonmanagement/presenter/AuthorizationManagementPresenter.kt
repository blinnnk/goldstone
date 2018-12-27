package io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.presenter

import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.thread.launchDefault
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
	 */
	override fun updateAuthorization(
		publicKey: String,
		permission: EOSActor,
		privateKey: EOSPrivateKey,
		isDelete: Boolean,
		hold: (response: EOSResponse?, error: GoldStoneError) -> Unit
	) {
		launchDefault {
			EOSAccountTable.getValidPermission(account, chainID)?.let { currentPermission ->
				val existedPermissions =
					EOSAccountTable.getPermissions(account, chainID)
				val existedPublicKeys = existedPermissions.find {
					it.permissionName.equals(permission.value, true)
				}?.requiredAuthorization?.publicKeys
				val totalKeys = when {
					existedPublicKeys.isNullOrEmpty() -> listOf(ActorKey(publicKey, 1))
					!isDelete -> existedPublicKeys.map { ActorKey(JSONObject(it)) }.plus(ActorKey(publicKey, 1))
					else -> existedPublicKeys.map { ActorKey(JSONObject(it)) }.minus(ActorKey(publicKey, 1))
				}
				EOSAuthorizationEditor(
					chainID,
					totalKeys,
					if (permission.isOwner()) EOSActor.Empty else EOSActor.Owner,
					permission,
					1,
					EOSAuthorization(account.name, currentPermission)
				)
			}?.send(
				privateKey,
				chaiURL = SharedChain.getEOSCurrent().getURL(),
				hold = hold
			)
		}
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

	override fun refreshAuthList() {
		EOSAPI.getAccountInfo(account) { accountInfo, error ->
			GlobalScope.launch(Dispatchers.Default) {
				// 错开 EOS 出 `2个块` 的时间, 防止更新太快拉取不到最新的数据
				delay(1000)
				if (accountInfo.isNotNull() && error.isNone()) {
					EOSAccountTable.dao.insert(accountInfo)
					setAuthList()
				}
			}
		}
	}
}