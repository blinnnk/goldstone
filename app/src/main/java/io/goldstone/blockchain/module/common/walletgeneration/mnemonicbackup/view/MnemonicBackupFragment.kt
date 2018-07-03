package io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.jump
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AttentionTextView
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.TagCell
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.presenter.MnemonicBackupPresenter
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.*

/**
 * @date 22/03/2018 9:32 PM
 * @author KaySaith
 */
class MnemonicBackupFragment : BaseFragment<MnemonicBackupPresenter>() {
	
	private val mnemonicCode by lazy { arguments?.getString(ArgumentKey.mnemonicCode) }
	private val confirmButton by lazy { RoundButton(context!!) }
	private val skipButton by lazy { RoundButton(context!!) }
	private val attentionTextView by lazy { AttentionTextView(context!!) }
	override val presenter = MnemonicBackupPresenter(this)
	
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, matchParent)
				attentionTextView.apply {
					text = CreateWalletText.mnemonicBackupAttention
				}.into(this)
				gridLayout {
					rowCount = 6
					columnCount = 2
					layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent)
					setMargins<LinearLayout.LayoutParams> { topMargin = 30.uiPX() }
					mnemonicCode?.split(" ")?.forEachIndexed { index, value ->
						val cell = TagCell(context).apply {
							setNumberAndText(index + 1, value)
						}
						cell.into(this)
					}
				}
				skipButton.apply {
					text = CommonText.skip.toUpperCase()
					setBlueStyle(50.uiPX())
				}.click {
					presenter.skipBackUp()
				}.into(this)
				confirmButton.apply {
					text = CommonText.confirm.toUpperCase()
					setBlueStyle(5.uiPX())
				}.click {
					presenter.goToMnemonicConfirmation(mnemonicCode)
				}.into(this)
			}
		}
	}
	
	override fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
		when (parent) {
			is WalletGenerationFragment -> {
				parent.presenter.removeSelfFromActivity()
				this.activity?.jump<SplashActivity>()
			}
			
			is WalletSettingsFragment -> {
				parent.headerTitle = WalletSettingsText.walletSettings
				parent.presenter.showWalletSettingListFragment()
			}
		}
	}
}