package io.goldstone.blockchain.module.home.dapp.dappcenter.model

import android.arch.persistence.room.*
import com.google.gson.annotations.SerializedName
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
	val timStamp: String,
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
	@Query("SELECT * FROM dappTable WHERE isRecommended = 1 ORDER BY timStamp DESC LIMIT 5")
	fun getRecommended(): List<DAPPTable>

	@Query("SELECT * FROM dappTable ORDER BY timStamp DESC")
	fun getAll(): List<DAPPTable>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(dapps: List<DAPPTable>)

	@Query("DELETE FROM dappTable")
	fun deleteAll()
}