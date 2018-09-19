package io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecordandlargesinglerecord.view.TraderMemorySalesRecordAndLargeSingleRecordFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.present.TraderMemoryDetailPresenter
import org.jetbrains.anko.*

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
class TraderMemoryDetailFragment : BaseFragment<TraderMemoryDetailPresenter>() {
	override val presenter: TraderMemoryDetailPresenter = TraderMemoryDetailPresenter(this)

	@SuppressLint("ResourceType", "CommitTransaction")
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
			verticalLayout {
				layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
				gravity = Gravity.CENTER_HORIZONTAL

				textView {
					text = "基础信息"
				}
				textView {
					text = "线形图"
				}
				frameLayout {
					layoutParams = LinearLayout.LayoutParams(matchParent, 200.uiPX()).apply {
						topMargin = 10.uiPX()
						bottomMargin = 10.uiPX()
					}
					id = 9999
				}
				addFragmentAndSetArgument<TraderMemorySalesRecordAndLargeSingleRecordFragment>(9999) {
				}

				textView {
					text = "持量大户"
				}
				textView {
					text = "成交资金分布"
				}
				textView {
					text = "买入/卖出"
				}
			}
		}
	}
}