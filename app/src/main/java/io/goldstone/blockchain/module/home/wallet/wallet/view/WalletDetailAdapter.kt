package io.goldstone.blockchain.module.home.wallet.wallet.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.module.home.wallet.wallet.model.WalletDetailCellModel
import org.jetbrains.anko.matchParent

/**
 * @date 23/03/2018 4:16 PM
 * @author KaySaith
 */

class WalletDetailAdapter(
  private var dataSet: ArrayList<WalletDetailCellModel>
) : RecyclerView.Adapter<WalletDetailAdapter.ViewHolder>() {

  enum class CellType(val value: Int) {
    Header(0), Cell(1), Footer(2)
  }

  private var headerView: ViewGroup? = null
  private var normalCell: WalletDetailCell? = null
  private var footerView: ViewGroup? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    var viewHolder: ViewHolder? = null
    when (viewType) {
      CellType.Header.value -> {
        headerView = WalletDetailHeaderView(parent.context)
        viewHolder = ViewHolder(headerView)
      }

      CellType.Cell.value -> {
        normalCell = WalletDetailCell(parent.context)
        viewHolder = ViewHolder(normalCell)
      }

      CellType.Footer.value -> {
        // 这个是底部填充用的让出指定内容的控件
        footerView = LinearLayout(parent.context).apply {
          layoutParams = LinearLayout.LayoutParams(matchParent, 80.uiPX())
        }
        viewHolder = ViewHolder(footerView)
      }

    }
    return viewHolder!!
  }

  override fun getItemViewType(position: Int): Int = when(position){
    0 -> CellType.Header.value
    dataSet.size + 1 -> CellType.Footer.value
    else -> CellType.Cell.value
  }

  override fun getItemCount(): Int = dataSet.size + 2

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val cellType = getItemViewType(position)
    when (cellType) {
      CellType.Header.value -> {
        // 赋值
      }
      CellType.Cell.value -> {
        // 赋值
        (holder.itemView as? WalletDetailCell)?.model = dataSet[position - 1]
      }
    }
  }

  inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

}
