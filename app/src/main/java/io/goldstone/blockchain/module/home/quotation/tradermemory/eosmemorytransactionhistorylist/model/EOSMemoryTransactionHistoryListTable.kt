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
	val quantity: Int,
	val time: Int,
	val txId: String,
	val type: Int
) {
	@Ignore
	constructor() : this(
		0,
		0,
		"",
		0
	)

	constructor(listModel: EOSMemoryTransactionHistoryListModel.ListModel) : this(
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
		@SerializedName("quantity")
		val quantity: Int,
		@SerializedName("time")
		val time: Int,
		@SerializedName("tx_id")
		val txId: String,
		@SerializedName("type")
		val type: Int
	)
}