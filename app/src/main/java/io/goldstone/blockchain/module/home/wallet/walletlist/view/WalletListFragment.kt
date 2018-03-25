package io.goldstone.blockchain.module.home.wallet.walletlist.view

import android.os.Bundle
import android.view.View
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.wallet.walletlist.model.WalletListModel
import io.goldstone.blockchain.module.home.wallet.walletlist.presenter.WalletListPresenter

/**
 * @date 24/03/2018 8:50 PM
 * @author KaySaith
 */

class WalletListFragment : BaseRecyclerFragment<WalletListPresenter, WalletListModel>() {

  override val presenter = WalletListPresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<WalletListModel>?) {
    asyncData?.let {
      recyclerView.adapter = WalletListAdapter(it)
    }
  }

  override fun setSlideUpWithCellHeight() = 75.uiPX()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    asyncData = arrayListOf(
      WalletListModel("KaySaith", "0x87d8s9...38x767d8", 19282.23, "https://b-ssl.duitang.com/uploads/item/201609/26/20160926124027_vxRkt.jpeg"),
      WalletListModel("Kay", "0x87d8s9...38x767d8", 0.0, "https://b-ssl.duitang.com/uploads/item/201609/26/20160926124020_ekBTr.thumb.700_0.jpeg"),
      WalletListModel("John", "0x87d8s9...38x767d8", 82.09, "https://img.qq1234.org/uploads/allimg/140717/3_140717103028_5.jpg"),
      WalletListModel("KaySaith", "0x87d8s9...38x767d8", 19282.23, "https://b-ssl.duitang.com/uploads/item/201609/26/20160926124027_vxRkt.jpeg"),
      WalletListModel("John", "0x87d8s9...38x767d8", 82.09, "http://img3.imgtn.bdimg.com/it/u=2727319063,4252015704&fm=27&gp=0.jpg")
    )
  }

}