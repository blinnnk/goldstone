package io.goldstone.blockchain.module.common.tokendetail.tokendetail.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel

/**
 * @date 27/03/2018 3:36 PM
 * @author KaySaith
 */

class TokenDetailAdapter(
  private var dataSet: ArrayList<TransactionListModel>
) : RecyclerView.Adapter<TokenDetailAdapter.ViewHolder>() {

  enum class CellType(val value: Int) {
    Header(0), Cell(1)
  }

  private var headerView: TokenDetailHeaderView? = null
  private var normalCell: TokenDetailCell? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    var viewHolder: ViewHolder? = null
    when (viewType) {
      CellType.Header.value -> {
        headerView = TokenDetailHeaderView(parent.context)
        viewHolder = ViewHolder(headerView)
      }

      CellType.Cell.value -> {
        normalCell = TokenDetailCell(parent.context)
        viewHolder = ViewHolder(normalCell)
      }

    }
    return viewHolder!!
  }

  override fun getItemViewType(position: Int): Int = when(position){
    0 -> CellType.Header.value
    else -> CellType.Cell.value
  }

  override fun getItemCount(): Int = dataSet.size + 1

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val cellType = getItemViewType(position)
    when (cellType) {
      CellType.Header.value -> {
        // 赋值
      }
      CellType.Cell.value -> {
        // 赋值
        (holder.itemView as? TokenDetailCell)?.apply {
          model = dataSet[position - 1]
        }
      }
    }
  }

  inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

}