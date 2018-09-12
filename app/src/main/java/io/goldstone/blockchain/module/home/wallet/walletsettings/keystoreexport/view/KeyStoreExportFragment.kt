package io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.AttentionTextView
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.CreateWalletText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.presenter.KeystoreExportPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.verticalLayout

/**
 * @date 06/04/2018 1:46 AM
 * @author KaySaith
 */
class KeystoreExportFragment : BaseFragment<KeystoreExportPresenter>() {

	private val attentionView by lazy { AttentionTextView(context!!) }
	private val privateKeyTextView by lazy { TextView(context) }
	private val passwordInput by lazy { RoundInput(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	override val presenter = KeystoreExportPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
			attentionView.apply {
				isCenter()
				setPadding(15.uiPX(), 30.uiPX(), 15.uiPX(), 20.uiPX())
				text = ImportWalletText.exportKeystore
			}.into(this)
			privateKeyTextView.apply {
				addCorner(CornerSize.default.toInt(), GrayScale.whiteGray)
				layoutParams = LinearLayout.LayoutParams(
					ScreenSize.Width - PaddingSize.device * 2, 200.uiPX()
				).apply {
					maxLines = 8
					movementMethod = ScrollingMovementMethod()
					topMargin = 20.uiPX()
					setPadding(20.uiPX(), 16.uiPX(), 20.uiPX(), 10.uiPX())
				}
				gravity = Gravity.CENTER_VERTICAL
				textSize = fontSize(15)
				textColor = GrayScale.black
				typeface = GoldStoneFont.heavy(context)
			}.click {
				// 如果 `textview` 的内容不是默认的 `placeholder` 就可以支持点击复制
				if (it.text.isNotEmpty()) {
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
				text = CommonText.confirm
				setBlueStyle(15.uiPX())
			}.click { button ->
				button.showLoadingStatus()
				presenter.getKeystoreJSON(passwordInput.text.toString()) {
					if (!it.isNullOrBlank()) {
						privateKeyTextView.text = it
					}
					button.showLoadingStatus(false)
				}
			}.into(this)
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		parentFragment?.let {
			if (it is BaseOverlayFragment<*>) {
				it.showAddButton(false)
			}
		}
	}

	override fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
		getParentFragment<WalletSettingsFragment> {
			headerTitle = WalletSettingsText.viewAddresses
			presenter.popFragmentFrom<KeystoreExportFragment>()
		}
	}
}