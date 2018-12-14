package io.goldstone.blockchain.module.home.profile.contacts.contractinput.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import com.blinnnk.extension.getParentFragment
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.button.roundButton
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.edittext.roundInput
import io.goldstone.blockchain.common.component.title.sessionTitle
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.ContactText
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.presenter.ContactInputPresenter
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import org.jetbrains.anko.*

/**
 * @date 16/04/2018 1:13 PM
 * @author KaySaith
 */
class ContactInputFragment : BaseFragment<ContactInputPresenter>() {

	override val pageTitle: String = ProfileText.contacts
	private lateinit var nameInput: RoundInput
	private lateinit var ethSeriesAddressInput: RoundInput
	private lateinit var eosAddressInput: RoundInput
	private lateinit var btcMainnetAddressInput: RoundInput
	private lateinit var bchAddressInput: RoundInput
	private lateinit var ltcAddressInput: RoundInput
	private lateinit var btcTestnetAddressInput: RoundInput
	private lateinit var eosJungleAddressInput: RoundInput
	private lateinit var eosKylinAddressInput: RoundInput
	private lateinit var confirmButton: RoundButton
	override val presenter = ContactInputPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, matchParent)
				leftPadding = PaddingSize.content
				rightPadding = PaddingSize.content
				nameInput = roundInput {
					title = ContactText.contactName
					setTextInput()
				}.lparams {
					width = matchParent
					topMargin = 40.uiPX()
				}
				sessionTitle {
					setTitle(ProfileText.contacts)
				}

				ethSeriesAddressInput = roundInput {
					title = CoinSymbol.eth
					hint = ContactText.ethERCAndETHint
				}.lparams {
					width = matchParent
					topMargin = 5.uiPX()
				}

				eosAddressInput = roundInput {
					title = CoinSymbol.eos
					hint = ContactText.eosHint
				}.lparams {
					width = matchParent
					topMargin = 5.uiPX()
				}

				btcMainnetAddressInput = roundInput {
					title = CoinSymbol.btc()
					hint = ContactText.btcMainnetAddress
				}.lparams {
					width = matchParent
					topMargin = 5.uiPX()
				}

				bchAddressInput = roundInput {
					title = CoinSymbol.bch
					hint = ContactText.bchAddress
				}.lparams {
					width = matchParent
					topMargin = 5.uiPX()
				}

				ltcAddressInput = roundInput {
					title = CoinSymbol.ltc
					hint = ContactText.ltcAddress
				}.lparams {
					width = matchParent
					topMargin = 5.uiPX()
				}

				eosJungleAddressInput = roundInput {
					title = "${CoinSymbol.eos} JUNGLE"
					hint = ContactText.eosJungleHint
					visibility = if (SharedValue.isTestEnvironment()) View.VISIBLE else View.GONE
				}.lparams {
					width = matchParent
					topMargin = 5.uiPX()
				}

				eosKylinAddressInput = roundInput {
					title = "${CoinSymbol.eos} JUNGLE"
					hint = ContactText.eosJungleHint
					visibility = if (SharedValue.isTestEnvironment()) View.VISIBLE else View.GONE
				}.lparams {
					width = matchParent
					topMargin = 5.uiPX()
				}

				btcTestnetAddressInput = roundInput {
					title = "${CoinSymbol.btc()} Series TEST"
					hint = ContactText.btcTestnetAddress
					visibility = if (SharedValue.isTestEnvironment()) View.VISIBLE else View.GONE
				}.lparams {
					width = matchParent
					topMargin = 5.uiPX()
				}

				confirmButton = roundButton {
					text = CommonText.confirm
					setGrayStyle(20.uiPX())
				}.click {
					presenter.addContact()
				}

				presenter.getAddressIfExist(
					ethSeriesAddressInput,
					eosAddressInput,
					eosJungleAddressInput,
					eosKylinAddressInput,
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
		eosKylinAddressInput.setText(data.eosKylin)
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
			eosKylinAddressInput,
			btcMainnetAddressInput,
			btcTestnetAddressInput,
			ltcAddressInput,
			bchAddressInput,
			confirmButton
		)
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<ProfileOverlayFragment> {
			if (childFragmentManager.fragments.size <= 1) {
				// 从账单详情添加快捷通讯录跳转过来, 是没有上级入口的. 这里直接销毁。
				presenter.removeSelfFromActivity()
			} else {
				presenter.popFragmentFrom<ContactInputFragment>()
			}
		}
	}
}