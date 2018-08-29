package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import com.bumptech.glide.Glide
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.component.button.SquareIcon
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.MarketSetTable
import org.jetbrains.anko.matchParent

/**
 * @date: 2018/8/29.
 * @author: yanglihai
 * @description:
 */
class MarketSetCell(context: Context) : BaseCell(context) {
	
	val switch by lazy { HoneyBaseSwitch(context) }
	private val tokenInfo by lazy {
		TwoLineTitles(context).apply {
			subtitle.visibility = View.GONE
		}
	}
	protected val icon by lazy { SquareIcon(context, SquareIcon.Companion.Style.Big) }
	
	init {
		hasArrow = false
		setHorizontalPadding()
		this.addView(icon.apply {
			setGrayStyle()
			y += 10.uiPX()
		})
		
		this.addView(tokenInfo.apply {
			setBlackTitles()
			x += 10.uiPX()
		})
		
		this.addView(switch.apply {
			layoutParams = RelativeLayout.LayoutParams(50.uiPX(), matchParent)
			setThemColor(Spectrum.green, Spectrum.lightGreen)
		})
		
		tokenInfo.apply {
			setCenterInVertical()
			x += 40.uiPX()
		}
		
		switch.apply {
			setCenterInVertical()
			setAlignParentRight()
		}
		
		setGrayStyle()
	}
	
	var marketSetTable: MarketSetTable? by observing(null) {
		marketSetTable?.apply {
			Glide.with(icon.image).load(url)
			tokenInfo.title.text = name
			switch.isChecked = status == 1
		}
	}
}