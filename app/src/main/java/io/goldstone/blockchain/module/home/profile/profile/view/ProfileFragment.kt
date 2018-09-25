package io.goldstone.blockchain.module.home.profile.profile.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.profile.model.ProfileModel
import io.goldstone.blockchain.module.home.profile.profile.presenter.ProfilePresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 25/03/2018 10:52 PM
 * @author KaySaith
 */
class ProfileFragment : BaseRecyclerFragment<ProfilePresenter, ProfileModel>() {

	override val pageTitle: String = "Settings"
	private val slideHeader by lazy { ProfileSlideHeader(context!!) }
	override val presenter = ProfilePresenter(this)

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<ProfileModel>?
	) {
		recyclerView.adapter = ProfileAdapter(asyncData.orEmptyArray()) { item, position ->
			// 分配点击事件
			item.apply {
				if (position == asyncData?.size) {
					upgradeEvent = Runnable {
						presenter.showUpgradeDialog()
					}
				}
				when {
					// 增加查询 GoldStoneID 的快捷方法
					model.title.equals(ProfileText.version, true) -> onClick {
						presenter.showGoldStoneID()
					}
					else -> onClick {
						presenter.showTargetFragment(model.title)
						preventDuplicateClicks()
					}
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

	override fun observingRecyclerViewVerticalOffset(offset: Int, range: Int) {
		if (offset >= headerHeight && !isShow) {
			slideHeader.onHeaderShowedStyle()
			isShow = true
		}

		if (offset < headerHeight && isShow) {
			slideHeader.onHeaderHidesStyle()
			isShow = false
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		mainActivity?.getHomeFragment()?.apply {
			presenter.showWalletDetailFragment()
		}
	}

	override fun onActivityResult(
		requestCode: Int,
		resultCode: Int,
		data: Intent?
	) {
		super.onActivityResult(requestCode, resultCode, data)
		presenter.onActivityResult(requestCode, resultCode)
	}
}

enum class ProfileCellType(val index: Int) {
	Contacts(0),
	CurrencySettings(1),
	Language(2),
	ChainNode(3),
	Pin(5),
	AboutUs(6)
}