package io.goldstone.blockchain.module.home.quotation.quotationrank.model

import android.arch.persistence.room.*
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase


/**
 * @author KaySaith
 * @date  2019/01/02
 */
@Entity(tableName = "quotationRank")
data class QuotationRankTable(
	@PrimaryKey(autoGenerate = true)
	val id: Int,
	@SerializedName("id_name")
	val idName: String,
	@SerializedName("market_cap")
	val marketCap: String,
	@SerializedName("change_percent_24h")
	val changePercent24h: String,
	val name: String,
	val color: String,
	val symbol: String,
	val price: Float,
	val rank: Int,
	@SerializedName("icon")
	val url: String,
	val volume: String,
	val address: String
) {
	companion object {
		@JvmField
		val dao = GoldStoneDataBase.database.quotationRankDao()
	}
}

@Dao
interface QuotationRankDao {
	@Insert
	fun insertAll(rankList: List<QuotationRankTable>)
	
	@Query("SELECT * FROM quotationRank")
	fun getAll(): List<QuotationRankTable>
	
	@Query("DELETE FROM quotationRank")
	fun deleteAll()
	
}