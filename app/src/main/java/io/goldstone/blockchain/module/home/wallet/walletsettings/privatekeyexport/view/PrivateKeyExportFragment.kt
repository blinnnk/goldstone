package io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.DescriptionView
import io.goldstone.blockchain.common.component.ValueView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.button.roundButton
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.edittext.roundInput
import io.goldstone.blockchain.common.component.valueView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.CreateWalletText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
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
	private lateinit var privateKeyValue: ValueView
	private lateinit var passwordInput: RoundInput
	private lateinit var confirmButton: RoundButton
	override val presenter = PrivateKeyExportPresenter(this)

	private val address by lazy {
		arguments?.getString(ArgumentKey.address)
	}
	private val chainType by lazy {
		arguments?.getInt(ArgumentKey.chainType)?.let { ChainType(it) }
	}

	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
			DescriptionView(context).isExportPrivateKey().into(this)
			// 如果 `textView` 的内容不是默认的 `placeholder` 就可以支持点击复制
			privateKeyValue = valueView {
				layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 120.uiPX())
			}.click {
				context.clickToCopy(privateKeyValue.getContent())
			}

			passwordInput = roundInput {
				horizontalPaddingSize = PaddingSize.gsCard
				setPasswordInput()
				title = CreateWalletText.password
			}

			passwordInput.setMargins<LinearLayout.LayoutParams> {
				topMargin = 30.uiPX()
			}

			confirmButton = roundButton {
				text = CommonText.confirm
				setBlueStyle(15.uiPX())
			}.click { button ->
				button.showLoadingStatus()
				PrivateKeyExportPresenter.getPrivateKey(
					address.orEmpty(),
					chainType!!,
					passwordInput.text.toString()
				) { privateKey, error ->
					launchUI {
						if (privateKey.isNotNull() && error.isNone())
							privateKeyValue.setContent(privateKey)
						else safeShowError(error)
						button.showLoadingStatus(false)
						activity?.apply { SoftKeyboard.hide(this) }
					}
				}
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		parentFragment?.let {
			if (it is BaseOverlayFragment<*>) {
				it.showAddButton(false) {}
			}
		}
	}

	override fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
		getParentFragment<WalletSettingsFragment> {
			presenter.popFragmentFrom<PrivateKeyExportFragment>()
		}
	}
}
