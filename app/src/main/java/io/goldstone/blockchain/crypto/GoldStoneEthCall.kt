package io.goldstone.blockchain.crypto

import android.annotation.SuppressLint
import android.content.Context
import io.goldstone.blockchain.kernel.network.APIPath
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

@SuppressLint("StaticFieldLeak")
/**
 * @date 31/03/2018 2:44 PM
 * @author KaySaith
 */

object GoldStoneEthCall {

  lateinit var context: Context

  private enum class Method(val method: String, val code: String = "") {
    GetSymbol("eth_call", "0x95d89b41000000000000000000000000"),
    GetTokenBalance("eth_call", "0x70a08231000000000000000000000000"),
    GetBalance("eth_getBalance"),
    GetTotalSupply("eth_call", "0x18160ddd0000000000000000000000000000000000000000000000000000000000000005"),
    GetTokenDecimal("eth_call", "0x313ce5670000000000000000000000000000000000000000000000000000000000000005"),
    GetTokenName("eth_call", "0x06fdde030000000000000000000000000000000000000000000000000000000000000005")
  }

  @JvmStatic private val contentType = MediaType.parse("application/json; charset=utf-8")
  @JvmStatic private infix fun String.withAddress(address: String) = this + address.checkAddressInRules()
  @JvmStatic private fun String.checkAddressInRules() =
    if (substring(0, 2) == "0x") substring(2 until length) else this

  /**
   * @description 通过 [contractAddress] 和 [walletAddress] 从节点获取全部的 `Token` 信息
   */
  @JvmStatic
  fun getAddressInfoInToken(contractAddress: String, walletAddress: String) {
    getTokenSymbol(contractAddress) { symbol ->
      getTokenName(contractAddress) { name ->
        getTokenDecimal(contractAddress) { decimal ->
          getTokenTotalSupply(contractAddress) { totalSupply ->
            getTokenBalanceWithContract(contractAddress, walletAddress) { tokenBalance ->
              // 用的时候再完善这里
              System.out.println(symbol + name + decimal + totalSupply + tokenBalance)
            }
          }
        }
      }
    }
  }

  /**
   * @description 查询某一个 [walletAddress] 在 [contractAddress] 下是否存有余额
   */
  @JvmStatic
  fun getAddressInfoWithTokenBalance(contractAddress: String, walletAddress: String) {
    getTokenBalanceWithContract(contractAddress, walletAddress) { tokenBalance ->
      getTokenSymbol(contractAddress) { symbol ->
        getTokenName(contractAddress) { name ->
          getTokenDecimal(contractAddress) { decimal ->
            getTokenTotalSupply(contractAddress) { totalSupply ->
              // 用的时候再完善这里
              System.out.println(symbol + name + decimal + totalSupply + tokenBalance)
            }
          }
        }
      }
    }
  }

  @JvmStatic
  fun getTokenBalanceWithContract(contractAddress: String, address: String, holdValue: (Double) -> Unit) {
    RequestBody.create(contentType,
      "{\"jsonrpc\":\"2.0\", \"method\":\"${Method.GetTokenBalance.method}\", \"params\":[{ \"to\": \"$contractAddress\", \"data\": \"${Method.GetTokenBalance.code withAddress address}\"}, \"latest\"], \"id\":1}"
    ).let {
      callEthBy(it) { holdValue(it.hexToDecimal()) }
    }
  }

  @JvmStatic
  fun getTokenSymbol(contractAddress: String, holdValue: (String) -> Unit = {}) {
    RequestBody.create(contentType,
      "{\"jsonrpc\":\"2.0\", \"method\":\"${Method.GetSymbol.method}\", \"params\":[{ \"to\": \"$contractAddress\", \"data\": \"${Method.GetSymbol.code}\"}, \"latest\"], \"id\":1}"
    ).let {
      callEthBy(it) { holdValue(it.toAscii()) }
    }
  }

  @JvmStatic
  fun getTokenDecimal(contractAddress: String, holdValue: (Double) -> Unit = {}) {
    RequestBody.create(contentType,
      "{\"jsonrpc\":\"2.0\", \"method\":\"${Method.GetSymbol.method}\", \"params\":[{ \"to\": \"$contractAddress\", \"data\": \"${Method.GetTokenDecimal.code}\"}, \"latest\"], \"id\":1}"
    ).let {
      callEthBy(it) { holdValue(it.hexToDecimal()) }
    }
  }

  @JvmStatic
  fun getTokenName(contractAddress: String, holdValue: (String) -> Unit = {}) {
    RequestBody.create(contentType,
      "{\"jsonrpc\":\"2.0\", \"method\":\"${Method.GetTokenName.method}\", \"params\":[{ \"to\": \"$contractAddress\", \"data\": \"${Method.GetTokenName.code}\"}, \"latest\"], \"id\":1}"
    ).let {
      callEthBy(it) { holdValue(it.toAscii()) }
    }
  }

  fun getEthBalance(address: String, holdValue: (Double) -> Unit = {}) {
    RequestBody.create(contentType,
      "{\"jsonrpc\":\"2.0\", \"method\":\"${Method.GetBalance.method}\", \"params\":[\"$address\", \"latest\"],\"id\":1}"
    ).let {
      callEthBy(it) { holdValue(it.hexToDecimal()) }
    }
  }

  fun getTokenTotalSupply(contractAddress: String, holdValue: (Double) -> Unit = {}) {
    RequestBody.create(contentType,
      "{\"jsonrpc\":\"2.0\", \"method\":\"${Method.GetTotalSupply.method}\", \"params\":[{ \"to\": \"$contractAddress\", \"data\": \"${Method.GetTotalSupply.code}\"}, \"latest\"], \"id\":1}"
    ).let {
      callEthBy(it) { holdValue(it.hexToDecimal()) }
    }
  }

  private fun callEthBy(body: RequestBody, hold: (String) -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder()
      .url(APIPath.ropstan)
      .method("POST", body)
      .header("Content-type", "application/json")
      .build()

    client.newCall(request).enqueue(object : Callback {
      override fun onFailure(call: Call, error: IOException) {
        System.out.println("$error")
      }

      @SuppressLint("SetTextI18n")
      override fun onResponse(call: Call, response: Response) {
        val data = response.body()?.string()
        val dataObject = JSONObject(data?.substring(data.indexOf("{"), data.lastIndexOf("}") + 1))
        hold(dataObject["result"].toString())
      }
    })
  }
}