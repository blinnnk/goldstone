package io.goldstone.blockchain.module.entrance.starting.presenter

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.addFragment
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.blinnnk.util.convertLocalJsonFileToJSONObjectArray
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.crypto.multichain.node.ChainNodeTable
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.BackupServerChecker
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.entrance.starting.view.StartingFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.jetbrains.anko.doAsync

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

	fun updateWalletInfoForUserInfo(walletList: List<WalletTable>) {
		walletList.apply {
			// 记录当前最大的钱包 `ID` 用来生成默认头像和名字
			SharedWallet.updateMaxWalletID(maxBy { it.id }?.id.orZero())
			SharedWallet.updateWalletCount(size)
		}
	}

	companion object {

		fun updateShareContentFromServer() {
			GoldStoneAPI.getShareContent { shareContent, error ->
				if (!shareContent.isNull() && error.isNone()) {
					val shareText = if (shareContent!!.title.isEmpty() && shareContent.content.isEmpty()) {
						ProfileText.shareContent
					} else {
						"${shareContent.title}\n${shareContent.content}\n${shareContent.url}"
					}
					GoldStoneDataBase.database.appConfigDao().updateShareContent(shareText)
				} else {
					BackupServerChecker.checkBackupStatusByException(error)
				}
			}
		}

		fun insertLocalTokens(context: Context, @WorkerThread callback: () -> Unit) {
			context.convertLocalJsonFileToJSONObjectArray(R.raw.local_token_list).map {
				DefaultTokenTable(it)
			}.let {
				GoldStoneDataBase.database.defaultTokenDao().insertAll(it)
				callback()
			}
		}

		fun insertLocalNodeList(context: Context, @WorkerThread callback: () -> Unit) {
			doAsync {
				context.convertLocalJsonFileToJSONObjectArray(R.raw.node_list).map {
					ChainNodeTable(it)
				}.let {
					GoldStoneDataBase.database.chainNodeDao().insertAll(it)
					callback()
				}
			}
		}

		fun insertLocalCurrency(context: Context, @WorkerThread callback: () -> Unit) {
			context.convertLocalJsonFileToJSONObjectArray(R.raw.support_currency_list).map {
				SupportCurrencyTable(it).apply {
					// 初始化的汇率显示本地 `Json` 中的值, 之后是通过网络更新
					if (currencySymbol.equals(CountryCode.currentCurrency, true)) {
						isUsed = true
						SharedWallet.updateCurrentRate(rate)
					}
				}
			}.let {
				GoldStoneDataBase.database.currencyDao().insertAll(it)
				callback()
			}
		}
	}
}