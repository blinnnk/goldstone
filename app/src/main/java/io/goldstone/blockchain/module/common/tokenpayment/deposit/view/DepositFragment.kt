package io.goldstone.blockchain.module.common.tokenpayment.deposit.view

import android.support.v4.app.Fragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.module.common.tokenpayment.deposit.presenter.DepositPresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.verticalLayout

/**
 * @date 2018/5/7 11:40 PM
 * @author KaySaith
 */

class DepositFragment : BaseFragment<DepositPresenter>() {

	override val presenter = DepositPresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {

		}
	}

}