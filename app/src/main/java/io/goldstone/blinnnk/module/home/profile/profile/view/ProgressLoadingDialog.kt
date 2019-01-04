package io.goldstone.blinnnk.module.home.profile.profile.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.*

/**
 * @date: 2018/8/10.
 * @author: yanglihai
 * @description:
 */
class ProgressLoadingDialog(context: Context) : Dialog(context) {
	private val root: LinearLayout = LinearLayout(context)
	private var progressBar: ProgressBar? = null

	init {
		root.apply {
			textView("downloading...") {
				textSize = fontSize(14)
				typeface = GoldStoneFont.heavy(context)
				textColor = Spectrum.blue
				horizontalGravity = Gravity.CENTER
				setPadding(0, 20.uiPX(), 0, 0)
			}
			progressBar = horizontalProgressBar()

			progressBar?.layoutParams =
				LinearLayout.LayoutParams(matchParent, wrapContent)
			(progressBar?.layoutParams as? LinearLayout.LayoutParams)?.apply {
				margin = 20.uiPX()
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		root.orientation = LinearLayout.VERTICAL
		window?.requestFeature(Window.FEATURE_NO_TITLE)

		setContentView(root)

		window?.apply {
			val params = attributes
			params.width = ScreenSize.widthWithPadding
			params.height = wrapContent
			attributes = params
		}
	}

	fun setProgress(progress: Int, max: Int) {
		progressBar?.apply {
			this.max = max
			this.progress = progress
		}
	}

}