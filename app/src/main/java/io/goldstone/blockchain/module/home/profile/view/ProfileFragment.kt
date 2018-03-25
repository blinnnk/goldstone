package io.goldstone.blockchain.module.home.profile.view

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.orZero
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.HoneyUIUtils
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.profile.model.ProfileModel
import io.goldstone.blockchain.module.home.profile.presenter.ProfilePresenter

/**
 * @date 25/03/2018 10:52 PM
 * @author KaySaith
 */

class ProfileFragment : BaseRecyclerFragment<ProfilePresenter, ProfileModel>() {

  private val slideHeader by lazy { ProfileSlideHeader(context!!) }

  override val presenter = ProfilePresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<ProfileModel>?) {
    recyclerView.adapter = ProfileAdapter(asyncData.orEmptyArray())
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    wrapper.addView(slideHeader)

    asyncData = arrayListOf(
      ProfileModel(R.drawable.contacts_icon, "Contacts", "8"),
      ProfileModel(R.drawable.currency_icon, "Currency Settings", "USD"),
      ProfileModel(R.drawable.language_icon, "Language", "EN"),
      ProfileModel(R.drawable.contacts_icon, "Contacts", "8"),
      ProfileModel(R.drawable.currency_icon, "Currency Settings", "USD"),
      ProfileModel(R.drawable.language_icon, "Language", "EN"),
      ProfileModel(R.drawable.contacts_icon, "Contacts", "8"),
      ProfileModel(R.drawable.currency_icon, "Currency Settings", "USD"),
      ProfileModel(R.drawable.language_icon, "Language", "EN"),
      ProfileModel(R.drawable.contacts_icon, "Contacts", "8"),
      ProfileModel(R.drawable.currency_icon, "Currency Settings", "USD"),
      ProfileModel(R.drawable.language_icon, "Language", "EN")
    )

    recyclerView.apply {
      // 完成特殊布局
      getItemViewAtAdapterPosition<ProfileCell>(3) {
        setMargins<RecyclerView.LayoutParams> { topMargin = 30.uiPX() }
      }

      getItemViewAtAdapterPosition<ProfileCell>(5) {
        setMargins<RecyclerView.LayoutParams> { topMargin = 30.uiPX() }
      }

      getItemViewAtAdapterPosition<ProfileCell>(asyncData?.lastIndex.orZero()) {
        setMargins<RecyclerView.LayoutParams> { bottomMargin = 80.uiPX() }
      }

      getItemViewAtAdapterPosition<ProfileCell>(0) {
        setMargins<RecyclerView.LayoutParams> { topMargin = 120.uiPX() }
        scrollToPosition(0)
      }
    }
  }

  private var isShow = false
  private val headerHeight = 50.uiPX()

  override fun observingRecyclerViewVerticalOffset(offset: Int) {
    System.out.println(headerHeight)
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