package io.goldstone.blinnnk.module.home.dapp.dappcenter.model

import android.arch.persistence.room.*
import android.support.annotation.UiThread
import com.blinnnk.extension.hasValue
import com.blinnnk.util.load
import com.blinnnk.util.then
import com.google.gson.annotations.SerializedName
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.kernel.commontable.FavoriteTable
import io.goldstone.blinnnk.kernel.commontable.value.TableType
import io.goldstone.blinnnk.kernel.database.GoldStoneDataBase
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
	val tags: String,
	@SerializedName("background_color")
	val backgroundColor: String
) : Serializable {
	fun getTagList(): List<String> = tags.split(",")

	companion object {
		@JvmField
		val dao = GoldStoneDataBase.database.dappDao()

		fun getDAPPUsedStatus(dappID: String, @UiThread hold: (Boolean) -> Unit) {
			load {
				FavoriteTable.dao.getDataCount(dappID, TableType.DAPP).hasValue()
			} then {
				hold(it)
			}
		}
	}
}

@Dao
interface DAPPDao {
	@Query("SELECT * FROM dappTable WHERE isRecommended = 1 ORDER BY weight DESC, timeStamp DESC LIMIT :limit")
	fun getRecommended(limit: Int = 5): List<DAPPTable>

	@Query("SELECT count(*) FROM dappTable WHERE isRecommended = 1")
	fun getRecommendedCount(): Int

	@Query("SELECT * FROM dappTable ORDER BY weight DESC, timeStamp DESC LIMIT :limit")
	fun getAll(limit: Int): List<DAPPTable>

	@Query("SELECT * FROM dappTable WHERE title LIKE '%' || :name || '%'")
	fun getBy(name: String): List<DAPPTable>

	@Query("SELECT dappTable.id AS id, dappTable.icon AS icon, dappTable.banner AS banner, dappTable.url AS url, dappTable.backgroundColor AS backgroundColor, dappTable.description AS description, dappTable.tags AS tags, dappTable.isRecommended AS isRecommended, favoriteTable.timeStamp AS timeStamp, dappTable.title AS title, dappTable.weight AS weight FROM dappTable, favoriteTable WHERE dappTable.id LIKE favoriteTable.valueID AND favoriteTable.walletID = :walletID AND favoriteTable.type = 3  ORDER BY timeStamp DESC LIMIT :limit")
	fun getUsed(limit: Int, walletID: Int = SharedWallet.getCurrentWalletID()): List<DAPPTable>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(dapps: List<DAPPTable>)

	@Query("DELETE FROM dappTable WHERE isRecommended = 1")
	fun deleteAllRecommend()

	@Query("DELETE FROM dappTable WHERE isRecommended = 0")
	fun deleteAllUnRecommended()
}