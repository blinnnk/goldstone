package io.goldstone.blockchain.kernel.commonmodel

import android.annotation.SuppressLint
import android.arch.persistence.room.*
import android.provider.Settings
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.blinnnk.extension.safeGet
import com.blinnnk.util.convertLocalJsonFileToJSONObjectArray
import io.goldstone.blockchain.R.raw.terms
import io.goldstone.blockchain.common.language.HoneyLanguage
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.crypto.multichain.ChainNameID
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

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
	var isRegisteredAddresses: Boolean = false,
	var language: Int = HoneyLanguage.getCodeBySymbol(CountryCode.currentLanguageSymbol),
	var currencyCode: String = CountryCode.currentCurrency,
	var pushToken: String = "",
	var isMainnet: Boolean = true,
	var shareContent: String = ProfileText.shareContent,
	var terms: String = "",
	var currentETCTestChainNameID: Int,
	var currentETHERC20AndETCTestChainNameID: Int,
	var currentBTCTestChainNameID: Int,
	var currentLTCTestChainNameID: Int,
	var currentBCHTestChainNameID: Int,
	var currentEOSTestChainNameID: Int,
	var currentETCChainNameID: Int,
	var currentBTCChainNameID: Int,
	var currentETHERC20AndETCChainNameID: Int,
	var currentBCHChainNameID: Int,
	var currentLTCChainNameID: Int,
	var currentEOSChainNameID: Int,
	var defaultCoinListMD5: String,
	var exchangeListMD5: String
) {

	companion object {
		fun getAppConfig(hold: (AppConfigTable?) -> Unit) {
			load {
				GoldStoneDataBase.database.appConfigDao().getAppConfig()
			} then {
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
				GoldStoneDataBase.database.appConfigDao().apply {
					getAppConfig().let {
						it.isNotEmpty() isTrue {
							update(it[0].apply { this.pincode = newPinCode })
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
				GoldStoneDataBase.database.appConfigDao().apply {
					getAppConfig().let {
						it.isNotEmpty() isTrue {
							update(it[0].apply { this.pushToken = token })
						}
					}
				}
			}
		}

		fun updateDefaultTokenMD5(md5: String) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().apply {
					getAppConfig().let {
						it.isNotEmpty() isTrue {
							update(it[0].apply { this.defaultCoinListMD5 = md5 })
						}
					}
				}
			}
		}
		
		fun updateExchangeListMD5(md5: String) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().apply {
					getAppConfig().firstOrNull()?.let {
						update(it.apply { this.exchangeListMD5 = md5 })
						
					}
				}
			}
		}

		fun updateRegisterAddressesStatus(isRegistered: Boolean, callback: () -> Unit = {}) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().apply {
					getAppConfig().let {
						it.isNotEmpty() isTrue {
							update(it[0].apply { this.isRegisteredAddresses = isRegistered })
							GoldStoneAPI.context.runOnUiThread {
								callback()
							}
						}
					}
				}
			}
		}

		fun updateRetryTimes(times: Int) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().apply {
					getAppConfig().let {
						it.isNotEmpty() isTrue {
							update(it[0].apply { this.retryTimes = times })
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
				GoldStoneDataBase.database.appConfigDao().apply {
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
			AppConfigTable.getAppConfig { it ->
				it?.let {
					doAsync {
						GoldStoneDataBase.database.appConfigDao().update(it.apply {
							showPincode = status
							if (!status) {
								pincode = null
							}
						})
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
				GoldStoneDataBase.database.appConfigDao().apply {
					getAppConfig().let {
						update(it[0].apply { this.language = code })
						GoldStoneAPI.context.runOnUiThread {
							callback()
						}
					}
				}
			}
		}

		fun updateChainStatus(
			isMainnet: Boolean,
			callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().apply {
					getAppConfig().let {
						update(it[0].apply {
							this.isMainnet = isMainnet
						})
						GoldStoneAPI.context.runOnUiThread {
							callback()
						}
					}
				}
			}
		}

		fun updateChainInfo(
			isMainnet: Boolean,
			etcChainNameID: Int,
			ethERC20AndETCChainNameID: Int,
			btcChainNameID: Int,
			bchChainNameID: Int,
			ltcChainNameID: Int,
			eosChainNameID: Int,
			callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().apply {
					getAppConfig().let {
						update(it[0].apply {
							this.isMainnet = isMainnet
							if (isMainnet) {
								currentBTCChainNameID = btcChainNameID
								currentLTCChainNameID = ltcChainNameID
								currentETCChainNameID = etcChainNameID
								currentETHERC20AndETCChainNameID = ethERC20AndETCChainNameID
								currentBCHChainNameID = bchChainNameID
								currentEOSChainNameID = eosChainNameID
							} else {
								currentBTCTestChainNameID = btcChainNameID
								currentLTCTestChainNameID = ltcChainNameID
								currentETCTestChainNameID = etcChainNameID
								currentETHERC20AndETCTestChainNameID = ethERC20AndETCChainNameID
								currentBCHTestChainNameID = bchChainNameID
								currentEOSTestChainNameID = eosChainNameID
							}
						})
						GoldStoneAPI.context.runOnUiThread {
							callback()
						}
					}
				}
			}
		}

		fun updateTerms(terms: String) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().apply {
					update(getAppConfig()[0].apply { this.terms = terms })
				}
			}
		}

		fun updateShareContent(shareContent: String) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().apply {
					update(getAppConfig()[0].apply { this.shareContent = shareContent })
				}
			}
		}

		fun updateCurrency(
			code: String,
			callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().apply {
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
				val goldStoneID =
					Settings.Secure.getString(
						GoldStoneAPI.context.contentResolver,
						Settings.Secure.ANDROID_ID
					) + System.currentTimeMillis()
				GoldStoneDataBase
					.database
					.appConfigDao()
					.insert(
						AppConfigTable(
							0,
							goldStoneID = goldStoneID,
							language = HoneyLanguage.getCodeBySymbol(CountryCode.currentLanguageSymbol),
							terms = getLocalTerms(),
							isMainnet = true,
							currentBTCChainNameID = ChainNameID.GoldStoneBTCMain.id,
							currentETCChainNameID = ChainNameID.GasTrackerETCMain.id,
							currentETHERC20AndETCChainNameID = ChainNameID.InfuraETHMain.id,
							currentBTCTestChainNameID = ChainNameID.GoldStoneBTCTest.id,
							currentETCTestChainNameID = ChainNameID.GasTrackerETCMorden.id,
							currentETHERC20AndETCTestChainNameID = ChainNameID.InfuraRopsten.id,
							currentLTCTestChainNameID = ChainNameID.GoldStoneLTCTest.id,
							currentLTCChainNameID = ChainNameID.GoldStoneLTC.id,
							currentBCHChainNameID = ChainNameID.GoldStoneBCHMain.id,
							currentBCHTestChainNameID = ChainNameID.GoldStoneBCHTest.id,
							currentEOSChainNameID = ChainNameID.GoldStoneEOSMain.id,
							currentEOSTestChainNameID = ChainNameID.GoldStoneEOSTest.id,
							defaultCoinListMD5 = "",
							exchangeListMD5 = ""
						)
					)
				GoldStoneAPI.context.runOnUiThread { callback() }
			}
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

	@Query("SELECT * FROM appConfig")
	fun getAppConfig(): List<AppConfigTable>

	@Insert
	fun insert(appConfigTable: AppConfigTable)

	@Update
	fun update(appConfigTable: AppConfigTable)

	@Delete
	fun delete(appConfigTable: AppConfigTable)
}