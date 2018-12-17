package io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbyfriend.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.isNull
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.clickToCopy
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.DescriptionView
import io.goldstone.blockchain.common.component.ValueView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.button.roundButton
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.edittext.roundInput
import io.goldstone.blockchain.common.component.title.SessionTitleView
import io.goldstone.blockchain.common.component.title.sessionTitle
import io.goldstone.blockchain.common.component.valueView
import io.goldstone.blockchain.common.language.EOSAccountText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbyfriend.presenter.RegisterByFriendPresenter
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.home.dapp.eosaccountregister.presenter.EOSAccountRegisterPresenter
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.verticalLayout


/**
 * @author KaySaith
 * @date  2018/09/25
 */
class RegisterByFriendFragment : BaseFragment<RegisterByFriendPresenter>() {

	override val pageTitle: String
		get() = getParentFragment<TokenDetailOverlayFragment>()?.token?.symbol?.symbol.orEmpty()
	private lateinit var accountNameInput: RoundInput
	private lateinit var confirmButton: RoundButton
	private lateinit var copyResultButton: RoundButton
	private val availableDescriptionView by lazy { DescriptionView(context!!) }
	private lateinit var availableResultView: ValueView
	private lateinit var availableSessionTitle: SessionTitleView
	private var isValidAccountName = false
	private var hasResultView = false
	override val presenter = RegisterByFriendPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams(matchParent, matchParent)
				gravity = Gravity.CENTER_HORIZONTAL
				DescriptionView(context).isRegisterByFriend().into(this)
				accountNameInput = roundInput {
					horizontalPaddingSize = PaddingSize.gsCard
					title = ImportWalletText.eosAccountName
					afterTextChanged = Runnable {
						if (hasResultView) {
							showAvailableResult(false, null)
							hasResultView = false
						}
						val checker = EOSAccount(getContent()).checker()
						if (checker.isValid()) {
							isValidAccountName = true
							setValidStatus(true, EOSAccountText.checkNameResultValid)
						} else {
							isValidAccountName = false
							setValidStatus(false, checker.shortDescription)
						}
					}
				}
				sessionTitle(EOSAccountText.copyPublicKey)
				valueView {
					gravity = Gravity.CENTER
					setContent(SharedAddress.getCurrentEOS())
				}.click {
					it.context.clickToCopy(SharedAddress.getCurrentEOS())
				}

				// 检测成功后这个会显示出来, 默认是隐藏的
				availableDescriptionView.apply {
					visibility = View.GONE
				}.isAvailableAccountName().into(this)
				availableSessionTitle = sessionTitle {
					visibility = View.GONE
					setTitle("AVAILABLE ACCOUNT INFO")
				}
				availableResultView = valueView {
					visibility = View.GONE
				}
				copyResultButton = roundButton {
					visibility = View.GONE
					setBlueStyle(20.uiPX())
					text = "Copy The Result"
				}.click {
					availableResultView.apply {
						context.clickToCopy(getContent())
					}
				}

				confirmButton = roundButton {
					setBlueStyle(20.uiPX())
					text = EOSAccountText.checkNameAvailability
				}.click {
					val account = EOSAccount(accountNameInput.getContent())
					when {
						isValidAccountName -> {
							it.showLoadingStatus()
							EOSAccountRegisterPresenter.checkNameIsAvailableInChain(account) { isAvailable, error ->
								it.showLoadingStatus(false)
								activity?.apply { SoftKeyboard.hide(this) }
								if (!isAvailable.isNull() && error.isNone()) {
									if (isAvailable) {
										showAvailableResult(true, account)
										hasResultView = true
									}
									else context.alert(EOSAccountText.checkNameResultUnavailable)
								} else context.alert(error.message)
							}
						}
						account.name.isEmpty() -> context.alert(EOSAccountText.checkNameResultEmpty)
						else -> context.alert(EOSAccountText.checkNameResultEmpty)
					}
				}
			}
		}
	}

	private fun showAvailableResult(status: Boolean, newAccount: EOSAccount?) {
		availableSessionTitle.visibility = if (status) View.VISIBLE else View.GONE
		availableResultView.visibility = if (status) View.VISIBLE else View.GONE
		availableDescriptionView.visibility = if (status) View.VISIBLE else View.GONE
		confirmButton.visibility = if (status) View.GONE else View.VISIBLE
		copyResultButton.visibility = if (status) View.VISIBLE else View.GONE
		val data = if (status)  newAccount?.name + "-" + SharedAddress.getCurrentEOS() else ""
		availableResultView.setContent(data)
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<TokenDetailOverlayFragment> {
			presenter.popFragmentFrom<RegisterByFriendFragment>()
		}
	}
}