package io.goldstone.blockchain.kernel.commontable

import android.annotation.SuppressLint
import android.arch.persistence.room.*
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.safeGet
import com.blinnnk.util.convertLocalJsonFileToJSONObjectArray
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.R.raw.terms
import io.goldstone.blockchain.common.language.HoneyLanguage
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.utils.ApkUtil
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import kotlinx.coroutines.*

/**
 * @date 23/04/2018 2:42 PM
 * @author KaySaith
 * @important
 * [goldStoneID] 这个 ID 是自身业务服务器和客户端用来做
 * 唯一校验的值, 不是常规意义的 `Device ID`
 */
@Entity(tableName = "appConfig")
data class AppConfigTable(
	@PrimaryKey(autoGenerate = true)
	var id: Int,
	var pincode: Int? = null,
	var showPincode: Boolean = false,
	var frozenTime: Long,
	var retryTimes: Int = 5,
	var goldStoneID: String = "",
	var isRegisteredAddresses: Boolean = false, // For Push
	var language: Int = HoneyLanguage.getCodeBySymbol(CountryCode.currentLanguageSymbol),
	var currencyCode: String = CountryCode.currentCurrency,
	var pushToken: String = "",
	var jsCode: String = "",
	var isMainnet: Boolean = true,
	var shareContent: String = ProfileText.shareContent,
	var terms: String = "",
	var defaultCoinListMD5: String,
	var exchangeListMD5: String,
	var nodeListMD5: String,
	var termMD5: String,
	var configMD5: String,
	var shareContentMD5: String,
	var dappRecommendMD5: String,
	var newDAPPMD5: String,
	var dappJSCodeMD5: String
) {

	companion object {
		@JvmField
		val dao = GoldStoneDataBase.database.appConfigDao()

		fun getAppConfig(thread: CoroutineDispatcher, hold: (AppConfigTable?) -> Unit) {
			GlobalScope.launch(Dispatchers.Default) {
				val config = dao.getAppConfig()
				withContext(thread) {
					hold(config)
				}
			}
		}

		fun setShowPinCodeStatus(status: Boolean, callback: (status: Boolean) -> Unit) {
			load { dao.updateShowPincodeStatus(status) } then { callback(status) }
		}

		@SuppressLint("HardwareIds")
		fun insertAppConfig(@WorkerThread callback: (AppConfigTable) -> Unit) {
			val config = AppConfigTable(
				0,
				frozenTime = 0L,
				goldStoneID = ApkUtil.generateGoldStoneID(),
				language = HoneyLanguage.getCodeBySymbol(CountryCode.currentLanguageSymbol),
				currencyCode = CountryCode.currentCurrency,
				terms = getLocalTerms(),
				jsCode = "{connect:function(data){return new Promise(function(resolve,reject){resolve(true)})},getIdentity:function(data){return new Promise(function(resolve,reject){identity={accounts:[{\"authority\":\"active\",\"blockchain\":'eos',\"name\":goldStoneAccountName}]};resolve(identity)})},identity:{accounts:[{\"authority\":\"active\",\"blockchain\":'eos',\"name\":goldStoneAccountName}]},forgetIdentity:function(){currentAccount=null;identity=null;return new Promise(function(resolve,reject){resolve()})},linkAccount:function(publicKey,network){console.log(\"linkAccount***\"+publicKey)},suggestNetwork:function(data){console.log(\"suggestNetwork***\");return new Promise(function(resolve,reject){resolve(true)})},isConnected:function(){return new Promise(function(resolve,reject){resolve(true)})},authenticate:function(nonce){console.log(\"authenticate***\"+nonce)},getOrRequestIdentity:function(data){console.log(\"getOrRequestIdentity***\"+JSON.stringify(data))},transactionResult:null,getIdentityResult:null,arbSignature:null,balance:null,tableRow:null,accountInfo:null,interval:null,tableRowInterval:null,accountInterval:null,balanceInterval:null,identityInterval:null,eos:function(data){console.log(\"scatter.eos\"+JSON.stringify(data));return{transaction:function(action){console.log(\"eos.transaction\"+JSON.stringify(action));window.control.transferEOS(JSON.stringify(action.actions[0]));return new Promise(function(resolve,reject){window.scatter.interval=setInterval(function(){if(window.scatter.transactionResult!==null){if(window.scatter.transactionResult==='failed'){reject(window.scatter.transactionResult)}else{resolve(window.scatter.transactionResult)};window.scatter.transactionResult=null;clearInterval(window.scatter.interval)}},1500)})},getRequiredKeys:function(){console.log(\"eos.getRequiredKeys ***\")},getAbi:function(data){console.log(\"eos.getAbi\");console.log(JSON.stringify(data));return new Promise(function(resolve,reject){resolve(\"get abi to do\")})},getTableRows:function(data){console.log(\"eos.getTableRows\");window.control.getTableRows(JSON.stringify(data));return new Promise(function(resolve,reject){window.scatter.tableRowInterval=setInterval(function(){if(window.scatter.tableRow!==null){if(window.scatter.tableRow==='failed'){reject(window.scatter.tableRow)}else{resolve(window.scatter.tableRow)};window.scatter.tableRow=null;clearInterval(window.scatter.tableRowInterval)}},1500)})},getAccount:function(data){console.log(\"eos.getAccount\");window.control.getEOSAccountInfo(JSON.stringify(data));return new Promise(function(resolve,reject){window.scatter.accountInterval=setInterval(function(){if(window.scatter.accountInfo!==null){if(window.scatter.accountInfo==='failed'){reject(window.scatter.accountInfo)}else{resolve(window.scatter.accountInfo)};clearInterval(window.scatter.accountInterval);window.scatter.accountInfo=null}},1500)})},getCurrencyBalance:function(code,name,symbol){console.log(\"eos.getCurrencyBalance\");window.control.getEOSAccountBalance(code,name,symbol);return new Promise(function(resolve,reject){window.scatter.balanceInterval=setInterval(function(){if(window.scatter.balance!==null){if(window.scatter.balance==='failed'){reject(window.scatter.balance)}else{resolve(window.scatter.balance)};clearInterval(window.scatter.balanceInterval);window.scatter.balance=null}},2000)})},getInfo:function(data){console.log(\"eos.getInfo\"+JSON.stringify(data))},contract:function(data){console.log(\"eos.contract\");console.log(JSON.stringify(data)+\"eos.contract\");return new Promise(function(resolve,reject){resolve({transfer:function(fromAccount,toAccount,quantity,memo){console.log(\"contract transfer\"+memo);var transferAction;if(toAccount!==undefined&&quantity!==undefined&&memo!==undefined){transferAction={from:fromAccount,to:toAccount,quantity:quantity,memo:memo}}else{transferAction=fromAccount};window.control.simpleTransfer(JSON.stringify(transferAction));return new Promise(function(resolve,reject){window.scatter.interval=setInterval(function(){if(window.scatter.transactionResult!==null){if(window.scatter.transactionResult==='failed'){reject(window.scatter.transactionResult)}else{resolve(window.scatter.transactionResult)};window.scatter.transactionResult=null;clearInterval(window.scatter.interval)}},1500)})}})})}}},getArbitrarySignature:function(publicKey,data,whatFor,isHash){console.log(\"eos.getArbitrarySignature\");window.control.getArbSignature(data);return new Promise(function(resolve,reject){window.scatter.interval=setInterval(function(){if(window.scatter.arbSignature!==null){resolve(window.scatter.arbSignature);window.scatter.arbSignature=null;clearInterval(window.scatter.interval)}},1500)})}}",
				defaultCoinListMD5 = "",
				exchangeListMD5 = "",
				nodeListMD5 = "",
				termMD5 = "",
				configMD5 = "",
				shareContentMD5 = "",
				dappRecommendMD5 = "",
				newDAPPMD5 = "",
				dappJSCodeMD5 = ""
			)
			dao.insert(config)
			callback(config)
		}

		private fun getLocalTerms(): String {
			GoldStoneApp.appContext.convertLocalJsonFileToJSONObjectArray(terms).let { localTerms ->
				localTerms.find {
					it.safeGet("language").equals(CountryCode.currentLanguageSymbol, true)
				}.let { data ->
					return if (data.isNull()) {
						localTerms.find {
							it.safeGet("language").equals(HoneyLanguage.English.symbol, true)
						}?.safeGet("terms").orEmpty()
					} else {
						return data.safeGet("terms")
					}
				}
			}
		}
	}
}

@Dao
interface AppConfigDao {

	@Query("SELECT * FROM appConfig LIMIT 1")
	fun getAppConfig(): AppConfigTable?

	@Query("UPDATE appConfig SET currencyCode = :currencyCode WHERE id = 1")
	fun updateCurrency(currencyCode: String)

	@Query("UPDATE appConfig SET defaultCoinListMD5 = :defaultCoinListMD5 WHERE id = 1")
	fun updateDefaultTokenMD5(defaultCoinListMD5: String)

	@Query("UPDATE appConfig SET nodeListMD5 = :nodeListMD5 WHERE id = 1")
	fun updateNodesMD5(nodeListMD5: String)

	@Query("UPDATE appConfig SET exchangeListMD5 = :exchangeListMD5 WHERE id = 1")
	fun updateExchangeMD5(exchangeListMD5: String)

	@Query("UPDATE appConfig SET termMD5 = :termMD5 WHERE id = 1")
	fun updateTermMD5(termMD5: String)

	@Query("UPDATE appConfig SET shareContentMD5 = :shareContentMD5 WHERE id = 1")
	fun updateShareContentMD5(shareContentMD5: String)

	@Query("UPDATE appConfig SET dappJSCodeMD5 = :dappJSCodeMD5 WHERE id = 1")
	fun updateDAPPJSCodeMD5(dappJSCodeMD5: String)

	@Query("UPDATE appConfig SET dappRecommendMD5 = :dappRecommendMD5 WHERE id = 1")
	fun updateRecommendMD5(dappRecommendMD5: String)

	@Query("UPDATE appConfig SET newDAPPMD5 = :newDAPPMD5 WHERE id = 1")
	fun updateNewDAPPMD5(newDAPPMD5: String)

	@Query("UPDATE appConfig SET configMD5 = :configMD5 WHERE id = 1")
	fun updateConfigMD5(configMD5: String)

	@Query("UPDATE appConfig SET pincode = :pinCode WHERE id = 1")
	fun updatePincode(pinCode: Int)

	@Query("UPDATE appConfig SET showPincode = :status WHERE id = 1")
	fun updateShowPincodeStatus(status: Boolean)

	@Query("UPDATE appConfig SET language = :code WHERE id = 1")
	fun updateLanguageCode(code: Int)

	@Query("UPDATE appConfig SET isRegisteredAddresses = :status WHERE id = 1")
	fun updateHasRegisteredAddress(status: Boolean)

	@Query("UPDATE appConfig SET pushToken = :token WHERE id = 1")
	fun updatePushToken(token: String)

	@Query("UPDATE appConfig SET retryTimes = :times WHERE id = 1")
	fun updateRetryTimes(times: Int)

	@Query("UPDATE appConfig SET isMainnet = :isMainnet WHERE id = 1")
	fun updateChainStatus(isMainnet: Boolean)

	@Query("UPDATE appConfig SET frozenTime = :time WHERE id = 1")
	fun updateFrozenTime(time: Long)

	@Query("UPDATE appConfig SET shareContent = :content WHERE id = 1")
	fun updateShareContent(content: String)

	@Query("UPDATE appConfig SET jsCode = :code WHERE id = 1")
	fun updateJSCode(code: String)

	@Query("UPDATE appConfig SET terms = :content WHERE id = 1")
	fun updateTerms(content: String)

	@Insert
	fun insert(appConfigTable: AppConfigTable)

	@Update
	fun update(appConfigTable: AppConfigTable)

	@Delete
	fun delete(appConfigTable: AppConfigTable)
}