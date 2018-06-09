package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.component.SquareIcon
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent

/**
 * @date 25/03/2018 5:12 PM
 * @author KaySaith
 */
open class TokenManagementListCell(context: Context) : BaseCell(context) {
	
	var model: DefaultTokenTable? by observing(null) {
		model?.apply {
			// 显示默认图判断
			when {
				iconUrl.isBlank() -> icon.image.imageResource = R.drawable.default_token
				symbol == CryptoSymbol.eth -> icon.image.imageResource = R.drawable.eth_icon
				else -> icon.image.glideImage(iconUrl)
			}
			tokenInfo.title.text = symbol
			tokenInfo.subtitle.text = name
			switch.isChecked = isUsed
		}
	}
	var searchModel: QuotationSelectionTable? by observing(null) {
		searchModel?.apply {
			tokenInfo.title.text = infoTitle
			tokenInfo.subtitle.text = name
			switch.isChecked = isSelecting
		}
	}
	val switch by lazy { HoneyBaseSwitch(context) }
	private val tokenInfo by lazy { TwoLineTitles(context) }
	protected val icon by lazy { SquareIcon(context) }
	
	init {
		hasArrow = false
		
		this.addView(icon.apply {
			setGrayStyle()
			y = 16.uiPX().toFloat()
		})
		
		this.addView(tokenInfo.apply { setBlackTitles() })
		
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
	
	fun showArrow() {
		removeView(switch)
		hasArrow = true
	}
	
	fun hideIcon() {
		icon.visibility = View.GONE
		tokenInfo.x = 0f
	}
}