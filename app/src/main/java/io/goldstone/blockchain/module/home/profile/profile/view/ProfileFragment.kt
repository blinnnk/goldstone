package io.goldstone.blockchain.module.home.profile.profile.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.RecyclerViewDivider
import io.goldstone.blockchain.common.base.baserecyclerfragment.RecyclerViewSessionModel
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.profile.model.ProfileModel
import io.goldstone.blockchain.module.home.profile.profile.presenter.ProfilePresenter
import org.jetbrains.anko.sdk27.coroutines.onClick

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
						getMainActivity()?.getHomeFragment()?.showUpgradeDialog()
					}
				}
				when {
					// 增加查询 GoldStoneID 的快捷方法
					model.title.equals(ProfileText.version, true) -> onClick {
						presenter.showGoldStoneID()
					}
					model.title.equals(ProfileText.eosAccountRegister, true) -> onClick {
						if (SharedAddress.getCurrentEOSAccount().isValid()) {
							presenter.showTargetFragment(model.title)
							preventDuplicateClicks()
						} else safeShowError(AccountError.InactivatedAccountName)
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
		val divider = RecyclerViewDivider(recyclerView)
		with(divider) {
			val sessionHeight = 30.uiPX()
			setTextColor(Spectrum.opacity3White)
			sessionData = listOf(
				RecyclerViewSessionModel(1, "General Settings", sessionHeight, PaddingSize.device.toFloat()),
				RecyclerViewSessionModel(6, "EOS Account", sessionHeight, PaddingSize.device.toFloat()),
				RecyclerViewSessionModel(7, "Security Settings", sessionHeight, PaddingSize.device.toFloat()),
				RecyclerViewSessionModel(8, "Personal Settings", sessionHeight, PaddingSize.device.toFloat()),
				RecyclerViewSessionModel(13, "Software", sessionHeight, PaddingSize.device.toFloat())
			)
			recyclerView.addItemDecoration(this)
		}
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
}

enum class ProfileCellType(val index: Int) {
	Contacts(0),
	CurrencySettings(1),
	Language(2),
	ChainNode(3),
	Pin(5),
	AboutUs(6)
}