package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.presenter

import com.blinnnk.extension.forEachOrEnd
import com.blinnnk.extension.isNull
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.timeUpThen
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListCell
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListFragment

/**
 * @date 25/03/2018 5:11 PM
 * @author KaySaith
 */

class TokenManagementListPresenter(
  override val fragment: TokenManagementListFragment
) : BaseRecyclerPresenter<TokenManagementListFragment, DefaultTokenTable>() {

  override fun updateData(asyncData: ArrayList<DefaultTokenTable>?) {
    DefaultTokenTable.getTokens { defaultTokens ->
      defaultTokens.forEachOrEnd { defaultToken, isEnd ->
        val address = WalletTable.current.address
        MyTokenTable.getTokensWith(address) { myTokens ->
          defaultToken.isUsed = !myTokens.find { defaultToken.symbol == it.symbol }.isNull()
          if (isEnd) fragment.asyncData = defaultTokens
        }
      }
    }
  }

  private var needShowLoadingView = true

  fun updateMyTokensInfoBy(cell: TokenManagementListCell) {
    /**
     * show `Loading View` at 100ms later to prevent too fast
     * to response the result that make ui flash
     */
    100L timeUpThen {
      if (needShowLoadingView) {
        fragment.getMainActivity()?.showLoadingView()
      }
    }
    cell.apply {
      if (switch.isChecked) {
        // once it is checked then insert this symbol into `MyTokenTable` database
        MyTokenTable.insertBySymbol(getSymbol(), WalletTable.current.address) {
          needShowLoadingView = false
          fragment.getMainActivity()?.removeLoadingView()
        }
      } else {
        needShowLoadingView = false
        fragment.getMainActivity()?.removeLoadingView()
        // once it is unchecked then delete this symbol from `MyTokenTable` database
        MyTokenTable.deleteBySymbol(getSymbol())
      }
      // prevent duplicate clicks
      cell.switch.preventDuplicateClicks()
    }
  }
}