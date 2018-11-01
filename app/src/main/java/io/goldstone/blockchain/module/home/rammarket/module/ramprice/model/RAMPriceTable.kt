package io.goldstone.blockchain.module.home.rammarket.module.ramprice.model

import android.arch.persistence.room.*

/**
 * @date: 2018/10/30.
 * @author: yanglihai
 * @description:
 */
@Entity(tableName = "ramPrice")
class RAMPriceTable(
	@PrimaryKey(autoGenerate = true)
	val id: Int,
	var minuteData: String?,
	var hourData: String?,
	var dayData: String?
)

@Dao
interface RAMPriceDao {
	@Query("select * from ramPrice limit 0,1")
	fun getData(): RAMPriceTable?
	
	@Insert
	fun insert(ramPriceTable: RAMPriceTable)
	
	@Update
	fun update(ramPriceTable: RAMPriceTable)
}