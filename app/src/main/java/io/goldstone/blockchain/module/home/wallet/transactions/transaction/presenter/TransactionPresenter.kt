package io.goldstone.blockchain.module.home.wallet.transactions.transaction.presenter

import android.os.Bundle
import android.support.v4.app.Fragment
import com.blinnnk.extension.hideChildFragment
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment

/**
 * @date 24/03/2018 2:37 AM
 * @author KaySaith
 */

class TransactionPresenter(
  override val fragment: TransactionFragment
) : BaseOverlayPresenter<TransactionFragment>() {

  inline fun<reified T: Fragment> showTargetFragment(title: String, previousTitle: String, bundle: Bundle? = null) {
    System.out.println("fuck what happened")
    fragment.apply {
      headerTitle = title
      childFragmentManager.fragments.last()?.let {
        hideChildFragment(it)
        addFragmentAndSetArgument<T>(ContainerID.content) {
          putAll(bundle)
        }
        overlayView.header.apply {
          showBackButton(true) {
            headerTitle = previousTitle
            popFragmentFrom<T>()
          }
          showCloseButton(false)
        }
      }
    }
  }

}

