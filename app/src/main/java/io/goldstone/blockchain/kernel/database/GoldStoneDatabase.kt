package io.goldstone.blockchain.kernel.database

import android.arch.persistence.room.*
import android.content.Context
import io.goldstone.blockchain.kernel.commonmodel.*
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionDao
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionDataConverter
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.*
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.model.TokenBalanceDao
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.model.TokenBalanceTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.EOSAccountInfoConverter
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.EOSDefaultAllChainNameConverter
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletDao
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContractDao
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionDao
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationDao
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenDao
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable

/**
 * @date 03/04/2018 12:53 PM
 * @author KaySaith
 */
@Database(
	entities = [
		(WalletTable::class),
		(MyTokenTable::class),
		(DefaultTokenTable::class),
		(TransactionTable::class),
		(TokenBalanceTable::class),
		(ContactTable::class),
		(AppConfigTable::class),
		(NotificationTable::class),
		(QuotationSelectionTable::class),
		(SupportCurrencyTable::class),
		(BTCSeriesTransactionTable::class),
		(EOSTransactionTable::class),
		(EOSAccountTable::class)
	],
	version = GoldStoneDataBase.databaseVersion,
	exportSchema = false
)
@TypeConverters(
	ListStringConverter::class,
	ResourceLimitConverter::class,
	TotalResourcesConverter::class,
	DelegateInfoConverter::class,
	VoterInfoConverter::class,
	RefundInfoConverter::class,
	PermissionsInfoConverter::class,
	RequiredAuthorizationConverter::class,
	EOSAccountInfoConverter::class,
	EOSDefaultAllChainNameConverter::class,
	EOSTransactionDataConverter::class
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
	abstract fun eosTransactionDao(): EOSTransactionDao
	abstract fun eosAccountDao(): EOSAccountDao

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

/**
 * 因为业务中很多存储 List<String> 的场景, 顾此在这里声明一个类型转换器来
 * 适应业务的需求.
 */
class ListStringConverter {
	@TypeConverter
	fun revertString(content: String): List<String> {
		return when {
			content.isEmpty() -> listOf()
			content.contains(",") -> content.split(",")
			else -> listOf(content)
		}
	}

	@TypeConverter
	fun convertListString(content: List<String>): String {
		var stringContent = ""
		content.forEach {
			stringContent += "$it,"
		}
		return if (stringContent.isEmpty()) stringContent
		else stringContent.substringBeforeLast(",")
	}
}


