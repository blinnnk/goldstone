package io.goldstone.blockchain.module.home.quotation.tradermemory

/**
 * @date: 2018/9/27.
 * @author: yanglihai
 * @description:
 */
object RAMTradePresenterManager {
	
	private val receiverList = arrayListOf<RefreshReceiver>()
	
	fun register(receiver: RefreshReceiver) {
		if (!receiverList.contains(receiver)){
			receiverList.add(receiver)
		}
	}
	
	fun unRegister(receiver: RefreshReceiver) {
		receiverList.remove(receiver)
	}
	
	fun refreshData(any: Any) {
		receiverList.forEach {
			it.onReceive(any)
		}
	}
}

interface RefreshReceiver {
	fun onReceive(any: Any)
}

