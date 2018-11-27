package io.goldstone.blockchain.module.home.profile.contacts.contracts.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.centerInVertical
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.scaleTo
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.rightPadding
import org.jetbrains.anko.textColor

/**
 * @date 26/03/2018 1:37 PM
 * @author KaySaith
 */
@SuppressLint("ViewConstructor")
open class ContactsCell(context: Context) : RelativeLayout(context) {

	var model: ContactTable by observing(ContactTable()) {
		info.apply {
			title.text = model.name
			subtitle.text = model.generateSubtitleIntro().scaleTo(45)
		}
		model.name.isNotEmpty() isTrue {
			fontIcon.text = model.name.substring(0, 1).toUpperCase()
		}
	}

	private fun ContactTable.generateSubtitleIntro(): String {
		val addresses = listOf(
			Pair("${CoinSymbol.etc}/${CoinSymbol.erc}", ethSeriesAddress.isNotEmpty()),
			Pair(CoinSymbol.eos, eosAddress.isNotEmpty()),
			Pair("${CoinSymbol.eos} JUNGLE", eosJungle.isNotEmpty()),
			Pair(CoinSymbol.btc(), btcMainnetAddress.isNotEmpty()),
			Pair(CoinSymbol.ltc, ltcAddress.isNotEmpty()),
			Pair(CoinSymbol.bch, bchAddress.isNotEmpty()),
			Pair("BTCTest", btcSeriesTestnetAddress.isNotEmpty())
		)
		val count = addresses.filter { it.second }.size
		val allTypes = addresses.asSequence().filter { it.second }.map { it.first }.toList().toString()
		val type = "(${allTypes.substring(1, allTypes.lastIndex)})"
		val unit = if (count > 1) "Addresses" else "Address"
		return "$count $unit $type"
	}

	private val fontIcon by lazy {
		TextView(context).apply {
			layoutParams = LinearLayout.LayoutParams(50.uiPX(), 50.uiPX())
			addCorner(25.uiPX(), GrayScale.lightGray)
			textSize = fontSize(18)
			textColor = GrayScale.gray
			typeface = GoldStoneFont.black(context)
			gravity = Gravity.CENTER
		}
	}
	private val info by lazy { TwoLineTitles(context) }
	private val cellHeight = 70.uiPX()

	init {
		id = ElementID.slideCell
		layoutParams = RelativeLayout.LayoutParams(matchParent, cellHeight)
		this.addView(fontIcon)
		fontIcon.centerInVertical()
		this.addView(
			info.apply {
				setBlackTitles()
				x += 60.uiPX()
			}
		)
		info.centerInVertical()
		addTouchRippleAnimation(Color.WHITE, GrayScale.lightGray, RippleMode.Square)
		leftPadding = PaddingSize.device
		rightPadding = PaddingSize.device
	}

	private val paint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		color = GrayScale.lightGray
	}

	private val horizontalPaddingSize = PaddingSize.device.toFloat()
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		canvas?.drawLine(
			horizontalPaddingSize,
			height - BorderSize.default,
			width - horizontalPaddingSize,
			height - BorderSize.default,
			paint
		)
	}

}