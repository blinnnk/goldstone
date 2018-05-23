package io.goldstone.blockchain.module.home.profile.chainselection.presenter

import android.graphics.Color
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ChainText
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.profile.chainselection.model.ChainSelectionModel
import io.goldstone.blockchain.module.home.profile.chainselection.view.ChainSelectionFragment

/**
 * @date 2018/5/11 4:26 PM
 * @author KaySaith
 */

class ChainSelectionPresenter(
	override val fragment: ChainSelectionFragment
) : BaseRecyclerPresenter<ChainSelectionFragment, ChainSelectionModel>() {

	override fun updateData() {
		fragment.asyncData = arrayListOf(
			ChainSelectionModel(ChainText.goldStoneMain, false, Spectrum.lightRed),
			ChainSelectionModel(ChainText.infuraMain, false, Spectrum.lightGreen),
			ChainSelectionModel(ChainText.ropstan, true, Color.GRAY),
			ChainSelectionModel(ChainText.koven, false, Spectrum.blue)
		)
	}
}