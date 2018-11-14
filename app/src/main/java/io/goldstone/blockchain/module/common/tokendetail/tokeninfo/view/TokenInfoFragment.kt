package io.goldstone.blockchain.module.common.tokendetail.tokeninfo.view

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blinnnk.extension.getGrandFather
import com.blinnnk.extension.into
import com.blinnnk.util.clickToCopy
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.gsfragment.GSFragment
import io.goldstone.blockchain.common.component.cell.GraySquareCell
import io.goldstone.blockchain.common.component.title.SessionTitleView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.crypto.multichain.isBTCSeries
import io.goldstone.blockchain.module.common.tokendetail.tokendetailcenter.view.TokenDetailCenterFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokendetail.tokeninfo.contract.TokenInfoContract
import io.goldstone.blockchain.module.common.tokendetail.tokeninfo.presenter.TokenInfoPresenter
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent


/**
 * @author KaySaith
 * @date  2018/09/11
 */

class TokenInfoFragment : GSFragment(), TokenInfoContract.GSView {

	override val pageTitle: String = "Token Info"

	private val token by lazy {
		getParentFragment<TokenDetailCenterFragment>()?.token
	}

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

	override lateinit var presenter: TokenInfoContract.GSPresenter

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		token?.let {
			presenter = TokenInfoPresenter(it, this)
			presenter.start()
		}
	}

	override fun showError(error: Throwable) = safeShowError(error)
	override fun showTransactionCount(count: Int) = transactionCountCell.setSubtitle("$count")
	override fun showActivationDate(date: String) = tokenInfoView.updateLatestActivationDate(date)
	override fun showTotalValue(received: String, sent: String) {
		totalReceiveCell.setSubtitle(received)
		totalSentCell.setSubtitle(sent)
	}

	override fun showAddress(address: String, hash160: String) {
		addressCell.click {
			it.context.clickToCopy(address)
		}.setSubtitle(address)
		hash160Cell.click {
			it.context.clickToCopy(hash160)
		}.setSubtitle(hash160)
	}

	override fun showBalance(balance: String) = balanceCell.setSubtitle(balance)
	override fun setTokenInfo(
		qrCode: Bitmap?,
		title: String,
		subtitle: String,
		icon: Int,
		url: String
	) {
		tokenInfoView.setData(qrCode, title, subtitle, icon) {
			TokenInfoPresenter.showThirdPartyAddressDetail(
				getGrandFather<TokenDetailOverlayFragment>(),
				url
			)
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
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
					if (token?.contract.isBTCSeries()) hash160Cell.into(this)
					SessionTitleView(context).setTitle(TokenDetailText.transaction).into(this)
					transactionCountCell.into(this)
					totalReceiveCell.into(this)
					totalSentCell.into(this)
				}
			}
		}.view
	}
}