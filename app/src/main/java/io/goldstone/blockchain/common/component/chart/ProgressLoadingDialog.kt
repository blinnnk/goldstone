package io.goldstone.blockchain.common.component.chart

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*

/**
 * @date: 2018/8/10.
 * @author: yanglihai
 * @description:
 */
class ProgressLoadingDialog(context: Context?) : Dialog(context!!) {
	
	private val root: LinearLayout = LinearLayout(context)
	
	private var progressBar: ProgressBar? = null
	
	init {
	  root.apply {
			textView("downloading...") {
				textSize = fontSize(14)
				typeface = GoldStoneFont.heavy(getContext())
				textColor = Spectrum.blue
				horizontalGravity = Gravity.CENTER
				setPadding(0, 20.uiPX(), 0, 0)
			}
			progressBar = horizontalProgressBar {  }
		
			progressBar?.layoutParams =
				LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
			(progressBar?.layoutParams as? LinearLayout.LayoutParams).let {
				it!!.leftMargin = 20.uiPX()
				it.rightMargin = 20.uiPX()
				it.bottomMargin = 20.uiPX()
				it.topMargin = 20.uiPX()
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
			params.width = ScreenSize.Width / 5 * 4
			params.height = WindowManager.LayoutParams.WRAP_CONTENT
			attributes = params
		}
	}
	
	fun setProgress(progress: Int, max: Int)  {
		progressBar?.let {
			it.max = max
			it.progress = progress
		}
		
	}
	
}