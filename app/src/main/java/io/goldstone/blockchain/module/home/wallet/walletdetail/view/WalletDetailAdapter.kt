package io.goldstone.blockchain.module.home.wallet.walletdetail.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.matchParent

/**
 * @date 23/03/2018 4:16 PM
 * @author KaySaith
 */

class WalletDetailAdapter(
  override val dataSet: ArrayList<WalletDetailCellModel>,
  private val holdCell: WalletDetailCell.() -> Unit,
  private val holdHeader: WalletDetailHeaderView.() -> Unit
  ) : HoneyBaseAdapterWithHeaderAndFooter<WalletDetailCellModel, WalletDetailHeaderView, WalletDetailCell, LinearLayout>() {

  override fun generateCell(context: Context) = WalletDetailCell(context)

  override fun generateFooter(context: Context) = LinearLayout(context).apply {
    layoutParams = LinearLayout.LayoutParams(matchParent, 10.uiPX())
  }

  override fun generateHeader(context: Context) = WalletDetailHeaderView(context).apply {
    holdHeader(this)
  }

  override fun WalletDetailCell.bindCell(data: WalletDetailCellModel, position: Int) {
    model = data
    holdCell(this)
  }

}