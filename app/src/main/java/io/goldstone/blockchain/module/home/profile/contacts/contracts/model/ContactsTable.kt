package io.goldstone.blockchain.module.home.profile.contacts.contracts.model

import android.arch.persistence.room.*
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase

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
  var address: String = ""
) {

  @Ignore constructor() : this(0, "", "", "")

  companion object {

    fun insertContact(contact: ContactTable, callback: () -> Unit = {}) {
      coroutinesTask({
        GoldStoneDataBase.database.contactDao().insert(contact)
      }) {
        callback()
      }
    }

    fun getAllContacts(callback: (ArrayList<ContactTable>) -> Unit = {}) {
      coroutinesTask({
        GoldStoneDataBase.database.contactDao().getAllContacts()
      }) {
        callback(it.toArrayList())
      }
    }

    fun deleteContactByID(id: Int, callback: () -> Unit = {}) {
      coroutinesTask({
        GoldStoneDataBase.database.contactDao().apply {
          getContacts(id)?.let { delete(it) }
        }
      }) {
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

  @Insert
  fun insert(contact: ContactTable)

  @Delete
  fun delete(contact: ContactTable)

  @Update
  fun update(contact: ContactTable)
}