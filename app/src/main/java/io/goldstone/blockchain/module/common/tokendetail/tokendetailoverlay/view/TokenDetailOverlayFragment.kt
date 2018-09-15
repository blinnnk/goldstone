package io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view

import android.view.ViewGroup
import com.blinnnk.extension.orFalse
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.eos.EOSWalletType
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.presenter.TokenDetailOverlayPresenter
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel

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
			// 正常的 `Token` 跳转到普通的界面
			else -> presenter.showTokenDetailCenterFragment(token)
		}
	}

}