package io.goldstone.blockchain.module.common.walletimport.walletimportcenter.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.crypto.multichain.PrivateKeyType
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.leftPadding


/**
 * @author KaySaith
 * @date  2018/09/06
 */
class SupportedChainMenu(context: Context) : LinearLayout(context) {

	private val title = ImportWalletText.supportedChain
	private val listOfChainIcon = listOf(
		Pair(R.drawable.btc_creator_icon, PrivateKeyType.BTC),
		Pair(R.drawable.eth_creator_icon, PrivateKeyType.ETHSeries),
		Pair(R.drawable.ltc_creator_icon, PrivateKeyType.LTC),
		Pair(R.drawable.bch_creator_icon, PrivateKeyType.BCH),
		Pair(R.drawable.etc_creator_icon, PrivateKeyType.ETHSeries),
		Pair(R.drawable.eos_creater_icon, PrivateKeyType.EOS)
	)
	private val iconSize = 55.uiPX()

	init {
		setWillNotDraw(false)
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 120.uiPX())
		leftPadding = (ScreenSize.widthWithPadding - iconSize * listOfChainIcon.size) / 2
		listOfChainIcon.forEach {
			ImageView(context).apply {
				imageResource = it.first
				layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
				y = 26.uiPX().toFloat()
				setColorFilter(GrayScale.lightGray)
			}.into(this)
		}
	}

	private val paintTextSize = 14.uiPX()
	val paint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		color = GrayScale.midGray
		typeface = GoldStoneFont.heavy(context)
		textSize = fontSize(paintTextSize)
	}

	private val lineFinalX = ScreenSize.widthWithPadding.toFloat()
	private val lineStartX = 0f
	private val finalY = 100.uiPX().toFloat()
	private val linePadding = 10.uiPX().toFloat()
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		val textWidth = paint.measureText(title)
		val textX = (width - textWidth) / 2
		canvas?.drawLine(
			lineStartX,
			10.uiPX().toFloat(),
			textX - linePadding,
			10.uiPX().toFloat(),
			paint
		)
		canvas?.drawLine(
			textX + textWidth + linePadding,
			10.uiPX().toFloat(),
			width.toFloat(),
			10.uiPX().toFloat(),
			paint
		)
		canvas?.drawText(title, textX, paintTextSize.toFloat(), paint)
		canvas?.drawLine(lineStartX, finalY, lineFinalX, finalY, paint)
	}
}