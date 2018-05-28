package io.goldstone.blockchain.module.common.walletimport.walletimport.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import com.blinnnk.base.HoneyBaseFragmentAdapter
import com.blinnnk.base.SubFragment
import io.goldstone.blockchain.common.component.MenuBar
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.common.value.ViewPagerID
import io.goldstone.blockchain.module.common.walletimport.keystoreimport.view.KeystoreImportFragment
import io.goldstone.blockchain.module.common.walletimport.mnemonicimport.view.MnemonicImportDetailFragment
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view.PrivateKeyImportFragment
import io.goldstone.blockchain.module.common.walletimport.watchonly.view.WatchOnlyImportFragment
import org.jetbrains.anko.support.v4.onPageChangeListener
import java.util.*

@SuppressLint("ViewConstructor")
/**
 * @date 23/03/2018 1:43 AM
 * @author KaySaith
 */
class WalletImportViewPager(val fragment: Fragment) : ViewPager(fragment.context!!) {
	
	private var fragmentList = ArrayList<SubFragment>()
	private val mnemonicImportFragment = MnemonicImportDetailFragment()
	private val keystoreImportFragment = KeystoreImportFragment()
	private val privateKeyFragment = PrivateKeyImportFragment()
	private val watchOnlyFragment = WatchOnlyImportFragment()
	private var hasMovedToLeft = false
	
	init {
		id = ViewPagerID.walletImport
		fragmentList.apply {
			add(SubFragment(mnemonicImportFragment, FragmentTag.mnemonicImportDetail))
			add(SubFragment(keystoreImportFragment, FragmentTag.keystoreImport))
			add(SubFragment(privateKeyFragment, FragmentTag.privateKeyImport))
			add(SubFragment(watchOnlyFragment, FragmentTag.watchOnlyImport))
		}
		adapter = HoneyBaseFragmentAdapter(fragment.childFragmentManager, fragmentList)
		
		onPageChangeListener {
			onPageSelected {
				(fragment as WalletImportFragment).menuBar.apply {
					setSelectedStyle(currentItem, this)
				}
			}
		}
	}
	
	fun setSelectedStyle(index: Int, menuBar: MenuBar) {
		menuBar.apply {
			selectItem(index)
			if (index == 3 && !hasMovedToLeft) {
				hasMovedToLeft = true
				floatRight()
			}
			if (index == 0 && hasMovedToLeft) {
				hasMovedToLeft = false
				floatLeft()
			}
		}
	}
	
	override fun onPageScrolled(position: Int, offset: Float, offsetPixels: Int) {
		super.onPageScrolled(position, offset, offsetPixels)
		(fragment as WalletImportFragment).menuBar.apply {
			selectItem(currentItem)
		}
	}
}