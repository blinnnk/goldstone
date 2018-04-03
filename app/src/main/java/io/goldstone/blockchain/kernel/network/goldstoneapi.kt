package io.goldstone.blockchain.kernel.network

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.common.utils.toArrayList
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TinyNumber
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

  @JvmStatic
  fun getDefaultTokens(hold:( ArrayList<DefaultTokenTable>) -> Unit) {
    requestData(APIPath.defaultTokenList) {
      forEachIndexed { index, token ->
        if(token.forceShow == TinyNumber.True.value) token.isUsed = true
        if (index == lastIndex) hold(this.toArrayList())
      }
    }
  }

  @JvmStatic private fun requestData(api: String, hold: List<DefaultTokenTable>.() -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder()
      .url(api)
      .build()

    client.newCall(request).enqueue(object : Callback {

      override fun onFailure(call: Call, error: IOException) {
        System.out.println("$error")
      }

      override fun onResponse(call: Call, response: Response) {
        val data = response.body()?.string()
        val dataObject = JSONObject(data?.substring(data.indexOf("{"), data.lastIndexOf("}") + 1))
        val jsonData = dataObject["list"].toString()
        val gson = Gson()
        val collectionType = object : TypeToken<Collection<DefaultTokenTable>>() {}.type
        hold(gson.fromJson(jsonData, collectionType))
      }

    })
  }
}



