package io.goldstone.blinnnk.module.home.dapp.dappexplorer.model

import android.arch.persistence.room.*
import io.goldstone.blinnnk.kernel.database.GoldStoneDataBase

/**
 * @date: 2019-01-23.
 * @author: yangLiHai
 * @description:
 */
@Entity(tableName = "dappRecent")
data class DAPPRecentVisitedTable(
	@PrimaryKey(autoGenerate = true)
	val id: Int,
	val name: String,
	@PrimaryKey()
	val url: String,
	var timing: Long
) {
	companion object {
		val dao = GoldStoneDataBase.database.recentDappDao()
	}
}

@Dao
interface DAPPRecentDao {
	@Query("SELECT * FROM dappRecent")
	fun getLimitRecentDAPPs(): List<DAPPRecentVisitedTable>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(table: DAPPRecentVisitedTable)
	
	@Update
	fun update(table: DAPPRecentVisitedTable)
}





