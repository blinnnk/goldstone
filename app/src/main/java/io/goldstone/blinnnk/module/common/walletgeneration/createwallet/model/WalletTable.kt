package io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model

import android.arch.persistence.room.*
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orFalse
import com.blinnnk.extension.orZero
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blinnnk.common.language.WalletText
import io.goldstone.blinnnk.common.sandbox.SandBoxManager
import io.goldstone.blinnnk.common.sharedpreference.SharedAddress
import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.common.thread.launchDefault
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.isEmptyThen
import io.goldstone.blinnnk.crypto.eos.EOSWalletType
import io.goldstone.blinnnk.crypto.eos.EOSWalletUtils
import io.goldstone.blinnnk.crypto.eos.account.EOSAccount
import io.goldstone.blinnnk.crypto.multichain.*
import io.goldstone.blinnnk.kernel.commontable.MyTokenTable
import io.goldstone.blinnnk.kernel.database.GoldStoneDataBase
import kotlinx.coroutines.*
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
	// 这个是服务指纹支付的加密, 因为指纹是在系统层面设置, 以及用户也会更改本地的 Keystore 密码,
	// 所以这个值本身是不可靠的, 故此没有复用 encryptMnemonic 这个字段而是开了新字段支持这个功能.
	var encryptFingerPrinterKey: String? = null,
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

	fun getAddressPathIndex(address: String, chainType: ChainType): Int {
		return when {
			chainType.isETH() -> {
				ethAddresses.find { it.address.equals(address, true) }?.index.orZero()
			}
			chainType.isETC() -> {
				etcAddresses.find { it.address.equals(address, true) }?.index.orZero()
			}
			chainType.isBTC() -> {
				btcAddresses.find { it.address.equals(address, true) }?.index.orZero()
			}
			chainType.isAllTest() -> {
				btcSeriesTestAddresses.find { it.address.equals(address, true) }?.index.orZero()
			}
			chainType.isLTC() -> {
				ltcAddresses.find { it.address.equals(address, true) }?.index.orZero()
			}
			chainType.isBCH() -> {
				bchAddresses.find { it.address.equals(address, true) }?.index.orZero()
			}
			chainType.isEOS() -> {
				eosAddresses.find { it.address.equals(address, true) }?.index.orZero()
			}
			else -> -1
		}
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
			walletType.isEOSMainnet() ->
				currentEOSAccountName.getTarget(ChainID.EOS)
			walletType.isEOSJungle() ->
				currentEOSAccountName.getTarget(ChainID.EOSJungle)
			walletType.isEOSKylin() ->
				currentEOSAccountName.getTarget(ChainID.EOSKylin)
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
			Pair(WalletType.eosOnly, currentEOSAddress),
			Pair(WalletType.eosMainnetOnly, currentEOSAccountName.main),
			Pair(WalletType.eosJungleOnly, currentEOSAccountName.jungle),
			Pair(WalletType.eosKylinOnly, currentEOSAccountName.kylin)
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
		dao.updateLastUsingWalletOff()
		dao.insert(this@WalletTable)
		// 如果直接返回 `Wallet` 那么 `ID` 不会显示为 `Room` 的自增 `ID`
		// 所以再插入之后在此查询获取最新的 `ID` 的 `Wallet`
		// 创建或导入钱包都需要用 ID 作为 Keystore 的标识位
		dao.findWhichIsUsing()?.let {
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
		dao.updateETHAddress(ethAddresses)
		callback(ethAddresses)
	}

	@WorkerThread
	fun updateETCAddresses(
		newAddress: Bip44Address,
		callback: (etcAddresses: List<Bip44Address>) -> Unit
	) {
		etcAddresses += newAddress
		dao.updateETCAddress(etcAddresses)
		callback(etcAddresses)
	}

	@WorkerThread
	fun updateBTCAddresses(
		newAddress: Bip44Address,
		callback: (bitcoinAddresses: List<Bip44Address>) -> Unit
	) {
		btcAddresses += newAddress
		dao.updateBTCAddress(btcAddresses)
		callback(btcAddresses)
	}

	@WorkerThread
	fun updateBCHAddresses(
		newAddress: Bip44Address,
		callback: (bitcoinCashAddresses: List<Bip44Address>) -> Unit
	) {
		bchAddresses += newAddress
		dao.updateBCHAddress(bchAddresses)
		callback(bchAddresses)
	}

	@WorkerThread
	fun updateBTCSeriesTestAddresses(
		newAddress: Bip44Address,
		callback: (bitcoinAddresses: List<Bip44Address>) -> Unit
	) {
		btcSeriesTestAddresses += newAddress
		dao.updateBTCSeriesTestAddress(btcSeriesTestAddresses)
		callback(btcSeriesTestAddresses)
	}

	@WorkerThread
	fun updateLTCAddresses(
		newAddress: Bip44Address,
		callback: (litecoinAddresses: List<Bip44Address>) -> Unit
	) {
		ltcAddresses += newAddress
		dao.updateLTCAddress(ltcAddresses)
		callback(ltcAddresses)
	}

	fun updateEOSAddresses(
		newAddress: Bip44Address,
		callback: (eosAddresses: List<Bip44Address>) -> Unit
	) {
		eosAddresses += newAddress
		WalletTable.dao.updateEOSAddress(eosAddresses)
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
			load { dao.getAllWallets() } then (hold)
		}

		fun getAllETHSeriesAddresses(hold: List<String>.() -> Unit) {
			load { dao.getAllWallets() } then { it ->
				hold(it.map { it.currentETHSeriesAddress })
			}
		}

		fun getAllBTCMainnetAddresses(hold: List<String>.() -> Unit) {
			load { dao.getAllWallets() } then { it ->
				hold(it.map { it.currentBTCAddress })
			}
		}

		fun getAllLTCAddresses(hold: List<String>.() -> Unit) {
			load { dao.getAllWallets() } then { it ->
				hold(it.map { it.currentLTCAddress })
			}
		}

		fun getAllEOSAccountNames(hold: List<String>.() -> Unit) {
			load {
				val allWallets = dao.getAllWallets()
				allWallets.map { it.currentEOSAccountName.getCurrent() }
			} then (hold)
		}

		fun getAllBCHAddresses(hold: List<String>.() -> Unit) {
			load { dao.getAllWallets() } then { it ->
				hold(it.map { it.currentBCHAddress })
			}
		}

		fun getAllBTCSeriesTestnetAddresses(hold: List<String>.() -> Unit) {
			load { dao.getAllWallets() } then { it ->
				hold(it.map { it.currentBTCSeriesTestAddress })
			}
		}

		fun getCurrent(thread: CoroutineDispatcher, hold: WalletTable.() -> Unit) {
			GlobalScope.launch(Dispatchers.Default) {
				val currentWallet = dao.findWhichIsUsing() ?: return@launch
				dao.update(currentWallet.apply { balance = SharedWallet.getCurrentBalance() })
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
				dao.findWhichIsUsing()
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
			load {
				dao.updateWalletName(newName)
				SandBoxManager.updateWalletTables()
			} then { callback() }
		}

		fun updateHint(newHint: String, callback: () -> Unit = {}) {
			load {
				dao.updateHint(newHint)
				SandBoxManager.updateWalletTables()
			} then { callback() }
		}

		fun updateHasBackupMnemonic(callback: () -> Unit) {
			load {
				dao.updateHasBackUp()
				SandBoxManager.updateWalletTables()
			} then { callback() }
		}

		fun initEOSAccountName(
			accountNames: List<EOSAccountInfo>,
			@UiThread callback: () -> Unit
		) {
			launchDefault {
				// 增量存储同一公钥下的多 `AccountName`
				var currentAccountNames =
					dao.getWalletByAddress(SharedAddress.getCurrentEOS())?.eosAccountNames ?: listOf()
				currentAccountNames += accountNames
				dao.updateCurrentEOSAccountNames(currentAccountNames.distinct())
				// 如果公钥下只有一个 `AccountName` 那么直接设为 `DefaultName`
				if (accountNames.isNotEmpty()) {
					WalletTable.updateEOSDefaultName(accountNames.first().name) { callback() }
				} else launchUI(callback)
			}
		}

		fun updateEOSDefaultName(defaultName: String, @UiThread callback: () -> Unit) {
			launchDefault {
				// 更新钱包数据库的 `Default EOS Address`
				dao.findWhichIsUsing()?.apply {
					dao.update(apply { currentEOSAccountName.updateCurrent(defaultName) })
					// 同时更新 `MyTokenTable` 里面的 `OwnerName`
					MyTokenTable.updateOrInsertOwnerName(defaultName, currentEOSAddress)
					launchUI(callback)
				}
			}
		}

		fun switchCurrentWallet(walletAddress: String, callback: (WalletTable) -> Unit) {
			GlobalScope.launch(Dispatchers.Default) {
				dao.updateLastUsingWalletOff()
				dao.getWalletByAddress(walletAddress)?.let {
					dao.update(it.apply { isUsing = true })
					launchUI { callback(it) }
				}
			}
		}

		fun deleteCurrentWallet(@WorkerThread callback: (WalletTable) -> Unit) {
			GlobalScope.launch(Dispatchers.Default) {
				val willDeleteWallet = dao.findWhichIsUsing()
				willDeleteWallet?.let {
					dao.delete(it)
					// 删除sandbox的存储
					SandBoxManager.updateWalletTables()
					callback(it)
				}
				dao.getAllWallets().let { wallets ->
					if (wallets.isNotEmpty()) {
						dao.update(wallets.first().apply { isUsing = true })
						SharedWallet.updateCurrentIsWatchOnlyOrNot(wallets.first().isWatchOnly.orFalse())
					}
				}
			}
		}

		@WorkerThread
		fun existAddressOrAccountName(address: String, chainID: ChainID?, hold: (isExisted: Boolean) -> Unit) {
			val isExisted = if (EOSAccount(address).isValid(false)) {
				dao.getAllWallets().map { it.eosAccountNames }.flatten().any {
					it.name.equals(address, true) && it.chainID.equals(chainID?.id, true)
				}
			} else !dao.getWalletByAddress(address).isNull()
			hold(isExisted)
		}
	}
}

@Dao
interface WalletDao {

	@Query("UPDATE wallet SET hasBackUpMnemonic = 1 WHERE isUsing = 1")
	fun updateHasBackUp()

	@Query("UPDATE wallet SET hint = :hint WHERE isUsing = 1")
	fun updateHint(hint: String)

	@Query("UPDATE wallet SET encryptFingerPrinterKey = null WHERE isUsing = 1")
	fun turnOffFingerprint()

	@Query("UPDATE wallet SET name = :walletName WHERE isUsing = 1")
	fun updateWalletName(walletName: String)

	@Query("SELECT * FROM wallet WHERE isUsing = 1")
	fun findWhichIsUsing(): WalletTable?

	@Query("SELECT encryptFingerPrinterKey FROM wallet WHERE isUsing LIKE 1")
	fun getEncryptFingerprintKey(): String?

	@Query("SELECT encryptMnemonic FROM wallet WHERE isUsing LIKE 1")
	fun getEncryptMnemonic(): String?

	@Query("UPDATE wallet SET isUsing = 0 WHERE isUsing LIKE 1")
	fun updateLastUsingWalletOff()

	@Query("SELECT * FROM wallet WHERE currentETHSeriesAddress LIKE :address OR currentEOSAddress LIKE :address OR currentBCHAddress LIKE :address OR currentLTCAddress LIKE :address OR currentBTCAddress LIKE :address OR currentBTCSeriesTestAddress LIKE :address")
	fun getWalletByAddress(address: String): WalletTable?

	@Query("SELECT eosAccountNames FROM wallet")
	fun getEOSAccountNames(): List<String>
	
	@Query("SELECT * FROM wallet where id = :id")
	fun getWalletByID(id: Int): WalletTable?
	
	@Query("SELECT * FROM wallet")
	fun getAllWallets(): List<WalletTable>

	@Query("SELECT count(*) FROM wallet")
	fun rowCount(): Int

	@Query("SELECT MAX(id) FROM wallet")
	fun getMaxID(): Int

	@Insert
	fun insert(wallet: WalletTable)

	@Delete
	fun delete(wallet: WalletTable)

	@Update
	fun update(wallet: WalletTable)

	@Query("UPDATE wallet SET encryptFingerPrinterKey = :encryptKey  WHERE isUsing = 1")
	fun updateFingerEncryptKey(encryptKey: String)

	@Query("UPDATE wallet SET eosAccountNames = :accounts  WHERE isUsing = 1")
	fun updateCurrentEOSAccountNames(accounts: List<EOSAccountInfo>)

	@Query("UPDATE wallet SET ethAddresses = :ethAddresses  WHERE isUsing = 1")
	fun updateETHAddress(ethAddresses: List<Bip44Address>)

	@Query("UPDATE wallet SET etcAddresses = :etcAddresses  WHERE isUsing = 1")
	fun updateETCAddress(etcAddresses: List<Bip44Address>)

	@Query("UPDATE wallet SET btcAddresses = :btcAddresses  WHERE isUsing = 1")
	fun updateBTCAddress(btcAddresses: List<Bip44Address>)

	@Query("UPDATE wallet SET btcSeriesTestAddresses = :btcSeriesTestAddresses  WHERE isUsing = 1")
	fun updateBTCSeriesTestAddress(btcSeriesTestAddresses: List<Bip44Address>)

	@Query("UPDATE wallet SET ltcAddresses = :ltcAddresses  WHERE isUsing = 1")
	fun updateLTCAddress(ltcAddresses: List<Bip44Address>)

	@Query("UPDATE wallet SET bchAddresses = :bchAddresses  WHERE isUsing = 1")
	fun updateBCHAddress(bchAddresses: List<Bip44Address>)

	@Query("UPDATE wallet SET eosAddresses = :eosAddresses  WHERE isUsing = 1")
	fun updateEOSAddress(eosAddresses: List<Bip44Address>)

}