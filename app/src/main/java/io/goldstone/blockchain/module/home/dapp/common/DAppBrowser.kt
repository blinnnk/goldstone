package io.goldstone.blockchain.module.home.dapp.common

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.Toast
import com.blinnnk.extension.isNotNull
import com.blinnnk.util.SystemUtils
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.component.overlay.LoadingView
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.AesCrypto
import io.goldstone.blockchain.crypto.eos.contract.EOSContractCaller
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.presenter.PaymentDetailPresenter
import org.jetbrains.anko.matchParent
import org.json.JSONObject


/**
 * @author KaySaith
 * @date  2018/11/29
 */
@SuppressLint("SetJavaScriptEnabled")
class DAppBrowser(context: Context) : WebView(context) {
	private val loadingView = LoadingView(context)

	init {
		settings.javaScriptEnabled = true
		webViewClient = WebViewClient()
		addJavascriptInterface(JSInterface(), "control")
		this.loadUrl("http://192.168.64.2/site/dapp/index.html")
		layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
		layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
		webChromeClient = object : WebChromeClient() {
			override fun onProgressChanged(view: WebView?, newProgress: Int) {
				super.onProgressChanged(view, newProgress)
				if (newProgress == 100) {

				}
			}
		}
	}


	inner class JSInterface {

		@JavascriptInterface
		fun toastMessage(message: String) {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show()
		}

		@JavascriptInterface
		fun getChainID(chainType: Int) {
			load {
				ChainType(chainType)
			} then {
				evaluateJavascript("javascript:getChainID(\"${it.getChainURL().chainID.id}\")", null)
			}
		}

		@JavascriptInterface
		fun encrypt(data: String) {
			load {
				AesCrypto.encrypt(data).orEmpty()
			} then { cryptoData ->
				// 直接返回不成功, 不知道为什么,  这里转换一下就好了
				val result = cryptoData.substring(0, cryptoData.lastIndex)
				evaluateJavascript("javascript:encrypt(\"$result\")", null)
			}
		}

		@JavascriptInterface
		fun decrypt(data: String) {
			load {
				AesCrypto.decrypt(data).orEmpty()
			} then { decryptData ->
				// 直接返回不成功, 不知道为什么,  这里转换一下就好了
				val result = decryptData.substring(0, decryptData.length)
				evaluateJavascript("javascript:decrypt(\"$result\")", null)
			}
		}

		@JavascriptInterface
		fun getSignHeader() {
			load {
				val goldStoneID = SharedWallet.getGoldStoneID()
				val timeStamp = System.currentTimeMillis().toString()
				val version = SystemUtils.getVersionCode(GoldStoneAPI.context).toString()
				RequisitionUtil.getSignHeader(goldStoneID, timeStamp, version)
			} then { signData ->
				System.out.println("sign$signData")
				evaluateJavascript("javascript:getSignHeader(\"$signData\")", null)
			}
		}

		@JavascriptInterface
		fun getEOSSingedData(data: String) {
			PaymentDetailPresenter.showGetPrivateKeyDashboard(
				context,
				cancelEvent = { loadingView.remove() },
				confirmEvent = { loadingView.show() }
			) { privateKey, error ->
				if (privateKey.isNotNull() && error.isNone()) {
					EOSContractCaller(JSONObject(data)).getPushTransactionObject(privateKey) { pushJson, hashError ->
						loadingView.remove()
						launchUI {
							if (pushJson.isNotNull() && hashError.isNone()) {
								val result = Uri.encode(pushJson)
								evaluateJavascript("javascript:getEOSSignedData(\"$result\")", null)
							} else {
								evaluateJavascript("javascript:getEOSSignedData(\"${hashError.message}\")", null)
							}
						}
					}
				} else launchUI {
					loadingView.remove()
					evaluateJavascript("javascript:getEOSSignedData(\"${error.message}\")", null)
				}
			}
		}
	}
}