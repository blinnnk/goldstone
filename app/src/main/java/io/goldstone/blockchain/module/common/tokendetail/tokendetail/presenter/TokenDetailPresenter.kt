package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel

/**
 * @date 27/03/2018 3:21 PM
 * @author KaySaith
 */

class TokenDetailPresenter(
  override val fragment: TokenDetailFragment
  ) : BaseRecyclerPresenter<TokenDetailFragment, TransactionListModel>() {

}