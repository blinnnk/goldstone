package io.goldstone.blockchain.module.home.profile.profile.view

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.orZero
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.value.ProfileText
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

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<ProfileModel>?) {

    recyclerView.adapter = ProfileAdapter(asyncData.orEmptyArray()) { item, position ->
      // 分配点击事件
      item.apply {
        onClick {
          presenter.showContactsFragment(model.title)
          preventDuplicateClicks()
        }
      }

      // 完成定制布局
      presenter.setCustomIntervalSize(item, position)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    wrapper.addView(slideHeader)

    asyncData = arrayListOf(
      ProfileModel(R.drawable.contacts_icon, ProfileText.contacts, "8"),
      ProfileModel(R.drawable.currency_icon, ProfileText.currency, "USD"),
      ProfileModel(R.drawable.language_icon, ProfileText.language, "EN"),
      ProfileModel(R.drawable.contacts_icon, ProfileText.contacts, "8"),
      ProfileModel(R.drawable.currency_icon, ProfileText.currency, "USD"),
      ProfileModel(R.drawable.language_icon, ProfileText.language, "EN"),
      ProfileModel(R.drawable.contacts_icon, ProfileText.aboutUs, "8"),
      ProfileModel(R.drawable.currency_icon, ProfileText.currency, "USD"),
      ProfileModel(R.drawable.language_icon, ProfileText.language, "EN"),
      ProfileModel(R.drawable.contacts_icon, ProfileText.contacts, "8"),
      ProfileModel(R.drawable.currency_icon, ProfileText.currency, "USD"),
      ProfileModel(R.drawable.language_icon, ProfileText.language, "EN")
    )
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