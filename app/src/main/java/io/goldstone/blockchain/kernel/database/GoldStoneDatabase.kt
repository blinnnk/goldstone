package io.goldstone.blockchain.kernel.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import io.goldstone.blockchain.kernel.commonmodel.MyTokenDao
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletDao
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenDao
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable

/**
 * @date 03/04/2018 12:53 PM
 * @author KaySaith
 */

@Database(entities = [(WalletTable::class), (MyTokenTable::class), (DefaultTokenTable::class)], version = GoldStoneDataBase.databaseVersion, exportSchema = false)
abstract class GoldStoneDataBase : RoomDatabase() {
  abstract fun walletDao(): WalletDao
  abstract fun myTokenDao(): MyTokenDao
  abstract fun defaultTokenDao(): DefaultTokenDao

  companion object {
    const val databaseVersion = 1
    private const val databaseName = "GoldStone.db"
    lateinit var database: GoldStoneDataBase

    fun initDatabase(context: Context) {
      database = Room.databaseBuilder(context, GoldStoneDataBase::class.java, databaseName).addMigrations().build()
    }
  }
}