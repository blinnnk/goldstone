package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.component.UnlimitedAvatar
import io.goldstone.blockchain.common.language.WalletText
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
class WalletListCardCell(context: Context) : RelativeLayout(context) {

	var model: WalletListModel by observing(WalletListModel()) {
		val currentType = if (model.isWatchOnly) WalletText.watchOnly else WalletType(model.type).type
		nameInfo.title.text = model.addressName
		nameInfo.subtitle.text = model.subtitle.scaleTo(28)
		walletInfo.title.text = currentType
		walletInfo.subtitle.text = WalletText.baseBip44
		balanceInfo.title.text = model.balance.formatCurrency()
		balanceInfo.subtitle.text =
			(WalletText.totalAssets + " (${Config.getCurrencyCode()})").toUpperCase()
		avatar.glideImage("")
		avatar.glideImage(UnlimitedAvatar(model.id, context).getBitmap())
		val colorSize = WalletColor.getAll().size
		container.addCorner(
			CornerSize.normal.toInt(),
			WalletColor.getAll()[model.id % colorSize]
		)
		container.elevation = 5.uiPX().toFloat()
	}
	private val waveView by lazy { WaveLoadingView(context) }
	private val subtitleSize = 12
	private val nameInfo = TwoLineTitles(context).apply {
		setBigWhiteStyle(24, subtitleSize)
	}
	private val walletInfo = TwoLineTitles(context).apply {
		setBigWhiteStyle(18, subtitleSize)
	}
	private val balanceInfo = TwoLineTitles(context).apply {
		setBigWhiteStyle(18, subtitleSize)
		isFloatRight = true
	}
	private val avatar = ImageView(context)
	private var container: RelativeLayout

	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, 175.uiPX())
		container = relativeLayout {
			lparams {
				width = ScreenSize.widthWithPadding
				height = 160.uiPX()
				setMargins(
					ShadowSize.default.toInt(),
					5.uiPX(),
					ShadowSize.default.toInt(),
					ShadowSize.default.toInt()
				)
				centerHorizontally()
			}
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
			}.setAlignParentRight()
			nameInfo
				.apply {
					layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
					x += 20.uiPX()
					y += 15.uiPX()
				}
				.into(this)
			walletInfo
				.apply {
					layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
					x += 20.uiPX()
					y -= 15.uiPX()
					setAlignParentBottom()
				}
				.into(this)
			balanceInfo.apply {
				layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
				setAlignParentBottom()
				setAlignParentRight()
				x -= 20.uiPX()
				y -= 15.uiPX()
			}
				.into(this)
		}
	}
}