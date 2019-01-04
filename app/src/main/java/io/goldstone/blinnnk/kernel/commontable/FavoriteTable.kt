package io.goldstone.blinnnk.kernel.commontable

import android.arch.persistence.room.*
import android.support.annotation.WorkerThread
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.kernel.commontable.value.TableType
import io.goldstone.blinnnk.kernel.database.GoldStoneDataBase
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/12/06
 */
@Entity(
	tableName = "favoriteTable",
	primaryKeys = ["walletID", "type", "valueID"]
)
data class FavoriteTable(
	val walletID: Int,
	val type: Int,
	val valueID: String,
	val timeStamp: String
) : Serializable {
	companion object {
		@JvmField
		val dao = GoldStoneDataBase.database.favoriteDao()

		@WorkerThread
		fun updateDAPPUsedStatus(dappID: String, callback: () -> Unit) {
			dao.insert(
				FavoriteTable(
					SharedWallet.getCurrentWalletID(),
					TableType.DAPP,
					dappID,
					System.currentTimeMillis().toString()
				)
			)
			callback()
		}
	}
}

@Dao
interface FavoriteDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(table: FavoriteTable)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(table: List<FavoriteTable>)

	@Query("SELECT count(*) FROM favoriteTable WHERE valueID = :valueID AND type = :type AND walletID = :walletID")
	fun getDataCount(valueID: String, type: Int, walletID: Int = SharedWallet.getCurrentWalletID()): Int

	@Query("DELETE FROM favoriteTable WHERE walletID = :walletID")
	fun deleteAll(walletID: Int)

	@Query("SELECT * FROM favoriteTable WHERE walletID = :walletID")
	fun getAll(walletID: Int = SharedWallet.getCurrentWalletID()): List<FavoriteTable>
}