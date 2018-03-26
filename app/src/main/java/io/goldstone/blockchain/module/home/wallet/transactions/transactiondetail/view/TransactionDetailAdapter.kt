package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel

/**
 * @date 27/03/2018 3:27 AM
 * @author KaySaith
 */

class TransactionDetailAdapter(
  private var dataSet: ArrayList<TransactionDetailModel>
) : RecyclerView.Adapter<TransactionDetailAdapter.ViewHolder>() {

  enum class CellType(val value: Int) {
    Header(0), Cell(1)
  }

  private var headerView: TransactionDetailHeaderView? = null
  private var normalCell: TransactionDetailCell? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    var viewHolder: ViewHolder? = null
    when (viewType) {
      CellType.Header.value -> {
        headerView = TransactionDetailHeaderView(parent.context)
        viewHolder = ViewHolder(headerView)
      }

      CellType.Cell.value -> {
        normalCell = TransactionDetailCell(parent.context)
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
        (holder.itemView as? TransactionDetailCell)?.apply {
          model = dataSet[position - 1]
          if (model.description == TransactionText.url) setTitleColor(Spectrum.darkBlue)
        }
      }
    }
  }

  inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

}
