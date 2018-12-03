package io.goldstone.blockchain.common.component.overlay

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.blinnnk.extension.centerInParent
import com.blinnnk.uikit.HoneyColor
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*
import java.lang.Exception

/**
 * @date 07/04/2018 12:29 AM
 * @author KaySaith
 */
@SuppressLint("SetTextI18n")
class LoadingView(private val context: Context) {
	private val dialog = MaterialDialog(context)
	fun show() {
		context.apply {
			val container = relativeLayout {
				lparams(matchParent, 200.uiPX())
				addLoadingCircle(this) {
					centerInParent()
					y -= 20.uiPX()
				}
				textView {
					text = LoadingText.gettingData
					textSize = fontSize(12)
					textColor = GrayScale.gray
					gravity = Gravity.CENTER_HORIZONTAL
					typeface = GoldStoneFont.heavy(context)
					leftPadding = 30.uiPX()
					rightPadding = 30.uiPX()
					layoutParams = RelativeLayout.LayoutParams(matchParent, 50.uiPX())
					y += 60.uiPX()
				}.centerInParent()
			}
			with(dialog) {
				customView(view = container)
				show()
			}
		}
	}

	fun remove() {
		dialog.dismiss()
	}

	companion object {
		fun addLoadingCircle(
			parent: ViewGroup,
			size: Int = 80.uiPX(),
			color: Int = HoneyColor.Red,
			getCircle: ProgressBar.() -> Unit = {}
		) {
			val loading = ProgressBar(
				parent.context,
				null,
				R.attr.progressBarStyleInverse
			).apply {
				indeterminateDrawable.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP)
				layoutParams = RelativeLayout.LayoutParams(size, size)
				getCircle(this)
			}
			parent.addView(loading)
		}
	}

}