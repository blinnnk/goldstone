@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.common.component.overlay

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.DialogText
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.*

/**
 * @date 2018/5/19 11:45 AM
 * @author KaySaith
 */

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class GoldStoneDialog(private val context: Context) {

	private val dialog: MaterialDialog = MaterialDialog(context)

	init {
		dialog.window.setLayout(ScreenSize.overlayContentWidth, wrapContent)
		val shape = GradientDrawable().apply {
			cornerRadius = CornerSize.small
			shape = GradientDrawable.RECTANGLE
			setSize(matchParent, matchParent)
			setColor(Spectrum.white)
		}
		dialog.window.setBackgroundDrawable(shape)
	}

	private fun generateCustomView(image: Int, description: String, hold: (RelativeLayout) -> Unit) {
		context.apply {
			val container = relativeLayout {
				id = ElementID.dialog
				isClickable = true
				layoutParams = RelativeLayout.LayoutParams(ScreenSize.overlayContentWidth, wrapContent)
				verticalLayout {
					lparams(matchParent, wrapContent)
					imageView {
						imageResource = image
						scaleType = ImageView.ScaleType.CENTER_CROP
						backgroundColor = GrayScale.whiteGray
						layoutParams = LinearLayout.LayoutParams(matchParent, 150.uiPX())
					}
					textView {
						text = description
						padding = 15.uiPX()
						layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
						textColor = GrayScale.midGray
						typeface = GoldStoneFont.heavy(context)
						textSize = fontSize(12)
					}
				}
			}
			hold(container)
		}
	}


	fun showChainErrorDialog() {
		generateCustomView(
			R.drawable.node_error_banner,
			"there are some errors on this chain, please search more information on internet"
		) {
			with(dialog) {
				title(text = "${SharedChain.getCurrentETH().chainID} ERROR")
				positiveButton(text = CommonText.gotIt)
				customView(view = it)
				show()
			}
		}
	}

	// 一个 App 的声明周期只弹出一次 `4G` 的 `Alert`
	fun showMobile4GConfirm(confirmAction: () -> Unit) {
		if (GoldStoneApp.hasShownMobileAlert) return
		generateCustomView(
			R.drawable.dialog_mobile_4g,
			"Are you willing to update resources in 4g environment?"
		) {
			with(dialog) {
				title(text = DialogText.mobileNetwork)
				positiveButton(text = CommonText.gotIt) {
					confirmAction()
					dialog.dismiss()
				}
				negativeButton(text = CommonText.cancel)
				customView(view = it)
				show()
				GoldStoneApp.hasShownMobileAlert = true
			}
		}
	}

	fun showNetworkStatus(callback: () -> Unit) {
		generateCustomView(
			R.drawable.network_browken_banner,
			DialogText.networkDescription
		) {
			with(dialog) {
				cancelOnTouchOutside(false)
				title(text = DialogText.networkTitle)
				positiveButton(text = CommonText.gotIt) {
					callback()
				}
				customView(view = it)
				show()
			}
		}
	}

	fun showUpgradeStatus(versionName: String, description: String, callback: () -> Unit) {
		generateCustomView(
			R.drawable.version_banner,
			description
		) {
			with(dialog) {
				title(text = versionName)
				positiveButton(text = CommonText.gotIt) {
					callback()
					dialog.dismiss()
				}
				customView(view = it)
				show()
			}
		}
	}

	fun showBackUpMnemonicStatus(callback: () -> Unit) {
		generateCustomView(
			R.drawable.succeed_banner,
			DialogText.backUpMnemonicDescription
		) {
			with(dialog) {
				title(text = DialogText.backUpMnemonic)
				positiveButton(text = CommonText.gotIt) {
					callback()
					dialog.dismiss()
				}
				negativeButton(text = CommonText.cancel)
				customView(view = it)
				show()
			}
		}
	}

	fun showBackUpSucceed() {
		generateCustomView(
			R.drawable.succeed_banner,
			DialogText.backUpMnemonicSucceed
		) {
			with(dialog) {
				title(text = CommonText.succeed)
				positiveButton(text = CommonText.gotIt) {
					dialog.dismiss()
				}
				customView(view = it)
				show()
			}
		}
	}
}