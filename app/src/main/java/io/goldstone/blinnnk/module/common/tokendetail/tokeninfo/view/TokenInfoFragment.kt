package io.goldstone.blinnnk.module.common.tokendetail.tokeninfo.view

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
import io.goldstone.blinnnk.common.base.gsfragment.GSFragment
import io.goldstone.blinnnk.common.component.cell.GraySquareCell
import io.goldstone.blinnnk.common.component.cell.graySquareCell
import io.goldstone.blinnnk.common.component.title.sessionTitle
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.language.TokenDetailText
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.common.utils.safeShowError
import io.goldstone.blinnnk.common.value.UMengEvent
import io.goldstone.blinnnk.crypto.multichain.isBTCSeries
import io.goldstone.blinnnk.crypto.multichain.isEOSToken
import io.goldstone.blinnnk.crypto.multichain.isERC20Token
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailcenter.view.TokenDetailCenterFragment
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blinnnk.module.common.tokendetail.tokeninfo.contract.TokenInfoContract
import io.goldstone.blinnnk.module.common.tokendetail.tokeninfo.presenter.TokenInfoPresenter
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

	private lateinit var tokenInfoView: TokenInfoView
	private lateinit var balanceCell: GraySquareCell
	private lateinit var addressCell: GraySquareCell
	private lateinit var tokenContractCell: GraySquareCell
	private lateinit var hash160Cell: GraySquareCell
	private lateinit var transactionCountCell: GraySquareCell
	private lateinit var totalReceiveCell: GraySquareCell
	private lateinit var totalSentCell: GraySquareCell
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
	override fun showTotalValue(received: String, sent: String) {
		totalReceiveCell.setSubtitle(received)
		totalSentCell.setSubtitle(sent)
	}

	override fun showAddress(address: String, hash160: String) {
		addressCell.click {
			it.context.clickToCopy(address)
			UMengEvent.add(context, UMengEvent.Click.TokenDetail.toCopyAddress, "common address")
		}.setSubtitle(address)
		if (token?.contract.isBTCSeries()) {
			hash160Cell.click {
				it.context.clickToCopy(hash160)
				UMengEvent.add(context, UMengEvent.Click.TokenDetail.toCopyAddress, "hash160")
			}.setSubtitle(hash160)
		}
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

					tokenInfoView = TokenInfoView(context)
					tokenInfoView.into(this)

					sessionTitle(TokenDetailText.balance)
					balanceCell = graySquareCell {
						setTitle(TokenDetailText.balance)
						setSubtitle(CommonText.calculating)
					}
					sessionTitle(TokenDetailText.accountInformation)
					addressCell = graySquareCell {
						setTitle(TokenDetailText.address)
					}
					if (token?.contract.isEOSToken() || token?.contract.isERC20Token()) {
						sessionTitle(TokenDetailText.tokenInfo)
						// 显示当前 `Token` 的合约地址
						tokenContractCell = graySquareCell {
							if (token?.contract.isERC20Token()) {
								setTitle(TokenDetailText.contract)
							} else {
								setTitle(TokenDetailText.code)
							}
							setSubtitle(token?.contract?.contract.orEmpty())
							click {
								context.clickToCopy(token?.contract?.contract.orEmpty())
							}
						}
					}
					// 只有 bitcoin series coin type 的会额外显示 `hash160` 格式的地址
					if (token?.contract.isBTCSeries()) {
						hash160Cell = graySquareCell {
							setTitle("HASH160")
						}
					}
					sessionTitle(TokenDetailText.transaction)

					transactionCountCell = graySquareCell {
						setTitle(TokenDetailText.transactionCount)
						setSubtitle(CommonText.calculating)
					}
					totalReceiveCell = graySquareCell {
						setTitle(TokenDetailText.totalReceived)
						setSubtitle(CommonText.calculating)
					}
					totalSentCell = graySquareCell {
						setTitle(TokenDetailText.totalSent)
						setSubtitle(CommonText.calculating)
					}
				}
			}
		}.view
	}
}