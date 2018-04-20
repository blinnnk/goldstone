package io.goldstone.blockchain.module.entrance.starting.presenter

import android.content.Context
import com.blinnnk.extension.addFragment
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.entrance.starting.view.StartingFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.jetbrains.anko.doAsync

/**
 * @date 22/03/2018 2:56 AM
 * @author KaySaith
 */

class StartingPresenter(override val fragment: StartingFragment) : BasePresenter<StartingFragment>() {

  fun showCreateWalletFragment() {
    fragment.activity?.addFragment<WalletGenerationFragment>(ContainerID.splash)
  }

  fun showImportWalletFragment() {
    fragment.activity?.addFragment<WalletImportFragment>(ContainerID.splash)
  }

  companion object {
    fun updateLocalDefaultTokens(context: Context) {

      // 准备默认的 `Token List`
      GoldStoneAPI.getDefaultTokens { serverTokens ->
        DefaultTokenTable.getTokens { localTokens ->
          /** 如果本地的 `Tokens` 是空的则直接插入全部服务端获取的 `Default Tokens` */
          localTokens.isEmpty().isTrue {
            context.doAsync {
              serverTokens.forEach {
                GoldStoneDataBase.database.defaultTokenDao().insert(it)
              }
            }
          } otherwise {
            /** 如果本地的 `Tokens` 不是空的, 那么筛选出本地没有的插入到数据库 */
            localTokens.forEach { localToken ->
              serverTokens.find { it.symbol == localToken.symbol }?.let {
                serverTokens.remove(it)
              }
            }

            if (serverTokens.isNotEmpty()) {
              context.doAsync {
                serverTokens.forEach {
                  GoldStoneDataBase.database.defaultTokenDao().insert(it)
                }
              }
            }

            /** Filter `Tokens`  which doesn't exist in server but exist in local */
            serverTokens.forEach { serverToken ->
              localTokens.find { it.symbol == serverToken.symbol }?.let {
                localTokens.remove(it)
              }
            }

            if (localTokens.isNotEmpty()) {
              context.doAsync {
                localTokens.forEach {
                  it.isDefault = false
                  GoldStoneDataBase.database.defaultTokenDao().update(it)
                }
              }
            }
          }
        }
      }
    }
  }

}