package io.goldstone.blinnnk.kernel.network.eos

import com.blinnnk.extension.isNotNull
import io.goldstone.blinnnk.common.error.GoldStoneError
import io.goldstone.blinnnk.crypto.eos.EOSTransactionSerialization
import io.goldstone.blinnnk.crypto.eos.EOSUtils
import io.goldstone.blinnnk.crypto.eos.netcpumodel.BandWidthModel
import io.goldstone.blinnnk.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blinnnk.crypto.eos.transaction.ExpirationType
import io.goldstone.blinnnk.crypto.eos.transaction.completeZero
import io.goldstone.blinnnk.crypto.multichain.ChainID
import io.goldstone.blinnnk.kernel.network.eos.contract.EOSTransactionInterface
import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.StakeType
import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.TradingType
import java.io.Serializable
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/19
 */
class EOSBandWidthTransaction(
	private val chainID: ChainID,
	private val fromAccount: EOSAuthorization,
	private val toAccountName: String,
	private val count: BigInteger,
	private val tradingType: TradingType,
	private val stakeType: StakeType,
	private val isTransfer: Boolean,
	private val expirationType: ExpirationType
) : Serializable, EOSTransactionInterface() {

	override fun serialized(hold: (serialization: EOSTransactionSerialization?, error: GoldStoneError) -> Unit) {
		val stakeToSelf = fromAccount.actor.equals(toAccountName, true)
		// 抵押释放资源的时候, 当 `from account` 和 to account 一样的时候, `transfer` 的值不能设置为 `true`
		// 这个状态下其实是否 `transfer` 无意义, 所以当程序辨别这种情况的时候默认修改这个值为 `false`
		val transferStatus = if (stakeToSelf) false else isTransfer
		// 判断抵押对象, 因为我们的界面是把 `CPU` 和 `NET` 切分的, 所以这里判断当前的值是什么
		val netEOSCount = if (tradingType == TradingType.NET) count else BigInteger.ZERO
		val cpuEOSCount = if (tradingType == TradingType.CPU) count else BigInteger.ZERO
		val bandWidthInfo = BandWidthModel(
			listOf(fromAccount),
			fromAccount.actor,
			toAccountName,
			netEOSCount,
			cpuEOSCount,
			stakeType,
			transferStatus
		)
		EOSAPI.getTransactionHeader(expirationType) { header, error ->
			if (header.isNotNull() && error.isNone()) {
				// 准备 Action
				//  `contextFreeActions` 目前只有空的状态
				val contextFreeActions = listOf<String>()
				val serializedActionSize = "01" // 目前不支持批量给多账户购买所以 `ActionSize` 写死 `1`
				val serializedContextFreeActions = EOSUtils.getVariableUInt(contextFreeActions.size)
				val serializedTransactionExtension = "00"
				val packedTX = header.serialize() + serializedContextFreeActions + serializedActionSize + bandWidthInfo.serialize() + serializedTransactionExtension
				val serializedCode = (chainID.id + packedTX).completeZero()
				hold(EOSTransactionSerialization(packedTX, serializedCode), error)
			} else hold(null, error)
		}
	}

}