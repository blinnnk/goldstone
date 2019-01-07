package io.goldstone.blinnnk.module.common.walletimport.walletimportcenter.view

import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import com.blinnnk.extension.alignParentBottom
import com.blinnnk.extension.alignParentRight
import com.blinnnk.extension.centerInVertical
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.component.GSCard
import io.goldstone.blinnnk.common.component.title.TwoLineTitles
import io.goldstone.blinnnk.common.language.ImportMethodText
import io.goldstone.blinnnk.common.language.ImportWalletText
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.common.value.Spectrum
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.wrapContent


/**
 * @author KaySaith
 * @date  2018/09/06
 */
class WalletImportMethodCell(context: Context) : GSCard(context) {

	private val titles = TwoLineTitles(context).apply {
		setBigWhiteStyle(18)
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
		setCardBackgroundColor(Spectrum.blue)
		setContentPadding(20.uiPX(), 0, 20.uiPX(), 0)
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.card, 110.uiPX())
		relativeLayout {
			titles.into(this)
			arrowIcon.into(this)
			arrowIcon.apply {
				alignParentRight()
				centerInVertical()
			}
			lparams(matchParent, matchParent)
			typeIcon.into(this)
			typeIcon.alignParentBottom()
			typeIcon.y += 45.uiPX()
		}
	}

	fun setMnemonicType() {
		titles.apply {
			title.text = ImportMethodText.mnemonic
			subtitle.text = ImportWalletText.importMnemonicsHint
			centerInVertical()
		}
		typeIcon.imageResource = R.drawable.mnemonic_icon
	}

	fun setPrivateKeyType() {
		titles.apply {
			title.text = ImportMethodText.privateKey
			subtitle.text = ImportWalletText.importPrivateKeyHint
			centerInVertical()
		}
		typeIcon.imageResource = R.drawable.private_key_import_icon
		typeIcon.y -= 10.uiPX()
	}

	fun setKeystoreType() {
		titles.apply {
			title.text = ImportMethodText.keystore
			subtitle.text = ImportWalletText.importKeystoreHint
			centerInVertical()
		}
		typeIcon.imageResource = R.drawable.keystore_import_icon
		typeIcon.y += 10.uiPX()
	}
}