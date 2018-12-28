package io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.view

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.GSCard
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.model.AuthorizationManagementModel
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/12/26
 */
@SuppressLint("ViewConstructor")
class AuthorizationManagementCell(
	context: Context,
	editAction: (publicKey: String, actor: EOSActor) -> Unit,
	deleteAction: (publicKey: String, actor: EOSActor) -> Unit
) : GSCard(context) {

	var model: AuthorizationManagementModel? by observing(null) {
		model?.apply {
			publicKeyView.text = publicKey
			authorizationType.text = permission.toUpperCase()
			if (EOSActor.getActorByValue(permission).isActive()) {
				borderView.backgroundColor = Spectrum.blue
			} else {
				borderView.backgroundColor = Spectrum.green
			}
			thresholdView.text = "THRESHOLD: $threshold"
			usingView.visibility = if (publicKey == SharedAddress.getCurrentEOS()) View.VISIBLE else View.GONE
		}
	}

	private lateinit var publicKeyView: TextView
	private lateinit var authorizationType: TextView
	private lateinit var thresholdView: TextView
	private lateinit var usingView: TextView
	private lateinit var editIcon: ImageView
	private lateinit var deleteIcon: ImageView
	private var borderView: View

	init {
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
		setCardBackgroundColor(GrayScale.whiteGray)
		resetCardElevation(ShadowSize.Card)
		borderView = View(context)
			.apply {
				layoutParams = LinearLayout.LayoutParams(6.uiPX(), matchParent)
			}
		borderView.into(this)
		verticalLayout {
			setPadding(20.uiPX(), 10.uiPX(), 15.uiPX(), 5.uiPX())
			lparams(matchParent, matchParent)
			relativeLayout {
				lparams(matchParent, wrapContent)
				authorizationType = textView {
					gravity = Gravity.CENTER
					layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
					textColor = GrayScale.black
					textSize = fontSize(12)
					typeface = GoldStoneFont.black(context)
				}
				authorizationType.centerInVertical()
				linearLayout {
					gravity = Gravity.END
					editIcon = imageView {
						imageResource = R.drawable.edit_contact_icon
						scaleType = ImageView.ScaleType.CENTER_INSIDE
						layoutParams = LinearLayout.LayoutParams(25.uiPX(), 30.uiPX())
						setColorFilter(GrayScale.midGray)
					}.click {
						editAction(
							model?.publicKey.orEmpty(),
							EOSActor.getActorByValue(model?.permission.orEmpty())
						)
					}
					deleteIcon = imageView {
						imageResource = R.drawable.delete_icon
						scaleType = ImageView.ScaleType.CENTER_INSIDE
						layoutParams = LinearLayout.LayoutParams(25.uiPX(), 30.uiPX())
						setColorFilter(GrayScale.midGray)
					}.click {
						deleteAction(
							model?.publicKey.orEmpty(),
							EOSActor.getActorByValue(model?.permission.orEmpty())
						)
					}
				}.lparams {
					width = wrapContent
					height = wrapContent
					alignParentRight()
					centerVertically()
				}
			}
			publicKeyView = textView {
				textColor = GrayScale.black
				textSize = fontSize(12)
				typeface = GoldStoneFont.heavy(context)
			}
			relativeLayout {
				lparams(matchParent, 30.uiPX())
				thresholdView = textView {
					textColor = GrayScale.midGray
					textSize = fontSize(12)
					typeface = GoldStoneFont.heavy(context)
				}
				thresholdView.centerInVertical()
				usingView = textView {
					text = "Current Publickey"
					visibility = View.GONE
					textColor = Spectrum.white
					textSize = fontSize(10)
					typeface = GoldStoneFont.heavy(context)
					addCorner(12.uiPX(), Spectrum.green)
					gravity = Gravity.CENTER
					leftPadding = 10.uiPX()
					rightPadding = 10.uiPX()
					layoutParams = RelativeLayout.LayoutParams(wrapContent, 20.uiPX())
				}
				usingView.alignParentRight()
				usingView.centerInVertical()
			}
		}
	}

	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		setMargins<RecyclerView.LayoutParams> {
			leftMargin = PaddingSize.device
		}
	}

	fun showEditIcons(status: Boolean) {
		editIcon.visibility = if (status) View.VISIBLE else View.GONE
		deleteIcon.visibility = if (status) View.VISIBLE else View.GONE
	}
}