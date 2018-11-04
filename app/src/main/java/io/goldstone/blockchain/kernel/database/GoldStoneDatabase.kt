package io.goldstone.blockchain.kernel.database

import android.arch.persistence.room.*
import android.content.Context
import io.goldstone.blockchain.crypto.multichain.node.ChainNodeDao
import io.goldstone.blockchain.crypto.multichain.node.ChainNodeTable
import io.goldstone.blockchain.kernel.commonmodel.*
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionDao
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionDataConverter
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.*
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.model.TokenBalanceDao
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.model.TokenBalanceTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.*
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContractDao
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeDao
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionDao
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationDao
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenDao
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.MyTokenDefaultTableDao
import java.math.BigInteger

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
		(ExchangeTable::class),
		(EOSAccountTable::class),
		(ChainNodeTable::class)
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
	EOSTransactionDataConverter::class,
	BigintegerConverter::class,
	ListBip44AddressConverter::class
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
	abstract fun eosTransactionDao(): EOSTransactionDao
	abstract fun eosAccountDao(): EOSAccountDao
	abstract fun myTokenDefaultTableDao(): MyTokenDefaultTableDao
	abstract fun chainNodeDao(): ChainNodeDao

	companion object {
		const val databaseVersion = 9
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

/**
 * 高精度的数字只能不用 `BigInteger` 来存储数据库增加类型转换
 */
class BigintegerConverter {
	@TypeConverter
	fun revertString(content: String): BigInteger {
		return when {
			content.isEmpty() -> BigInteger.ZERO
			content.any { !it.toString().matches(Regex(".*[0-9].*")) } -> BigInteger.ZERO
			else -> content.toBigInteger()
		}
	}

	@TypeConverter
	fun convertBigInteger(content: BigInteger): String {
		return content.toBigDecimal().toPlainString()
	}
}


