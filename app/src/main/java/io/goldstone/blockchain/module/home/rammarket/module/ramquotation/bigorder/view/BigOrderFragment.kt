package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigorder.view

import android.annotation.SuppressLint
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigorder.presenter.BigOrderPresenter

@SuppressLint("ValidFragment")
/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
class BigOrderFragment : BaseRecyclerFragment<BigOrderPresenter, String>() {
	
	override val pageTitle: String = EOSRAMExchangeText.ramExchange
	override val presenter: BigOrderPresenter = BigOrderPresenter(this)
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<String>?
	) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}