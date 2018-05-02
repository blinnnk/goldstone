package io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.presenter.KeystoreExportPresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.textColor
import org.jetbrains.anko.verticalLayout
import android.text.method.ScrollingMovementMethod
import com.blinnnk.util.clickToCopy
import org.jetbrains.anko.matchParent

/**
 * @date 06/04/2018 1:46 AM
 * @author KaySaith
 */

class KeystoreExportFragment : BaseFragment<KeystoreExportPresenter>() {

	private val privateKeyTextView by lazy { TextView(context) }
	private val passwordInput by lazy { RoundInput(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }

	override val presenter = KeystoreExportPresenter(this)

	@SuppressLint("SetTextI18n")
	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			lparams(matchParent, matchParent)
			privateKeyTextView.apply {
				addCorner(CornerSize.default.toInt(), GrayScale.whiteGray)
				layoutParams = LinearLayout.LayoutParams(
					ScreenSize.Width - PaddingSize.device * 2, 200.uiPX()
				).apply {
					maxLines = 8
					movementMethod = ScrollingMovementMethod()
					leftMargin = PaddingSize.device
					topMargin = 20.uiPX()
					setPadding(20.uiPX(), 16.uiPX(), 20.uiPX(), 10.uiPX())
				}
				gravity = Gravity.CENTER_VERTICAL
				textSize = 5.uiPX().toFloat()
				textColor = GrayScale.black
				typeface = GoldStoneFont.heavy(context)
				text = "Enter password and then click confirm button to get private key"
			}.click {
					context.clickToCopy(privateKeyTextView.text.toString())
				}.into(this)

			passwordInput.apply {
				setPasswordInput()
				setMargins<LinearLayout.LayoutParams> {
					topMargin = 30.uiPX()
				}
				text = CreateWalletText.password
			}.into(this)

			confirmButton.apply {
				text = CommonText.confirm.toUpperCase()
				setBlueStyle()
				setMargins<LinearLayout.LayoutParams> {
					topMargin = 15.uiPX()
				}
			}.click {
					presenter.getPrivateKeyByAddress(passwordInput) {
						privateKeyTextView.text = this
					}
				}.into(this)
		}
	}

}