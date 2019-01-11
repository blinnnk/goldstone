package io.goldstone.blinnnk.module.home.dapp.eosaccountregister.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.model.MutablePair
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.base.basefragment.BaseFragment
import io.goldstone.blinnnk.common.base.view.ColumnSectionTitle
import io.goldstone.blinnnk.common.component.DescriptionView
import io.goldstone.blinnnk.common.component.button.RoundButton
import io.goldstone.blinnnk.common.component.button.roundButton
import io.goldstone.blinnnk.common.component.cell.GraySquareCell
import io.goldstone.blinnnk.common.component.cell.graySquareCell
import io.goldstone.blinnnk.common.component.edittext.RoundInput
import io.goldstone.blinnnk.common.component.edittext.WalletEditText
import io.goldstone.blinnnk.common.component.edittext.roundInput
import io.goldstone.blinnnk.common.component.overlay.Dashboard
import io.goldstone.blinnnk.common.language.*
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.NetworkUtil
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.common.utils.safeShowError
import io.goldstone.blinnnk.common.value.PaddingSize
import io.goldstone.blinnnk.crypto.eos.EOSValue
import io.goldstone.blinnnk.crypto.eos.account.EOSAccount
import io.goldstone.blinnnk.crypto.eos.base.showDialog
import io.goldstone.blinnnk.crypto.multichain.CryptoValue
import io.goldstone.blinnnk.crypto.utils.formatCount
import io.goldstone.blinnnk.crypto.utils.formatCurrency
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blinnnk.module.home.dapp.eosaccountregister.presenter.EOSAccountRegisterPresenter
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/21
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EOSAccountRegisterFragment : BaseFragment<EOSAccountRegisterPresenter>() {

	override val pageTitle: String = ProfileText.eosAccountRegister
	private lateinit var confirmButton: RoundButton
	private lateinit var accountNameInput: RoundInput
	private val publickeyInput by lazy { WalletEditText(context!!) }
	private lateinit var settingButton: GraySquareCell
	private lateinit var resourceCoast: GraySquareCell
	private val gridSessionTitle by lazy { ColumnSectionTitle(context!!) }
	private var assignResources =
		listOf(
			MutablePair(TokenDetailText.ram, "4096"),
			MutablePair("CPU (EOS)", "0.1"),
			MutablePair("NET (EOS)", "0.1")
		)

	override val presenter = EOSAccountRegisterPresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams(matchParent, matchParent)
				gravity = Gravity.CENTER_HORIZONTAL
				DescriptionView(context).isNameRule().into(this)
				accountNameInput = roundInput {
					horizontalPaddingSize = PaddingSize.gsCard
					title = ImportWalletText.eosAccountName
					afterTextChanged = Runnable {
						val checker = EOSAccount(getContent()).checker()
						if (checker.isValid()) setValidStatus(true, "Valid")
						else setValidStatus(false, checker.shortDescription)
					}
				}

				DescriptionView(context).isRegisterResource().into(this)

				publickeyInput.apply {
					hint = ImportWalletText.registerEOSPublicKey
					setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
				}.into(this)

				gridSessionTitle.apply {
					showTitles(assignResources)
					setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
				}.into(this)

				settingButton = graySquareCell {
					showArrow()
					setTitle(EOSAccountText.advancedSettings)
				}.click {
					getParentContainer()?.showCustomDashboard(assignResources)
				}

				resourceCoast = graySquareCell {
					setTitle(EOSAccountText.estimatedSpentOfActiveAccount)
					setSubtitle(CommonText.calculating)
				}

				confirmButton = roundButton {
					text = CommonText.confirm.toUpperCase()
					setBlueStyle(20.uiPX())
				}.click { button ->
					button.showLoadingStatus()
					presenter.registerAccount(
						EOSAccount(accountNameInput.getContent()),
						publickeyInput.getContent(),
						BigInteger(assignResources[0].right),
						assignResources[1].right.toDouble(),
						assignResources[2].right.toDouble(),
						cancelAction = { button.showLoadingStatus(false) }
					) { response, error ->
						launchUI {
							if (response.isNotNull() && error.isNone()) response.showDialog(context)
							else safeShowError(error)
							button.showLoadingStatus(false)
						}
					}
				}
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		if (NetworkUtil.hasNetworkWithAlert(context)) setExpenditure()
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		if (parent is TokenDetailOverlayFragment) {
			parent.presenter.popFragmentFrom<EOSAccountRegisterFragment>()
		} else super.setBaseBackEvent(activity, parent)
	}

	private fun setExpenditure() {
		presenter.getEOSCurrencyAndRAMPrice { currency, ramPrice, error ->
			if (currency.isNotNull() && ramPrice.isNotNull() && error.isNone()) GlobalScope.launch(Dispatchers.Main) {
				val eosCount = assignResources[1].right.toDouble() + assignResources[2].right.toDouble() + assignResources[0].right.toIntOrZero() * ramPrice
				val totalCurrency = eosCount * currency
				resourceCoast.setSubtitle("≈ ${eosCount.formatCount(4)} EOS ≈ ${totalCurrency.formatCurrency()} (${SharedWallet.getCurrencyCode()})")
			} else safeShowError(error)
		}
	}

	private fun ViewGroup.showCustomDashboard(values: List<MutablePair<String, String>>) {
		val settingInputs = LinearLayout(context).apply {
			orientation = LinearLayout.VERTICAL
			layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
			values.forEachIndexed { index, mutablePair ->
				RoundInput(context).apply {
					horizontalPaddingSize = 20.uiPX()
					setNumberInput(index != 0)
					id = index
					layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
					this.title = mutablePair.left
					hint = mutablePair.right
				}.into(this)
			}
		}

		fun LinearLayout.updateSettingValue() {
			(0 until values.size).forEach { index ->
				val newValue = findViewById<RoundInput>(index)?.getContent()
				if (!newValue.isNullOrEmpty()) {
					val formattedNumber =
						if (values[index].left.contains(TokenDetailText.ram, true))
							"${newValue.toIntOrNull().orElse(EOSValue.defaultRegisterAssignRAM)}"
						else "${newValue.convertToDouble(CryptoValue.eosDecimal).orElse(EOSValue.defaultRegisterAssignBandWidth)}"
					// 更新界面上的值
					gridSessionTitle.updateValues(index, formattedNumber)
					// 更新内存里面的值
					assignResources[index].right = formattedNumber
					// 更新预估价值
					setExpenditure()
				}
			}
		}

		Dashboard(context) {
			showDashboard(
				EOSAccountText.customizeResource,
				EOSAccountText.customizeNewAccountResourceDescription,
				settingInputs,
				{ it.updateSettingValue() }
			) {}
		}
	}

}