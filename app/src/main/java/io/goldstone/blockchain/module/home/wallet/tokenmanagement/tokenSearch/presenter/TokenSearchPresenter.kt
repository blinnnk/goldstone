package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.presenter

import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.view.TokenSearchFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TokenManagementListModel

/**
 * @date 27/03/2018 11:23 AM
 * @author KaySaith
 */

class TokenSearchPresenter(
  override val fragment: TokenSearchFragment
  ) : BaseRecyclerPresenter<TokenSearchFragment, TokenManagementListModel>() {

}