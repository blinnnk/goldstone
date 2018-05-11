package io.goldstone.blockchain.module.home.profile.lanaguage.view

import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.profile.lanaguage.model.LanguageModel
import io.goldstone.blockchain.module.home.profile.lanaguage.presenter.LanguagePresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 26/03/2018 6:41 PM
 * @author KaySaith
 */

class LanguageFragment : BaseRecyclerFragment<LanguagePresenter, LanguageModel>() {

	override val presenter = LanguagePresenter(this)

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<LanguageModel>?
	) {
		recyclerView.adapter = LanguageAdapter(asyncData.orEmptyArray()) { item, _ ->
			item.apply {
				onClick {
					presenter.setLanguage(model.name) {
						setSwitchStatusBy(this)
					}
					preventDuplicateClicks()
				}
			}
		}
	}

	override fun setSlideUpWithCellHeight() =
		50.uiPX()

}