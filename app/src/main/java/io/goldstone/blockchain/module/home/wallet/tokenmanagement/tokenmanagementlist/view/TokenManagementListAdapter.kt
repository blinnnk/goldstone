package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TokenManagementListModel

/**
 * @date 25/03/2018 5:12 PM
 * @author KaySaith
 */

class TokenManagementListAdapter(
  override val dataSet: ArrayList<TokenManagementListModel>
  ) : HoneyBaseAdapter<TokenManagementListModel, TokenManagementListCell>() {

  override fun generateCell(context: Context) = TokenManagementListCell(context)

  override fun TokenManagementListCell.bindCell(data: TokenManagementListModel) {
    model = data
  }

}