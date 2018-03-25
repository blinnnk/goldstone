package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TokenManagementListModel
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.presenter.TokenManagementListPresenter

/**
 * @date 25/03/2018 5:11 PM
 * @author KaySaith
 */

class TokenManagementListFragment
  : BaseRecyclerFragment<TokenManagementListPresenter, TokenManagementListModel>() {

  override val presenter = TokenManagementListPresenter(this)

  override fun setRecyclerViewAdapter(
    recyclerView: BaseRecyclerView,
    asyncData: ArrayList<TokenManagementListModel>?
  ) {
    recyclerView.adapter = TokenManagementListAdapter(asyncData.orEmptyArray())
  }

  override fun setSlideUpWithCellHeight() = 60.uiPX()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    asyncData = arrayListOf(
      TokenManagementListModel(R.drawable.etc_icon, "ETH", "Global, Ethereum", true),
      TokenManagementListModel(R.drawable.eos_icon, "EOS", "Global, EOS", true),
      TokenManagementListModel(R.drawable.xrp_icon, "XRP", "Global, Ripple", false),
      TokenManagementListModel(R.drawable.etc_icon, "ETH", "Global, Ethereum", true),
      TokenManagementListModel(R.drawable.eos_icon, "EOS", "Global, EOS", true),
      TokenManagementListModel(R.drawable.xrp_icon, "XRP", "Global, Ripple", false),
      TokenManagementListModel(R.drawable.etc_icon, "ETH", "Global, Ethereum", true),
      TokenManagementListModel(R.drawable.eos_icon, "EOS", "Global, EOS", true),
      TokenManagementListModel(R.drawable.xrp_icon, "XRP", "Global, Ripple", false),
      TokenManagementListModel(R.drawable.etc_icon, "ETH", "Global, Ethereum", true),
      TokenManagementListModel(R.drawable.eos_icon, "EOS", "Global, EOS", true),
      TokenManagementListModel(R.drawable.xrp_icon, "XRP", "Global, Ripple", false)
    )

  }

}