package io.goldstone.blockchain.module.home.profile.profile.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.profile.profile.model.ProfileModel
import io.goldstone.blockchain.module.home.profile.profile.presenter.ProfilePresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 25/03/2018 10:52 PM
 * @author KaySaith
 */

class ProfileFragment : BaseRecyclerFragment<ProfilePresenter, ProfileModel>() {

	private val slideHeader by lazy { ProfileSlideHeader(context!!) }
	override val presenter = ProfilePresenter(this)

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView, asyncData: ArrayList<ProfileModel>?
	) {

		recyclerView.adapter = ProfileAdapter(asyncData.orEmptyArray()) { item, position ->
			// 分配点击事件
			item.apply {
				// 调整布局
				if (position == 4) {
					item.layoutParams.height = 90.uiPX()
					isCenterInVertical = false
				} else {
					item.layoutParams.height = 60.uiPX()
					isCenterInVertical = true
				}
				onClick {
					presenter.showContactsFragment(model.title)
					preventDuplicateClicks()
				}
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		wrapper.addView(slideHeader)
	}

	private var isShow = false
	private val headerHeight = 50.uiPX()

	override fun observingRecyclerViewVerticalOffset(offset: Int) {
		if (offset >= headerHeight && !isShow) {
			slideHeader.onHeaderShowedStyle()
			isShow = true
		}

		if (offset < headerHeight && isShow) {
			slideHeader.onHeaderHidesStyle()
			isShow = false
		}
	}

}