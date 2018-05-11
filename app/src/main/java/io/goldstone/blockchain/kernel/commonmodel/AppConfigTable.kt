package io.goldstone.blockchain.kernel.commonmodel

import android.annotation.SuppressLint
import android.arch.persistence.room.*
import android.provider.Settings
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.common.value.HoneyLanguage
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 23/04/2018 2:42 PM
 * @author KaySaith
 * @important
 * [deviceID] 这个 ID 是自身业务服务器和客户端用来做
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
	var isRegisteredAddresses: Boolean = false,
	var language: Int = HoneyLanguage.getLanguageCodeBySymbol(CountryCode.currentLanguageSymbol),
	var currencyCode: String = CountryCode.currentCurrency,
	var pushToken: String = ""
) {

	companion object {
		fun getAppConfig(hold: (AppConfigTable?) -> Unit) {
			coroutinesTask({
				GoldStoneDataBase.database.appConfigDao()
					.getAppConfig()
			}) {
				it.isNotEmpty() isTrue {
					hold(it[0])
				} otherwise {
					hold(null)
				}
			}
		}

		fun updatePinCode(
			newPinCode: Int,
			callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao()
					.apply {
						getAppConfig().let {
							it.isNotEmpty() isTrue {
								update(it[0].apply { it[0].pincode = newPinCode })
								GoldStoneAPI.context.runOnUiThread {
									callback()
								}
							}
						}
					}
			}
		}

		fun updatePushToken(token: String) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao()
					.apply {
						getAppConfig().let {
							it[0].pushToken = token
						}
					}
			}
		}

		fun updateRegisterAddressesStatus(
			isRegistered: Boolean,
			callback: () -> Unit = {}
		) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao()
					.apply {
						getAppConfig().let {
							it.isNotEmpty() isTrue {
								update(it[0].apply { it[0].isRegisteredAddresses = isRegistered })
								GoldStoneAPI.context.runOnUiThread {
									callback()
								}
							}
						}
					}
			}
		}

		fun updateRetryTimes(
			times: Int,
			callback: () -> Unit = {}
		) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao()
					.apply {
						getAppConfig().let {
							it.isNotEmpty() isTrue {
								update(it[0].apply { it[0].retryTimes = times })
								GoldStoneAPI.context.runOnUiThread {
									callback()
								}
							}
						}
					}
			}
		}

		fun setFrozenTime(
			frozenTime: Long?,
			callback: () -> Unit = {}
		) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao()
					.apply {
						getAppConfig().let {
							it.isNotEmpty() isTrue {
								update(it[0].apply { this.frozenTime = frozenTime })
								GoldStoneAPI.context.runOnUiThread { callback() }
							}
						}
					}
			}
		}

		fun setShowPinCodeStatus(
			status: Boolean,
			callback: () -> Unit
		) {
			AppConfigTable.getAppConfig {
				it?.let {
					doAsync {
						GoldStoneDataBase.database.appConfigDao()
							.update(it.apply { showPincode = status })
						GoldStoneAPI.context.runOnUiThread {
							callback()
						}
					}
				}
			}
		}

		fun updateLanguage(
			code: Int,
			callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao()
					.apply {
						getAppConfig().let {
							update(it[0].apply { language = code })
							GoldStoneAPI.context.runOnUiThread {
								callback()
							}
						}
					}
			}
		}

		fun updateCurrency(
			code: String,
			callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao()
					.apply {
						getAppConfig().let {
							update(it[0].apply { currencyCode = code })
							GoldStoneAPI.context.runOnUiThread { callback() }
						}
					}
			}
		}

		@SuppressLint("HardwareIds")
		fun insertAppConfig(callback: () -> Unit) {
			doAsync {
				val goldStoneID = Settings.Secure.getString(
					GoldStoneAPI.context.contentResolver,
					Settings.Secure.ANDROID_ID
				) + System.currentTimeMillis()
				GoldStoneDataBase.database.appConfigDao()
					.insert(
						AppConfigTable(
							0,
							null,
							false,
							null,
							5,
							goldStoneID
						)
					)
				GoldStoneAPI.context.runOnUiThread { callback() }
			}
		}
	}
}

@Dao
interface AppConfigDao {

	@Query("SELECT * FROM appConfig")
	fun getAppConfig(): List<AppConfigTable>

	@Insert
	fun insert(appConfigTable: AppConfigTable)

	@Update
	fun update(appConfigTable: AppConfigTable)

	@Delete
	fun delete(appConfigTable: AppConfigTable)
}