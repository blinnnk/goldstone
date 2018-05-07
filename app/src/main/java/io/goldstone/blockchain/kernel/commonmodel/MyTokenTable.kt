package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import com.blinnnk.extension.*
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.crypto.toEthCount
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 01/04/2018 12:38 AM
 * @author KaySaith
 */

@Entity(tableName = "myTokens")
data class MyTokenTable(
	@PrimaryKey(autoGenerate = true) var id: Int, var ownerAddress: String,
	var symbol: String,
	var balance: Double
) {
	companion object {

		fun insert(model: MyTokenTable) {
			GoldStoneDataBase.database.myTokenDao().insert(model)
		}

		fun getTokensWith(walletAddress: String, callback: (ArrayList<MyTokenTable>) -> Unit = {}) {
			coroutinesTask({
				GoldStoneDataBase.database.myTokenDao().getTokensBy(walletAddress)
			}) {
				callback(it.toArrayList())
			}
		}

		fun forEachMyTokens(
			walletAddress: String,
			holdToken: (token: MyTokenTable, contract: String, isEnd: Boolean) -> Unit
		) {
			DefaultTokenTable.getTokens { tokenInfo ->
				getTokensWith(walletAddress) { myTokens ->
					myTokens.forEachOrEnd { myToken, isEnd ->
						holdToken(
							myToken,
							tokenInfo.find { it.symbol == myToken.symbol }?.contract.orEmpty(),
							isEnd
						)
					}
				}
			}
		}

		fun deleteBySymbol(symbol: String, address: String, callback: () -> Unit = {}) {
			doAsync {
				GoldStoneDataBase.database.myTokenDao().apply {
					getTokenBySymbolAndAddress(symbol, address).let {
						it.isNull().isFalse {
							delete(it)
						}
						GoldStoneAPI.context.runOnUiThread {
							callback()
						}
					}
				}
			}
		}

		fun deleteByAddress(address: String, callback: () -> Unit = {}) {
			coroutinesTask({
				GoldStoneDataBase.database.myTokenDao().apply {
					getTokensBy(address).forEach { delete(it) }
				}
			}) {
				callback()
			}
		}

		fun insertBySymbol(symbol: String, ownerAddress: String, callback: () -> Unit = {}) {
			coroutinesTask({
				GoldStoneDataBase.database.apply {
					// 安全判断, 如果钱包里已经有这个 `Symbol` 则不添加
					myTokenDao().getTokensBy(ownerAddress).find { it.symbol == symbol }.isNull() isTrue {
						getBalanceAndInsertWithSymbol(symbol, ownerAddress)
					}
				}
			}) {
				callback()
			}
		}

		private fun getBalanceAndInsertWithSymbol(
			symbol: String, ownerAddress: String, callback: (balance: Double) -> Unit = {}
		) {
			// 获取选中的 `Symbol` 的 `Token` 对应 `WalletAddress` 的 `Balance`
			if (symbol == CryptoSymbol.eth) {
				GoldStoneEthCall.getEthBalance(ownerAddress) {
					insert(MyTokenTable(0, ownerAddress, symbol, it))
					callback(it)
				}
			} else {
				DefaultTokenTable.getTokenBySymbol(symbol) {
					GoldStoneEthCall.getTokenBalanceWithContract(it.contract, ownerAddress) {
						insert(MyTokenTable(0, ownerAddress, symbol, it))
						callback(it)
					}
				}
			}
		}

		fun getBalanceWithSymbol(
			symbol: String,
			ownerAddress: String,
			convertByDecimal: Boolean = false,
			callback: (balance: Double) -> Unit = {}
		) {
			// 获取选中的 `Symbol` 的 `Token` 对应 `WalletAddress` 的 `Balance`
			if (symbol == CryptoSymbol.eth) {
				GoldStoneEthCall.getEthBalance(ownerAddress) {
					val balance = if (convertByDecimal) it.toEthCount() else it
					callback(balance)
				}
			} else {
				DefaultTokenTable.getTokenBySymbol(symbol) { token ->
					GoldStoneEthCall.getTokenBalanceWithContract(token.contract, ownerAddress) {
						val balance =
							if (convertByDecimal) CryptoUtils.toCountByDecimal(it, token.decimals) else it
						callback(balance)
					}
				}
			}
		}

		fun updateCurrentWalletBalanceWithSymbol(balance: Double, symbol: String) {
			doAsync {
				GoldStoneDataBase.database.myTokenDao().apply {
					getTokenBySymbolAndAddress(symbol, WalletTable.current.address).let {
						update(it.apply { this.balance = balance })
					}
				}
			}
		}
	}
}

@Dao
interface MyTokenDao {

	@Query("SELECT * FROM myTokens WHERE symbol LIKE :symbol AND ownerAddress LIKE :walletAddress")
	fun getTokenBySymbolAndAddress(symbol: String, walletAddress: String): MyTokenTable

	@Query("SELECT * FROM myTokens WHERE ownerAddress LIKE :walletAddress")
	fun getTokensBy(walletAddress: String): List<MyTokenTable>

	@Query("SELECT * FROM myTokens WHERE symbol LIKE :symbol")
	fun getTokenBySymbol(symbol: String): MyTokenTable

	@Insert
	fun insert(token: MyTokenTable)

	@Update
	fun update(token: MyTokenTable)

	@Delete
	fun delete(token: MyTokenTable)
}