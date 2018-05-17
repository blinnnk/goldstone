package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.Gravity
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInHorizontal
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.*

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
			type = TabItemType.Wallet
			x += PaddingSize.device
		}.into(this)

		marketButton.apply {
			type = TabItemType.Market
		}.into(this)

		profileButton.apply {
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

class TabItem(context: Context) : LinearLayout(context) {

	var type: TabItemType by observing(TabItemType.Market) {
		iconImage.imageResource = when(type) {
			TabItemType.Market -> R.drawable.market_icon
			TabItemType.Wallet -> R.drawable.wallet_detail_icon
			TabItemType.Profile -> R.drawable.profile_icon
		}
		titleView.text = when(type) {
			TabItemType.Market -> QuotationText.market.toLowerCase()
			TabItemType.Wallet -> WalletText.wallet.toLowerCase()
			TabItemType.Profile -> ProfileText.profile.toLowerCase()
		}
	}

	private val iconSize = HomeSize.tabBarHeight
	private val iconImage = ImageView(context).apply {
		layoutParams = LinearLayout.LayoutParams(iconSize, 28.uiPX())
		scaleType = ImageView.ScaleType.CENTER_INSIDE
		setColorFilter(GrayScale.midGray)
	}

	private val titleView by lazy { TextView(context).apply {
		textSize = fontSize(10)
		textColor = GrayScale.midGray
		typeface = GoldStoneFont.heavy(context)
		layoutParams = LinearLayout.LayoutParams(matchParent, 20.uiPX())
		gravity = Gravity.CENTER_HORIZONTAL
	} }

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