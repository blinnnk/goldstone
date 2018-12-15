package io.goldstone.blockchain.module.home.dapp.common

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.support.annotation.UiThread
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toIntOrZero
import com.blinnnk.util.SystemUtils
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.component.overlay.Dashboard
import io.goldstone.blockchain.common.component.overlay.LoadingView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.AesCrypto
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.crypto.eos.EOSTransactionMethod
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.base.showDialog
import io.goldstone.blockchain.crypto.eos.contract.EOSContractCaller
import io.goldstone.blockchain.crypto.eos.ecc.Sha256
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.multichain.getAddress
import io.goldstone.blockchain.kernel.commontable.MyTokenTable
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.EOSMethod
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.presenter.PaymentDetailPresenter
import org.jetbrains.anko.matchParent
import org.json.JSONObject

/**
 * @author KaySaith
 * @date  2018/11/29
 */
@Suppress("DEPRECATION")
@SuppressLint("SetJavaScriptEnabled", "ViewConstructor")
class DAPPBrowser(context: Context, url: String, hold: (progress: Int) -> Unit) : WebView(context) {
	private val loadingView = LoadingView(context)
	private val jsInterface = JSInterface()
	private val account = SharedAddress.getCurrentEOSAccount()
	private val chainID = SharedChain.getEOSCurrent().chainID

	init {
		webViewClient = WebViewClient()
		settings.javaScriptEnabled = true
		addJavascriptInterface(jsInterface, "control")
		settings.javaScriptCanOpenWindowsAutomatically = true
		settings.domStorageEnabled = true
		settings.databaseEnabled = true
		settings.allowFileAccess = true
		settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
		settings.useWideViewPort = true
		setLayerType(View.LAYER_TYPE_HARDWARE, null)
		settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
		settings.setAppCacheEnabled(true)
		settings.setAppCacheMaxSize(15 * 1024 * 1024)
		settings.cacheMode = WebSettings.LOAD_DEFAULT

		this.loadUrl(url)
		layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
		webChromeClient = object : WebChromeClient() {
			override fun onProgressChanged(view: WebView?, newProgress: Int) {
				super.onProgressChanged(view, newProgress)
				hold(newProgress)
				fun evaluateJS() {
					view?.evaluateJavascript("javascript:(function(){" +
						"scatter={connect:function(data){return new Promise(function(resolve,reject){resolve(true)})},getIdentity:function(data){return new Promise(function(resolve,reject){identity={accounts:[{'authority':'active','blockchain':'eos','name':'${account.name}'}]};resolve(identity)})},identity:{accounts:[{'authority':'active','blockchain':'eos','name':'${account.name}'}]},forgetIdentity:function(){currentAccount=null;return new Promise(function(resolve,reject){accounts=null;resolve()})},suggestNetwork:function(data){return new Promise(function(resolve,reject){resolve(true)})},txID:null,arbSignature:null,balance:null,tableRow:null,accountInfo:null,interval:null,socketInterval:null,eos:function(){return{transaction:function(action){window.control.transferEOS(JSON.stringify(action.actions[0]));return new Promise(function(resolve,reject){window.scatter.interval=setInterval(function(){if(window.scatter.txID!==null){if(window.scatter.txID==='failed'){reject(window.scatter.txID)}else{resolve(window.scatter.txID)};window.scatter.txID=null;clearInterval(window.scatter.interval)}},1500)})},getTableRows:function(data){window.control.getTableRows(JSON.stringify(data));return new Promise(function(resolve,reject){window.scatter.interval=setInterval(function(){if(window.scatter.tableRow!==null){if(window.scatter.tableRow==='failed'){reject(window.scatter.tableRow)}else{resolve(window.scatter.tableRow)};window.scatter.tableRow=null;clearInterval(window.scatter.interval)}},1500)})},getAccount:function(data){window.control.getEOSAccountInfo(JSON.stringify(data));return new Promise(function(resolve,reject){window.scatter.interval=setInterval(function(){if(window.scatter.accountInfo!==null){if(window.scatter.accountInfo==='failed'){reject(window.scatter.accountInfo)}else{resolve(window.scatter.accountInfo)};window.scatter.accountInfo=null;clearInterval(window.scatter.interval)}},1500)})},getCurrencyBalance:function(code,name){window.control.getEOSAccountBalance(code,name);return new Promise(function(resolve,reject){window.scatter.socketInterval=setInterval(function(){if(window.scatter.balance!==null){if(window.scatter.balance==='failed'){reject(window.scatter.balance)}else{resolve(window.scatter.balance)};window.scatter.balance=null;clearInterval(window.scatter.socketInterval)}},2000)})},contract:function(data){return new Promise(function(resolve,reject){resolve({transfer:function(fromAccount,toAccount,quantity,memo){console.log(\"contract transfer\"+memo);var transferAction;if(toAccount!==undefined&&quantity!==undefined&&memo!==undefined){transferAction={from:fromAccount,to:toAccount,quantity:quantity,memo:memo}}else{transferAction=fromAccount};window.control.simpleTransfer(JSON.stringify(transferAction));return new Promise(function(resolve,reject){window.scatter.interval=setInterval(function(){if(window.scatter.txID!==null){if(window.scatter.txID==='failed'){reject(window.scatter.txID)}else{resolve(window.scatter.txID)};window.scatter.txID=null;clearInterval(window.scatter.interval)}},1500)})}})})}}},getArbitrarySignature:function(publicKey,data,whatFor,isHash){window.control.getArbSignature(data);return new Promise(function(resolve,reject){window.scatter.interval=setInterval(function(){console.log(window.scatter.arbSignature);if(window.scatter.arbSignature!==null){resolve(window.scatter.arbSignature);window.scatter.arbSignature=null;clearInterval(window.scatter.interval)}},1500)})}};" +
						"event=document.createEvent('HTMLEvents');" +
						"event.initEvent('scatterLoaded',true,true);" +
						"document.dispatchEvent(event);" +
						"})()", null)
				}
				evaluateJS()
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
		// 销毁的时候清理用于接收 Promise 而设定的 Interval
		evaluateJavascript("javascript:(function(){clearInterval(window.scatter.interval);})()", null)
		destroy()
	}

	fun backEvent(callback: () -> Unit) {
		jsInterface.backEvent(callback)
	}

	inner class JSInterface {

		@JavascriptInterface
		fun getEOSAccountBalance(code: String, accountName: String) {
			// Scatter 合约的方法, 有传回 `Code` 这里目前暂时只支持查询了 `EOS Balance`
			Log.d("Scatter", code)
			EOSAPI.getAccountEOSBalance(EOSAccount(accountName)) { balance, error ->
				launchUI {
					if (balance.isNotNull() && error.isNone()) {
						evaluateJavascript("javascript:(function(){scatter.balance=\"$balance\"})()", null)
					} else {
						evaluateJavascript("javascript:(function(){scatter.balance=\"failed\"})()", null)
					}
				}
			}
		}

		@JavascriptInterface
		fun getEOSAccountInfo(accountName: String) {
			launchUI {
				val accountObject = try {
					JSONObject(accountName)
				} catch (error: Exception) {
					evaluateJavascript("javascript:(function(){scatter.accountInfo=\"failed\"})()", null)
					println("GoldStone-DAPP Get AccountERROR: ${error.message}\n DATA: $accountName")
					return@launchUI
				}
				// Scatter 合约的方法, 有传回 `Code` 这里目前暂时只支持查询了 `EOS Balance`
				EOSAPI.getStringAccountInfo(EOSAccount(accountObject.safeGet("account_name"))) { accountInfo, error ->
					launchUI {
						if (accountInfo.isNotNull() && error.isNone()) {
							evaluateJavascript("javascript:(function(){scatter.accountInfo=$accountInfo})()", null)
						} else {
							evaluateJavascript("javascript:(function(){scatter.accountInfo=\"failed\"})()", null)
						}
					}
				}
			}
		}

		@JavascriptInterface
		fun getEOSAccountPermissions() {
			load {
				EOSAccountTable.getPermissions(account, chainID)
			} then { permissions ->
				val list = "[${permissions.joinToString(",") { it.generateObject() }}]"
				callWeb("getPermissions", list)
			}
		}

		@JavascriptInterface
		fun getTableRows(data: String) {
			launchUI {
				val tableObject = try {
					JSONObject(data)
				} catch (error: Exception) {
					evaluateJavascript("javascript:(function(){scatter.tableRow=\"failed\"})()", null)
					println("GoldStone-DAPP Get Table Row ERROR: ${error.message}\n DATA: $data")
					return@launchUI
				}
				val option =
					if (tableObject.safeGet("lower_bound").isNotEmpty()) Pair("lower_bound", tableObject.safeGet("lower_bound"))
					else null
				val limit =
					if (tableObject.safeGet("limit").isNotEmpty()) Pair("limit", tableObject.safeGet("limit").toIntOrZero())
					else null

				val indexPosition =
					if (tableObject.safeGet("index_position").isNotEmpty()) Pair("index_position", tableObject.safeGet("index_position").toIntOrZero())
					else null

				val keyType =
					if (tableObject.safeGet("key_type").isNotEmpty()) Pair("key_type", tableObject.safeGet("key_type"))
					else null

				EOSAPI.getTableRows(
					tableObject.safeGet("scope"),
					tableObject.safeGet("code"),
					tableObject.safeGet("table"),
					option,
					limit,
					indexPosition,
					keyType
				) { result, error ->
					launchUI {
						if (result.isNotNull() && error.isNone()) {
							evaluateJavascript("javascript:(function(){scatter.tableRow=$result})()", null)
						} else {
							evaluateJavascript("javascript:(function(){scatter.tableRow=\"failed\"})()", null)
						}
					}
				}
			}
		}

		@JavascriptInterface
		fun getArbSignature(data: String) {
			launchUI {
				Dashboard(context) {
					showAlert(
						"Signed Data Request",
						"Current DAPP request your sign data to verify your account, this behavior doesn't need any pay."
					) {
						PaymentDetailPresenter.showGetPrivateKeyDashboard(
							context,
							confirmEvent = {
								loadingView.show()
							}
						) { privateKey, error ->
							launchUI {
								loadingView.remove()
							}
							if (privateKey.isNotNull() && error.isNone()) {
								val signature = privateKey.sign(Sha256.from(data.toByteArray())).toString()
								launchUI {
									evaluateJavascript("javascript:(function(){scatter.arbSignature=\"$signature\"})()", null)
								}
							}
						}
					}
				}
			}
		}

		@JavascriptInterface
		fun transferEOS(action: String) {
			launchUI {
				val actionObject = try {
					JSONObject(action)
				} catch (error: Exception) {
					evaluateJavascript("javascript:(function(){scatter.txID=\"failed\"})()", null)
					println("GoldStone-DAPP Transfer EOS ERROR: $error\n DATA: $action")
					return@launchUI
				}
				if (actionObject.safeGet("name").equals(EOSTransactionMethod.Transfer.value, true)) {
					scatterEOSTransaction(actionObject)
				} else {
					scatterSignOperation(actionObject)
				}
			}
		}

		private val scatterSignOperation: (action: JSONObject) -> Unit = { action ->
			showOperationDashboard(
				action,
				cancelEvent = {
					loadingView.remove()
					evaluateJavascript("javascript:(function(){scatter.txID=\"failed\"})()", null)
				},
				confirmEvent = {
					PaymentDetailPresenter.showGetPrivateKeyDashboard(
						context,
						cancelEvent = { loadingView.remove() },
						confirmEvent = { loadingView.show() }
					) { privateKey, error ->
						if (privateKey.isNotNull() && error.isNone()) {
							EOSContractCaller(action, ChainID.EOS).send(privateKey) { response, pushTransactionError ->
								launchUI {
									loadingView.remove()
									if (response.isNotNull() && pushTransactionError.isNone()) {
										response.showDialog(context)
									} else {
										ErrorDisplayManager(pushTransactionError).show(context)
									}
								}
							}
						} else launchUI {
							loadingView.remove()
							evaluateJavascript("javascript:(function(){scatter.txID=\"failed\"})()", null)
						}
					}
				}
			)
		}

		private val scatterEOSTransaction: (action: JSONObject) -> Unit = { action ->
			showQuickPaymentDashboard(
				action,
				false,
				cancelEvent = {
					evaluateJavascript("javascript:(function(){scatter.txID=\"failed\"})()", null)
				},
				confirmEvent = { loadingView.show() }
			) { response, error ->
				launchUI {
					loadingView.remove()
					if (response.isNotNull() && error.isNone()) {
						response.showDialog(context)
						evaluateJavascript("javascript:(function(){scatter.txID=\"${response.transactionID}\"})()", null)
					} else {
						ErrorDisplayManager(error).show(context)
						evaluateJavascript("javascript:(function(){scatter.txID=\"failed\"})()", null)
					}
				}
			}
		}

		/**
		 * action like {"from":"beautifulleo","to":"betlottoinst","quantity":"0.1000 EOS","memo":"1|1029338"}
		 */
		@JavascriptInterface
		fun simpleTransfer(action: String) {
			launchUI {
				val actionObject = try {
					JSONObject(action)
				} catch (error: Exception) {
					evaluateJavascript("javascript:(function(){scatter.txID=\"failed\"})()", null)
					println("GoldStone-DAPP Transfer EOS ERROR: $error\n DATA: $action")
					return@launchUI
				}
				showQuickPaymentDashboard(
					actionObject,
					true,
					cancelEvent = {
						evaluateJavascript("javascript:(function(){scatter.txID=\"failed\"})()", null)
					},
					confirmEvent = { loadingView.show() }
				) { response, error ->
					launchUI {
						if (response.isNotNull() && error.isNone()) {
							response.showDialog(context)
							evaluateJavascript("javascript:(function(){scatter.txID=\"${response.transactionID}\"})()", null)
						} else {
							ErrorDisplayManager(error).show(context)
							evaluateJavascript("javascript:(function(){scatter.txID=\"failed\"})()", null)
						}
					}
				}
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
		fun getGoldStoneID() {
			load {
				SharedWallet.getGoldStoneID()
			} then {
				evaluateJavascript("javascript:getGoldStoneID(\"$it\")", null)
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
		fun getVersionName() {
			load {
				SystemUtils.getVersionCode(context)
			} then {
				callWeb("getVersionName", "$it")
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
		fun getSignHeader(timeStamp: String) {
			load {
				val goldStoneID = SharedWallet.getGoldStoneID()
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
			System.out.println(data)
			launchUI {
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
		}

		@JavascriptInterface
		fun backEvent(callback: () -> Unit) {
			evaluateJavascript("javascript:backEvent()") {
				if (it.equals("\"finished\"", true)) callback()
			}
		}
	}
}

fun WebView.callWeb(methodName: String, value: String) {
	return evaluateJavascript("javascript:$methodName(\"${Uri.encode(value)}\")", null)
}