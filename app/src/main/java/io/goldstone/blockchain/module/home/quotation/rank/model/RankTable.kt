package io.goldstone.blockchain.module.home.quotation.rank.model

import android.arch.persistence.room.*
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.io.Serializable

/**
 * @date: 2018/8/16.
 * @author: yanglihai
 * @description: rank列表数据库table
 */
@Entity(tableName = "rankListTable")
data class RankTable(
	@PrimaryKey(autoGenerate = true)
	var id: Int,
	@SerializedName("id_name")
	var idName: String = "",
	@SerializedName("market_cap")
	var marketCap: String = "",
	@SerializedName("change_percent_24h")
	var changePercent24h: String = "",
	@SerializedName("name")
	var name: String = "",
	@SerializedName("color")
	var color: String = "",
	@SerializedName("symbol")
	var symbol: String = "",
	@SerializedName("price")
	var price: String = "",
	@SerializedName("rank")
	var rank: String = "",
	@SerializedName("icon")
	var icon: String = ""

) : Serializable {
	
	constructor(): this(0)
	
	companion object {
	  fun insertRankList(rankTableList: List<RankTable>, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.rankDao().insert(rankTableList)
				GoldStoneAPI.context.runOnUiThread {
					callback()
				}
			}
		}
		
		fun queryRankData(callback: (List<RankTable>) -> Unit) {
			doAsync {
				val rankTableList = GoldStoneDataBase.database.rankDao().queryRankList()
				GoldStoneAPI.context.runOnUiThread {
					callback(rankTableList)
				}
			}
		}
		
		fun clearRankTable(callback: () -> Unit){
			doAsync {
				GoldStoneDataBase.database.rankDao().clear()
				GoldStoneAPI.context.runOnUiThread {
					callback()
				}
			}
		}
		
	}
}


@Dao
interface RankDao {
	@Query("select * from rankListTable")
	fun queryRankList() : List<RankTable>
	
	@Insert
	fun insert(rankTable: RankTable)
	
	@Insert
	fun insert(rankTableList: List<RankTable>)
	
	@Delete
	fun delete(rankTable: RankTable)
	
	@Query("delete from rankListTable")
	fun clear()
	
	@Update
	fun update(rankTable: RankTable)

}






