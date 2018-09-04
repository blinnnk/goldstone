package io.goldstone.blockchain.kernel.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import io.goldstone.blockchain.kernel.commonmodel.*
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.model.TokenBalanceDao
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.model.TokenBalanceTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletDao
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContractDao
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.*
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationDao
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenDao
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable

/**
 * @date 03/04/2018 12:53 PM
 * @author KaySaith
 */
@Database(
	entities = [(WalletTable::class), (MyTokenTable::class), (DefaultTokenTable::class),
		(TransactionTable::class), (TokenBalanceTable::class), (ContactTable::class),
		(AppConfigTable::class), (NotificationTable::class), (QuotationSelectionTable::class),
		(SupportCurrencyTable::class), (BTCSeriesTransactionTable::class), (ExchangeTable::class)],
	version = GoldStoneDataBase.databaseVersion,
	exportSchema = false
)
abstract class GoldStoneDataBase : RoomDatabase() {
	
	abstract fun walletDao(): WalletDao
	abstract fun myTokenDao(): MyTokenDao
	abstract fun defaultTokenDao(): DefaultTokenDao
	abstract fun transactionDao(): TransactionDao
	abstract fun tokenBalanceDao(): TokenBalanceDao
	abstract fun contactDao(): ContractDao
	abstract fun appConfigDao(): AppConfigDao
	abstract fun notificationDao(): NotificationDao
	abstract fun quotationSelectionDao(): QuotationSelectionDao
	abstract fun currencyDao(): SupportCurrencyDao
	abstract fun btcSeriesTransactionDao(): BTCSeriesTransactionDao
	abstract fun exchangeTableDao(): ExchangeDao
	
	companion object {
		const val databaseVersion = 6
		private const val databaseName = "GoldStone.db"
		lateinit var database: GoldStoneDataBase
		
		fun initDatabase(context: Context) {
			database =
				Room.databaseBuilder(context, GoldStoneDataBase::class.java, databaseName)
					.addMigrations()
					.fallbackToDestructiveMigration()
					.build()
		}
	}
}