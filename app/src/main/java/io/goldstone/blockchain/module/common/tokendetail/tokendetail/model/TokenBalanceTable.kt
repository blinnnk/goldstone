package io.goldstone.blockchain.module.common.tokendetail.tokendetail.model

import android.arch.persistence.room.*
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.crypto.toEthCount
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.util.*

/**
 * @date 08/04/2018 5:10 PM
 * @author KaySaith
 */
@Entity(tableName = "tokenBalance")
data class TokenBalanceTable(
	@PrimaryKey(autoGenerate = true) var id: Int,
	var symbol: String,
	var date: Long,
	var insertTime: Long,
	var balance: Double,
	var address: String
) {

	companion object {

		fun getBalanceBySymbol(
			address: String,
			symbol: String,
			hold: (ArrayList<TokenBalanceTable>) -> Unit
		) {
			coroutinesTask({
				GoldStoneDataBase.database.tokenBalanceDao()
					.getTokenBalanceBySymbolAndAddress(address, symbol)
			}) {
				hold(it.toArrayList())
			}
		}

		fun getTodayBalance(address: String, symbol: String, callback: (Double) -> Unit) {
			if (symbol == CryptoSymbol.eth) {
				doAsync {
					GoldStoneEthCall.getEthBalance(address) { balance ->
						GoldStoneAPI.context.runOnUiThread {
							callback(balance.toEthCount())
						}
					}
				}
			} else {
				doAsync {
					DefaultTokenTable.getContractAddressBySymbol(symbol) { contractAddress ->
						GoldStoneEthCall.getTokenCountWithDecimalByContract(
							contractAddress,
							address
						) { balance ->
							GoldStoneAPI.context.runOnUiThread {
								callback(balance)
							}
						}
					}
				}
			}
		}

		fun insertOrUpdate(symbol: String, address: String, date: Long, balance: Double) {
			val addTime = System.currentTimeMillis()
			GoldStoneDataBase.database.tokenBalanceDao().apply {
				getBalanceByDate(date, address, symbol).let {
					it.isNull() isTrue {
						insert(TokenBalanceTable(0, symbol, date, addTime, balance, address))
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
			coroutinesTask({
				GoldStoneDataBase.database.tokenBalanceDao().apply {
					getTokenBalanceByAddress(address).forEach { delete(it) }
				}
			}) {
				callback()
			}
		}

	}
}

@Dao
interface TokenBalanceDao {

	@Query("SELECT * FROM tokenBalance WHERE address LIKE :address")
	fun getTokenBalanceByAddress(address: String): List<TokenBalanceTable>

	@Query("SELECT * FROM tokenBalance WHERE symbol LIKE :symbol AND address LIKE :address ORDER BY date DESC")
	fun getTokenBalanceBySymbolAndAddress(address: String, symbol: String): List<TokenBalanceTable>

	@Query("SELECT * FROM tokenBalance WHERE date LIKE :date AND address LIKE :address AND symbol LIKE :symbol")
	fun getBalanceByDate(date: Long, address: String, symbol: String): TokenBalanceTable?

	@Insert
	fun insert(token: TokenBalanceTable)

	@Update
	fun update(token: TokenBalanceTable)

	@Delete
	fun delete(token: TokenBalanceTable)
}