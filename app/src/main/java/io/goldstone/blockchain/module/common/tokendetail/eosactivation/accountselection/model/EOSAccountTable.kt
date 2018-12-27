package io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model

import android.arch.persistence.room.*
import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.math.BigInteger

/**
 * @author KaySaith
 * @date  2018/09/12
 */
@Entity(tableName = "eosAccount", primaryKeys = ["name", "recordPublicKey", "chainID"])
data class EOSAccountTable(
	val name: String,
	val balance: String,
	val privileged: Boolean,
	val createdTime: String, // Formatted "2018-08-31T03:16:27.000"
	@Embedded(prefix = "netLimit")
	val netLimit: ResourceLimit,
	@Embedded(prefix = "cpuLimit")
	val cpuLimit: ResourceLimit,
	val ramUsed: BigInteger,
	val ramQuota: BigInteger,
	val cpuWeight: BigInteger,
	var netWeight: BigInteger,
	@Embedded(prefix = "totalResource")
	val totalResource: TotalResources,
	val delegateInfo: List<DelegateBandWidthInfo>,
	@Embedded(prefix = "voterInfo")
	val voterInfo: VoterInfo?,
	val refundInfo: RefundRequestInfo,
	@SerializedName("permissions")
	val permissions: List<PermissionsInfo>,
	val recordPublicKey: String,
	val chainID: String,
	var totalDelegateBandInfo: List<DelegateBandWidthInfo>
) : Serializable {
	constructor(
		data: JSONObject,
		recordPublicKey: String,
		chainID: ChainID
	) : this(
		data.safeGet("account_name"),
		data.safeGet("core_liquid_balance"),
		data.safeGet("privileged").toBoolean(),
		data.safeGet("created"),
		ResourceLimit(JSONObject(data.safeGet("net_limit"))),
		ResourceLimit(JSONObject(data.safeGet("cpu_limit"))),
		data.safeGet("ram_usage").toBigIntegerOrZero(),
		data.safeGet("ram_quota").toBigIntegerOrZero(),
		data.safeGet("cpu_weight").toBigIntegerOrZero(),
		data.safeGet("net_weight").toBigIntegerOrZero(),
		TotalResources(JSONObject(data.safeGet("total_resources"))),
		listOf(checkDelegateBandWidthDataOrGetObject(data) ?: DelegateBandWidthInfo()),
		checkVoterDataOrGetObject(data),
		checkRefundRequestOrGetObject(data),
		PermissionsInfo.getPermissions(JSONArray(data.safeGet("permissions"))),
		recordPublicKey,
		chainID.id,
		listOf()
	)

	companion object {

		@JvmField
		val dao = GoldStoneDataBase.database.eosAccountDao()

		@WorkerThread
		fun updateOrInsert(account: EOSAccountTable, chainID: ChainID) {
			GoldStoneDataBase.database.eosAccountDao().apply {
				val localAccount = getAccount(account.name, chainID.id)
				if (localAccount.isNull()) insert(account)
				else {
					insert(account.apply { totalDelegateBandInfo = localAccount.totalDelegateBandInfo })
				}
			}
		}

		@WorkerThread
		fun getValidPermission(account: EOSAccount, chainID: ChainID): EOSActor? {
			val permission = EOSAccountTable.getPermissions(account, chainID)
			val targetPermission = permission.find {
				it.requiredAuthorization.publicKeys.find { publicKey ->
					JSONObject(publicKey).safeGet("key").equals(SharedAddress.getCurrentEOS(), true)
				}.isNotNull()
			}?.permissionName
			return EOSActor.getActorByValue(targetPermission.orEmpty())
		}

		@WorkerThread
		fun getPermissions(account: EOSAccount, chainID: ChainID): List<PermissionsInfo> {
			val permissions = dao.getPermissions(account.name, chainID.id)
			return if (!permissions.isNullOrBlank()) {
				val permissionList =
					JSONArray(dao.getPermissions(account.name, chainID.id)).toJSONObjectList()
				permissionList.map { PermissionsInfo(it) }
			} else listOf()
		}

		// 特殊情况下这几个字段的返回值会是 `null`
		private fun checkRefundRequestOrGetObject(content: JSONObject): RefundRequestInfo {
			val data = content.safeGet("refund_request")
			val hasData = !data.isNullValue()
			return if (hasData) RefundRequestInfo(JSONObject(data)) else RefundRequestInfo()
		}

		private fun checkDelegateBandWidthDataOrGetObject(content: JSONObject): DelegateBandWidthInfo? {
			val data = content.safeGet("self_delegated_bandwidth")
			val hasData = !data.isNullValue()
			return if (hasData) DelegateBandWidthInfo(JSONObject(data)) else null
		}

		private fun checkVoterDataOrGetObject(content: JSONObject): VoterInfo? {
			val data = content.safeGet("voter_info")
			val hasData = !data.isNullValue()
			return if (hasData) VoterInfo(JSONObject(data)) else null
		}

	}
}

@Dao
interface EOSAccountDao {
	@Query("SELECT * FROM eosAccount")
	fun getAll(): List<EOSAccountTable>

	@Query("SELECT * FROM eosAccount WHERE name = :name AND chainID = :chainID")
	fun getAccount(name: String, chainID: String): EOSAccountTable?

	@Query("SELECT permissions FROM eosAccount WHERE name = :name AND chainID = :chainID")
	fun getPermissions(name: String, chainID: String): String?

	@Query("UPDATE eosAccount SET totalDelegateBandInfo = :data WHERE name = :name AND chainID = :chainID")
	fun updateDelegateBandwidthData(data: List<DelegateBandWidthInfo>, name: String, chainID: String)

	@Query("UPDATE eosAccount SET refundInfo = :data WHERE name = :name AND chainID = :chainID")
	fun updateRefundData(data: RefundRequestInfo, name: String, chainID: String)

	@Query("SELECT * FROM eosAccount WHERE name IN (:names) AND chainID = :chainID")
	fun getAccounts(names: List<String>, chainID: String = SharedChain.getEOSCurrent().chainID.id): List<EOSAccountTable>

	@Query("SELECT * FROM eosAccount WHERE recordPublicKey LIKE :publicKey")
	fun getByKey(publicKey: String): List<EOSAccountTable>

	@Query("DELETE FROM eosAccount WHERE recordPublicKey LIKE :publicKey")
	fun deleteByKey(publicKey: String)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(table: EOSAccountTable)

	@Update
	fun update(table: EOSAccountTable)

	@Delete
	fun delete(table: EOSAccountTable)

	@Delete
	fun deleteAll(table: List<EOSAccountTable>)

}