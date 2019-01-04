package io.goldstone.blockchain.module.home.wallet.walletdetail.view

import android.R
import android.content.Context
import android.graphics.PorterDuff
import android.view.Gravity
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.FixTextLength
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.button.StoneButton
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.AvatarManager
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.WalletDetailSize
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailHeaderModel
import me.itangqi.waveloadingview.WaveLoadingView
import org.jetbrains.anko.*

/**
 * @date 23/03/2018 4:21 PM
 * @author KaySaith
 */
class WalletDetailHeaderView(context: Context) : RelativeLayout(context) {
	var model: WalletDetailHeaderModel? by observing(null) {
		model?.apply {
			balanceTitle.text = totalBalance.toDouble().formatCurrency()
			currentAccount.info.title.text = object : FixTextLength() {
				override var text = model?.name.orEmpty()
				override val maxWidth = 26.uiPX().toFloat()
				override val textSize: Float = fontSize(16)
			}.getFixString()

			currentAccount.info.subtitle.text = address
			balanceSubtitle.text = WalletSlideHeader.setBalanceInfo()
			// 钱包一样的话每次刷新不用重新加载图片给内存造成压力
			if (avatar.isNull())
				currentAccount.avatar.glideImage(AvatarManager.getAvatarPath(SharedWallet.getCurrentWalletID()))
			else currentAccount.avatar.glideImage(avatar)
		}
	}
	val currentAccount = CurrentAccountView(context)
	private val waveView = WaveLoadingView(context)
	private var progressBar: ProgressBar? = null
	private val balanceTitle = TextView(context)
	private lateinit var balanceSubtitle: TextView
	val sendButton = StoneButton(context)
	val depositButton = StoneButton(context)

	init {
		backgroundColor = Spectrum.blue
		setWillNotDraw(false)
		layoutParams = RelativeLayout.LayoutParams(matchParent, WalletDetailSize.headerHeight)
		waveView.apply {
			layoutParams =
				RelativeLayout.LayoutParams(matchParent, WalletDetailSize.headerHeight)
			setShapeType(WaveLoadingView.ShapeType.RECTANGLE)
			progressValue = 30
			waveColor = Spectrum.backgroundBlue
			setAnimDuration(30000)
			setAmplitudeRatio(50)
			startAnimation()
		}.into(this)

		currentAccount.into(this)
		currentAccount.apply {
			centerInHorizontal()
			y += 28.uiPX()
		}

		verticalLayout {
			balanceTitle.apply {
				textSize = fontSize(36)
				typeface = GoldStoneFont.black(context)
				textColor = Spectrum.white
				gravity = Gravity.CENTER_HORIZONTAL
			}.into(this)
			balanceSubtitle = textView {
				textSize = fontSize(12)
				typeface = GoldStoneFont.medium(context)
				textColor = Spectrum.opacity5White
				gravity = Gravity.CENTER_HORIZONTAL
			}.lparams(matchParent, matchParent)
		}.apply {
			y += 15.uiPX()
			centerInParent()
		}

		relativeLayout {
			lparams {
				width = matchParent
				height = 50.uiPX()
				alignParentBottom()
			}

			sendButton.apply {
				text = CommonText.send
			}.into(this)
			sendButton.setMargins<RelativeLayout.LayoutParams> {
				leftMargin = 15.uiPX()
			}
			depositButton.apply { text = CommonText.deposit }.into(this)
			depositButton.setMargins<RelativeLayout.LayoutParams> {
				rightMargin = 15.uiPX()
			}
			depositButton.alignParentRight()
		}
	}

	fun showLoadingView(status: Boolean) {
		if (status && progressBar.isNull()) {
			progressBar = ProgressBar(
				this.context,
				null,
				R.attr.progressBarStyleInverse
			).apply {
				indeterminateDrawable.setColorFilter(Spectrum.white, PorterDuff.Mode.SRC_ATOP)
				layoutParams = RelativeLayout.LayoutParams(16.uiPX(), 16.uiPX())
				y -= 70.uiPX()
			}
			progressBar?.into(this)
			progressBar?.alignParentBottom()
			progressBar?.centerInHorizontal()
		} else {
			if (progressBar.isNotNull()) {
				removeView(progressBar)
				progressBar = null
			}
		}
	}

	fun clearBitmap() {
	}
}

