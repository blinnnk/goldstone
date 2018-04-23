package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListAdapter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListCell
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 25/03/2018 5:11 PM
 * @author KaySaith
 */

// 获取数据后把默认列表防止到内存里加快每次访问的速度
private var defaultTokenList: ArrayList<DefaultTokenTable>? = null

class TokenManagementListPresenter(
  override val fragment: TokenManagementListFragment
) : BaseRecyclerPresenter<TokenManagementListFragment, DefaultTokenTable>() {

  override fun updateData() {
    defaultTokenList.isNull().isFalse {
      fragment.asyncData = defaultTokenList
      fragment.prepareMyDefaultTokens()
    } otherwise {
      fragment.prepareMyDefaultTokens()
    }
  }

  private fun TokenManagementListFragment.prepareMyDefaultTokens() {
    doAsync {
      // 在异步线程更新数据
      DefaultTokenTable.getTokens { defaultTokens ->
        defaultTokens.forEachOrEnd { defaultToken, isEnd ->
          val address = WalletTable.current.address
          MyTokenTable.getTokensWith(address) { myTokens ->
            defaultToken.isUsed = !myTokens.find { defaultToken.symbol == it.symbol }.isNull()
            if (isEnd) {
              // 在主线程更新 `UI`
              context?.runOnUiThread {
                asyncData.isNull() isTrue {
                  asyncData = defaultTokens
                } otherwise {
                  diffAndUpdateSingleCellAdapterData<TokenManagementListAdapter>(defaultTokens)
                }
                defaultTokenList = defaultTokens
              }
            }
          }
        }
      }
    }
  }

  companion object {

    private var needShowLoadingView = true
    fun updateMyTokensInfoBy(cell: TokenManagementListCell, activity: MainActivity) {
      /**
       * show `Loading View` at 100ms later to prevent too fast
       * to response the result that make ui flash
       */
      100L timeUpThen {
        if (needShowLoadingView) {
          activity.showLoadingView()
        }
      }
      cell.apply {
        if (switch.isChecked) {
          // once it is checked then insert this symbol into `MyTokenTable` database
          MyTokenTable.insertBySymbol(getSymbol(), WalletTable.current.address) {
            needShowLoadingView = false
            activity.removeLoadingView()
          }
        } else {
          needShowLoadingView = false
          activity.removeLoadingView()
          // once it is unchecked then delete this symbol from `MyTokenTable` database
          MyTokenTable.deleteBySymbol(getSymbol(), WalletTable.current.address)
        }
        // prevent duplicate clicks
        cell.switch.preventDuplicateClicks()
      }
    }
  }
}