package io.goldstone.blockchain.module.common.tokendetail.tokenasset.view

import android.graphics.Bitmap
import android.os.Build
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.scaleTo
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.view.GrayCardView
import io.goldstone.blockchain.common.component.ProgressView
import io.goldstone.blockchain.common.component.cell.GraySquareCell
import io.goldstone.blockchain.common.component.title.SessionTitleView
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.EOSAccountText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.common.tokendetail.tokenasset.presenter.TokenAssetPresenter
import io.goldstone.blockchain.module.common.tokendetail.tokeninfo.contract.TokenInfoViewInterface
import io.goldstone.blockchain.module.common.tokendetail.tokeninfo.view.TokenInfoView
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/10
 */

class TokenAssetFragment : BaseFragment<TokenAssetPresenter>(), TokenInfoViewInterface {

	override val pageTitle: String = "Asset"
	private val tokenInfoView by lazy { TokenInfoView(context!!) }
	private val balanceCell by lazy {
		GraySquareCell(context!!).apply {
			setTitle(TokenDetailText.balance)
			setSubtitle(CommonText.calculating)
		}
	}

	private val refundsCell by lazy {
		GraySquareCell(context!!).apply {
			setTitle(TokenDetailText.refunds)
			setSubtitle(CommonText.calculating)
		}
	}

	private val transactionCountCell by lazy {
		GraySquareCell(context!!).apply {
			setTitle(TokenDetailText.transactionCount)
			setSubtitle(CommonText.calculating)
		}
	}

	private val authorizationCell by lazy {
		GraySquareCell(context!!).apply {
			showArrow()
			setTitle(EOSAccountText.authority)
			setSubtitle(SharedAddress.getCurrentEOSAccount().accountName)
			click {
				val type = SharedWallet.getCurrentWalletType()
				when {
					type.isEOSMainnet() -> context.alert("This is a single name  watch only")
					type.isEOSJungle() -> context.alert("This is a single jungle name watch only")
					else -> presenter.showPublicKeyAccountNames()
				}
			}
		}
	}

	private val accountAddress by lazy {
		GraySquareCell(context!!).apply {
			setTitle(EOSAccountText.publicKey)
			val address =
				if (SharedAddress.getCurrentEOS().isEmpty()) "Account Name Only"
				else SharedAddress.getCurrentEOS().scaleTo(24)
			setSubtitle(address)
			onClick {
				this@apply.context?.clickToCopy(SharedAddress.getCurrentEOS())
				preventDuplicateClicks()
			}
		}
	}

	private val assetCard by lazy {
		GrayCardView(context!!).apply {
			layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, 255.uiPX())
		}
	}

	private val ramAssetCell by lazy {
		ProgressView(context!!).apply {
			setTitle(TokenDetailText.ram)
			setSubtitle(CommonText.calculating)
		}
	}

	private val cpuAssetCell by lazy {
		ProgressView(context!!).apply {
			setTitle(TokenDetailText.cpu)
			setSubtitle(CommonText.calculating)
		}
	}

	private val netAssetCell by lazy {
		ProgressView(context!!).apply {
			setTitle(TokenDetailText.net)
			setSubtitle(CommonText.calculating)
		}
	}

	override val presenter = TokenAssetPresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams(matchParent, wrapContent)
				bottomPadding = 20.uiPX()
				gravity = Gravity.CENTER_HORIZONTAL
				tokenInfoView.into(this)
				showAccountManagementCells()
				showTransactionCells()
				showAssetDashboard()
				SessionTitleView(context).setTitle(TokenDetailText.assetTools).into(this)
				linearLayout {
					lparams(ScreenSize.widthWithPadding, wrapContent)
					generateMethodCards()
				}
			}
		}
	}

	override fun setTokenInfo(qrCode: Bitmap?, title: String, subtitle: String, icon: Int, action: () -> Unit) {
		tokenInfoView.setData(qrCode, title, subtitle, icon, action)
	}

	override fun updateLatestActivationDate(date: String) {
		tokenInfoView.updateLatestActivationDate(date)
	}

	fun setEOSBalance(balance: String) {
		balanceCell.setSubtitle(balance)
	}

	fun setEOSRefunds(description: String) {
		refundsCell.setSubtitle(description)
	}

	fun setResourcesValue(
		ramAvailable: BigInteger,
		ramTotal: BigInteger,
		ramEOSCount: String,
		cpuAvailable: BigInteger,
		cpuTotal: BigInteger,
		cpuWeight: String,
		netAvailable: BigInteger,
		netTotal: BigInteger,
		netWeight: String
	) {
		ramAssetCell.setLeftValue(ramAvailable, TokenDetailText.available)
		ramAssetCell.setRightValue(ramTotal, TokenDetailText.total)
		ramAssetCell.setSubtitle(ramEOSCount)
		cpuAssetCell.setSubtitle(cpuWeight)
		cpuAssetCell.setLeftValue(
			cpuAvailable,
			TokenDetailText.available,
			true
		)
		cpuAssetCell.setRightValue(
			cpuTotal,
			TokenDetailText.total,
			true
		)
		netAssetCell.setSubtitle(netWeight)
		netAssetCell.setLeftValue(
			netAvailable,
			TokenDetailText.available
		)
		netAssetCell.setRightValue(
			netTotal,
			TokenDetailText.total
		)
	}

	fun setTransactionCount(count: String) {
		transactionCountCell.setSubtitle(count)
	}

	private fun ViewGroup.showAccountManagementCells() {
		SessionTitleView(context).setTitle(TokenDetailText.accountManagement).into(this)
		authorizationCell.into(this)
		accountAddress.into(this)
	}

	private fun ViewGroup.showTransactionCells() {
		SessionTitleView(context).setTitle(TokenDetailText.balance).into(this)
		balanceCell.into(this)
		refundsCell.into(this)
		transactionCountCell.into(this)
	}

	private fun ViewGroup.showAssetDashboard() {
		SessionTitleView(context).setTitle(TokenDetailText.resources).into(this)
		assetCard.apply {
			addView(ramAssetCell)
			addView(cpuAssetCell)
			addView(netAssetCell)
		}.into(this)
	}

	private fun ViewGroup.generateMethodCards() {
		listOf(
			Pair(R.drawable.cpu_icon, TokenDetailText.delegateCPU),
			Pair(R.drawable.net_icon, TokenDetailText.delegateNET),
			Pair(R.drawable.ram_icon, TokenDetailText.buySellRAM)
		).forEach { pair ->
			generateCardView(pair)
		}
	}

	private fun ViewGroup.generateCardView(info: Pair<Int, String>) {
		val cardWidth = (ScreenSize.widthWithPadding) / 3
		GrayCardView(context).apply {
			layoutParams = RelativeLayout.LayoutParams(cardWidth, 130.uiPX())
			getContainer().apply {
				onClick {
					if (SharedWallet.isWatchOnlyWallet())
						this@TokenAssetFragment.context.alert(AlertText.watchOnly)
					else presenter.showResourceTradingFragmentByTitle(info.second)
					preventDuplicateClicks()
				}
				imageView {
					setColorFilter(GrayScale.gray)
					scaleType = ImageView.ScaleType.CENTER_INSIDE
					imageResource = info.first
					layoutParams = RelativeLayout.LayoutParams(cardWidth, 80.uiPX())
				}
				textView(info.second) {
					textSize = fontSize(11)
					textColor = GrayScale.midGray
					typeface = GoldStoneFont.black(context)
					layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
					gravity = Gravity.CENTER_HORIZONTAL
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
						lineHeight = 13.uiPX()
					}
				}
			}
		}.into(this)
	}
}