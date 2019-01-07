package io.goldstone.blinnnk.module.home.wallet.notifications.notificationlist.model

import io.goldstone.blinnnk.crypto.multichain.ChainID
import io.goldstone.blinnnk.crypto.multichain.orEmpty
import io.goldstone.blinnnk.module.home.wallet.transactions.transactiondetail.model.TransactionSealedModel
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/11/08
 */
data class NotificationModel(
	override val hash: String,
	override val chainID: ChainID?,
	override val isReceive: Boolean,
	override val symbol: String,
	override val count: Double,
	val timeStamp: Long,
	override val toAddress: String,
	override val fromAddress: String
) : TransactionSealedModel(
	false,
	hash,
	symbol,
	fromAddress,
	toAddress,
	count,
	BigInteger.ZERO,
	isReceive,
	chainID?.getContract().orEmpty(),
	false,
	false,
	-1,
	timeStamp.toString(), // 通知中心的通知时间作为 transactionTime 记录在本地数据库
	-1,
	"",
	"",
	chainID
)