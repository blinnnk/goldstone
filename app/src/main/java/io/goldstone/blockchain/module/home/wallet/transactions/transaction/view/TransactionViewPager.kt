package io.goldstone.blockchain.module.home.wallet.transactions.transaction.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import com.blinnnk.base.HoneyBaseFragmentAdapter
import com.blinnnk.base.SubFragment
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.common.value.ViewPagerID
import io.goldstone.blockchain.common.value.WalletType
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.bitcointransactionlist.view.BitcoinTransactionListFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.classictransactionlist.view.ClassicTransactionListFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.view.TransactionListFragment
import java.util.*

@SuppressLint("ViewConstructor")
/**
 * @date 23/03/2018 1:43 AM
 * @author KaySaith
 */
class TransactionViewPager(val fragment: Fragment) : ViewPager(fragment.context!!) {
	
	private var fragmentList = ArrayList<SubFragment>()
	private val classicTransactions by lazy { ClassicTransactionListFragment() }
	private val ethereumTransactions by lazy { TransactionListFragment() }
	private val bitcoinTransactions by lazy { BitcoinTransactionListFragment() }
	
	init {
		id = ViewPagerID.transactions
		fragmentList.apply {
			when (Config.getCurrentWalletType()) {
				WalletType.BTCTestOnly.content, WalletType.BTCOnly.content -> {
					add(SubFragment(bitcoinTransactions, FragmentTag.btcTransactions))
				}
				
				WalletType.ETHERCAndETCOnly.content -> {
					add(SubFragment(ethereumTransactions, FragmentTag.ethERC20Transactions))
					add(SubFragment(classicTransactions, FragmentTag.etcTransactions))
				}
				
				else -> {
					add(SubFragment(ethereumTransactions, FragmentTag.ethERC20Transactions))
					add(SubFragment(bitcoinTransactions, FragmentTag.btcTransactions))
					add(SubFragment(classicTransactions, FragmentTag.etcTransactions))
				}
			}
		}
		adapter = HoneyBaseFragmentAdapter(fragment.childFragmentManager, fragmentList)
	}
}