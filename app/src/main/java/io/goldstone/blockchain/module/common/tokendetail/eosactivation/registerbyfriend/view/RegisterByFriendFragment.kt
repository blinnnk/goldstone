package io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbyfriend.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import com.blinnnk.extension.into
import com.blinnnk.extension.isNull
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.DescriptionView
import io.goldstone.blockchain.common.component.KeyValueView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.title.SessionTitleView
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbyfriend.presenter.RegisterByFriendPresenter
import io.goldstone.blockchain.module.home.dapp.eosaccountregister.presenter.EOSAccountRegisterPresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.verticalLayout


/**
 * @author KaySaith
 * @date  2018/09/25
 */
class RegisterByFriendFragment : BaseFragment<RegisterByFriendPresenter>() {

	private val accountNameInput by lazy { RoundInput(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	private val copyResultButton by lazy { RoundButton(context!!) }
	private val availableDescriptionView by lazy { DescriptionView(context!!) }
	private val availableResultView by lazy { KeyValueView(context!!) }
	private val availableSessionTitle by lazy { SessionTitleView(context!!) }
	private var isValidAccountName = false
	override val presenter = RegisterByFriendPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams(matchParent, matchParent)
				gravity = Gravity.CENTER_HORIZONTAL
				DescriptionView(context).isRegisterByFriend().into(this)
				accountNameInput.apply {
					title = ImportWalletText.eosAccountName
					afterTextChanged = Runnable {
						val checker = EOSAccount(getContent()).checker()
						if (checker.isValid()) {
							isValidAccountName = true
							setValidStatus(true, "Valid")
						} else {
							isValidAccountName = false
							setValidStatus(false, checker.shortDescription)
						}
					}
				}.into(this)
				SessionTitleView(context).apply { setTitle("CLICK TO COPY") }.into(this)
				KeyValueView(context).apply {
					gravity = Gravity.CENTER
					text = Config.getCurrentEOSAddress()
				}.click {
					it.context.clickToCopy(Config.getCurrentEOSAddress())
				}.into(this)

				// 检测成功后这个会显示出来, 默认是隐藏的
				availableDescriptionView.apply {
					visibility = View.GONE
				}.isAvailableAccountName().into(this)
				availableSessionTitle.apply {
					visibility = View.GONE
					setTitle("AVAILABLE ACCOUNT INFO")
				}.into(this)
				availableResultView.apply { visibility = View.GONE }.into(this)
				copyResultButton.apply {
					visibility = View.GONE
					setBlueStyle(20.uiPX())
					text = "Copy The Result"
				}.click {
					availableResultView.apply {
						context.clickToCopy(getContent())
					}
				}.into(this)

				confirmButton.apply {
					setBlueStyle(20.uiPX())
					text = "Check Name Is Available In Chain"
				}.click {
					val account = EOSAccount(accountNameInput.getContent())
					when {
						isValidAccountName -> {
							it.showLoadingStatus()
							EOSAccountRegisterPresenter.checkNameIsAvailableInChain(account) { isAvailable, error ->
								it.showLoadingStatus(false)
								activity?.apply { SoftKeyboard.hide(this) }
								if (!isAvailable.isNull() && error.isNone()) {
									if (isAvailable!!) showAvailableResult(account)
									else context.alert("unavailable account name")
								} else context.alert(error.message)
							}
						}
						account.accountName.isEmpty() -> context.alert("Empty Account Name")
						else -> context.alert("Invalid Account Name")
					}
				}.into(this)
			}
		}
	}

	private fun showAvailableResult(newAccount: EOSAccount) {
		availableSessionTitle.visibility = View.VISIBLE
		availableResultView.visibility = View.VISIBLE
		availableDescriptionView.visibility = View.VISIBLE
		confirmButton.visibility = View.GONE
		copyResultButton.visibility = View.VISIBLE
		availableResultView.text = newAccount.accountName + "-" + Config.getCurrentEOSAddress()
	}
}