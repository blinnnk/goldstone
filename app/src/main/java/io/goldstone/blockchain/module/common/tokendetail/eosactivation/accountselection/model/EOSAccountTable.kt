package io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model

import android.arch.persistence.room.*
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isNullValue
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toBigIntegerOrZero
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/12
 */
@Entity(tableName = "eosAccount")
data class EOSAccountTable(
	@PrimaryKey(autoGenerate = true)
	var id: Int,
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
	@Embedded(prefix = "delegateInfo")
	val delegateInfo: DelegateBandWidthInfo?,
	@Embedded(prefix = "voterInfo")
	val voterInfo: VoterInfo?,
	@Embedded(prefix = "refundInfo")
	val refundInfo: RefundRequestInfo?,
	@SerializedName("permissions")
	val permissions: List<PermissionsInfo>,
	val recordPublicKey: String
) : Serializable {
	constructor(data: JSONObject, recordPublicKey: String) : this(
		0,
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
		checkDelegateBandWidthDataOrGetObject(data),
		checkVoterDataOrGetObject(data),
		checkRefundRequestOrGetObject(data),
		PermissionsInfo.getPermissions(JSONArray(data.safeGet("permissions"))),
		recordPublicKey
	)

	companion object {

		fun isActivationPublicKey(publicKey: String, hold: (Boolean) -> Unit) {
			load {
				GoldStoneDataBase.database.eosAccountDao().getByKey(publicKey)
			} then { hold(!it.isNull()) }
		}

		fun getAccountByName(
			name: String,
			getResultInUIThread: Boolean = true,
			hold: (account: EOSAccountTable?) -> Unit
		) {
			doAsync {
				val account = GoldStoneDataBase.database.eosAccountDao().getAccount(name)
				if (getResultInUIThread) GoldStoneAPI.context.runOnUiThread { hold(account) }
				else hold(account)
			}
		}

		fun preventDuplicateInsert(account: EOSAccountTable) {
			doAsync {
				GoldStoneDataBase.database.eosAccountDao().apply {
					if (getAccount(account.name).isNull()) {
						insert(account)
					}
				}
			}
		}

		private fun checkRefundRequestOrGetObject(content: JSONObject): RefundRequestInfo? {
			val data = content.safeGet("refund_request")
			val hasData = !data.isNullValue()
			return if (hasData) RefundRequestInfo(JSONObject(data)) else null
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

	@Query("SELECT * FROM eosAccount WHERE name LIKE :name")
	fun getAccount(name: String): EOSAccountTable?

	@Query("SELECT * FROM eosAccount WHERE recordPublicKey LIKE :publicKey")
	fun getByKey(publicKey: String): EOSAccountTable?

	@Insert
	fun insert(table: EOSAccountTable)

	@Update
	fun update(table: EOSAccountTable)

	@Delete
	fun delete(table: EOSAccountTable)
}