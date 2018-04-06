package io.goldstone.blockchain

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.jetbrains.anko.doAsync

/**
 * @date 22/03/2018 3:02 PM
 * @author KaySaith
 */

class GoldStoneApp : Application() {

  override fun onCreate() {
    super.onCreate()

    // 创建数据库
    GoldStoneDataBase.initDatabase(this)

    // 初始化加密查询的 `Context`
    GoldStoneEthCall.context = this

    // 初始化 `Api` 的上下文
    GoldStoneAPI.context = this

    // 检查本地的默认 `Tokens`
    updateLocalDefaultTokens(this)

    /*
    * Querying the language type of the current account
    * set and displaying the interface from the database.
    */
    WalletTable.getCurrentWalletInfo {
      it?.apply { currentLanguage = language }
    }

  }

  companion object {

    var currentLanguage: Int? = null

    fun reload(context: Context) {
      val startActivity = Intent(context, SplashActivity::class.java)
      val pendingIntentId = 123456
      val pendingIntent = PendingIntent.getActivity(context, pendingIntentId, startActivity,
        PendingIntent.FLAG_CANCEL_CURRENT
      )
      val service = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
      service.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)
      System.exit(2)
    }

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

            if (serverTokens.size > 0) {
              context.doAsync {
                serverTokens.forEach {
                  GoldStoneDataBase.database.defaultTokenDao().insert(it)
                }
              }
            }

            /** 筛选出服务器没有本地却有的 `Tokens` 从本地移除 */
            serverTokens.forEach { serverToken ->
              localTokens.find { it.symbol == serverToken.symbol }?.let {
                localTokens.remove(it)
              }
            }

            if (localTokens.size > 0) {
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