package io.goldstone.blockchain.module.home.wallet.walletsettings.hint.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.button.roundButton
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.edittext.roundInput
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.CreateWalletText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.module.common.passcode.view.PasscodeFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.view.AddressManagerFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.hint.presenter.HintPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import kotlinx.coroutines.Dispatchers
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.horizontalPadding
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 24/04/2018 10:54 AM
 * @author KaySaith
 */
class HintFragment : BaseFragment<HintPresenter>() {

	override val pageTitle: String = WalletSettingsText.hint
	private lateinit var hintInput: RoundInput
	private lateinit var confirmButton: RoundButton
	override val presenter = HintPresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
			hintInput = roundInput {
				title = CreateWalletText.hint
				horizontalPaddingSize = PaddingSize.gsCard
			}
			hintInput.setMargins<LinearLayout.LayoutParams> {
				topMargin = 30.uiPX()
			}

			confirmButton = roundButton {
				text = CommonText.confirm.toUpperCase()
				setBlueStyle(10.uiPX())
			}.click {
				presenter.updateHint(hintInput)
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		WalletTable.getCurrent(Dispatchers.Main) {
			// 如果有设置 `hint` 并且有设置 `passcode` 那么首先展示 `passcode`
			if (SharedValue.getPincodeDisplayStatus()) getParentFragment<ProfileOverlayFragment> {
				activity?.addFragmentAndSetArguments<PasscodeFragment>(ContainerID.main)
			}
			hintInput.hint = this.hint
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<WalletSettingsFragment> {
			presenter.popFragmentFrom<HintFragment>()
		}
	}
}