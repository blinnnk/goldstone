package io.goldstone.blockchain.module.entrance.starting.presenter

import android.content.Context
import com.blinnnk.extension.addFragment
import com.blinnnk.extension.forEachOrEnd
import com.blinnnk.extension.isNull
import com.blinnnk.extension.safeGet
import com.blinnnk.util.convertLocalJsonFileToJSONObjectArray
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.entrance.starting.view.StartingFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TinyNumber
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

@Suppress("IMPLICIT_CAST_TO_ANY")
/**
 * @date 22/03/2018 2:56 AM
 * @author KaySaith
 */
class StartingPresenter(override val fragment: StartingFragment) :
	BasePresenter<StartingFragment>() {
	
	fun showCreateWalletFragment() {
		fragment.activity?.addFragment<WalletGenerationFragment>(ContainerID.splash)
	}
	
	fun showImportWalletFragment() {
		fragment.activity?.addFragment<WalletImportFragment>(ContainerID.splash)
	}
	
	companion object {
		
		fun insertLocalTokens(context: Context, callback: () -> Unit) {
			doAsync {
				context.convertLocalJsonFileToJSONObjectArray(R.raw.local_token_list)
					.forEachOrEnd { token, isEnd ->
						DefaultTokenTable(
							token, token.get("force_show").toString().toInt() == TinyNumber.True.value
						).let {
							GoldStoneDataBase.database.defaultTokenDao().insert(it)
							context.runOnUiThread {
								if (isEnd) callback()
							}
						}
					}
			}
		}
		
		fun insertLocalCurrency(context: Context, callback: () -> Unit) {
			doAsync {
				context.convertLocalJsonFileToJSONObjectArray(R.raw.support_currency_list)
					.forEachOrEnd { item, isEnd ->
						val model = if (item.safeGet("currencySymbol") == CountryCode.currentCurrency) {
							SupportCurrencyTable(item).apply { isUsed = true }
						} else {
							SupportCurrencyTable(item)
						}
						
						GoldStoneDataBase.database.currencyDao().insert(model)
						
						context.runOnUiThread {
							if (isEnd) callback()
						}
					}
			}
		}
		
		fun updateLocalDefaultTokens(errorCallback: () -> Unit) {
			doAsync {
				GoldStoneAPI.getDefaultTokens(errorCallback) { serverTokens ->
					if (serverTokens.isNotEmpty()) {
						serverTokens.forEach {
							GoldStoneDataBase.database.defaultTokenDao().apply {
								val localToken = getTokenByContract(it.contract)
								if (localToken.isNull()) {
									insert(it)
								} else {
									if (localToken!!.iconUrl != it.iconUrl) {
										// 数据有变化直接更新服务器数据
										update(localToken.apply { iconUrl = it.iconUrl })
									}
								}
							}
						}
					}
				}
			}
		}
	}
}