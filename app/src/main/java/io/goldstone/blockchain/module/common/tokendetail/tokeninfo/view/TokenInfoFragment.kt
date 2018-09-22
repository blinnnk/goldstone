package io.goldstone.blockchain.module.common.tokendetail.tokeninfo.view

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import com.blinnnk.extension.into
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.cell.GraySquareCell
import io.goldstone.blockchain.common.component.title.SessionTitleView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.module.common.tokendetail.tokeninfo.contract.TokenInfoViewInterface
import io.goldstone.blockchain.module.common.tokendetail.tokeninfo.presenter.TokenInfoPresenter
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/09/11
 */

class TokenInfoFragment : BaseFragment<TokenInfoPresenter>(), TokenInfoViewInterface {

	private val tokenInfoView by lazy {
		TokenInfoView(context!!)
	}

	private val balanceCell by lazy {
		GraySquareCell(context!!).apply {
			setTitle(TokenDetailText.balance)
			setSubtitle(CommonText.calculating)
		}
	}

	private val addressCell by lazy {
		GraySquareCell(context!!).apply {
			setTitle(TokenDetailText.address)
		}
	}

	private val hash160Cell by lazy {
		GraySquareCell(context!!).apply {
			setTitle("HASH160")
		}
	}

	private val transactionCountCell by lazy {
		GraySquareCell(context!!).apply {
			setTitle(TokenDetailText.transactionCount)
			setSubtitle(CommonText.calculating)
		}
	}

	private val totalReceiveCell by lazy {
		GraySquareCell(context!!).apply {
			setTitle(TokenDetailText.totalReceived)
			setSubtitle(CommonText.calculating)
		}
	}

	private val totalSentCell by lazy {
		GraySquareCell(context!!).apply {
			setTitle(TokenDetailText.totalSent)
			setSubtitle(CommonText.calculating)
		}
	}

	override val presenter = TokenInfoPresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams(matchParent, wrapContent)
				gravity = Gravity.CENTER_HORIZONTAL
				tokenInfoView.into(this)
				SessionTitleView(context).setTitle(TokenDetailText.balance).into(this)
				balanceCell.into(this)
				SessionTitleView(context).setTitle(TokenDetailText.accountInformation).into(this)
				addressCell.into(this)
				// 只有 bitcoin series coin type 的会额外显示 `hash160` 格式的地址
				if (presenter.isBTCSeriesCoin()) hash160Cell.into(this)
				SessionTitleView(context).setTitle(TokenDetailText.transaction).into(this)
				transactionCountCell.into(this)
				totalReceiveCell.into(this)
				totalSentCell.into(this)
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter.showTransactionInfo {
			if (!it.isNone()) context.alert(it.message)
		}
		showBalance()
		showAddress()
	}

	fun <T> showTransactionCount(count: T) {
		transactionCountCell.setSubtitle("$count")
	}

	override fun setTokenInfo(
		qrCode: Bitmap?,
		title: String,
		subtitle: String,
		icon: Int,
		action: () -> Unit
	) {
		tokenInfoView.setData(qrCode, title, subtitle, icon, action)
	}

	override fun updateLatestActivationDate(date: String) {
		tokenInfoView.updateLatestActivationDate(date)
	}


	fun showTotalValue(received: String, sent: String) {
		totalReceiveCell.setSubtitle(received)
		totalSentCell.setSubtitle(sent)
	}

	private fun showAddress() {
		presenter.getAddress { address, hash160 ->
			addressCell.setSubtitle(address)
			hash160?.let { hash160Cell.setSubtitle(it) }
		}
	}

	private fun showBalance() {
		presenter.getBalance {
			balanceCell.setSubtitle(it)
		}
	}
}