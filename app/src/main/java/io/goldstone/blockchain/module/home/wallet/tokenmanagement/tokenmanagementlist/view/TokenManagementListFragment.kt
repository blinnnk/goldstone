package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view

import com.blinnnk.extension.orEmptyArray
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.presenter.TokenManagementListPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 25/03/2018 5:11 PM
 * @author KaySaith
 */

class TokenManagementListFragment
  : BaseRecyclerFragment<TokenManagementListPresenter, DefaultTokenTable>() {

  override val presenter = TokenManagementListPresenter(this)

  override fun setRecyclerViewAdapter(
    recyclerView: BaseRecyclerView,
    asyncData: ArrayList<DefaultTokenTable>?
  ) {
    recyclerView.adapter = TokenManagementListAdapter(asyncData.orEmptyArray()) { cell ->
      cell.switch.onClick { presenter.updateMyTokensInfoBy(cell) }
    }
  }

  override fun setSlideUpWithCellHeight() = 60.uiPX()

}