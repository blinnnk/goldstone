package io.goldstone.blockchain.module.home.profile.contacts.contractinput.presenter

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.orElse
import com.blinnnk.extension.orZero
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.*
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.ContactText
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
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
import io.goldstone.blockchain.module.home.profile.contacts.contracts.event.ContactUpdateEvent
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.view.ContactFragment
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import org.bitcoinj.params.TestNet3Params
import org.greenrobot.eventbus.EventBus

/**
 * @date 16/04/2018 1:13 PM
 * @author KaySaith
 */
class ContactInputPresenter(
	override val fragment: ContactInputFragment
) : BasePresenter<ContactInputFragment>() {

	private val contactID by lazy {
		fragment.arguments?.getInt(ArgumentKey.contactID)
	}

	private var nameText = ""
	private var ethSeriesAddressText = ""
	private var eosAccountNameText = ""
	private var eosJungleAccountNameText = ""
	private var eosKylinAccountNameText = ""
	private var btcMainnetAddressText = ""
	private var btcTestnetAddressText = ""
	private var etcAddressText = ""
	private var ltcAddressText = ""
	private var bchAddressText = ""

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		contactID?.apply {
			load {
				ContactTable.dao.getContact(this)
			} then {
				it?.apply { fragment.setAddressValue(this) }
			}
		}
	}

	fun getAddressIfExist(
		ethSeriesInput: EditText,
		eosInput: EditText,
		eosJungleInput: EditText,
		eosKylinInput: EditText,
		btcMainnetInput: EditText,
		bchInput: EditText,
		btcSeriesTestnetInput: EditText,
		ltcInput: EditText
	) {
		fragment.getParentFragment<ProfileOverlayFragment>()?.apply {
			contactAddressModel?.let {
				val addressType = MultiChainUtils.isValidMultiChainAddress(it.address, CoinSymbol(it.symbol))
				when (addressType) {
					AddressType.ETHSeries -> {
						ethSeriesInput.setText(it.address)
						ethSeriesAddressText = it.address
					}

					AddressType.EOSJungle,
					AddressType.EOSKylin,
					AddressType.EOS,
					AddressType.EOSAccountName -> {
						if (!SharedValue.isTestEnvironment()) {
							eosInput.setText(it.address)
							eosAccountNameText = it.address
						} else {
							if (addressType == AddressType.EOSKylin) {
								eosKylinInput.setText(it.address)

							} else {
								eosJungleInput.setText(it.address)
								eosKylinAccountNameText = it.address
							}
						}
					}

					AddressType.BTC -> {
						btcMainnetInput.setText(it.address)
						btcMainnetAddressText = it.address
					}

					AddressType.BCH -> {
						if (SharedValue.isTestEnvironment()) {
							btcSeriesTestnetInput.setText(it.address)
							btcTestnetAddressText = it.address
						} else {
							bchInput.setText(it.address)
							bchAddressText = it.address
						}
					}

					AddressType.BTCSeriesTest -> {
						btcSeriesTestnetInput.setText(it.address)
						btcTestnetAddressText = it.address
					}

					AddressType.LTC -> {
						if (SharedValue.isTestEnvironment()) {
							btcSeriesTestnetInput.setText(it.address)
							btcTestnetAddressText = it.address
						} else {
							ltcInput.setText(it.address)
							ltcAddressText = it.address
						}
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
					+ eosAccountNameText.count()
					+ eosJungleAccountNameText.count()
					+ eosKylinAccountNameText.count()
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
		if (ethSeriesAddressText.isNotEmpty() && !Address(ethSeriesAddressText).isValid()) {
			fragment.context?.alert(ContactText.wrongAddressFormat("ETH/ERC20"))
			return
		}

		// 检查是否是合规的以太坊或以太经典的地址格式
		if (eosAccountNameText.isNotEmpty() && !EOSAccount(eosAccountNameText).isValid(false)) {
			fragment.context?.alert(ContactText.wrongAddressFormat("EOS"))
			return
		}

		// 检查是否是合规的以太坊或以太经典的地址格式
		if (eosJungleAccountNameText.isNotEmpty() && !EOSAccount(eosJungleAccountNameText).isValid(false)) {
			fragment.context?.alert(ContactText.wrongAddressFormat("EOS"))
			return
		}

		// 检查是否是合规的以太坊或以太经典的地址格式
		if (eosKylinAccountNameText.isNotEmpty() && !EOSAccount(eosKylinAccountNameText).isValid(false)) {
			fragment.context?.alert(ContactText.wrongAddressFormat("EOS"))
			return
		}

		// 检查是否是合规的以太坊或以太经典的地址格式
		if (ltcAddressText.isNotEmpty() && !LTCWalletUtils.isValidAddress(ltcAddressText)) {
			fragment.context?.alert(ContactText.wrongAddressFormat(CoinSymbol.ltc))
			return
		}

		// 检查是否是合规的以太坊或以太经典的地址格式
		if (bchAddressText.isNotEmpty() && !BCHWalletUtils.isValidAddress(bchAddressText)) {
			fragment.context?.alert(ContactText.wrongAddressFormat(CoinSymbol.bch))
			return
		}

		// 检查是否是合规的测试网比特币私钥地址格式
		if (btcTestnetAddressText.isNotEmpty()) {
			if (!BTCUtils.isValidTestnetAddress(formattedBTCSeriesTestAddress(btcTestnetAddressText))) {
				fragment.context?.alert(ContactText.wrongAddressFormat("BTCTest"))
				return
			}
		}
		// 检查是否是合规的主网比特币私钥地址格式
		if (
			btcMainnetAddressText.isNotEmpty() &&
			!BTCUtils.isValidMainnetAddress(btcMainnetAddressText)
		) {
			fragment.context?.alert(ContactText.wrongAddressFormat(CoinSymbol.btc))
			return
		}
		// 符合以上规则的可以进入插入地址
		ContactTable.insertContact(
			ContactTable(
				contactID.orZero(),
				"",
				nameText,
				"",
				ethSeriesAddressText,
				eosAccountNameText,
				eosJungleAccountNameText,
				eosKylinAccountNameText,
				btcMainnetAddressText,
				formattedBTCSeriesTestAddress(btcTestnetAddressText),
				etcAddressText,
				ltcAddressText,
				bchAddressText
			)
		) {
			EventBus.getDefault().post(ContactUpdateEvent(true))
			fragment.getParentFragment<ProfileOverlayFragment> {
				if (contactAddressModel.isNotNull()) {
					// 从账单详情快捷添加地址进入的页面
					replaceFragmentAndSetArgument<ContactFragment>(ContainerID.content)
					activity?.apply { SoftKeyboard.hide(this) }
				} else {
					presenter.popFragmentFrom<ContactInputFragment>()
				}
			}
		}
	}

	private fun formattedBTCSeriesTestAddress(address: String): String {
		return if (BCHWalletUtils.isNewCashAddress(address))
			BCHWalletUtils.formattedToLegacy(address, TestNet3Params.get())
		else address
	}

	fun setConfirmButtonStyle(
		nameInput: EditText,
		ethETHSeriesInput: EditText,
		eosInput: EditText,
		eosJungleInput: EditText,
		eosKylinInput: EditText,
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

		eosKylinInput.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(text: Editable?) {
				eosKylinAccountNameText = text.toString().orElse("")
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
					+ eosKylinAccountNameText.count()
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