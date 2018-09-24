package io.goldstone.blockchain.module.home.profile.contacts.contractinput.presenter

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.getParentFragment
import com.blinnnk.util.replaceFragmentAndSetArgument
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.ContactText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.ethereum.Address
import io.goldstone.blockchain.crypto.ethereum.isValid
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
import io.goldstone.blockchain.crypto.multichain.AddressType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.utils.MultiChainUtils
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.view.ContactInputFragment
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.view.ContactFragment
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment

/**
 * @date 16/04/2018 1:13 PM
 * @author KaySaith
 */
class ContactInputPresenter(
	override val fragment: ContactInputFragment
) : BasePresenter<ContactInputFragment>() {

	private var nameText = ""
	private var ethSeriesAddressText = ""
	private var eosAccountNameText = ""
	private var eosJungleAccountNameText = ""
	private var btcMainnetAddressText = ""
	private var btcTestnetAddressText = ""
	private var etcAddressText = ""
	private var ltcAddressText = ""
	private var bchAddressText = ""

	fun getAddressIfExist(
		ethSeriesInput: EditText,
		eosInput: EditText,
		eosJungleInput: EditText,
		btcMainnetInput: EditText,
		bchInput: EditText,
		btcSeriesTestnetInput: EditText,
		ltcInput: EditText
	) {
		fragment.getParentFragment<ProfileOverlayFragment>()?.apply {
			contactAddressModel?.let {
				when (MultiChainUtils.isValidMultiChainAddress(it.address, it.symbol)) {
					AddressType.ETHSeries -> {
						ethSeriesInput.setText(it.address)
						ethSeriesAddressText = it.address
					}

					AddressType.EOSJungle, AddressType.EOS, AddressType.EOSAccountName -> {
						if (!Config.isTestEnvironment()) {
							eosInput.setText(it.address)
							eosAccountNameText = it.address
						} else {
							eosJungleInput.setText(it.address)
							eosJungleAccountNameText = it.address
						}
					}

					AddressType.BTC -> {
						btcMainnetInput.setText(it.address)
						btcMainnetAddressText = it.address
					}

					AddressType.BCH -> {
						bchInput.setText(it.address)
						bchAddressText = it.address
					}

					AddressType.BTCSeriesTest -> {
						btcSeriesTestnetInput.setText(it.address)
						btcTestnetAddressText = it.address
					}

					AddressType.LTC -> {
						ltcInput.setText(it.address)
						ltcAddressText = it.address
					}
				}
			}
		}
	}

	fun addContact() {
		// 名字必须不为空
		if (nameText.isEmpty()) {
			fragment.context?.alert(ContactText.emptyNameAlert)
			return
		}
		// 至少有一个地址输入框有输入
		if ((
				ethSeriesAddressText.count()
					+ eosJungleAccountNameText.count()
					+ btcMainnetAddressText.count()
					+ btcTestnetAddressText.count()
					+ ltcAddressText.count()
					+ bchAddressText.count()
				) == 0
		) {
			fragment.context?.alert(ContactText.emptyAddressAlert)
			return
		}
		// 检查是否是合规的以太坊或以太经典的地址格式
		if (!Address(ethSeriesAddressText).isValid() && ethSeriesAddressText.isNotEmpty()) {
			fragment.context?.alert(ContactText.wrongAddressFormat("ETH/ERC20"))
			return
		}

		// 检查是否是合规的以太坊或以太经典的地址格式
		if (!EOSAccount(eosAccountNameText).isValid() && eosAccountNameText.isNotEmpty()) {
			fragment.context?.alert(ContactText.wrongAddressFormat("EOS"))
			return
		}

		// 检查是否是合规的以太坊或以太经典的地址格式
		if (!EOSAccount(eosJungleAccountNameText).isValid() && eosJungleAccountNameText.isNotEmpty()) {
			fragment.context?.alert(ContactText.wrongAddressFormat("EOS"))
			return
		}

		// 检查是否是合规的以太坊或以太经典的地址格式
		if (!LTCWalletUtils.isValidAddress(ltcAddressText) && ltcAddressText.isNotEmpty()) {
			fragment.context?.alert(ContactText.wrongAddressFormat(CoinSymbol.ltc))
			return
		}

		// 检查是否是合规的以太坊或以太经典的地址格式
		if (!BCHWalletUtils.isValidAddress(bchAddressText) && bchAddressText.isNotEmpty()) {
			fragment.context?.alert(ContactText.wrongAddressFormat(CoinSymbol.bch))
			return
		}

		// 检查是否是合规的测试网比特币私钥地址格式
		if (
			btcTestnetAddressText.isNotEmpty() &&
			!BTCUtils.isValidTestnetAddress(btcTestnetAddressText)
		) {
			fragment.context?.alert(ContactText.wrongAddressFormat("BTCTest"))
			return
		}
		// 检查是否是合规的主网比特币私钥地址格式
		if (
			btcMainnetAddressText.isNotEmpty() &&
			!BTCUtils.isValidMainnetAddress(btcMainnetAddressText)
		) {
			fragment.context?.alert(ContactText.wrongAddressFormat(CoinSymbol.btc()))
			return
		}
		// 符合以上规则的可以进入插入地址
		ContactTable.insertContact(
			ContactTable(
				0,
				"",
				nameText,
				"",
				ethSeriesAddressText,
				eosAccountNameText,
				eosJungleAccountNameText,
				btcMainnetAddressText,
				btcTestnetAddressText,
				etcAddressText,
				ltcAddressText,
				bchAddressText
			)
		) {
			fragment.getParentFragment<ProfileOverlayFragment> {
				if (!contactAddressModel.isNull()) {
					// 从账单详情快捷添加地址进入的页面
					replaceFragmentAndSetArgument<ContactFragment>(ContainerID.content)
					activity?.apply { SoftKeyboard.hide(this) }
				} else {
					presenter.popFragmentFrom<ContactInputFragment>()
				}
			}
		}
	}

	fun setConfirmButtonStyle(
		nameInput: EditText,
		ethETHSeriesInput: EditText,
		eosInput: EditText,
		eosJungleInput: EditText,
		btcMainnetInput: EditText,
		btcTestnetInput: EditText,
		ltcInput: EditText,
		bchInput: EditText,
		confirmButton: RoundButton
	) {
		nameInput.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(text: Editable?) {
				nameText = text.toString().orElse("")
				setStyle(confirmButton)
			}

			override fun beforeTextChanged(
				text: CharSequence?,
				start: Int,
				count: Int,
				after: Int
			) {
			}

			override fun onTextChanged(
				text: CharSequence?,
				start: Int,
				before: Int,
				count: Int
			) {
			}
		})

		ethETHSeriesInput.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(text: Editable?) {
				ethSeriesAddressText = text.toString().orElse("")
				setStyle(confirmButton)
			}

			override fun beforeTextChanged(
				text: CharSequence?,
				start: Int,
				count: Int,
				after: Int
			) {
			}

			override fun onTextChanged(
				text: CharSequence?,
				start: Int,
				before: Int,
				count: Int
			) {
			}
		})

		eosInput.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(text: Editable?) {
				eosAccountNameText = text.toString().orElse("")
				setStyle(confirmButton)
			}

			override fun beforeTextChanged(
				text: CharSequence?,
				start: Int,
				count: Int,
				after: Int
			) {
			}

			override fun onTextChanged(
				text: CharSequence?,
				start: Int,
				before: Int,
				count: Int
			) {
			}
		})

		eosJungleInput.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(text: Editable?) {
				eosJungleAccountNameText = text.toString().orElse("")
				setStyle(confirmButton)
			}

			override fun beforeTextChanged(
				text: CharSequence?,
				start: Int,
				count: Int,
				after: Int
			) {
			}

			override fun onTextChanged(
				text: CharSequence?,
				start: Int,
				before: Int,
				count: Int
			) {
			}
		})

		btcMainnetInput.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(text: Editable?) {
				btcMainnetAddressText = text.toString().orElse("")
				setStyle(confirmButton)
			}

			override fun beforeTextChanged(
				text: CharSequence?,
				start: Int,
				count: Int,
				after: Int
			) {
			}

			override fun onTextChanged(
				text: CharSequence?,
				start: Int,
				before: Int,
				count: Int
			) {
			}
		})

		btcTestnetInput.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(text: Editable?) {
				btcTestnetAddressText = text.toString().orElse("")
				setStyle(confirmButton)
			}

			override fun beforeTextChanged(
				text: CharSequence?,
				start: Int,
				count: Int,
				after: Int
			) {
			}

			override fun onTextChanged(
				text: CharSequence?,
				start: Int,
				before: Int,
				count: Int
			) {
			}
		})

		ltcInput.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(text: Editable?) {
				ltcAddressText = text.toString().orElse("")
				setStyle(confirmButton)
			}

			override fun beforeTextChanged(
				text: CharSequence?,
				start: Int,
				count: Int,
				after: Int
			) {
			}

			override fun onTextChanged(
				text: CharSequence?,
				start: Int,
				before: Int,
				count: Int
			) {
			}
		})

		bchInput.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(text: Editable?) {
				bchAddressText = text.toString().orElse("")
				setStyle(confirmButton)
			}

			override fun beforeTextChanged(
				text: CharSequence?,
				start: Int,
				count: Int,
				after: Int
			) {
			}

			override fun onTextChanged(
				text: CharSequence?,
				start: Int,
				before: Int,
				count: Int
			) {
			}
		})
	}

	private fun setStyle(confirmButton: RoundButton) {
		if (
			nameText.count()
			* (
				ethSeriesAddressText.count()
					+ eosJungleAccountNameText.count()
					+ eosAccountNameText.count()
					+ btcMainnetAddressText.count()
					+ btcTestnetAddressText.count()
					+ ltcAddressText.count()
					+ bchAddressText.count()
				)
			!= 0
		) {
			confirmButton.setBlueStyle(20.uiPX())
		} else {
			confirmButton.setGrayStyle(20.uiPX())
		}
	}
}