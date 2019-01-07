package io.goldstone.blinnnk.kernel.commontable

import android.arch.persistence.room.*
import io.goldstone.blinnnk.kernel.database.GoldStoneDataBase


/**
 * @author KaySaith
 * @date  2019/01/07
 */
@Entity(tableName = "md5Table")
data class MD5Table(
	@PrimaryKey
	val tableKey: String,
	val md5Value: String
) {
	companion object {
		@JvmField
		val dao = GoldStoneDataBase.database.md5Dao()
	}
}

@Dao
interface MD5TableDao {
	@Query("SELECT md5Value FROM md5Table WHERE tableKey = :key ")
	fun getValue(key: String): String?

	@Query("SELECT tableKey FROM md5Table WHERE md5Value = :value ")
	fun getKey(value: String): String?

	@Query("SELECT * FROM md5Table")
	fun getAll(): List<MD5Table>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun updateValue(data: MD5Table)
}