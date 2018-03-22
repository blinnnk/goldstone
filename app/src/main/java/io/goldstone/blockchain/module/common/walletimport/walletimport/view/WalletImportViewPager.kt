package io.goldstone.blockchain.module.common.walletimport.walletimport.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import com.blinnnk.base.HoneyBaseFragmentAdapter
import com.blinnnk.base.SubFragment
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.common.value.ViewPagerID
import io.goldstone.blockchain.module.common.walletimport.keystoreimport.view.KeystoreImportFragment
import io.goldstone.blockchain.module.common.walletimport.mnemonicimport.view.MnemonicImportDetailFragment
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view.PrivateKeyImportFragment
import io.goldstone.blockchain.module.common.walletimport.watchonly.view.WatchOnltyImportFragment
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
  private val watchOnlyFragment = WatchOnltyImportFragment()

  init {
    id = ViewPagerID.walletImport
    fragmentList.apply {
      add(SubFragment(mnemonicImportFragment, FragmentTag.mnemonicImportDetail))
      add(SubFragment(keystoreImportFragment, FragmentTag.keystoreImport))
      add(SubFragment(privateKeyFragment, FragmentTag.privateKeyImport))
      add(SubFragment(watchOnlyFragment, FragmentTag.watchOnlyImport))
    }
    adapter = HoneyBaseFragmentAdapter(fragment.childFragmentManager, fragmentList)
  }

}