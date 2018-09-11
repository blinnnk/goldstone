package io.goldstone.blockchain.module.common.tokendetail.tokeninfo.view

import android.graphics.Bitmap
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.SessionTitleView
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.component.button.RadiusButton
import io.goldstone.blockchain.common.component.cell.GraySquareCell
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.common.tokendetail.tokeninfo.presenter.TokenInfoPresenter
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick


/**
 * @author KaySaith
 * @date  2018/09/11
 */

class TokenInfoFragment : BaseFragment<TokenInfoPresenter>() {

	private val qrImage by lazy { ImageView(context!!) }
	private val coinInfo by lazy { TwoLineTitles(context!!) }

	private val balanceCell by lazy {
		GraySquareCell(context!!).apply {
			showArrow()
			setTitle("BALANCE")
			setSubtitle(CommonText.calculating)
		}
	}

	private val addressCell by lazy {
		GraySquareCell(context!!).apply {
			showArrow()
			setTitle("ADDRESS")
		}
	}

	private val hash160Cell by lazy {
		GraySquareCell(context!!).apply {
			showArrow()
			setTitle("HASH160")
		}
	}

	private val transactionCountCell by lazy {
		GraySquareCell(context!!).apply {
			showArrow()
			setTitle("TRANSACTION COUNT")
			setSubtitle(CommonText.calculating)
		}
	}

	private val totalReceiveCell by lazy {
		GraySquareCell(context!!).apply {
			showArrow()
			setTitle("TOTAL RECEIVED")
			setSubtitle(CommonText.calculating)
		}
	}

	private val totalSentCell by lazy {
		GraySquareCell(context!!).apply {
			showArrow()
			setTitle("TOTAL SENT")
			setSubtitle(CommonText.calculating)
		}
	}

	private val checkDetailButton by lazy {
		RadiusButton(context!!).apply {
			setTitle("CHECK DETAIL")
			setIcon(R.drawable.bithumb_icon)
		}
	}

	override val presenter = TokenInfoPresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams(matchParent, wrapContent)
				gravity = Gravity.CENTER_HORIZONTAL
				linearLayout {
					layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
					topPadding = 15.uiPX()
					qrImage.apply {
						x -= 10.uiPX()
						layoutParams = LinearLayout.LayoutParams(150.uiPX(), 150.uiPX())
					}.into(this)
					verticalLayout {
						lparams(ScreenSize.widthWithPadding - 160.uiPX(), wrapContent)
						topPadding = 20.uiPX()
						coinInfo.apply {
							setBlackTitles(fontSize(14), 5.uiPX())
							layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
							isFloatRight = true
							bottomPadding = 15.uiPX()
						}.into(this)
						checkDetailButton.into(this)
					}
				}
				SessionTitleView(context).setTitle("BALANCE").into(this)
				balanceCell.into(this)
				SessionTitleView(context).setTitle("ACCOUNT INFORMATION").into(this)
				addressCell.into(this)
				// 只有 bitcoin series coin type 的会额外显示 `hash160` 格式的地址
				if (presenter.isBTCSeriesCoin()) hash160Cell.into(this)
				SessionTitleView(context).setTitle("TRANSACTION").into(this)
				transactionCountCell.into(this)
				totalReceiveCell.into(this)
				totalSentCell.into(this)
			}
		}
	}

	fun showQRCodeImage(code: Bitmap?) {
		qrImage.glideImage(code)
	}

	fun showAddress(address: String, has160: String?) {
		addressCell.setSubtitle(address)
		has160?.let { hash160Cell.setSubtitle(it) }
	}

	fun showTransactionCount(count: Int) {
		transactionCountCell.setSubtitle("$count")
	}

	fun showCoinInfo(title: String, subtitle: String) {
		coinInfo.apply {
			this.title.text = title
			this.subtitle.text = "this account's latest active time is $subtitle"
		}
	}

	fun setCheckDetailButtonIconAndEvent(icon: Int, action: () -> Unit) {
		checkDetailButton.setIcon(icon)
		checkDetailButton.onClick {
			checkDetailButton.preventDuplicateClicks()
			action()
		}
	}

	fun showBalance(balance: String) {
		balanceCell.setSubtitle(balance)
	}

	fun showTotalValue(received: String, sent: String) {
		totalReceiveCell.setSubtitle(received)
		totalSentCell.setSubtitle(sent)
	}
}