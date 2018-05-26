package io.goldstone.blockchain.module.home.profile.chainselection.presenter

import android.graphics.Color
import com.blinnnk.extension.jump
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ChainID
import io.goldstone.blockchain.common.value.ChainText
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
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
		AppConfigTable.getAppConfig {
			it?.apply {
				fragment.asyncData = arrayListOf(
					ChainSelectionModel(ChainText.goldStoneMain, chainID == ChainID.Main.id, Spectrum.lightRed, ChainID.Main.id),
					ChainSelectionModel(ChainText.rinkeby, chainID == ChainID.Rinkeby.id, Spectrum.lightGreen, ChainID.Rinkeby.id),
					ChainSelectionModel(ChainText.ropstan, chainID == ChainID.Ropstan.id, Color.GRAY, ChainID.Ropstan.id),
					ChainSelectionModel(ChainText.koven, chainID == ChainID.Kovan.id, Spectrum.blue, ChainID.Kovan.id)
				)
			}
		}
	}

	fun updateCurrentChainID(chainID: String) {
		AppConfigTable.updateChainID(chainID) {
			fragment.activity?.jump<SplashActivity>()
		}
	}
}