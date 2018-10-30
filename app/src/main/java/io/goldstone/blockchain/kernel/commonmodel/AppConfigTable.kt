package io.goldstone.blockchain.kernel.commonmodel

import android.annotation.SuppressLint
import android.arch.persistence.room.*
import android.provider.Settings
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import com.blinnnk.extension.safeGet
import com.blinnnk.util.convertLocalJsonFileToJSONObjectArray
import io.goldstone.blockchain.R.raw.terms
import io.goldstone.blockchain.common.language.HoneyLanguage
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.uiThread

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
	var frozenTime: Long? = null,
	var retryTimes: Int = 5,
	var goldStoneID: String = "",
	var isRegisteredAddresses: Boolean = false, // For Push
	var language: Int = HoneyLanguage.getCodeBySymbol(CountryCode.currentLanguageSymbol),
	var currencyCode: String = CountryCode.currentCurrency,
	var pushToken: String = "",
	var isMainnet: Boolean = true,
	var shareContent: String = ProfileText.shareContent,
	var terms: String = "",
	var defaultCoinListMD5: String,
	var exchangeListMD5: String,
	var nodeListMD5: String
) {

	companion object {
		fun getAppConfig(hold: (AppConfigTable?) -> Unit) {
			load {
				GoldStoneDataBase.database.appConfigDao().getAppConfig()
			} then (hold)
		}

		fun updatePinCode(newPinCode: Int, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().updatePincode(newPinCode)
				GoldStoneAPI.context.runOnUiThread {
					callback()
				}
			}
		}

		fun updatePushToken(token: String) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().updatePushToken(token)
			}
		}

		fun updateRegisterAddressesStatus(isRegistered: Boolean, callback: () -> Unit = {}) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().updateHasRegisteredAddress(isRegistered)
				GoldStoneAPI.context.runOnUiThread {
					callback()
				}
			}
		}

		fun updateRetryTimes(times: Int) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().updateRetryTimes(times)
			}
		}

		fun setFrozenTime(frozenTime: Long?, callback: () -> Unit = {}) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().updateFrozenTime(frozenTime.orElse(0L))
				GoldStoneAPI.context.runOnUiThread { callback() }
			}
		}

		fun setShowPinCodeStatus(status: Boolean, callback: () -> Unit) {
			AppConfigTable.getAppConfig { it ->
				it?.let {
					doAsync {
						GoldStoneDataBase.database.appConfigDao().update(
							it.apply {
								showPincode = status
								if (!status) pincode = null
							}
						)
						GoldStoneAPI.context.runOnUiThread {
							callback()
						}
					}
				}
			}
		}

		fun updateLanguage(code: Int, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().updateLanguageCode(code)
				GoldStoneAPI.context.runOnUiThread {
					callback()
				}
			}
		}

		fun updateChainStatus(isMainnet: Boolean, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().updateChainStatus(isMainnet)
				GoldStoneAPI.context.runOnUiThread {
					callback()
				}
			}
		}

		fun updateCurrency(code: String, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().updateCurrency(code)
				uiThread { callback() }
			}
		}

		@SuppressLint("HardwareIds")
		fun insertAppConfig(@WorkerThread callback: (AppConfigTable) -> Unit) {
			val goldStoneID =
				Settings.Secure.getString(GoldStoneAPI.context.contentResolver, Settings.Secure.ANDROID_ID) + System.currentTimeMillis()

			val config = AppConfigTable(
				0,
				goldStoneID = goldStoneID,
				language = HoneyLanguage.getCodeBySymbol(CountryCode.currentLanguageSymbol),
				terms = getLocalTerms(),
				isMainnet = true,
				defaultCoinListMD5 = "",
				exchangeListMD5 = "",
				nodeListMD5 = ""
			)
			GoldStoneDataBase.database.appConfigDao().insert(config)
			callback(config)
		}

		private fun getLocalTerms(): String {
			GoldStoneAPI.context.convertLocalJsonFileToJSONObjectArray(terms).let { localTerms ->
				localTerms.find {
					it.safeGet("language").equals(CountryCode.currentLanguageSymbol, true)
				}.let { it ->
					return if (it.isNull()) {
						localTerms.find {
							it.safeGet("language").equals(HoneyLanguage.English.symbol, true)
						}?.safeGet("terms").orEmpty()
					} else {
						return it!!.safeGet("terms")
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

	@Query("UPDATE appConfig SET currencyCode = :newCurrencyCode WHERE id = 1")
	fun updateCurrency(newCurrencyCode: String)

	@Query("UPDATE appConfig SET pincode = :pinCode WHERE id = 1")
	fun updatePincode(pinCode: Int)

	@Query("UPDATE appConfig SET language = :code WHERE id = 1")
	fun updateLanguageCode(code: Int)

	@Query("UPDATE appConfig SET isRegisteredAddresses = :status WHERE id = 1")
	fun updateHasRegisteredAddress(status: Boolean)

	@Query("UPDATE appConfig SET pushToken = :token WHERE id = 1")
	fun updatePushToken(token: String)

	@Query("UPDATE appConfig SET retryTimes = :times WHERE id = 1")
	fun updateRetryTimes(times: Int)

	@Query("UPDATE appConfig SET nodeListMD5 = :md5 WHERE id = 1")
	fun updateNodeListMD5(md5: String)

	@Query("UPDATE appConfig SET isMainnet = :isMainnet WHERE id = 1")
	fun updateChainStatus(isMainnet: Boolean)

	@Query("UPDATE appConfig SET defaultCoinListMD5 = :md5 WHERE id = 1")
	fun updateDefaultMD5(md5: String)

	@Query("UPDATE appConfig SET frozenTime = :time WHERE id = 1")
	fun updateFrozenTime(time: Long)

	@Query("UPDATE appConfig SET shareContent = :content WHERE id = 1")
	fun updateShareContent(content: String)

	@Query("UPDATE appConfig SET terms = :content WHERE id = 1")
	fun updateTerms(content: String)

	@Insert
	fun insert(appConfigTable: AppConfigTable)

	@Update
	fun update(appConfigTable: AppConfigTable)

	@Delete
	fun delete(appConfigTable: AppConfigTable)
}