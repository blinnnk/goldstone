package io.goldstone.blockchain.module.home.profile.contacts.contractinput.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.title.SessionTitleView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.ContactText
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.presenter.ContactInputPresenter
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.verticalLayout

/**
 * @date 16/04/2018 1:13 PM
 * @author KaySaith
 */
class ContactInputFragment : BaseFragment<ContactInputPresenter>() {

	override val pageTitle: String = ProfileText.contacts
	private val nameInput by lazy { RoundInput(context!!) }
	private val ethSeriesAddressInput by lazy { RoundInput(context!!) }
	private val eosAddressInput by lazy { RoundInput(context!!) }
	private val btcMainnetAddressInput by lazy { RoundInput(context!!) }
	private val bchAddressInput by lazy { RoundInput(context!!) }
	private val ltcAddressInput by lazy { RoundInput(context!!) }
	private val btcTestnetAddressInput by lazy { RoundInput(context!!) }
	private val eosJungleAddressInput by lazy { RoundInput(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	override val presenter = ContactInputPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, matchParent)
				nameInput.apply {
					title = ContactText.contactName
					setTextInput()
					setMargins<LinearLayout.LayoutParams> { topMargin = 40.uiPX() }
				}.into(this)

				SessionTitleView(context).setTitle("Contact Address").into(this)

				ethSeriesAddressInput.apply {
					setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
					title = CoinSymbol.eth
					hint = ContactText.ethERCAndETHint
				}.into(this)

				eosAddressInput.apply {
					setMargins<LinearLayout.LayoutParams> { topMargin = 5.uiPX() }
					title = CoinSymbol.eos
					hint = ContactText.eosHint
				}.into(this)

				btcMainnetAddressInput.apply {
					setMargins<LinearLayout.LayoutParams> { topMargin = 5.uiPX() }
					title = CoinSymbol.btc()
					hint = ContactText.btcMainnetAddress
				}.into(this)

				bchAddressInput.apply {
					setMargins<LinearLayout.LayoutParams> { topMargin = 5.uiPX() }
					title = CoinSymbol.bch
					hint = ContactText.bchAddress
				}.into(this)

				ltcAddressInput.apply {
					setMargins<LinearLayout.LayoutParams> { topMargin = 5.uiPX() }
					title = CoinSymbol.ltc
					hint = ContactText.ltcAddress
				}.into(this)

				eosJungleAddressInput.apply {
					setMargins<LinearLayout.LayoutParams> { topMargin = 5.uiPX() }
					title = "${CoinSymbol.eos} JUNGLE"
					hint = ContactText.eosJungleHint
					visibility = if (SharedValue.isTestEnvironment()) View.VISIBLE else View.GONE
				}.into(this)

				btcTestnetAddressInput.apply {
					setMargins<LinearLayout.LayoutParams> { topMargin = 5.uiPX() }
					title = "${CoinSymbol.btc()} TEST"
					hint = ContactText.btcTestnetAddress
					visibility = if (SharedValue.isTestEnvironment()) View.VISIBLE else View.GONE
				}.into(this)


				confirmButton.apply {
					text = CommonText.confirm
					setGrayStyle(20.uiPX())
				}.click {
					presenter.addContact()
				}.into(this)

				presenter.getAddressIfExist(
					ethSeriesAddressInput,
					eosAddressInput,
					eosJungleAddressInput,
					btcMainnetAddressInput,
					bchAddressInput,
					btcTestnetAddressInput,
					ltcAddressInput
				)
			}
		}
	}

	fun setAddressValue(data: ContactTable) {
		nameInput.setText(data.name)
		ethSeriesAddressInput.setText(data.ethSeriesAddress)
		eosAddressInput.setText(data.eosAddress)
		eosJungleAddressInput.setText(data.eosJungle)
		btcMainnetAddressInput.setText(data.btcMainnetAddress)
		btcTestnetAddressInput.setText(data.btcSeriesTestnetAddress)
		ltcAddressInput.setText(data.ltcAddress)
		bchAddressInput.setText(data.bchAddress)
	}

	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(view, savedInstanceState)
		presenter.setConfirmButtonStyle(
			nameInput,
			ethSeriesAddressInput,
			eosAddressInput,
			eosJungleAddressInput,
			btcMainnetAddressInput,
			btcTestnetAddressInput,
			ltcAddressInput,
			bchAddressInput,
			confirmButton
		)
	}

	override fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
		getParentFragment<ProfileOverlayFragment> {
			if (childFragmentManager.fragments.size <= 1) {
				// 从账单详情添加快捷通讯录跳转过来, 是没有上级入口的. 这里直接销毁。
				presenter.removeSelfFromActivity()
			} else {
				headerTitle = ProfileText.contacts
				presenter.popFragmentFrom<ContactInputFragment>()
			}
		}
	}
}