package io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.formatCurrency
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
	private var valueHeader: TwoLineTitles? = null
	override val presenter = TokenDetailOverlayPresenter(this)
	
	override fun ViewGroup.initView() {
		setValueHeader(token)
		when {
			isFromQuickTransfer -> presenter.showAddressSelectionFragment(true)
			isFromQuickDeposit -> presenter.showDepositFragment(true)
			else -> presenter.showTokenDetailFragment(token)
		}
	}
	
	@SuppressLint("SetTextI18n")
	fun setValueHeader(token: WalletDetailCellModel?) {
		overlayView.header.title.isHidden()
		valueHeader.isNull() isTrue {
			customHeader = {
				valueHeader = TwoLineTitles(context)
				valueHeader?.apply {
					title.text = "${WalletText.tokenDetailHeaderText} ${token?.symbol}"
					subtitle.text =
						CryptoUtils.scaleTo32(
							"${token?.count} ${token?.symbol} ≈ ${token?.currency?.formatCurrency()} " +
							"(${Config.getCurrencyCode
							()})"
						)
					setBigWhiteStyle(fontSize(14).toInt())
					isCenter = true
				}?.into(this)
				valueHeader?.apply {
					setCenterInHorizontal()
					y += 15.uiPX()
				}
			}
		} otherwise {
			valueHeader?.visibility = View.VISIBLE
		}
	}
	
	fun recoverHeader() {
		overlayView.header.title.visibility = View.VISIBLE
		valueHeader?.visibility = View.GONE
	}
	
	fun recoveryValueHeader() {
		overlayView.header.title.visibility = View.GONE
		valueHeader?.visibility = View.VISIBLE
	}
}