package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.value.Current
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * @author KaySaith
 * @date  2018/09/26
 * @description
 *  `MyTokenTable`, `DefaultTokenTable` 这两个表经常关联查询,
 *  为了优化性能和代码整洁, 这里封装一个连表查询类.
 */

@Suppress("LABEL_NAME_CLASH")
data class MyTokenWithDefaultTable(
	var iconUrl: String,
	var symbol: String,
	var tokenName: String,
	var decimal: Int,
	var count: Double,
	var price: Double,
	var currency: Double = 0.0,
	var contract: String,
	var weight: Int,
	var chainID: String
) {
	companion object {
		@WorkerThread
		fun getMyDefaultTokens(hold: (List<WalletDetailCellModel>) -> Unit) = GlobalScope.launch(Dispatchers.Default) {
			val wallet = WalletTable.dao.findWhichIsUsing() ?: return@launch
			val addresses = wallet.getCurrentAddresses(true)
			val eosWalletType = wallet.getEOSWalletType()
			val data =
				GoldStoneDataBase.database.myTokenDefaultTableDao().getData(addresses)
			hold(data.map { WalletDetailCellModel(it, eosWalletType) })
		}
	}
}

@Dao
interface MyTokenDefaultTableDao {
	@Query("SELECT defaultTokens.iconUrl AS iconUrl, defaultTokens.symbol AS symbol, defaultTokens.name AS tokenName, defaultTokens.decimals AS decimal, defaultTokens.price AS price, defaultTokens.weight AS weight,  myTokens.balance * defaultTokens.price AS currency, myTokens.balance AS count, myTokens.contract AS contract, myTokens.chainID AS chainID FROM defaultTokens, myTokens WHERE defaultTokens.contract = myTokens.contract AND defaultTokens.chainID = myTokens.chainID AND defaultTokens.symbol = myTokens.symbol AND myTokens.isClosed = :isClose AND myTokens.chainID IN (:currentChainIDs) AND myTokens.ownerName IN (:ownerNames)")
	fun getData(ownerNames: List<String>, isClose: Boolean = false, currentChainIDs: List<String> = Current.chainIDs()): List<MyTokenWithDefaultTable>
}