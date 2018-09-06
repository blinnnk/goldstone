package io.goldstone.blockchain.module.common.walletimport.walletimportcenter.view

import android.content.Context
import android.graphics.Color
import android.widget.ImageView
import android.widget.RelativeLayout
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.language.ImportMethodText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.ShadowSize
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/09/06
 */
class WalletImportMethodCell(context: Context) : RelativeLayout(context) {

	private val titles = TwoLineTitles(context).apply {
		setBigWhiteStyle()
		title.typeface = GoldStoneFont.heavy(context)
		layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
	}
	private val iconSize = 30.uiPX()
	private val arrowIcon = ImageView(context).apply {
		imageResource = R.drawable.arrow_icon
		layoutParams = RelativeLayout.LayoutParams(iconSize, iconSize)
		scaleType = ImageView.ScaleType.CENTER_CROP
		alpha = 0.2f
	}

	private val typeIconSize = 70.uiPX()
	private val typeIcon = ImageView(context).apply {
		layoutParams = RelativeLayout.LayoutParams(typeIconSize, typeIconSize)
		setColorFilter(Spectrum.white)
		alpha = 0.2f
	}

	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, 120.uiPX())
		relativeLayout {
			addCorner(CornerSize.normal.toInt(), Spectrum.blue)
			titles.into(this)
			arrowIcon.into(this)
			arrowIcon.apply {
				setAlignParentRight()
				setCenterInVertical()
			}
			elevation = ShadowSize.Cell
			val paddingSize = 15.uiPX()
			lparams {
				width = ScreenSize.widthWithPadding
				height = 110.uiPX()
				leftPadding = paddingSize
				rightPadding = paddingSize
				y -= 5.uiPX()
				centerInParent()
			}
			typeIcon.into(this)
			typeIcon.setAlignParentBottom()
			typeIcon.y += 45.uiPX()
		}
	}

	fun setMnemonicType() {
		titles.apply {
			title.text = ImportMethodText.mnemonic
			subtitle.text = "a type of password consisting of 12 words"
			setCenterInVertical()
		}
		typeIcon.imageResource = R.drawable.mnemonic_icon
	}

	fun setPrivateKeyType() {
		titles.apply {
			title.text = ImportMethodText.privateKey
			subtitle.text = "the private key of any supported chain type"
			setCenterInVertical()
		}
		typeIcon.imageResource = R.drawable.private_key_import_icon
		typeIcon.y -= 10.uiPX()
	}

	fun setKeystoreType() {
		titles.apply {
			title.text = ImportMethodText.keystore
			subtitle.text = "a string of characters in a format file"
			setCenterInVertical()
		}
		typeIcon.imageResource = R.drawable.keystore_import_icon
		typeIcon.y += 10.uiPX()
	}
}