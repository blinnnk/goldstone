package io.goldstone.blockchain.kernel.network

import android.annotation.SuppressLint
import android.content.Context
import com.blinnnk.extension.forEachOrEnd
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.common.utils.toArrayList
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TinyNumber
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.ERC20TransactionModel
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

@SuppressLint("StaticFieldLeak")
/**
 * @date 31/03/2018 8:08 PM
 * @author KaySaith
 */

object GoldStoneAPI {

  /** 网络请求很多是全台异步所以使用 `Application` 的 `Context` */
  lateinit var context: Context

  /**
   * 从服务器获取产品指定的默认的 `DefaultTokenList`
   */
  @JvmStatic
  fun getDefaultTokens(hold: (ArrayList<DefaultTokenTable>) -> Unit) {
    requestData<DefaultTokenTable>(APIPath.defaultTokenList, "list") {
      forEachOrEnd { token, isEnd ->
        if(token.forceShow == TinyNumber.True.value) token.isUsed = true
        if (isEnd) hold(this.toArrayList())
      }
    }
  }

  fun getERC20TokenTransaction(address: String, startBlock: String = "0", hold: (ArrayList<ERC20TransactionModel>) -> Unit) {
    requestData<ERC20TransactionModel>(EtherScanApi.getAllTokenTransaction(address, startBlock), "result") {
      hold(this.toArrayList())
    }
  }

  /**
   * 从 `EtherScan` 获取指定钱包地址的 `TransactionList`
   */
  @JvmStatic
  fun getTransactionListByAddress(address: String, startBlock: String = "0", hold: ArrayList<TransactionTable>.() -> Unit) {
    requestData<TransactionTable>(EtherScanApi.transactions(address, startBlock), "result") {
      hold(this.toArrayList())
    }
  }

  @JvmStatic private inline fun<reified T> requestData(api: String, keyName: String, crossinline hold: List<T>.() -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder()
      .url(api)
      .build()
    client.newCall(request).enqueue(object : Callback {
      override fun onFailure(call: Call, error: IOException) {
        println("$error")
      }
      override fun onResponse(call: Call, response: Response) {
        val data = response.body()?.string()
        val dataObject = JSONObject(data?.substring(data.indexOf("{"), data.lastIndexOf("}") + 1))
        val jsonData = dataObject[keyName].toString()
        val gson = Gson()

        val collectionType = object : TypeToken<Collection<T>>() {}.type
        try {
          hold(gson.fromJson(jsonData, collectionType))
        } catch (error: Exception) {
          println(error)
        }
      }
    })
  }
}



