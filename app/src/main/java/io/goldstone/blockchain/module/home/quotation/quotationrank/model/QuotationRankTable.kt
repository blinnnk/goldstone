package io.goldstone.blockchain.module.home.quotation.quotationrank.model

import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName


/**
 * @author KaySaith
 * @date  2019/01/02
 */
data class QuotationRankTable(
	@PrimaryKey(autoGenerate = true)
	val id: Int,
	@SerializedName("id_name")
	val idName: String,
	@SerializedName("market_cap")
	val marketCap: String,
	@SerializedName("change_percent_24h")
	val changePercent24h: String,
	val name: String,
	val color: String,
	val symbol: String,
	val price: Float,
	val rank: Int,
	val icon: String,
	val volume: String
)