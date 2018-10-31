package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.extension.toUpperCaseFirstLetter
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.button.IconWithTitle
import io.goldstone.blockchain.common.component.cell.TopBottomLineCell
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.TokenInformationModel
import org.jetbrains.anko.gridLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.wrapContent

/**
 * @date 25/04/2018 9:48 AM
 * @author KaySaith
 */
@SuppressLint("ViewConstructor")
@Suppress("DEPRECATION")
class TokenSocialMedia(
	context: Context,
	clickEvent: (url: String) -> Unit
) : TopBottomLineCell(context) {
	
	var model: TokenInformationModel by observing(TokenInformationModel()) {
		if (model.socialMedia.isEmpty()) return@observing
		/***
		 * 按照约定的规则拆分数据
		 * Mulitiple data split with `,` Single data split with `|`
		 * [0] `Name` [1] `Icon Url` [2] `Link Url`
		 * */
		model.socialMedia.split(",").forEach {
			IconWithTitle(context).apply {
				layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding / 3, wrapContent)
				val data = it.split("|")
				setCcontent(data[1], data[0].toUpperCaseFirstLetter())
				onClick {
					clickEvent(data[2])
					this@apply.preventDuplicateClicks()
				}
			}.into(container)
		}
	}
	private var container: GridLayout
	
	init {
		setHorizontalPadding(PaddingSize.device.toFloat())
		setTitle(QuotationText.socimalMedia)
		layoutParams = RelativeLayout.LayoutParams(matchParent, 140.uiPX())
		container = gridLayout {
			layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
			rowCount = 1
			columnCount = 3
			y -= 10.uiPX()
			x += PaddingSize.device
		}
		container.setAlignParentBottom()
	}
}