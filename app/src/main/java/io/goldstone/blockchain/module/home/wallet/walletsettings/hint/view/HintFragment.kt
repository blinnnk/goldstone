package io.goldstone.blockchain.module.home.wallet.walletsettings.hint.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.language.CreateWalletText
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.common.passcode.view.PasscodeFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.hint.presenter.HintPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 24/04/2018 10:54 AM
 * @author KaySaith
 */
class HintFragment : BaseFragment<HintPresenter>() {
	
	private val hintInput by lazy { RoundInput(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	override val presenter = HintPresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
			hintInput.apply {
				title = CreateWalletText.hint
				setMargins<LinearLayout.LayoutParams> {
					topMargin = 30.uiPX()
				}
			}.into(this)
			
			confirmButton.apply {
				text = CommonText.confirm.toUpperCase()
				setBlueStyle()
				y += 10.uiPX()
			}.click {
				presenter.updateHint(hintInput)
			}.into(this)
		}
	}
	
	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(view, savedInstanceState)
		WalletTable.getCurrentWallet {
			// 如果有设置 `hint` 并且有设置 `passcode` 那么首先展示 `passcode`
			AppConfigTable.getAppConfig {
				it?.showPincode?.isTrue {
					getParentFragment<ProfileOverlayFragment> {
						activity?.addFragmentAndSetArguments<PasscodeFragment>(ContainerID.main)
					}
				}
			}
			
			hintInput.hint = this.hint
		}
	}
	
	override fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
		getParentFragment<WalletSettingsFragment> {
			headerTitle = WalletSettingsText.walletSettings
			presenter.showWalletSettingListFragment()
		}
	}
}