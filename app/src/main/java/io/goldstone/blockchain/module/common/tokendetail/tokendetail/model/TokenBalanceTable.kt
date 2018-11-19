package io.goldstone.blockchain.module.common.tokendetail.tokendetail.model

import android.arch.persistence.room.*
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import java.io.Serializable

/**
 * @date 08/04/2018 5:10 PM
 * @author KaySaith
 */
@Entity(tableName = "tokenBalance", primaryKeys = ["date", "contract", "address"])
data class TokenBalanceTable(
	var contract: String,
	var date: Long,
	var insertTime: Long,
	var balance: Double,
	var address: String
): Serializable {
	companion object {
		@JvmField
		val dao = GoldStoneDataBase.database.tokenBalanceDao()
	}
}

@Dao
interface TokenBalanceDao {

	@Query("SELECT * FROM tokenBalance WHERE address LIKE :address")
	fun getTokenBalanceByAddress(address: String): List<TokenBalanceTable>

	@Query("DELETE FROM tokenBalance WHERE address LIKE :address")
	fun deleteTokenBalanceByAddress(address: String)

	@Query("SELECT * FROM tokenBalance WHERE contract LIKE :contract AND address LIKE :address ORDER BY date DESC")
	fun getTokenBalanceByContractAndAddress(address: String, contract: String): List<TokenBalanceTable>

	@Query("SELECT * FROM tokenBalance WHERE date LIKE :date AND address LIKE :address AND contract LIKE :contract")
	fun getBalanceByDate(date: Long, address: String, contract: String): TokenBalanceTable?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(token: TokenBalanceTable)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(tokens: List<TokenBalanceTable>)

	@Update
	fun update(token: TokenBalanceTable)

	@Delete
	fun delete(token: TokenBalanceTable)

	@Delete
	fun deleteAll(token: List<TokenBalanceTable>)
}