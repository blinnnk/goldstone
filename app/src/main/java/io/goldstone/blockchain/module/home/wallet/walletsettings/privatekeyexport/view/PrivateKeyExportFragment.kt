package io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.DescriptionView
import io.goldstone.blockchain.common.component.KeyValueView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.CreateWalletText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter.PrivateKeyExportPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 06/04/2018 1:02 AM
 * @author KaySaith
 */
class PrivateKeyExportFragment : BaseFragment<PrivateKeyExportPresenter>() {

	override val pageTitle: String = WalletSettingsText.exportPrivateKey
	private val privateKeyTextView by lazy { KeyValueView(context!!) }
	private val passwordInput by lazy { RoundInput(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	override val presenter = PrivateKeyExportPresenter(this)

	private val address by lazy { arguments?.getString(ArgumentKey.address) }
	private val chainType by lazy { arguments?.getInt(ArgumentKey.chainType)?.let { ChainType(it) } }

	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
			DescriptionView(context).isExportPrivateKey().into(this)
			privateKeyTextView.apply {
				layoutParams.height = 80.uiPX()
			}.click {
				// 如果 `textView` 的内容不是默认的 `placeholder` 就可以支持点击复制
				if (it.text.isNotEmpty()) context.clickToCopy(privateKeyTextView.text.toString())
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
				setBlueStyle(15.uiPX())
			}.click { it ->
				it.showLoadingStatus()
				PrivateKeyExportPresenter.getPrivateKey(
					context,
					address.orEmpty(),
					chainType!!,
					passwordInput.text.toString()
				) { privateKey, error ->
					if (privateKey != null && error.isNone()) {
						privateKeyTextView.text = privateKey
					} else context.alert(error.message)
					it.showLoadingStatus(false)
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
			presenter.popFragmentFrom<PrivateKeyExportFragment>()
		}
	}
}
