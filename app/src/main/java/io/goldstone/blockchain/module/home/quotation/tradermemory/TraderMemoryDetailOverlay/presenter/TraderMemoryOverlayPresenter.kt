package io.goldstone.blockchain.module.home.quotation.tradermemory.TraderMemoryDetailOverlay.presenter

import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.home.quotation.tradermemory.TraderMemoryDetailOverlay.view.TraderMemoryOverlayFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.view.PersonalMemoryTransactionRecordFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.view.TraderMemoryDetailFragment

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
class TraderMemoryOverlayPresenter(
	override val fragment: TraderMemoryOverlayFragment
) : BaseOverlayPresenter<TraderMemoryOverlayFragment>() {

	fun showTraderMemoryDetailFragment() {
		fragment.addFragmentAndSetArgument<TraderMemoryDetailFragment>(ContainerID.content) {
		}
	}

	fun showPersonalMemoryTransactionRecord(account: String?) {
		fragment.addFragmentAndSetArgument<PersonalMemoryTransactionRecordFragment>(ContainerID.content) {
			putString(
				"account",
				account
			)
			putBoolean(
				"isSalesRecord",
				true
			)
		}
	}
}