package io.goldstone.blockchain.module.common.walletgeneration.createwallet.model

import android.arch.persistence.room.*
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orFalse
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.isEmptyThen
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.crypto.eos.EOSWalletType
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import kotlinx.coroutines.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.io.Serializable

/**
 * @date 29/03/2018 10:35 PM
 * @author KaySaith
 */
@Entity(tableName = "wallet")
data class WalletTable(
	//@PrimaryKey autoGenerate 自增
	@PrimaryKey(autoGenerate = true)
	var id: Int,
	var avatarID: Int,
	var name: String,
	var currentETHSeriesAddress: String,
	var currentETCAddress: String,
	var currentBTCAddress: String,
	var currentBTCSeriesTestAddress: String,
	var currentLTCAddress: String,
	var currentBCHAddress: String,
	var currentEOSAddress: String,
	var currentEOSAccountName: EOSDefaultAllChainName,
	var ethAddresses: List<Bip44Address>,
	var btcAddresses: List<Bip44Address>,
	var btcSeriesTestAddresses: List<Bip44Address>,
	var etcAddresses: List<Bip44Address>,
	var ltcAddresses: List<Bip44Address>,
	var bchAddresses: List<Bip44Address>,
	var eosAddresses: List<Bip44Address>,
	var eosAccountNames: List<EOSAccountInfo>,
	var ethPath: String,
	var etcPath: String,
	var btcPath: String,
	var btcTestPath: String,
	var ltcPath: String,
	var bchPath: String,
	var eosPath: String,
	var isUsing: Boolean,
	var hint: String? = null,
	var isWatchOnly: Boolean = false,
	var balance: Double? = 0.0,
	var encryptMnemonic: String? = null,
	var hasBackUpMnemonic: Boolean = false
) : Serializable {

	constructor(
		walletName: String,
		currentETHSeriesAddress: String,
		currentBTCTestAddress: String,
		currentBTCAddress: String,
		currentETCAddress: String,
		currentLTCAddress: String,
		currentBCHAddress: String,
		currentEOSAddress: String,
		currentEOSAccountName: EOSDefaultAllChainName,
		eosAccountNames: List<EOSAccountInfo>
	) : this(
		0,
		SharedWallet.getMaxWalletID() + 1,
		walletName,
		currentETHSeriesAddress = currentETHSeriesAddress,
		isUsing = true,
		isWatchOnly = true,
		hasBackUpMnemonic = true,
		currentBTCSeriesTestAddress = currentBTCTestAddress,
		currentBTCAddress = currentBTCAddress,
		currentETCAddress = currentETCAddress,
		currentLTCAddress = currentLTCAddress,
		currentBCHAddress = currentBCHAddress,
		currentEOSAddress = currentEOSAddress,
		currentEOSAccountName = currentEOSAccountName,
		ethPath = "",
		etcPath = "",
		btcPath = "",
		bchPath = "",
		btcTestPath = "",
		ltcPath = "",
		eosPath = "",
		ethAddresses = listOf(),
		etcAddresses = listOf(),
		btcAddresses = listOf(),
		bchAddresses = listOf(),
		btcSeriesTestAddresses = listOf(),
		ltcAddresses = listOf(),
		eosAddresses = listOf(),
		eosAccountNames = eosAccountNames
	)

	fun getCurrentBip44Addresses(): List<Bip44Address> {
		return listOf(
			Bip44Address(currentBTCAddress, ChainType.BTC.id),
			Bip44Address(currentLTCAddress, ChainType.LTC.id),
			Bip44Address(currentBCHAddress, ChainType.BCH.id),
			Bip44Address(currentBTCSeriesTestAddress, ChainType.AllTest.id),
			Bip44Address(currentETCAddress, ChainType.ETC.id),
			Bip44Address(currentETHSeriesAddress, ChainType.ETH.id),
			Bip44Address(currentEOSAddress isEmptyThen currentEOSAccountName.getCurrent(), ChainType.EOS.id)
		).filter {
			it.address.isNotEmpty()
		}
	}

	fun getCurrentMainnetBip44Addresses(): List<Bip44Address> {
		return listOf(
			Bip44Address(currentBTCAddress, ChainType.BTC.id),
			Bip44Address(currentLTCAddress, ChainType.LTC.id),
			Bip44Address(currentBCHAddress, ChainType.BCH.id),
			Bip44Address(currentETCAddress, ChainType.ETC.id),
			Bip44Address(currentETHSeriesAddress, ChainType.ETH.id),
			Bip44Address(currentEOSAddress isEmptyThen currentEOSAccountName.getCurrent(), ChainType.EOS.id)
		).filter { it.address.isNotEmpty() }
	}

	fun getCurrentTestnetBip44Addresses(): List<Bip44Address> {
		// 这里 `BTCTestSeries` 的地址返回的是实际的链 `ID` 因为这个数据是用来做展示
		// 根据 `ChainID` 决定相同的地址展示什么 `Symbol` 前缀
		return listOf(
			Bip44Address(currentBTCSeriesTestAddress, ChainType.BTC.id),
			Bip44Address(currentBTCSeriesTestAddress, ChainType.BCH.id),
			Bip44Address(currentBTCSeriesTestAddress, ChainType.LTC.id),
			Bip44Address(currentETCAddress, ChainType.ETC.id),
			Bip44Address(currentETHSeriesAddress, ChainType.ETH.id),
			Bip44Address(currentEOSAddress isEmptyThen currentEOSAccountName.getCurrent(), ChainType.EOS.id)
		).filter { it.address.isNotEmpty() }
	}

	fun getCurrentAllBip44Address(): List<Bip44Address> {
		return listOf(
			btcAddresses,
			ltcAddresses,
			bchAddresses,
			btcSeriesTestAddresses,
			etcAddresses,
			ethAddresses,
			eosAddresses
		).flatten()
	}

	fun getCurrentAddresses(useEOSAccountName: Boolean = false): List<String> {
		return listOf(
			currentBTCAddress,
			currentBTCSeriesTestAddress,
			currentETCAddress,
			currentETHSeriesAddress,
			currentLTCAddress,
			currentBCHAddress,
			if (useEOSAccountName) {
				currentEOSAccountName.getCurrent() isEmptyThen currentEOSAddress
			} else listOf(
				currentEOSAddress,
				currentEOSAccountName.getCurrent(),
				currentEOSAccountName.getUnEmptyValue()
			).firstOrNull { it.isNotEmpty() } ?: ""
		).asSequence().filter { it.isNotEmpty() }.distinctBy { it }.toList()
	}

	fun getCurrentBip44Address(chainType: ChainType): Bip44Address {
		return when {
			chainType.isETH() -> {
				val index = ethAddresses.find { it.address.equals(currentETHSeriesAddress, true) }?.index.orZero()
				Bip44Address(currentETHSeriesAddress, index, ChainType.ETH.id)
			}
			chainType.isETC() -> {
				val index = etcAddresses.find { it.address.equals(currentETCAddress, true) }?.index.orZero()
				Bip44Address(currentETCAddress, index, ChainType.ETC.id)
			}
			chainType.isBTC() -> {
				val index = btcAddresses.find { it.address.equals(currentBTCAddress, true) }?.index.orZero()
				Bip44Address(currentBTCAddress, index, ChainType.BTC.id)
			}
			chainType.isAllTest() -> {
				val index = btcSeriesTestAddresses.find { it.address.equals(currentBTCSeriesTestAddress, true) }?.index.orZero()
				Bip44Address(currentBTCSeriesTestAddress, index, ChainType.AllTest.id)
			}
			chainType.isLTC() -> {
				val index = ltcAddresses.find { it.address.equals(currentLTCAddress, true) }?.index.orZero()
				Bip44Address(currentLTCAddress, index, ChainType.LTC.id)
			}
			chainType.isBCH() -> {
				val index = bchAddresses.find { it.address.equals(currentBCHAddress, true) }?.index.orZero()
				Bip44Address(currentBCHAddress, index, ChainType.BCH.id)
			}
			chainType.isEOS() -> {
				val index = eosAddresses.find { it.address.equals(currentEOSAddress, true) }?.index.orZero()
				Bip44Address(currentEOSAddress, index, ChainType.EOS.id)
			}
			else -> Bip44Address()
		}
	}

	fun getEOSWalletType(): EOSWalletType {
		return when {
			EOSAccount(currentEOSAccountName.getCurrent()).isValid(false) -> EOSWalletType.Available
			// 当前 `ChainID` 下的 `Name` 个数大于 `1` 并且越过第一步判断那么为未设置默认账户状态
			eosAccountNames.filter {
				it.chainID.equals(SharedChain.getEOSCurrent().chainID.id, true) &&
					it.publicKey.equals(SharedAddress.getCurrentEOS(), true)
			}.size > 1 -> EOSWalletType.NoDefault
			else -> EOSWalletType.Inactivated
		}
	}

	fun getAddressDescription(): String {
		val walletType = getWalletType()
		return when {
			walletType.isLTC() -> currentLTCAddress
			walletType.isBCH() -> currentBCHAddress
			walletType.isETHSeries() -> currentETHSeriesAddress
			walletType.isBTCTest() -> currentBTCSeriesTestAddress
			walletType.isBTC() -> currentBTCAddress
			walletType.isEOS() -> currentEOSAddress
			walletType.isEOSMainnet() || walletType.isEOSJungle() ->
				currentEOSAccountName.getCurrent()
			walletType.isBIP44() -> WalletText.bip44MultiChain
			else -> WalletText.multiChain
		}
	}

	fun getWalletType(): WalletType {
		val types = listOf(
			Pair(WalletType.btcOnly, currentBTCAddress),
			Pair(WalletType.btcTestOnly, currentBTCSeriesTestAddress),
			Pair(WalletType.ethSeries, currentETHSeriesAddress),
			Pair(WalletType.ltcOnly, currentLTCAddress),
			Pair(WalletType.bchOnly, currentBCHAddress),
			Pair(WalletType.eosMainnetOnly, currentEOSAccountName.main),
			Pair(WalletType.eosJungleOnly, currentEOSAccountName.jungle),
			Pair(WalletType.eosOnly, currentEOSAddress)
		).filter {
			it.second.isNotEmpty() && if (it.first == WalletType.eosOnly) EOSWalletUtils.isValidAddress(currentEOSAddress) else true
		}
		return when {
			// 减 `2` 是去除掉 `EOS` 的两个网络状态的计数, 此计数并不影响判断是否是全链钱包
			// 通过私钥导入的多链钱包没有 Path 值所以通过这个来判断是否是
			// BIP44 钱包还是单纯的多链钱包
			types.size > 6 -> if (ethPath.isNotEmpty()) WalletType.BIP44 else WalletType.MultiChain
			else -> WalletType(types.firstOrNull()?.first)
		}
	}

	@WorkerThread
	infix fun insert(callback: (wallet: WalletTable) -> Unit) {
		WalletTable.dao.apply {
			findWhichIsUsing(true)?.let { update(it.apply { isUsing = false }) }
			insert(this@WalletTable)
		}.findWhichIsUsing(true)?.let {
			SharedWallet.updateCurrentIsWatchOnlyOrNot(it.isWatchOnly.orFalse())
			callback(it)
		}
	}

	@WorkerThread
	fun updateETHSeriesAddresses(
		newAddress: Bip44Address,
		callback: (ethSeriesAddresses: List<Bip44Address>) -> Unit
	) {
		ethAddresses += newAddress
		GoldStoneDataBase.database.walletDao().updateETHAddress(ethAddresses)
		callback(ethAddresses)
	}

	@WorkerThread
	fun updateETCAddresses(
		newAddress: Bip44Address,
		callback: (etcAddresses: List<Bip44Address>) -> Unit
	) {
		etcAddresses += newAddress
		GoldStoneDataBase.database.walletDao().updateETCAddress(etcAddresses)
		callback(etcAddresses)
	}

	@WorkerThread
	fun updateBTCAddresses(
		newAddress: Bip44Address,
		callback: (bitcoinAddresses: List<Bip44Address>) -> Unit
	) {
		btcAddresses += newAddress
		GoldStoneDataBase.database.walletDao().updateBTCAddress(btcAddresses)
		callback(btcAddresses)
	}

	@WorkerThread
	fun updateBCHAddresses(
		newAddress: Bip44Address,
		callback: (bitcoinCashAddresses: List<Bip44Address>) -> Unit
	) {
		bchAddresses += newAddress
		GoldStoneDataBase.database.walletDao().updateBCHAddress(bchAddresses)
		callback(bchAddresses)
	}

	@WorkerThread
	fun updateBTCSeriesTestAddresses(
		newAddress: Bip44Address,
		callback: (bitcoinAddresses: List<Bip44Address>) -> Unit
	) {
		btcSeriesTestAddresses += newAddress
		GoldStoneDataBase.database.walletDao().updateBTCSeriesTestAddress(btcSeriesTestAddresses)
		callback(btcSeriesTestAddresses)
	}

	@WorkerThread
	fun updateLTCAddresses(
		newAddress: Bip44Address,
		callback: (litecoinAddresses: List<Bip44Address>) -> Unit
	) {
		ltcAddresses += newAddress
		GoldStoneDataBase.database.walletDao().updateLTCAddress(ltcAddresses)
		callback(ltcAddresses)
	}

	fun updateEOSAddresses(
		newAddress: Bip44Address,
		callback: (eosAddresses: List<Bip44Address>) -> Unit
	) {
		eosAddresses += newAddress
		GoldStoneDataBase.database.walletDao().updateEOSAddress(eosAddresses)
		callback(eosAddresses)
	}

	companion object {
		@JvmField
		val dao = GoldStoneDataBase.database.walletDao()

		@WorkerThread
		fun getWalletAddressCount(hold: (Int) -> Unit) = WalletTable.getCurrent(Dispatchers.Default) {
			val currentType = SharedWallet.getCurrentWalletType()
			when {
				currentType.isBIP44() -> {
					val ethAddressCount = ethAddresses.size
					val etcAddressCount = etcAddresses.size
					val btcAddressCount = btcAddresses.size
					val btcTestAddressCount = btcSeriesTestAddresses.size
					val ltcAddressCount = ltcAddresses.size
					val bchAddressCount = bchAddresses.size
					val eosAddressCount = eosAddresses.size
					hold(
						ethAddressCount +
							etcAddressCount +
							btcAddressCount +
							btcTestAddressCount +
							ltcAddressCount +
							bchAddressCount +
							eosAddressCount
					)
				}
				currentType.isMultiChain() -> hold(7)
				currentType.isETHSeries() -> hold(1)
				currentType.isBTCTest() -> hold(1)
				currentType.isBTC() -> hold(1)
				currentType.isLTC() -> hold(1)
				currentType.isBCH() -> hold(1)
				currentType.isEOS() -> hold(1)
			}
		}

		fun getAll(hold: List<WalletTable>.() -> Unit) {
			load { GoldStoneDataBase.database.walletDao().getAllWallets() } then (hold)
		}

		fun getAllETHSeriesAddresses(hold: List<String>.() -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().getAllWallets()
			} then { it ->
				hold(it.map { it.currentETHSeriesAddress })
			}
		}

		fun getAllBTCMainnetAddresses(hold: List<String>.() -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().getAllWallets()
			} then { it ->
				hold(it.map { it.currentBTCAddress })
			}
		}

		fun getAllLTCAddresses(hold: List<String>.() -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().getAllWallets()
			} then { it ->
				hold(it.map { it.currentLTCAddress })
			}
		}

		fun getAllEOSAccountNames(hold: List<String>.() -> Unit) {
			load {
				val allWallets =
					GoldStoneDataBase.database.walletDao().getAllWallets()
				allWallets.map { it.currentEOSAccountName.getCurrent() }
			} then (hold)
		}

		fun getAllBCHAddresses(hold: List<String>.() -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().getAllWallets()
			} then { it ->
				hold(it.map { it.currentBCHAddress })
			}
		}

		fun getAllBTCSeriesTestnetAddresses(hold: List<String>.() -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().getAllWallets()
			} then { it ->
				hold(it.map { it.currentBTCSeriesTestAddress })
			}
		}

		fun getCurrent(thread: CoroutineDispatcher, hold: WalletTable.() -> Unit) {
			GlobalScope.launch(Dispatchers.Default) {
				val walletDao = GoldStoneDataBase.database.walletDao()
				val currentWallet = walletDao.findWhichIsUsing(true) ?: return@launch
				walletDao.update(currentWallet.apply { balance = SharedWallet.getCurrentBalance() })
				withContext(thread) {
					hold(currentWallet)
				}
			}
		}

		fun getWatchOnlyWallet(hold: Bip44Address.() -> Unit) {
			WalletTable.getCurrent(Dispatchers.Main) {
				if (isWatchOnly) getCurrentBip44Addresses().firstOrNull()?.let(hold)
			}
		}

		@WorkerThread
		fun getLatestAddressIndex(
			chainType: ChainType,
			hold: (wallet: WalletTable, ethAddressIndex: Int) -> Unit
		) {
			val currentWallet =
				GoldStoneDataBase.database.walletDao().findWhichIsUsing(true)
			currentWallet?.apply {
				// 清理数据格式
				val latestIndex = when {
					chainType.isETH() -> ethAddresses.maxBy { it.index }?.index
					chainType.isETC() -> etcAddresses.maxBy { it.index }?.index
					chainType.isBTC() -> btcAddresses.maxBy { it.index }?.index
					chainType.isAllTest() -> btcSeriesTestAddresses.maxBy { it.index }?.index
					chainType.isLTC() -> ltcAddresses.maxBy { it.index }?.index
					chainType.isBCH() -> bchAddresses.maxBy { it.index }?.index
					chainType.isEOS() -> eosAddresses.maxBy { it.index }?.index
					else -> null
				}
				// 获取最近的 `Address Index` 数值
				hold(this, latestIndex.orZero())
			}
		}

		fun updateName(newName: String, callback: () -> Unit) {
			load { GoldStoneDataBase.database.walletDao().updateWalletName(newName) } then { callback() }
		}

		fun updateHint(newHint: String, callback: () -> Unit = {}) {
			load { GoldStoneDataBase.database.walletDao().updateHint(newHint) } then { callback() }
		}

		fun updateHasBackupMnemonic(callback: () -> Unit) {
			load { GoldStoneDataBase.database.walletDao().updateHasBackUp() } then { callback() }
		}

		fun initEOSAccountName(
			accountNames: List<EOSAccountInfo>,
			@UiThread callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					// 增量存储同一公钥下的多 `AccountName`
					var currentAccountNames =
						getWalletByAddress(SharedAddress.getCurrentEOS())?.eosAccountNames ?: listOf()
					currentAccountNames += accountNames
					updateCurrentEOSAccountNames(currentAccountNames.distinct())
				}
				// 如果公钥下只有一个 `AccountName` 那么直接设为 `DefaultName`
				if (accountNames.isNotEmpty()) {
					WalletTable.updateEOSDefaultName(accountNames.first().name) { callback() }
				} else GoldStoneAPI.context.runOnUiThread { callback() }
			}
		}

		fun updateEOSDefaultName(defaultName: String, @UiThread callback: () -> Unit) {
			doAsync {
				// 更新钱包数据库的 `Default EOS Address`
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.apply {
						update(apply { currentEOSAccountName.updateCurrent(defaultName) })
						// 同时更新 `MyTokenTable` 里面的 `OwnerName`
						MyTokenTable.updateOrInsertOwnerName(defaultName, currentEOSAddress)
						GoldStoneAPI.context.runOnUiThread { callback() }
					}
				}
			}
		}

		fun switchCurrentWallet(
			walletAddress: String,
			callback: (WalletTable) -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					updateLastUsingWalletOff()
					getWalletByAddress(walletAddress)?.let {
						update(it.apply { isUsing = true })
						GoldStoneAPI.context.runOnUiThread { callback(it) }
					}
				}
			}
		}

		fun deleteCurrentWallet(@WorkerThread callback: (WalletTable?) -> Unit) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					val willDeleteWallet = findWhichIsUsing(true)
					willDeleteWallet?.let { delete(it) }
					getAllWallets().let { wallets ->
						if (wallets.isNotEmpty()) {
							update(wallets.first().apply { isUsing = true })
							SharedWallet.updateCurrentIsWatchOnlyOrNot(wallets.first().isWatchOnly.orFalse())
						}
						callback(willDeleteWallet)
					}
				}
			}
		}

		@WorkerThread
		fun existAddressOrAccountName(address: String, chainID: ChainID?, hold: (isExisted: Boolean) -> Unit) {
			val walletDao = GoldStoneDataBase.database.walletDao()
			val isExisted = if (EOSAccount(address).isValid(false)) {
				walletDao.getAllWallets().map { it.eosAccountNames }.flatten().any {
					it.name.equals(address, true) && it.chainID.equals(chainID?.id, true)
				}
			} else !walletDao.getWalletByAddress(address).isNull()
			hold(isExisted)
		}
	}
}

@Dao
interface WalletDao {

	@Query("UPDATE wallet SET hasBackUpMnemonic = :hasBackUp WHERE isUsing LIKE :isUsing")
	fun updateHasBackUp(hasBackUp: Boolean = true, isUsing: Boolean = true)

	@Query("UPDATE wallet SET hint = :hint WHERE isUsing LIKE :isUsing")
	fun updateHint(hint: String, isUsing: Boolean = true)

	@Query("UPDATE wallet SET name = :walletName WHERE isUsing LIKE :isUsing")
	fun updateWalletName(walletName: String, isUsing: Boolean = true)

	@Query("SELECT * FROM wallet WHERE isUsing LIKE :status ORDER BY id DESC")
	fun findWhichIsUsing(status: Boolean): WalletTable?

	@Query("UPDATE wallet SET isUsing = :status WHERE isUsing LIKE :lastUsing")
	fun updateLastUsingWalletOff(status: Boolean = false, lastUsing: Boolean = true)

	@Query("SELECT * FROM wallet WHERE currentETHSeriesAddress LIKE :address OR currentEOSAddress LIKE :address OR currentBCHAddress LIKE :address OR currentLTCAddress LIKE :address OR currentBTCAddress LIKE :address OR currentBTCSeriesTestAddress LIKE :address")
	fun getWalletByAddress(address: String): WalletTable?

	@Query("SELECT eosAccountNames FROM wallet")
	fun getEOSAccountNames(): List<String>

	@Query("SELECT * FROM wallet")
	fun getAllWallets(): List<WalletTable>

	@Insert
	fun insert(wallet: WalletTable)

	@Delete
	fun delete(wallet: WalletTable)

	@Update
	fun update(wallet: WalletTable)

	@Query("UPDATE wallet SET eosAccountNames = :accounts  WHERE isUsing = :status")
	fun updateCurrentEOSAccountNames(accounts: List<EOSAccountInfo>, status: Boolean = true)

	@Query("UPDATE wallet SET ethAddresses = :ethAddresses  WHERE isUsing = :status")
	fun updateETHAddress(ethAddresses: List<Bip44Address>, status: Boolean = true)

	@Query("UPDATE wallet SET etcAddresses = :etcAddresses  WHERE isUsing = :status")
	fun updateETCAddress(etcAddresses: List<Bip44Address>, status: Boolean = true)

	@Query("UPDATE wallet SET btcAddresses = :btcAddresses  WHERE isUsing = :status")
	fun updateBTCAddress(btcAddresses: List<Bip44Address>, status: Boolean = true)

	@Query("UPDATE wallet SET btcSeriesTestAddresses = :btcSeriesTestAddresses  WHERE isUsing = :status")
	fun updateBTCSeriesTestAddress(btcSeriesTestAddresses: List<Bip44Address>, status: Boolean = true)

	@Query("UPDATE wallet SET ltcAddresses = :ltcAddresses  WHERE isUsing = :status")
	fun updateLTCAddress(ltcAddresses: List<Bip44Address>, status: Boolean = true)

	@Query("UPDATE wallet SET bchAddresses = :bchAddresses  WHERE isUsing = :status")
	fun updateBCHAddress(bchAddresses: List<Bip44Address>, status: Boolean = true)

	@Query("UPDATE wallet SET eosAddresses = :eosAddresses  WHERE isUsing = :status")
	fun updateEOSAddress(eosAddresses: List<Bip44Address>, status: Boolean = true)
}