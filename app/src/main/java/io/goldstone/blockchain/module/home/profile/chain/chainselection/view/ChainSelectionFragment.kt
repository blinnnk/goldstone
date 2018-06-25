package io.goldstone.blockchain.module.home.profile.chain.chainselection.view

import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.value.ChainText
import io.goldstone.blockchain.module.home.profile.chain.chainselection.model.ChainSelectionModel
import io.goldstone.blockchain.module.home.profile.chain.chainselection.presenter.ChainSelectionPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 2018/5/11 4:26 PM
 * @author KaySaith
 */
class ChainSelectionFragment : BaseRecyclerFragment<ChainSelectionPresenter, ChainSelectionModel>() {
	
	override val presenter = ChainSelectionPresenter(this)
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<ChainSelectionModel>?
	) {
		recyclerView.adapter = ChainSelectionAdapter(asyncData.orEmptyArray()) {
			onClick {
				presenter.showNodeSelectionFragment(model.title == ChainText.mainnet)
				preventDuplicateClicks()
			}
		}
	}
}