package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.view

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.FixTextLength
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.GSCard
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.component.title.twoLineTitles
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.AvatarManager
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.crypto.multichain.WalletType
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.model.WalletListModel
import me.itangqi.waveloadingview.WaveLoadingView
import org.jetbrains.anko.*
import java.util.*

/**
 * @date 2018/8/4 11:49 PM
 * @author KaySaith
 */
class WalletListCardCell(context: Context) : GSCard(context) {

	var model: WalletListModel by observing(WalletListModel()) {
		nameInfo.title.text = getFixedTitleLength(model.addressName)
		nameInfo.subtitle.text = model.subtitle.scaleTo(28)
		walletInfo.text = WalletType(model.type).getDisplayName()
		balanceInfo.title.text = model.balance.formatCurrency()
		balanceInfo.subtitle.text = (WalletText.totalAssets + " (${SharedWallet.getCurrencyCode()})").toUpperCase()
		avatar.glideImage(AvatarManager.getAvatarPath(model.id))
		val colorSize = WalletColor.getAll().size
		val themeColor = WalletColor.getAll()[model.id % colorSize]
		setCardBackgroundColor(themeColor)
		currentWalletSign.visibility = if (model.isUsing) View.VISIBLE else View.GONE
		if (model.isUsing) currentWalletSign.textColor = themeColor
	}
	private val waveView by lazy { WaveLoadingView(context) }
	private val subtitleSize = 12
	private lateinit var nameInfo: TwoLineTitles
	private lateinit var walletInfo: TextView

	private lateinit var balanceInfo: TwoLineTitles
	private lateinit var avatar: ImageView
	private lateinit var currentWalletSign: TextView

	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, 175.uiPX())
		resetCardElevation(ShadowSize.Cell)
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.card, 170.uiPX())
		waveView.apply {
			layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
			setShapeType(WaveLoadingView.ShapeType.RECTANGLE)
			progressValue = 35
			waveColor = Color.BLACK
			setAnimDuration((36000 * (1 + Random().nextDouble())).toLong())
			setAmplitudeRatio(50)
			startAnimation()
			alpha = 0.1f
		}.into(this)

		// Avatar
		verticalLayout {
			addCorner(AvatarSize.middle, Spectrum.white)
			x = ScreenSize.card - AvatarSize.middle - 45.uiPX() * 1f
			lparams {
				width = AvatarSize.middle
				height = AvatarSize.middle
				margin = 20.uiPX()
				elevation = 10.uiPX().toFloat()
			}
			avatar = imageView {
				scaleX = 1.02f
				scaleY = 1.02f
				layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
			}
		}

		relativeLayout {
			padding = 20.uiPX()
			lparams(matchParent, matchParent)
			nameInfo = twoLineTitles {
				setBigWhiteStyle(22, subtitleSize)
				layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
			}

			currentWalletSign = textView {
				visibility = View.GONE
				addCorner(CornerSize.small.toInt(), Spectrum.white)
				setPadding(8.uiPX(), 1.uiPX(), 8.uiPX(), 1.uiPX())
				textSize = fontSize(9)
				textColor = GrayScale.black
				text = WalletText.isUsing
				typeface = GoldStoneFont.heavy(context)
				y -= 24.uiPX()
			}
			currentWalletSign.alignParentBottom()

			walletInfo = textView {
				textSize = fontSize(12)
				textColor = Spectrum.opacity5White
				typeface = GoldStoneFont.medium(context)
				layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
			}
			walletInfo.alignParentBottom()

			balanceInfo = twoLineTitles {
				y += 3.uiPX()
				setBigWhiteStyle(16, subtitleSize)
				isFloatRight = true
				layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
				alignParentBottom()
				alignParentRight()
			}
		}
	}

	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		setMargins<RecyclerView.LayoutParams> {
			leftMargin = 10.uiPX()
		}
	}

	companion object {
		fun getFixedTitleLength(name: String): String {
			return object : FixTextLength() {
				override var text = name
				override val maxWidth = ScreenSize.overlayContentWidth - AvatarSize.big - 20.uiPX().toFloat()
				override val textSize: Float = 24.uiPX().toFloat()
			}.getFixString()
		}
	}
}