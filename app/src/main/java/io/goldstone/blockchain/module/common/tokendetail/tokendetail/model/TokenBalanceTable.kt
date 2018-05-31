package io.goldstone.blockchain.module.common.tokendetail.tokendetail.model

import android.arch.persistence.room.*
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.crypto.toEthCount
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
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
	@PrimaryKey(autoGenerate = true)
	var id: Int,
	var contract: String,
	var date: Long,
	var insertTime: Long,
	var balance: Double,
	var address: String
) {

	companion object {

		fun getBalanceByContract(
			contract: String,
			address: String = WalletTable.current.address,
			hold: (ArrayList<TokenBalanceTable>) -> Unit
		) {
			coroutinesTask({
				GoldStoneDataBase.database.tokenBalanceDao()
					.getTokenBalanceByContractAndAddress(address, contract)
			}) {
				hold(it.toArrayList())
			}
		}

		fun getTodayBalance(address: String, contract: String, callback: (Double) -> Unit) {
			if (contract == CryptoValue.ethContract) {
				doAsync {
					GoldStoneEthCall.getEthBalance(address) { balance ->
						GoldStoneAPI.context.runOnUiThread {
							callback(balance.toEthCount())
						}
					}
				}
			} else {
				doAsync {
					GoldStoneEthCall.getTokenCountWithDecimalByContract(
						contract,
						address
					) { balance ->
						GoldStoneAPI.context.runOnUiThread {
							callback(balance)
						}
					}
				}
			}
		}

		fun insertOrUpdate(contract: String, address: String, date: Long, balance: Double) {
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

	@Query("SELECT * FROM tokenBalance WHERE contract LIKE :contract AND address LIKE :address ORDER BY date DESC")
	fun getTokenBalanceByContractAndAddress(address: String, contract: String): List<TokenBalanceTable>

	@Query("SELECT * FROM tokenBalance WHERE date LIKE :date AND address LIKE :address AND contract LIKE :contract")
	fun getBalanceByDate(date: Long, address: String, contract: String): TokenBalanceTable?

	@Insert
	fun insert(token: TokenBalanceTable)

	@Update
	fun update(token: TokenBalanceTable)

	@Delete
	fun delete(token: TokenBalanceTable)
}