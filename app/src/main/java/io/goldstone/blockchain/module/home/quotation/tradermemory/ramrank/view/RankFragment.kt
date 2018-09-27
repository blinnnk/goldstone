package io.goldstone.blockchain.module.home.quotation.tradermemory.ramrank.view

import android.support.v4.app.Fragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramrank.presenter.RankPresenter
import org.jetbrains.anko.*

/**
 * @date: 2018/9/25.
 * @author: yanglihai
 * @description:
 */
class RankFragment : BaseFragment<RankPresenter>() {
	override val pageTitle: String
		get() = ""
	override val presenter: RankPresenter = RankPresenter(this)
	val ramRankView by lazy { RAMRankView(context!!) }
	override fun AnkoContext<Fragment>.initView() {
		frameLayout {
			addView(ramRankView)
		}
	}
}