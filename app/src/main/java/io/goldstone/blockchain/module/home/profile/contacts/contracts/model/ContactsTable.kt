package io.goldstone.blockchain.module.home.profile.contacts.contracts.model

import android.arch.persistence.room.*
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import java.io.Serializable

/**
 * @date 26/03/2018 1:37 PM
 * @author KaySaith
 */
@Entity(tableName = "contact")
data class ContactTable(
	@PrimaryKey(autoGenerate = true)
	var id: Int = 0,
	var avatar: String = "",
	var name: String = "",
	var defaultAddress: String,
	var ethSeriesAddress: String,
	var eosAddress: String,
	var eosJungle: String,
	var btcMainnetAddress: String,
	var btcSeriesTestnetAddress: String,
	var etcAddress: String,
	var ltcAddress: String,
	var bchAddress: String
) : Serializable {

	@Ignore
	constructor() : this(
		0,
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		""
	)

	companion object {
		@JvmField
		val dao = GoldStoneDataBase.database.contactDao()

		fun insertContact(contact: ContactTable, callback: () -> Unit = {}) {
			load {
				GoldStoneDataBase.database.contactDao().insert(contact)
			} then {
				callback()
			}
		}

		fun deleteContactByID(id: Int, callback: () -> Unit) {
			load {
				GoldStoneDataBase.database.contactDao().deleteByID(id)
			} then { callback() }
		}

		fun getAllContactAddresses(hold: (all: List<String>) -> Unit) {
			load {
				val all =
					GoldStoneDataBase.database.contactDao().getAllContacts()
				all.map {
					listOf(
						it.bchAddress,
						it.ltcAddress,
						it.btcSeriesTestnetAddress,
						it.btcMainnetAddress,
						it.eosJungle,
						it.eosAddress,
						it.etcAddress,
						it.ethSeriesAddress
					)
				}.flatten()
			} then (hold)
		}

	}
}

fun List<ContactTable>.getCurrentAddresses(contract: TokenContract): List<ContactTable> {
	return when {
		contract.isBTC() -> map {
			it.apply {
				defaultAddress =
					if (SharedValue.isTestEnvironment()) it.btcSeriesTestnetAddress
					else it.btcMainnetAddress
			}
		}
		contract.isLTC() -> map {
			it.apply {
				defaultAddress =
					if (SharedValue.isTestEnvironment()) it.btcSeriesTestnetAddress
					else it.ltcAddress
			}
		}
		contract.isBCH() -> map {
			it.apply {
				defaultAddress =
					if (SharedValue.isTestEnvironment()) it.btcSeriesTestnetAddress
					else it.bchAddress
			}
		}
		contract.isEOS() || contract.isEOSToken() -> map {
			it.apply {
				defaultAddress =
					if (SharedValue.isTestEnvironment()) it.eosJungle
					else it.eosAddress
			}
		}
		else -> map {
			it.apply {
				defaultAddress = ethSeriesAddress
			}
		}
	}
}

fun List<ContactTable>.getContactName(address: String): String {
	// `BTC` 的 `toAddress` 可能是多地址, 所以采用了包含关系判断.
	return find {
		it.ethSeriesAddress.equals(address, true)
			|| it.btcSeriesTestnetAddress.contains(address, true)
			|| it.btcMainnetAddress.contains(address, true)
	}?.name ?: address
}

@Dao
interface ContractDao {

	@Query("SELECT * FROM contact ORDER BY id DESC")
	fun getAllContacts(): List<ContactTable>

	@Query("SELECT * FROM contact WHERE id LIKE :id")
	fun getContact(id: Int): ContactTable?

	@Query("SELECT * FROM contact WHERE (ethSeriesAddress LIKE :address OR bchAddress LIKE :address  OR ltcAddress LIKE :address  OR etcAddress LIKE :address  OR btcMainnetAddress LIKE :address OR btcSeriesTestnetAddress LIKE :address)")
	fun getContactByAddress(address: String): ContactTable?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(contact: ContactTable)

	@Query("DELETE FROM contact WHERE id LIKE :id")
	fun deleteByID(id: Int)

	@Delete
	fun delete(contact: ContactTable)

	@Update
	fun update(contact: ContactTable)
}