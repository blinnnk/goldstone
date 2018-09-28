package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable

/**
 * @date 25/03/2018 5:12 PM
 * @author KaySaith
 */

class TokenManagementListAdapter(
  override val dataSet: ArrayList<DefaultTokenTable>,
  private val callback: TokenManagementListCell.() -> Unit
  ) : HoneyBaseAdapter<DefaultTokenTable, TokenManagementListCell>() {

  override fun generateCell(context: Context) = TokenManagementListCell(context)

  override fun TokenManagementListCell.bindCell(data: DefaultTokenTable, position: Int) {
    tokenSearchModel = data
    callback(this)
  }

}