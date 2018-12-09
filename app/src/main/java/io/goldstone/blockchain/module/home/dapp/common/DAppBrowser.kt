package io.goldstone.blockchain.module.home.dapp.common

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import com.blinnnk.extension.isNotNull
import com.blinnnk.util.SystemUtils
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.component.overlay.Dashboard
import io.goldstone.blockchain.common.component.overlay.LoadingView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.AesCrypto
import io.goldstone.blockchain.common.utils.safeToJSONObject
import io.goldstone.blockchain.crypto.eos.contract.EOSContractCaller
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.multichain.getAddress
import io.goldstone.blockchain.kernel.commontable.MyTokenTable
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.presenter.PaymentDetailPresenter
import org.jetbrains.anko.matchParent
import org.json.JSONObject

/**
 * @author KaySaith
 * @date  2018/11/29
 */
@SuppressLint("SetJavaScriptEnabled", "ViewConstructor")
class DAPPBrowser(context: Context, url: String, hold: (progress: Int) -> Unit) : WebView(context) {
	private val loadingView = LoadingView(context)
	private val jsInterface = JSInterface()

	init {
		settings.javaScriptEnabled = true
		webViewClient = WebViewClient()
		addJavascriptInterface(jsInterface, "control")
		settings.domStorageEnabled = true
		settings.javaScriptCanOpenWindowsAutomatically = true
		settings.cacheMode = WebSettings.LOAD_NO_CACHE
		settings.domStorageEnabled = true
		settings.databaseEnabled = true
		settings.setAppCacheEnabled(true)
		settings.allowFileAccess = true
		settings.setSupportZoom(true)
		settings.builtInZoomControls = true
		settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
		settings.useWideViewPort = true

		this.loadUrl(url)
		layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
		webChromeClient = object : WebChromeClient() {
			override fun onProgressChanged(view: WebView?, newProgress: Int) {
				super.onProgressChanged(view, newProgress)
				hold(newProgress)
				val account = SharedAddress.getCurrentEOSAccount()
				fun evaluateJS() {
					view?.evaluateJavascript("javascript:(function(){" +
						"scatter={connect:function(data){return new Promise(function(resolve,reject){resolve(true)})},getIdentity:function(data){return new Promise(function(resolve,reject){resolve({accounts:[{'authority':'active','blockchain':'eos','name':'${account.name}'}]})})},identity:{accounts:[{'authority':'active','blockchain':'eos','name':'${account.name}'}]},suggestNetwork:function(data){return new Promise(function(resolve,reject){resolve(true)})},txID:undefined,interval:undefined,eos:function(){return{transaction:function(action){window.control.transferEOS(JSON.stringify(action.actions[0]));window.scatter.interval=setInterval(function(){console.log(window.scatter.txID);if(window.scatter.txID!==undefined){return new Promise(function(resolve,reject){resolve(window.scatter.txID);clearInterval(window.scatter.interval);window.scatter.txID=null})}},1000);},getTableRows:function(data){console.log('+++++'+JSON.stringify(data))},contract:function(data){return new Promise(function(resolve,reject){resolve(true)})}}},getArbitrarySignature:function(publicKey,data,whatFor,isHash){alert(publicKey+data+whatFor+isHash);return new Promise(function(resolve,reject){resolve(signature)})}};" +
						"event=document.createEvent('HTMLEvents');" +
						"event.initEvent('scatterLoaded',true,true);" +
						"document.dispatchEvent(event);" +
						"})()", null)
				}
				evaluateJS() // for auto login
				if (newProgress == 100) {
					evaluateJS() // for totally
				}
			}


			override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
				println("GoldStone-DAPP-Browser: ${consoleMessage?.message()}")
				return super.onConsoleMessage(consoleMessage)
			}

		}
	}

	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		System.out.println("++++")
		// 销毁的时候清理用于接收 Promise 而设定的 Interval
		evaluateJavascript("javascript:(function(){clearInterval(window.scatter.interval);})()", null)
	}

	fun backEvent(callback: () -> Unit) {
		jsInterface.backEvent(callback)
	}

	inner class JSInterface {

		@JavascriptInterface
		fun transferEOS(action: String) {
			showQuickPaymentDashboard(action.safeToJSONObject()) {
				System.out.println("hello test $it")
				evaluateJavascript("javascript:(function(){scatter.txID=\"$it\"})()", null)
			}
		}

		/**
		 * @Important
		 * 所有 `JSInterface` 的线程发起都在  `Thread JSInterFace` 线程, 所以需要
		 * 在发起的时候就进行 `UI `展示的时候需要首先声明为 `UI` 线程.
		 */
		@JavascriptInterface
		fun toastMessage(message: String) {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show()
		}

		@JavascriptInterface
		fun alert(title: String, message: String) {
			launchUI {
				Dashboard(context!!) {
					showAlert(
						title,
						message,
						CommonText.confirm,
						{ dialog.dismiss() }
					) {
						evaluateJavascript("javascript:showAlert(\"clickedConfirmButton\")", null)
					}
				}
			}
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
		fun getLanguageCode() {
			load {
				SharedWallet.getCurrentLanguageCode()
			} then {
				evaluateJavascript("javascript:getLanguageCode(\"$it\")", null)
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
				evaluateJavascript("javascript:decrypt(\"${Uri.encode(decryptData)}\")", null)
			}
		}

		@JavascriptInterface
		fun getSignHeader() {
			load {
				val goldStoneID = SharedWallet.getGoldStoneID()
				val timeStamp = System.currentTimeMillis().toString()
				val version = SystemUtils.getVersionCode(GoldStoneApp.appContext).toString()
				RequisitionUtil.getSignHeader(goldStoneID, timeStamp, version)
			} then { signData ->
				evaluateJavascript("javascript:getSignHeader(\"$signData\")", null)
			}
		}

		@JavascriptInterface
		fun getAccountAddress(contract: String, symbol: String, isEOSAccountName: Boolean) {
			load {
				TokenContract(contract, symbol, null).getAddress(isEOSAccountName)
			} then {
				evaluateJavascript("javascript:getAccountAddress(\"$it\")", null)
			}
		}

		@JavascriptInterface
		fun getBalance(contract: String, symbol: String) {
			launchUI {
				val tokenContract = TokenContract(contract, symbol, null)
				loadingView.show()
				MyTokenTable.getBalanceByContract(tokenContract) { balance, error ->
					launchUI {
						loadingView.remove()
						if (balance.isNotNull() && error.isNone()) {
							evaluateJavascript("javascript:getBalance(\"${Uri.encode(balance.toString())}\")", null)
						} else {
							evaluateJavascript("javascript:getBalance(\"${Uri.encode(error.message)}\")", null)
						}
					}
				}
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
						launchUI {
							loadingView.remove()
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

		@JavascriptInterface
		fun backEvent(callback: () -> Unit) {
			evaluateJavascript("javascript:backEvent()") {
				if (it.equals("\"finished\"", true)) callback()
			}
		}
	}
}