@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.common.component

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.animation.updateAlphaAnimation
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.extension.isNull
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import org.jetbrains.anko.*

/**
 * @date 2018/5/19 11:45 AM
 * @author KaySaith
 */
class GoldStoneDialog(context: Context) : RelativeLayout(context) {
	
	private val image by lazy { ImageView(context) }
	private val content by lazy { TwoLineTitles(context) }
	private val cancelButton by lazy {
		DialogButton(context, CommonText.cancel).click {
			GoldStoneDialog.remove(context)
		}
	}
	private val confirmButton by lazy {
		DialogButton(context, CommonText.confirm)
	}
	private lateinit var buttonLayout: LinearLayout
	
	init {
		id = ElementID.dialog
		isClickable = true
		layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
		backgroundColor = GrayScale.Opacity5Black
		updateAlphaAnimation(1f)
		verticalLayout {
			addCorner(CornerSize.small, Spectrum.white)
			elevation = 40.uiPX().toFloat()
			lparams(300.uiPX(), wrapContent)
			image.apply {
				scaleType = ImageView.ScaleType.CENTER_CROP
				backgroundColor = GrayScale.whiteGray
				layoutParams = LinearLayout.LayoutParams(matchParent, 150.uiPX())
			}.into(this)
			content.apply {
				padding = 15.uiPX()
				layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
				y = 10.uiPX().toFloat()
				setDialogStyle()
			}.into(this)
			buttonLayout = linearLayout {
				bottomPadding = 10.uiPX()
				lparams(wrapContent, wrapContent)
			}
		}.setCenterInParent()
	}
	
	fun <T> setImage(src: T) {
		image.glideImage(src)
	}
	
	fun setContent(
		title: String,
		subtitle: String
	) {
		content.title.text = title
		content.subtitle.text = subtitle
	}
	
	fun showOnlyConfirmButton(
		buttonTitle: String = CommonText.confirm,
		clickEvent: () -> Unit
	) {
		buttonLayout.x = 210.uiPX().toFloat()
		confirmButton.text = buttonTitle
		confirmButton.click {
			clickEvent()
		}.into(buttonLayout)
	}
	
	fun showButtons(confirmTitle: String = CommonText.confirm, confirmEvent: () -> Unit) {
		buttonLayout.x = 140.uiPX().toFloat()
		cancelButton.into(buttonLayout)
		confirmButton.text = confirmTitle
		confirmButton.click {
			confirmEvent()
		}.into(buttonLayout)
	}
	
	inner class DialogButton(
		context: Context,
		title: String
	) : TextView(context) {
		
		init {
			gravity = Gravity.END or Gravity.CENTER_VERTICAL
			text = title
			textSize = fontSize(14)
			typeface = GoldStoneFont.heavy(context)
			textColor = Spectrum.blue
			layoutParams = LinearLayout.LayoutParams(70.uiPX(), 35.uiPX())
			addTouchRippleAnimation(Color.TRANSPARENT, Spectrum.blue, RippleMode.Round)
		}
	}
	
	companion object {
		fun show(
			context: Context,
			hold: GoldStoneDialog.() -> Unit
		) {
			// 判断父级所在主题添加到不同的顶层 `Layout` 里面
			(context as? Activity)?.let {
				when (it) {
					is SplashActivity -> {
						it.findViewById<RelativeLayout>(ContainerID.splash)?.apply {
							// 防止重复添加
							findViewById<GoldStoneDialog>(ElementID.dialog).isNull {
								GoldStoneDialog(context).let {
									hold(it)
									it.into(this)
								}
							}
						}
					}
					
					else -> {
						it.findViewById<RelativeLayout>(ContainerID.main)?.apply {
							// 防止重复添加
							findViewById<GoldStoneDialog>(ElementID.dialog).isNull {
								GoldStoneDialog(context).let {
									hold(it)
									it.into(this)
								}
							}
						}
					}
				}
			}
		}
		
		fun remove(context: Context) {
			(context as? Activity)?.let { activity ->
				when (activity) {
					is SplashActivity -> {
						activity.findViewById<RelativeLayout>(ContainerID.splash)?.apply {
							findViewById<GoldStoneDialog>(ElementID.dialog)?.let {
								it.updateAlphaAnimation(0f) { removeView(it) }
							}
						}
					}
					
					else -> {
						activity.findViewById<RelativeLayout>(ContainerID.main)?.apply {
							findViewById<GoldStoneDialog>(ElementID.dialog)?.let {
								it.updateAlphaAnimation(0f) { removeView(it) }
							}
						}
					}
				}
			}
		}
		
		private fun showChainErrorDialog(context: Context) {
			GoldStoneDialog.show(context) {
				showOnlyConfirmButton("Got It") {
					GoldStoneDialog.remove(context)
				}
				setImage(R.drawable.node_error_banner)
				setContent(
					Config.getCurrentChainName() + " ERROR",
					"there are some errors on this chain, please search more information on internet"
				)
			}
		}
		
		fun chainError(reason: String?, error: Exception?, context: Context) {
			if (reason.equals(ErrorTag.chain, true)) {
				GoldStoneDialog.showChainErrorDialog(context)
			}
			LogUtil.error("ChainErrorDialog", error)
		}
	}
}