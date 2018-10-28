package io.goldstone.blockchain.crypto.multichain

import android.arch.persistence.room.*
import com.google.gson.annotations.SerializedName


/**
 * @author KaySaith
 * @date  2018/10/28
 */
@Entity(tableName = "chainNode")
class ChainNodeTable(
	@SerializedName("node_name")
	val name: String,
	@SerializedName("url")
	val url: String,
	@SerializedName("encrypt_status")
	val isEncrypt: Boolean,
	@SerializedName("chain_type")
	val chainType: Int,
	@SerializedName("chain_id")
	@PrimaryKey
	val chainID: String,
	@SerializedName("weight")
	val weight: Int,
	@SerializedName("net_type")
	val isMainnet: Int,
	@SerializedName("key_list")
	val keyList: List<String>
) {

}

@Dao
interface ChainNodeDao {

	@Query("SELECT * FROM chainNode")
	fun getAll(): List<ChainNodeTable>

	@Insert
	fun insert(chainTable: ChainNodeTable)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(chainNodes: List<ChainNodeTable>)

	@Update
	fun update(chainTable: ChainNodeTable)

	@Delete
	fun delete(chainTable: ChainNodeTable)
}