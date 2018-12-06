package io.goldstone.blockchain.kernel.commontable

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/12/06
 */
@Entity(
	tableName = "favoriteTable",
	foreignKeys = [
		ForeignKey(
			entity = WalletTable::class,
			parentColumns = arrayOf("id"),
			childColumns = arrayOf("walletID"),
			onDelete = CASCADE
		)
	]
)

data class FavoriteTable(
	@PrimaryKey
	val walletID: Int,
	val type: Int,
	val valueID: Int
) : Serializable {

}

@Dao
interface FavoriteDao {

}