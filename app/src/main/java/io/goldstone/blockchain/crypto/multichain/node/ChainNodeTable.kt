package io.goldstone.blockchain.crypto.multichain.node

import android.arch.persistence.room.*
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toList
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/10/28
 */
@Entity(tableName = "chainNode")
class ChainNodeTable(
	@PrimaryKey
	@SerializedName("id")
	val id: String,
	@SerializedName("node_name")
	val name: String,
	@SerializedName("url")
	val url: String,
	@SerializedName("encrypt_status")
	val isEncrypt: Int,
	@SerializedName("chain_type")
	val chainType: Int,
	@SerializedName("chain_id")
	val chainID: String,
	@SerializedName("weight")
	val weight: Int,
	@SerializedName("net_type")
	val netType: Int, // 0 Mainnet 1 Testnet
	@SerializedName("key_list")
	val keyList: List<String>,
	@SerializedName("is_default")
	var isUsed: Int
) : Serializable {
	constructor(data: JSONObject) : this(
		data.safeGet("id"),
		data.safeGet("node_name"),
		data.safeGet("url"),
		data.safeGet("encrypt_status").toInt(),
		data.safeGet("chain_type").toInt(),
		data.safeGet("chain_id"),
		data.safeGet("weight").toInt(),
		data.safeGet("net_type").toInt(),
		JSONArray(data.safeGet("key_list")).toList(),
		data.safeGet("is_default").toInt()
	)

	companion object {
		@JvmField
		val dao = GoldStoneDataBase.database.chainNodeDao()
	}
}

@Dao
interface ChainNodeDao {

	@Query("SELECT * FROM chainNode")
	fun getAll(): List<ChainNodeTable>

	@Query("SELECT count(*) FROM chainNode")
	fun rowCount(): Int

	@Query("SELECT * FROM chainNode WHERE netType = 0 ORDER BY chainType")
	fun getMainnet(): List<ChainNodeTable>

	@Query("SELECT * FROM chainNode WHERE netType = 1 ORDER BY chainType")
	fun getTestnet(): List<ChainNodeTable>

	@Query("SELECT * FROM chainNode WHERE chainType = 194")
	fun getEOSNodes(): List<ChainNodeTable>

	@Query("SELECT * FROM chainNode WHERE chainType = 194 AND isUsed = 1")
	fun getCurrentEOSNode(): ChainNodeTable

	@Query("SELECT * FROM chainNode WHERE chainType = 194 AND netType = 0")
	fun getMainnetEOSNode(): ChainNodeTable

	@Query("SELECT * FROM chainNode WHERE chainType = 194 AND netType = 1 AND chainID = :chainID")
	fun getJungleEOSNode(chainID: String = ChainID.EOSJungle.id): ChainNodeTable

	@Query("SELECT * FROM chainNode WHERE chainType = 194 AND netType = 1 AND chainID = :chainID")
	fun getKylinEOSNode(chainID: String = ChainID.EOSKylin.id): ChainNodeTable

	@Query("SELECT * FROM chainNode WHERE chainType = 61")
	fun getETCNodes(): List<ChainNodeTable>

	@Query("SELECT * FROM chainNode WHERE chainType = 60")
	fun getETHNodes(): List<ChainNodeTable>

	@Query("SELECT * FROM chainNode WHERE netType = 0 AND isUsed = 1 ORDER BY chainType")
	fun getUsedMainnet(): List<ChainNodeTable>

	@Query("SELECT * FROM chainNode WHERE netType = 1 AND isUsed = 1 ORDER BY chainType")
	fun getUsedTestnet(): List<ChainNodeTable>

	@Query("UPDATE chainNode SET isUsed = :isUsed WHERE id = :id AND chainID = :chainID")
	fun updateIsUsed(id: String, isUsed: Int, chainID: String)

	@Query("UPDATE chainNode SET isUsed = 1 WHERE id = (SELECT id FROM chainNode WHERE chainID = :chainID LIMIT 1) AND chainID = :chainID")
	fun updateUsedEOSChain(chainID: String)

	@Query("UPDATE chainNode SET isUsed = 0 WHERE netType = :netType")
	fun clearIsUsedStatus(netType: Int)

	@Insert
	fun insert(chainTable: ChainNodeTable)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(chainNodes: List<ChainNodeTable>)

	@Update
	fun update(chainTable: ChainNodeTable)

	@Delete
	fun delete(chainTable: ChainNodeTable)

	@Query("DELETE FROM chainNode")
	fun deleteAll()
}