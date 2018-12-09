package io.goldstone.blockchain.module.home.dapp.dapplist.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blockchain.module.home.dapp.dapplist.contract.DAPPListContract
import io.goldstone.blockchain.module.home.dapp.dapplist.model.DAPPType
import io.goldstone.blockchain.module.home.dapp.dapplist.presenter.DAPPListPresenter


/**
 * @author KaySaith
 * @date  2018/12/08
 */
class DAPPListFragment : GSRecyclerFragment<DAPPTable>(), DAPPListContract.GSView {

	override val pageTitle: String
		get() = when (type) {
			DAPPType.Recommend -> "Recommend DAPP"
			DAPPType.Latest -> "Latest Used DAPP"
			DAPPType.New -> "New DAPP"
			else -> ""
		}
	override lateinit var presenter: DAPPListContract.GSPresenter

	private val type by lazy {
		arguments?.getSerializable(ArgumentKey.dappType) as? DAPPType
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		asyncData = arrayListOf()
		presenter = DAPPListPresenter()
		presenter.start()
		type?.apply {
			presenter.getData(this) {
				updateAdapterDataSet<DAPPListAdapter>(it.toArrayList())
			}
		}
	}

	override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<DAPPTable>?) {
		recyclerView.adapter = DAPPListAdapter(asyncData.orEmptyArray())
	}

	override fun showError(error: Throwable) {
		ErrorDisplayManager(error).show(context)
	}
}