package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view

import android.view.ViewGroup
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.TokenManagementText
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.presenter.TokenManagementPresenter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListFragment

/**
 * @date 25/03/2018 2:46 AM
 * @author KaySaith
 */

class TokenManagementFragment : BaseOverlayFragment<TokenManagementPresenter>() {

  override val presenter = TokenManagementPresenter(this)

  override fun ViewGroup.initView() {
    headerTitle = TokenManagementText.addToken

    addFragmentAndSetArgument<TokenManagementListFragment>(ContainerID.content) {
      // Send Arguments
    }

  }

}