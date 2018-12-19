package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.alignParentBottom
import com.blinnnk.extension.centerInHorizontal
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.GradientType
import io.goldstone.blockchain.common.component.GradientView
import io.goldstone.blockchain.common.component.button.RoundShadowIcon
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.component.title.twoLineTitles
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.TransactionSize
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import org.jetbrains.anko.*

/**
 * @date 27/03/2018 3:33 AM
 * @author KaySaith
 */
class TransactionDetailHeaderView(context: Context) : RelativeLayout(context) {

	private lateinit var info: TwoLineTitles
	private val gradientView = GradientView(context)
	private val statusIcon = RoundShadowIcon(context)
	private val conformationBar by lazy { TextView(context) }

	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, TransactionSize.headerView)
		gradientView.apply {
			setStyle(GradientType.Tree, TransactionSize.headerView)
			layoutParams = RelativeLayout.LayoutParams(matchParent, TransactionSize.headerView)
		}.into(this)
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			statusIcon.into(this)
			info = twoLineTitles {
				layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
				isCenter = true
				setWildStyle()
			}
			info.setMargins<LinearLayout.LayoutParams> {
				topMargin = 20.uiPX()
			}
		}.apply {
			val params = RelativeLayout.LayoutParams(matchParent, wrapContent)
			params.margin = 50.uiPX()
			layoutParams = params
		}.centerInHorizontal()

		conformationBar.apply {
			visibility = View.GONE
			textSize = fontSize(12)
			typeface = GoldStoneFont.heavy(context)
			textColor = Spectrum.white
			gravity = Gravity.CENTER
			backgroundColor = GrayScale.Opacity5Black
			layoutParams = RelativeLayout.LayoutParams(matchParent, 35.uiPX())
		}.into(this)
		conformationBar.alignParentBottom()
	}

	@SuppressLint("SetTextI18n")
	fun setIconStyle(
		headerModel: TransactionHeaderModel
	) {
		val type =
			if (headerModel.isReceive) TransactionText.transferResultReceived
			else TransactionText.transferResultSent
		info.title.text =
			"$type ${headerModel.count.formatCount()} " +
			"${headerModel.symbol} ${if (headerModel.isReceive)
				TransactionText.transferResultFrom else TransactionText.transferResultTo}"
		info.subtitle.text = headerModel.address

		if (headerModel.isError) {
			statusIcon.iconColor = Spectrum.lightRed
			statusIcon.src = R.drawable.error_icon
			statusIcon.showPendingIcon(false)
			return
		}

		if (headerModel.isPending) {
			statusIcon.iconColor = Spectrum.lightRed
			statusIcon.showPendingIcon()
		} else {
			statusIcon.showPendingIcon(false)
			if (!headerModel.isReceive) {
				statusIcon.iconColor = Spectrum.DarkYellow
				statusIcon.src = R.drawable.send_icon
				statusIcon.setColorFilter(GrayScale.Opacity5Black)
			} else {
				statusIcon.iconColor = Spectrum.green
				statusIcon.src = R.drawable.receive_icon
			}
		}
	}
}