package io.goldstone.blockchain.module.home.profile.contacts.contracts.model

import android.arch.persistence.room.*
import com.blinnnk.extension.isNull
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
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
	var ethERCAndETCAddress: String,
	var btcMainnetAddress: String,
	var btcTestnetAddress: String
) : Serializable {
	
	@Ignore constructor() : this(
		0,
		"",
		"",
		"",
		"",
		"",
		""
	)
	
	companion object {
		
		fun insertContact(
			contact: ContactTable,
			callback: () -> Unit = {}
		) {
			load {
				GoldStoneDataBase.database.contactDao().insert(contact)
			} then {
				callback()
			}
		}
		
		fun getAllContacts(callback: (ArrayList<ContactTable>) -> Unit) {
			load {
				GoldStoneDataBase.database.contactDao().getAllContacts()
			} then {
				callback(it.toArrayList())
			}
		}
		
		fun hasContacts(address: String, hasContact: (Boolean) -> Unit) {
			load {
				GoldStoneDataBase.database.contactDao().getContactByAddress(address)
			} then {
				hasContact(!it.isNull())
			}
		}
		
		fun deleteContactByID(
			id: Int,
			callback: () -> Unit
		) {
			load {
				GoldStoneDataBase.database.contactDao().apply {
					getContacts(id)?.let { delete(it) }
				}
			} then {
				callback()
			}
		}
	}
}

@Dao
interface ContractDao {
	
	@Query("SELECT * FROM contact ORDER BY id DESC")
	fun getAllContacts(): List<ContactTable>
	
	@Query("SELECT * FROM contact WHERE id LIKE :id")
	fun getContacts(id: Int): ContactTable?
	
	@Query("SELECT * FROM contact WHERE (ethERCAndETCAddress LIKE :address OR btcMainnetAddress LIKE :address OR btcTestnetAddress LIKE :address)")
	fun getContactByAddress(address: String): ContactTable?
	
	@Insert
	fun insert(contact: ContactTable)
	
	@Delete
	fun delete(contact: ContactTable)
	
	@Update
	fun update(contact: ContactTable)
}