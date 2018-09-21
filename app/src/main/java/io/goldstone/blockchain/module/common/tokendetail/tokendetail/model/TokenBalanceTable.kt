package io.goldstone.blockchain.module.common.tokendetail.tokendetail.model

import android.arch.persistence.room.*
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import org.jetbrains.anko.doAsync

/**
 * @date 08/04/2018 5:10 PM
 * @author KaySaith
 */
@Entity(tableName = "tokenBalance")
data class TokenBalanceTable(
	@PrimaryKey(autoGenerate = true)
	var id: Int,
	var contract: String,
	var date: Long,
	var insertTime: Long,
	var balance: Double,
	var address: String
) {

	constructor(symbol: String, time: Long) : this(
		0,
		symbol,
		time,
		0L,
		0.0,
		""
	)

	companion object {

		fun getBalanceByContract(
			contract: String,
			address: String,
			hold: (List<TokenBalanceTable>) -> Unit
		) {
			load {
				GoldStoneDataBase.database.tokenBalanceDao()
					.getTokenBalanceByContractAndAddress(address, contract)
			} then {
				hold(it)
			}
		}

		fun insertOrUpdate(
			contract: String,
			address: String,
			date: Long,
			balance: Double
		) {
			val addTime = System.currentTimeMillis()
			GoldStoneDataBase.database.tokenBalanceDao().apply {
				getBalanceByDate(date, address, contract).let {
					it.isNull() isTrue {
						insert(TokenBalanceTable(0, contract, date, addTime, balance, address))
					} otherwise {
						it?.apply {
							this.balance = balance
							insertTime = addTime
							update(it)
						}
					}
				}
			}
		}

		fun deleteByAddress(address: String, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.tokenBalanceDao().apply {
					val balances = getTokenBalanceByAddress(address)
					deleteAll(balances)
					callback()
				}
			}
		}
	}
}

@Dao
interface TokenBalanceDao {

	@Query("SELECT * FROM tokenBalance WHERE address LIKE :address")
	fun getTokenBalanceByAddress(address: String): List<TokenBalanceTable>

	@Query("SELECT * FROM tokenBalance WHERE contract LIKE :contract AND address LIKE :address ORDER BY date DESC")
	fun getTokenBalanceByContractAndAddress(
		address: String,
		contract: String
	): List<TokenBalanceTable>

	@Query("SELECT * FROM tokenBalance WHERE date LIKE :date AND address LIKE :address AND contract LIKE :contract")
	fun getBalanceByDate(date: Long, address: String, contract: String): TokenBalanceTable?

	@Insert
	fun insert(token: TokenBalanceTable)

	@Update
	fun update(token: TokenBalanceTable)

	@Delete
	fun delete(token: TokenBalanceTable)

	@Delete
	fun deleteAll(token: List<TokenBalanceTable>)
}