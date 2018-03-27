package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.presenter.TokenSearchPresenter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TokenManagementListModel

/**
 * @date 27/03/2018 11:22 AM
 * @author KaySaith
 */

class TokenSearchFragment : BaseRecyclerFragment<TokenSearchPresenter, TokenManagementListModel>() {

  override val presenter = TokenSearchPresenter(this)

  override fun setRecyclerViewAdapter(
    recyclerView: BaseRecyclerView,
    asyncData: ArrayList<TokenManagementListModel>?
  ) {
    recyclerView.adapter = TokenSearchAdapter(asyncData.orEmptyArray())
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    asyncData = arrayListOf(
      TokenManagementListModel(R.drawable.etc_icon, "ETH", "Global, Ethereum", false),
      TokenManagementListModel(R.drawable.eos_icon, "EOS", "Global, EOS", false),
      TokenManagementListModel(R.drawable.xrp_icon, "XRP", "Global, Ripple", false)
      )

  }

}