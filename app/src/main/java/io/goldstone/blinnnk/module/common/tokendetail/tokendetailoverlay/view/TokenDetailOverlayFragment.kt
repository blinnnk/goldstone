package io.goldstone.blinnnk.module.common.tokendetail.tokendetailoverlay.view

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.orFalse
import io.goldstone.blinnnk.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blinnnk.common.component.button.RoundButton
import io.goldstone.blinnnk.common.language.TokenDetailText
import io.goldstone.blinnnk.common.utils.getMainActivity
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.common.value.ContainerID
import io.goldstone.blinnnk.common.value.FragmentTag
import io.goldstone.blinnnk.crypto.eos.EOSWalletType
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailoverlay.presenter.TokenDetailOverlayPresenter
import io.goldstone.blinnnk.module.home.home.view.findIsItExist
import io.goldstone.blinnnk.module.home.wallet.walletdetail.model.WalletDetailCellModel

/**
 * @date 27/03/2018 3:41 PM
 * @author KaySaith
 */
class TokenDetailOverlayFragment : BaseOverlayFragment<TokenDetailOverlayPresenter>() {

	val token by lazy {
		arguments?.get(ArgumentKey.tokenDetail) as? WalletDetailCellModel
	}
	val isFromQuickTransfer by lazy {
		arguments?.getBoolean(ArgumentKey.fromQuickTransfer).orFalse()
	}
	val isFromQuickDeposit by lazy {
		arguments?.getBoolean(ArgumentKey.fromQuickDeposit).orFalse()
	}
	var confirmButton: RoundButton? = null

	override val presenter = TokenDetailOverlayPresenter(this)

	override fun ViewGroup.initView() {
		headerTitle = TokenDetailText.tokenDetail
		when {
			isFromQuickTransfer -> presenter.showAddressSelectionFragment(true)
			isFromQuickDeposit -> presenter.showDepositFragment(true)
			// 已经激活并且设置了默认账号
			token?.eosWalletType == EOSWalletType.Available ->
				presenter.showTokenDetailCenterFragment(token)
			// 已经激活到没有设置默认账号
			token?.eosWalletType == EOSWalletType.NoDefault ->
				presenter.showEOSAccountSelectionFragment(token)
			// 没激活果的公钥地址
			token?.eosWalletType == EOSWalletType.Inactivated ->
				presenter.showEOSActivationModeFragment(token)
			else -> presenter.showTokenDetailCenterFragment(token)
		}
	}

	companion object {
		fun show(context: Context?, bundle: Bundle) {
			context?.getMainActivity()?.apply {
				findIsItExist(FragmentTag.tokenDetail) isFalse {
					addFragmentAndSetArguments<TokenDetailOverlayFragment>(
						ContainerID.main,
						FragmentTag.tokenDetail
					) {
						putAll(bundle)
					}
				}
			}
		}
	}
}