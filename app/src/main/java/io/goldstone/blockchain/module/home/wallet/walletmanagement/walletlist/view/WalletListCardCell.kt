package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.FixTextLength
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.GSCard
import io.goldstone.blockchain.common.component.UnlimitedAvatar
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.crypto.multichain.WalletType
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.model.WalletListModel
import me.itangqi.waveloadingview.WaveLoadingView
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

/**
 * @date 2018/8/4 11:49 PM
 * @author KaySaith
 */
class WalletListCardCell(context: Context) : RelativeLayout(context) {

	var model: WalletListModel by observing(WalletListModel()) {
		val currentType = if (model.isWatchOnly) WalletText.watchOnly else model.type
		nameInfo.title.text = getFixedTitleLength(model.addressName)
		nameInfo.subtitle.text = model.subtitle.scaleTo(28)
		walletInfo.title.text = currentType
		walletInfo.subtitle.text = WalletType(model.type).getDisplayName()
		balanceInfo.title.text = model.balance.formatCurrency()
		balanceInfo.subtitle.text = (WalletText.totalAssets + " (${SharedWallet.getCurrencyCode()})").toUpperCase()
		avatar.glideImage(UnlimitedAvatar(model.id, context).getBitmap())
		val colorSize = WalletColor.getAll().size
		container.setCardBackgroundColor(WalletColor.getAll()[model.id % colorSize])
	}
	private val waveView by lazy { WaveLoadingView(context) }
	private val subtitleSize = 12
	private val nameInfo = TwoLineTitles(context).apply {
		setBigWhiteStyle(24, subtitleSize)
	}
	private val walletInfo = TwoLineTitles(context).apply {
		setBigWhiteStyle(16, subtitleSize)
	}
	private val balanceInfo = TwoLineTitles(context).apply {
		setBigWhiteStyle(16, subtitleSize)
		isFloatRight = true
	}
	private val avatar = ImageView(context)
	private var container: GSCard

	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, 175.uiPX())
		gravity = Gravity.CENTER_HORIZONTAL
		container = GSCard(context)
		container.into(this)
		container.apply {
			resetCardElevation(ShadowSize.Cell)
			layoutParams = RelativeLayout.LayoutParams(ScreenSize.card, matchParent)
			relativeLayout {
				lparams(matchParent, matchParent)
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
				verticalLayout {
					addCorner(AvatarSize.middle, Spectrum.white)
					gravity = Gravity.START
					lparams {
						width = AvatarSize.middle
						height = AvatarSize.middle
						margin = 10.uiPX()
						elevation = 10.uiPX().toFloat()
						x -= 10.uiPX()
						y += 10.uiPX()
					}
					avatar.apply {
						scaleX = 1.01f
						scaleY = 1.01f
						layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
					}.into(this)
				}.alignParentRight()
				nameInfo.apply {
					layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
					x += 20.uiPX()
					y += 15.uiPX()
				}.into(this)
				walletInfo.apply {
					layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
					x += 20.uiPX()
					y -= 15.uiPX()
					alignParentBottom()
				}.into(this)
				balanceInfo.apply {
					layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
					alignParentBottom()
					alignParentRight()
					x -= 20.uiPX()
					y -= 15.uiPX()
				}.into(this)
			}
		}
	}

	fun setClickEvent(action: () -> Unit) {
		container.onClick {
			action()
			container.preventDuplicateClicks()
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