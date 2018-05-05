package io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter.PrivateKeyExportPresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.verticalLayout

/**
 * @date 06/04/2018 1:02 AM
 * @author KaySaith
 */

class PrivateKeyExportFragment : BaseFragment<PrivateKeyExportPresenter>() {

	private val privateKeyTextView by lazy { TextView(context) }
	private val passwordInput by lazy { RoundInput(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }

	override val presenter = PrivateKeyExportPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			lparams(matchParent, matchParent)
			privateKeyTextView.apply {
				addCorner(CornerSize.default.toInt(), GrayScale.whiteGray)
				layoutParams = LinearLayout.LayoutParams(
					ScreenSize.Width - PaddingSize.device * 2, 120.uiPX()
				).apply {
					leftMargin = PaddingSize.device
					topMargin = 20.uiPX()
					setPadding(20.uiPX(), 16.uiPX(), 20.uiPX(), 10.uiPX())
				}
				gravity = Gravity.CENTER_VERTICAL
				textSize = 5.uiPX().toFloat()
				textColor = GrayScale.black
				typeface = GoldStoneFont.heavy(context)
				text = ImportWalletText.exportPrivateKey
			}.click {
				// 如果 `textview` 的内容不是默认的 `placeholder` 就可以支持点击复制
				if (it.text.toString() != ImportWalletText.exportPrivateKey) {
					context.clickToCopy(privateKeyTextView.text.toString())
				}
			}.into(this)

			passwordInput.apply {
				setPasswordInput()
				setMargins<LinearLayout.LayoutParams> {
					topMargin = 30.uiPX()
				}
				title = CreateWalletText.password
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