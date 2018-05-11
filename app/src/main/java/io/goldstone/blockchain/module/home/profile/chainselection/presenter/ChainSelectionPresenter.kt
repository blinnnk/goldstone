package io.goldstone.blockchain.module.home.profile.chainselection.presenter

import android.graphics.Color
import android.widget.GridLayout
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
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
			ChainSelectionModel("Main (Gold Stone)", false, Spectrum.lightRed),
			ChainSelectionModel("Main (Infura)", false, Spectrum.lightGreen),
			ChainSelectionModel("Ropstan Testnet", true, Color.GRAY),
			ChainSelectionModel("Koven Testnet", false, Spectrum.blue),
			ChainSelectionModel("Rinkeby Testnet", false, Spectrum.yellow)
		)
	}
}