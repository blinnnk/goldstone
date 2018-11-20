package io.goldstone.blockchain.module.home.rammarket.module.ramtransactionsearch.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.rammarket.module.ramtransactionsearch.contract.RAMTransactionSearchContract
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 21/04/2018 4:32 PM
 * @author yangLiHai
 */
class RAMTransactionSearchPresenter(
	private val gsView: RAMTransactionSearchContract.GSView,
	var account: String?
) : RAMTransactionSearchContract.GSPresenter {
	
	private var endID = 0
	
	override fun start() {
	}
	
	override fun searchByName(@UiThread callback: () -> Unit) {
		if (account == null) callback()
		doAsync {
			GoldStoneAPI.getEOSRAMTransactionsByAccount(account!!, endID) { data, error ->
				if (data != null && error.isNone()) {
					data.forEach {
						it.account = account!!
					}
					GoldStoneAPI.context.runOnUiThread {
						gsView.notifyUI(endID == 0, data.toArrayList())
						data.isNotEmpty() isTrue {
							endID = data[data.lastIndex].id
						}
						callback()
					}
				} else {
					GoldStoneAPI.context.runOnUiThread {
						gsView.showError(error)
						callback()
					}
				}
			}
		}
	}
	
	override fun loadFirstPage() {
		endID = 0
		gsView.showLoading(true)
		searchByName {
			gsView.showLoading(false)
		}
	}
	
	override fun loadMore() {
		account?.apply {
			gsView.showBottomLoading(true)
			searchByName {
				gsView.showBottomLoading(false)
			}
		}
	}
	
}