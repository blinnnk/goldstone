package io.goldstone.blockchain.module.home.dapp.eosaccountregister.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.view.ColumnSectionTitle
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.cell.GraySquareCell
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.edittext.WalletEditText
import io.goldstone.blockchain.common.component.title.AttentionTextView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.module.home.dapp.eosaccountregister.presenter.EOSAccountRegisterPresenter
import org.jetbrains.anko.*
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/21
 */
class EOSAccountRegisterFragment : BaseFragment<EOSAccountRegisterPresenter>() {

	private val confirmButton by lazy { RoundButton(context!!) }
	private val accountNameInput by lazy { RoundInput(context!!) }
	private val publickeyInput by lazy { WalletEditText(context!!) }
	private val settingButton by lazy { GraySquareCell(context!!) }
	private val resourceCoast by lazy { GraySquareCell(context!!) }

	override val presenter = EOSAccountRegisterPresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams(matchParent, matchParent)
				gravity = Gravity.CENTER_HORIZONTAL
				AttentionTextView(context).apply {
					isCenter()
					bottomPadding = 20.uiPX()
					text = "The username must be 12 characters long, and the character content can only contain the letters A~Z or the numbers 1~5."
				}.into(this)
				accountNameInput.apply {
					title = ImportWalletText.eosAccountName
					afterTextChanged = Runnable {
						val checker =
							EOSWalletUtils.isValidAccountName(getContent())
						if (checker.isValid()) setValidStatus(true, "Available")
						else setValidStatus(false, checker.shortDescription)
					}
				}.into(this)

				AttentionTextView(context).apply {
					isCenter()
					topPadding = 20.uiPX()
					text = "Registering an account requires injecting a certain amount of resources into the new account, so that the new account can complete the most basic operations."
				}.into(this)

				publickeyInput.apply {
					hint = ImportWalletText.registerEOSPublicKey
					setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
				}.into(this)

				ColumnSectionTitle(context).apply {
					showTitles(listOf(Pair("Assign RAM", "4096 Bytes"), Pair("Assign CPU", "0.1 EOS"), Pair("Assign NET", "0.1 EOS")))
					setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
				}.into(this)

				settingButton.apply {
					showArrow()
					setTitle("Advanced Settings")
				}.into(this)

				resourceCoast.apply {
					setTitle("Estimated Expenditure")
					setSubtitle("0.3 EOS ≈ 45.72 (CNY)")
				}.into(this)

				confirmButton.apply {
					text = CommonText.confirm.toUpperCase()
					setBlueStyle(20.uiPX())
				}.click { button ->
					button.showLoadingStatus()
					presenter.registerAccount(
						accountNameInput.getContent(),
						publickeyInput.getContent(),
						BigInteger.valueOf(4096),
						0.5,
						0.5
					) {
						if (!it.isNone()) context.alert(it.message)
						button.showLoadingStatus(false)
					}
				}.into(this)
			}
		}
	}

}