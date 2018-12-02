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
import io.goldstone.blockchain.common.component.overlay.Dashboard
import io.goldstone.blockchain.common.component.overlay.LoadingView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.AesCrypto
import io.goldstone.blockchain.crypto.eos.contract.EOSContractCaller
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.multichain.getAddress
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.presenter.PaymentDetailPresenter
import org.jetbrains.anko.matchParent
import org.json.JSONObject


/**
 * @author KaySaith
 * @date  2018/11/29
 */
@SuppressLint("SetJavaScriptEnabled", "ViewConstructor")
class DAppBrowser(context: Context, url: String) : WebView(context) {
	private val loadingView = LoadingView(context)
	private val jsInterface = JSInterface()

	init {
		settings.javaScriptEnabled = true
		webViewClient = WebViewClient()
		addJavascriptInterface(jsInterface, "control")
		this.loadUrl(url)
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

	fun backEvent(callback: () -> Unit) {
		jsInterface.backEvent(callback)
	}

	inner class JSInterface {
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
				val version = SystemUtils.getVersionCode(GoldStoneAPI.context).toString()
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