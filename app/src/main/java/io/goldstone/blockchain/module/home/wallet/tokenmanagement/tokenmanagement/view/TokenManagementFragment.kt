package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view

import android.view.ViewGroup
import com.blinnnk.extension.getRealScreenHeight
import com.blinnnk.extension.orZero
import com.blinnnk.extension.timeUpThen
import com.blinnnk.uikit.AnimationDuration
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.TokenManagementText
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.presenter.TokenManagementPresenter

/**
 * @date 25/03/2018 2:46 AM
 * @author KaySaith
 */
class TokenManagementFragment : BaseOverlayFragment<TokenManagementPresenter>() {
	
	override val presenter = TokenManagementPresenter(this)
	
	override fun ViewGroup.initView() {
		headerTitle = TokenManagementText.addToken
		presenter.showTokenManagementFragment()
	}
	
	override fun setContentHeight(): Int {
		// 这个界面采用侵入式体验一直全屏
		AnimationDuration.Default timeUpThen {
			getMainActivity()?.hideHomeFragment()
		}
		return context?.getRealScreenHeight().orZero()
	}
}