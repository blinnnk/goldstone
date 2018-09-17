package io.goldstone.blockchain.module.home.wallet.walletdetail.view

import android.R
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.FixTextLength
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.UnlimitedAvatar
import io.goldstone.blockchain.common.component.button.RoundButtonWithIcon
import io.goldstone.blockchain.common.component.button.StoneButton
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailHeaderModel
import me.itangqi.waveloadingview.WaveLoadingView
import org.jetbrains.anko.*

/**
 * @date 23/03/2018 4:21 PM
 * @author KaySaith
 * @rewriteDate 26/07/2018 3:30 PM
 * @reWriter wcx
 * @description 修改获取头像方法 UnlimitedAvatar创建bitmap
 */
class WalletDetailHeaderView(context: Context) : RelativeLayout(context) {
	
	private val avatarBitmap = UnlimitedAvatar(Config.getCurrentWalletID(), context).getBitmap()
	var model: WalletDetailHeaderModel? by observing(null) {
		model?.apply {
			if (avatar.isNull())
				currentAccount.avatar.glideImage(avatarBitmap)
			else currentAccount.avatar.glideImage(avatar)
			
			currentAccount.info.title.text = object : FixTextLength() {
				override var text = model?.name.orEmpty()
				override val maxWidth = 26.uiPX().toFloat()
				override val textSize: Float = fontSize(16)
			}.getFixString()
			
			currentAccount.info.subtitle.text = address
			balanceTitle.text = totalBalance.toDouble().formatCurrency()
		}
	}
	val addTokenButton by lazy { RoundButtonWithIcon(context) }
	val currentAccount by lazy { CurrentAccountView(context) }
	private val waveView by lazy { WaveLoadingView(context) }
	private var progressBar: ProgressBar? = null
	private val balanceTitle by lazy { TextView(context) }
	private val sectionHeaderHeight = 25.uiPX()
	val sendButton by lazy { StoneButton(context) }
	val depositButton by lazy { StoneButton(context) }
	
	init {
		setWillNotDraw(false)
		
		layoutParams = RelativeLayout.LayoutParams(matchParent, WalletDetailSize.headerHeight)
		
		waveView.apply {
			layoutParams =
				RelativeLayout.LayoutParams(matchParent, WalletDetailSize.headerHeight - 50.uiPX())
			setShapeType(WaveLoadingView.ShapeType.RECTANGLE)
			progressValue = 30
			waveColor = Color.parseColor("#FF265A80")
			setAnimDuration(30000)
			setAmplitudeRatio(50)
			startAnimation()
		}.into(this)
		
		currentAccount.into(this)
		currentAccount.apply {
			setCenterInHorizontal()
			y += 30.uiPX()
		}
		
		verticalLayout {
			balanceTitle.apply {
				textSize = fontSize(36)
				typeface = GoldStoneFont.black(context)
				textColor = Spectrum.white
				gravity = Gravity.CENTER_HORIZONTAL
			}.into(this)
			
			textView(WalletSlideHeader.setBalanceInfo()) {
				textSize = fontSize(12)
				typeface = GoldStoneFont.medium(context)
				textColor = Spectrum.opacity5White
				gravity = Gravity.CENTER_HORIZONTAL
			}.lparams(matchParent, matchParent)
		}.apply {
			setCenterInParent()
		}
		
		relativeLayout {
			lparams {
				width = matchParent
				height = 80.uiPX()
				alignParentBottom()
				y -= sectionHeaderHeight
			}
			
			sendButton.apply {
				text = CommonText.send
			}.into(this)
			sendButton.setMargins<RelativeLayout.LayoutParams> {
				leftMargin = 15.uiPX()
			}
			
			depositButton.apply {
				text = CommonText.deposit
			}.into(this)
			depositButton.setMargins<RelativeLayout.LayoutParams> {
				rightMargin = 15.uiPX()
			}
			
			depositButton.setAlignParentRight()
		}
		
		textView {
			text = WalletText.section.toUpperCase()
			typeface = GoldStoneFont.heavy(context)
			textColor = Spectrum.white
			textSize = fontSize(15)
			y -= 10.uiPX()
		}.apply {
			setAlignParentBottom()
			x += PaddingSize.device
		}
		
		addTokenButton.apply {
			setTitle(WalletText.addToken.toUpperCase())
			x -= PaddingSize.device
			y -= 10.uiPX()
		}.into(this)
		
		addTokenButton.apply {
			removeIcon()
			layoutParams.height = 24.uiPX()
			setAlignParentRight()
			setAlignParentBottom()
		}
	}
	
	fun showLoadingView(status: Boolean) {
		if (status && progressBar.isNull()) {
			progressBar = ProgressBar(
				this.context,
				null,
				R.attr.progressBarStyleInverse
			).apply {
				indeterminateDrawable.setColorFilter(
					Spectrum.white,
					android.graphics.PorterDuff.Mode.MULTIPLY
				)
				layoutParams = RelativeLayout.LayoutParams(16.uiPX(), 16.uiPX())
				x = WalletText.section.toUpperCase().measureTextWidth(16.uiPX().toFloat()) + 16.uiPX()
				y -= 12.uiPX()
			}
			progressBar?.into(this)
			progressBar?.setAlignParentBottom()
		} else {
			if (!progressBar.isNull()) {
				removeView(progressBar)
				progressBar = null
			}
		}
	}
}

