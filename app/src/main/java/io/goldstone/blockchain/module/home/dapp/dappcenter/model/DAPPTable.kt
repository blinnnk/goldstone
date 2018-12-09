package io.goldstone.blockchain.module.home.dapp.dappcenter.model

import android.arch.persistence.room.*
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.kernel.commontable.value.TableType
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/12/01
 */
@Entity(tableName = "dappTable")
data class DAPPTable(
	@PrimaryKey
	@SerializedName("id")
	val id: String,
	@SerializedName("icon")
	val icon: String,
	@SerializedName("banner")
	val banner: String,
	@SerializedName("url")
	val url: String,
	@SerializedName("title")
	val title: String,
	@SerializedName("description")
	val description: String,
	@SerializedName("recommended_status")
	val isRecommended: Int, // 是否推荐   0 未推荐   1推荐
	@SerializedName("create_time")
	val timeStamp: String,
	@SerializedName("weight")
	val weight: Int,
	@SerializedName("tags")
	val tags: List<String>
) : Serializable {

	companion object {
		@JvmField
		val dao = GoldStoneDataBase.database.dappDao()
	}
}

@Dao
interface DAPPDao {
	@Query("SELECT * FROM dappTable WHERE isRecommended = 1 ORDER BY timeStamp DESC LIMIT :limit")
	fun getRecommended(limit: Int = 5): List<DAPPTable>

	@Query("SELECT * FROM dappTable ORDER BY timeStamp DESC LIMIT :limit")
	fun getAll(limit: Int): List<DAPPTable>

	@Query("SELECT * FROM dappTable WHERE title LIKE '%' || :name || '%'")
	fun getBy(name: String): List<DAPPTable>

	@Query("SELECT dappTable.id AS id, dappTable.icon AS icon, dappTable.banner AS banner, dappTable.url AS url, dappTable.description AS description, dappTable.tags AS tags, dappTable.isRecommended AS isRecommended, favoriteTable.timeStamp AS timeStamp, dappTable.title AS title, dappTable.weight AS weight FROM dappTable, favoriteTable WHERE dappTable.id = favoriteTable.valueID AND favoriteTable.walletID = :walletID AND favoriteTable.type = :tableType  ORDER BY timeStamp DESC LIMIT :limit")
	fun getUsed(limit: Int, tableType: Int = TableType.DAPP, walletID: Int = SharedWallet.getCurrentWalletID()): List<DAPPTable>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(dapps: List<DAPPTable>)

	@Query("DELETE FROM dappTable")
	fun deleteAll()
}