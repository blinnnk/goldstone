package io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import com.google.gson.annotations.SerializedName

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
@Entity(tableName = "price_alarm_clock")
data class EOSMemoryTransactionHistoryListTable(
	val account: String,
	val price: Double,
	val quantity: Double,
	val time: Long,
	val txId: String,
	val type: Int
) {
	@Ignore
	constructor() : this(
		"",
		0.0,
		0.0,
		0,
		"",
		0
	)

	constructor(listModel: EOSMemoryTransactionHistoryListModel.ListModel) : this(
		listModel.account,
		listModel.price,
		listModel.quantity,
		listModel.time,
		listModel.txId,
		listModel.type
	)
}

data class EOSMemoryTransactionHistoryListModel(
	@SerializedName("code")
	val code: Int,
	@SerializedName("tx_list")
	val txList: List<ListModel>
) {
	data class ListModel(
		@SerializedName("account")
		val account: String,
		@SerializedName("price")
		val price: Double,
		@SerializedName("quantity")
		val quantity: Double,
		@SerializedName("time")
		val time: Long,
		@SerializedName("tx_id")
		val txId: String,
		@SerializedName("type")
		val type: Int
	)
}