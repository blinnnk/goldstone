package io.goldstone.blockchain.module.home.profile.contacts.contractinput.presenter

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orElse
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.getParentFragment
import com.blinnnk.util.replaceFragmentAndSetArgument
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ContactText
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.crypto.Address
import io.goldstone.blockchain.crypto.isValid
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.view.ContactInputFragment
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.view.ContactFragment
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.presenter.memoryTransactionListData

/**
 * @date 16/04/2018 1:13 PM
 * @author KaySaith
 */
class ContactInputPresenter(
	override val fragment: ContactInputFragment
) : BasePresenter<ContactInputFragment>() {
	
	private var nameText = ""
	private var addressText = ""
	
	fun getAddressIfExist(nameInput: EditText) {
		fragment.getParentFragment<ProfileOverlayFragment>()?.apply {
			contactAddress?.let {
				nameInput.setText(it)
				addressText = it
			}
		}
	}
	
	fun addContact() {
		if (nameText.isEmpty()) {
			fragment.context?.alert(ContactText.emptyNameAlert)
			return
		}
		
		if (addressText.isEmpty()) {
			fragment.context?.alert(ContactText.emptyAddressAlert)
			return
		}
		
		if (Address(addressText).isValid() && nameText.isNotEmpty()) {
			ContactTable.insertContact(
				ContactTable(0, "", nameText, addressText)
			) {
				// 通信录的地址是实时显示到账单的, 当通信录有更新的时候清空缓存中的数据
				memoryTransactionListData = null
				fragment.getParentFragment<ProfileOverlayFragment> {
					if (!contactAddress.isNullOrBlank()) {
						// 从账单详情快捷添加地址进入的页面
						replaceFragmentAndSetArgument<ContactFragment>(ContainerID.content)
						activity?.apply { SoftKeyboard.hide(this) }
					} else {
						presenter.popFragmentFrom<ContactInputFragment>()
					}
				}
			}
		} else {
			if (nameText.isNotEmpty()) {
				fragment.context?.alert(ContactText.wrongAddressFormat)
				return
			}
		}
	}
	
	fun setConfirmButtonStyle(
		nameInput: EditText,
		addressInput: EditText,
		confirmButton: RoundButton
	) {
		nameInput.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(text: Editable?) {
				nameText = text.orElse("").toString()
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
		
		addressInput.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(text: Editable?) {
				addressText = text.orElse("").toString()
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
		if (nameText.count() * addressText.count() != 0) {
			confirmButton.setBlueStyle(20.uiPX())
		} else {
			confirmButton.setGrayStyle(20.uiPX())
		}
	}
}