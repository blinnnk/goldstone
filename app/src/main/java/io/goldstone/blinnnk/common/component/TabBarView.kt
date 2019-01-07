package io.goldstone.blinnnk.common.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.Gravity
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.language.ProfileText
import io.goldstone.blinnnk.common.language.QuotationText
import io.goldstone.blinnnk.common.language.WalletText
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.*
import org.jetbrains.anko.*


/**
 * @date 23/03/2018 12:55 PM
 * @author KaySaith
 */

class TabBarView(context: Context) : GridLayout(context) {

	val walletButton by lazy { TabItem(context) }
	val dAppCenterButton by lazy { TabItem(context) }
	val marketButton by lazy { TabItem(context) }
	val settingsButton by lazy { TabItem(context) }

	init {
		columnCount = 4
		rowCount = 1
		setWillNotDraw(false)
		isClickable = true
		layoutParams = LinearLayout.LayoutParams(matchParent, HomeSize.tabBarHeight)
		val childWidth = ScreenSize.Width / 4
		useDefaultMargins = true

		walletButton.apply {
			type = TabItemType.Wallet
			layoutParams = LinearLayout.LayoutParams(childWidth, matchParent)
		}.into(this)

		dAppCenterButton.apply {
			type = TabItemType.DAppCenter
			layoutParams = LinearLayout.LayoutParams(childWidth, matchParent)
		}.into(this)

		marketButton.apply {
			type = TabItemType.Market
			layoutParams = LinearLayout.LayoutParams(childWidth, matchParent)
		}.into(this)

		settingsButton.apply {
			type = TabItemType.Setting
			layoutParams = LinearLayout.LayoutParams(childWidth, matchParent)
		}.into(this)

		// 默认选中
		walletButton.setSelectedStyle()
		backgroundColor = Spectrum.white
	}

	private val paint = Paint().apply {
		isAntiAlias = true
		color = GrayScale.Opacity1Black
		style = Paint.Style.FILL
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		canvas?.drawLine(0f, 0f, width.toFloat(), 1f, paint)
	}

}

enum class TabItemType {
	Market, Wallet, DAppCenter, Setting
}

class TabItem(context: Context) : LinearLayout(context) {

	var type: TabItemType by observing(TabItemType.Market) {
		iconImage.imageResource = when (type) {
			TabItemType.Market -> R.drawable.market_icon
			TabItemType.Wallet -> R.drawable.wallet_detail_icon
			TabItemType.DAppCenter -> R.drawable.dapp_center_icon
			TabItemType.Setting -> R.drawable.setting_icon
		}
		titleView.text = when (type) {
			TabItemType.Market -> QuotationText.market.toLowerCase()
			TabItemType.Wallet -> WalletText.wallet.toLowerCase()
			TabItemType.DAppCenter -> ProfileText.dappCenter.toLowerCase()
			TabItemType.Setting -> ProfileText.settings.toLowerCase()
		}
	}

	private val iconSize = HomeSize.tabBarHeight
	private val iconImage = ImageView(context).apply {
		layoutParams = LinearLayout.LayoutParams(iconSize, 28.uiPX())
		scaleType = ImageView.ScaleType.CENTER_INSIDE
		setColorFilter(GrayScale.midGray)
		y += 1.uiPX()
	}

	private val titleView by lazy {
		TextView(context).apply {
			textSize = fontSize(9)
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.heavy(context)
			layoutParams = LinearLayout.LayoutParams(matchParent, 20.uiPX())
			gravity = Gravity.CENTER_HORIZONTAL
		}
	}

	init {
		topPadding = 3.uiPX()
		orientation = VERTICAL
		gravity = Gravity.CENTER_HORIZONTAL
		layoutParams = LinearLayout.LayoutParams(iconSize, matchParent)
		isClickable = true
		iconImage.into(this)
		titleView.into(this)
	}

	fun setSelectedStyle() {
		iconImage.setColorFilter(GrayScale.Opacity8Black)
		titleView.textColor = GrayScale.Opacity8Black
		invalidate()
	}

	fun resetStyle() {
		iconImage.setColorFilter(GrayScale.midGray)
		titleView.textColor = GrayScale.midGray
		invalidate()
	}

}