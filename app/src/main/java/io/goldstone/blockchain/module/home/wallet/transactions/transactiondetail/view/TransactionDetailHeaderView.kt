package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.updateColorAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.HoneyColor
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.GradientType
import io.goldstone.blockchain.common.component.GradientView
import io.goldstone.blockchain.common.component.button.RoundIcon
import io.goldstone.blockchain.common.component.title.TwoLineTitles
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

	private val info = TwoLineTitles(context)
	private val gradientView = GradientView(context)
	private val icon = RoundIcon(context)
	private var pendingIcon: ProgressBar? = null
	private val conformationBar by lazy { TextView(context) }

	init {
		bottomPadding = 10.uiPX()
		gradientView.apply {
			setStyle(GradientType.Tree, TransactionSize.headerView)
			layoutParams = RelativeLayout.LayoutParams(matchParent, TransactionSize.headerView)
		}.into(this)

		verticalLayout {
			lparams {
				width = (ScreenSize.Width * 0.8).toInt()
				height = wrapContent
				topMargin = 40.uiPX()
			}
			gravity = Gravity.CENTER_HORIZONTAL
			icon.apply {
				y += 5.uiPX()
				iconSize = 60.uiPX()
				setColorFilter(GrayScale.Opacity2Black)
				elevation = 10.uiPX().toFloat()
			}.into(this)

			info.apply {
				layoutParams = LinearLayout.LayoutParams(matchParent, 70.uiPX())
				isCenter = true
				setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
				setWildStyle()
			}.into(this)
		}.setCenterInHorizontal()

		conformationBar.apply {
			visibility = View.GONE
			textSize = fontSize(12)
			typeface = GoldStoneFont.heavy(context)
			textColor = Spectrum.white
			gravity = Gravity.CENTER
			backgroundColor = GrayScale.Opacity5Black
			layoutParams = RelativeLayout.LayoutParams(matchParent, 35.uiPX())
		}.into(this)
		conformationBar.setAlignParentBottom()
	}

	@SuppressLint("SetTextI18n")
	fun updateConformationBar(confirmedCount: Int, latestIrreversibleCount: Int) {
		conformationBar.visibility = View.VISIBLE
		conformationBar.text = "$confirmedCount / ${TransactionText.confirmedBlocks(latestIrreversibleCount)}"
		conformationBar.updateColorAnimation(Spectrum.green, GrayScale.Opacity5Black)
	}

	@SuppressLint("SetTextI18n")
	fun updateEOSConformationBar(confirmedCount: Int, latestIrreversibleCount: Int) {
		conformationBar.visibility = View.VISIBLE
		conformationBar.text = "$confirmedCount / ${TransactionText.irreversibleConfirmedBlocks(latestIrreversibleCount)}"
		conformationBar.updateColorAnimation(Spectrum.green, GrayScale.Opacity5Black)
	}

	@SuppressLint("SetTextI18n")
	fun updateEOSConformationBarFinished() {
		conformationBar.visibility = View.VISIBLE
		conformationBar.text = TransactionText.irreversibleBlockConfirmed
		conformationBar.updateColorAnimation(Spectrum.green, GrayScale.Opacity5Black)
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
			icon.iconColor = Spectrum.lightRed
			icon.src = R.drawable.error_icon
			showPendingIcon(false)
			return
		}

		if (headerModel.isPending) {
			icon.iconColor = Spectrum.lightRed
			showPendingIcon()
		} else {
			showPendingIcon(false)
			if (!headerModel.isReceive) {
				icon.iconColor = Spectrum.DarkYellow
				icon.src = R.drawable.send_icon
				icon.setColorFilter(GrayScale.Opacity5Black)
			} else {
				icon.iconColor = Spectrum.green
				icon.src = R.drawable.receive_icon
			}
		}
	}

	private fun showPendingIcon(status: Boolean = true) {
		pendingIcon isNotNull {
			if (!status) removeView(pendingIcon)
		} otherwise {
			if (status) {
				pendingIcon =
					ProgressBar(this.context, null, android.R.attr.progressBarStyleInverse).apply {
						indeterminateDrawable.setColorFilter(
							HoneyColor.HoneyWhite,
							android.graphics.PorterDuff.Mode.MULTIPLY
						)
						RelativeLayout.LayoutParams(32.uiPX(), 32.uiPX())
						y += 50.uiPX()
					}
				addView(pendingIcon)
				pendingIcon?.setCenterInHorizontal()
			}
		}
	}
}