package io.goldstone.blockchain.module.common.walletgeneration.createwallet.view

import android.content.Context
import android.text.Html
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.*

@Suppress("DEPRECATION")
/**
 * @date 2018/6/11 3:42 PM
 * @author KaySaith
 */
class AgreementContentView(context: Context) : LinearLayout(context) {
	
	private val content = """
		<h1><font color="#000000">SAFE IS FIRST THING!</font></h1><br>
    <p><font color="#303b4d">Goldstone use a decentrolized way to send
		transactions to keep everything
		safe. Private
		keys / keystore / mnemonics managed locally, never leave your phone.</font></p>
		"""
	private val icon = ImageView(context).apply {
		setColorFilter(GrayScale.black)
		imageResource = R.drawable.goldstone_coin_icon
		layoutParams = LinearLayout.LayoutParams(50.uiPX(), 50.uiPX())
		scaleType = ImageView.ScaleType.CENTER_INSIDE
	}
	
	init {
		y = HomeSize.headerHeight.toFloat()
		layoutParams = LinearLayout.LayoutParams(matchParent, ScreenSize.heightWithOutHeader)
		backgroundColor = Spectrum.white
		orientation = VERTICAL
		
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams {
					width = matchParent
					height = matchParent
					setPadding(PaddingSize.device, PaddingSize.device, PaddingSize.device, 0)
				}
				linearLayout {
					layoutParams = LinearLayout.LayoutParams(matchParent, 80.uiPX())
					icon.into(this)
					textView(SplashText.goldStone) {
						textSize = fontSize(24)
						textColor = GrayScale.black
						typeface = GoldStoneFont.heavy(context)
						layoutParams = LinearLayout.LayoutParams(wrapContent, 50.uiPX())
						gravity = Gravity.CENTER_VERTICAL
					}
				}
				
				textView {
					setText(Html.fromHtml(content), TextView.BufferType.SPANNABLE)
				}
			}
		}
	}
}
