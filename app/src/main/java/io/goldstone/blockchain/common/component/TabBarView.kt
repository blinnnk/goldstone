package io.goldstone.blockchain.common.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setBold
import com.blinnnk.extension.setCenterInHorizontal
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.HoneySvgPathConvert
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent

/**
 * @date 23/03/2018 12:55 PM
 * @author KaySaith
 */

class TabBarView(context: Context) : RelativeLayout(context) {

	val walletButton by lazy { TabItem(context) }
	val marketButton by lazy { TabItem(context) }
	val profileButton by lazy { TabItem(context) }

	init {
		layoutParams = LinearLayout.LayoutParams(matchParent, HomeSize.tabBarHeight)

		walletButton.apply {
			text = "wallet"
			type = TabItemType.Wallet
			x += PaddingSize.device
		}.into(this)

		marketButton.apply {
			text = "markets"
			type = TabItemType.Market
		}.into(this)

		profileButton.apply {
			text = "profile"
			type = TabItemType.Profile
			x -= PaddingSize.device
		}.into(this)

		// 修改位置
		marketButton.setCenterInHorizontal()
		profileButton.setAlignParentRight()

		// 默认选中
		walletButton.setSelectedStyle()

		outlineProvider = ViewOutlineProvider.BOUNDS
		backgroundColor = Spectrum.white

		elevation = 50.uiPX().toFloat()
	}

}

enum class TabItemType {
	Market, Wallet, Profile
}

class TabItem(context: Context) : View(context) {

	var text by observing("") {
		invalidate()
	}

	var type by observing(TabItemType.Market) {
		invalidate()
	}

	private val iconPaint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		color = GrayScale.midGray
	}

	private val iconSize = HomeSize.tabBarHeight

	private val textPaint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		color = GrayScale.midGray
		textSize = 12.uiPX().toFloat()
		typeface = GoldStoneFont.heavy(context)
	}

	private val path = HoneySvgPathConvert()

	init {
		layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
		isClickable = true
		scaleX = 0.75f
		scaleY = 0.75f
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)

		// `Wallet` 的 `Path` 尺寸不同这里的位置也相对调整
		val iconLeft = if (type == TabItemType.Wallet) (width - 30.uiPX()) / 2f
		else (width - 34.uiPX()) / 2f
		val textX = if (type == TabItemType.Wallet) (textPaint.measureText(text) - 26.uiPX()) / 2f
		else (textPaint.measureText(text) - 34.uiPX()) / 2f

		canvas?.translate(iconLeft, 2.uiPX().toFloat())

		val drawPath = when (type) {
			TabItemType.Market  -> path.parser(SvgPath.market)
			TabItemType.Profile -> path.parser(SvgPath.profile)
			TabItemType.Wallet  -> path.parser(SvgPath.wallet)
		}

		canvas?.drawPath(drawPath, iconPaint)
		canvas?.save()


		canvas?.drawText(text, -textX, 45.uiPX().toFloat(), textPaint)

	}

	fun setSelectedStyle() {
		iconPaint.color = GrayScale.Opacity8Black
		textPaint.color = GrayScale.Opacity8Black
		invalidate()
	}

	fun resetStyle() {
		iconPaint.color = GrayScale.midGray
		textPaint.color = GrayScale.midGray
		invalidate()
	}

}