package io.goldstone.blockchain.module.home.profile.contacts.contractinput.presenter

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orElse
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.view.ContractInputFragment
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import org.web3j.crypto.WalletUtils

/**
 * @date 16/04/2018 1:13 PM
 * @author KaySaith
 */

class ContractInputPresenter(
  override val fragment: ContractInputFragment
) : BasePresenter<ContractInputFragment>() {
  //
  private var nameText = ""
  private var addressText = ""

  fun addContact() {

    if (nameText.isEmpty()) {
      fragment.context?.alert("You must enter a contact name")
    }

    if (addressText.isEmpty()) {
      fragment.context?.alert("You must enter a wallet address")
    }

    if(WalletUtils.isValidAddress(addressText)) {
      ContactTable.insertContact(
        ContactTable(
          0,
          "",
          nameText,
          addressText)
      ) {
        fragment.getParentFragment<ProfileOverlayFragment> {
          presenter.popFragmentFrom<ContractInputFragment>()
        }
      }
    } else {
      fragment.context?.alert("Wrong Address Format")
    }
  }

  fun setConfirmButtonStyle(
    nameInput: EditText,
    addressInput: EditText,
    confirmButton: RoundButton
  ) {
    nameInput.addTextChangedListener(object: TextWatcher{
      override fun afterTextChanged(text: Editable?) {
        nameText = text.orElse("").toString()
        setStyle(confirmButton)
      }
      override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {

      }
    })

    addressInput.addTextChangedListener(object: TextWatcher{
      override fun afterTextChanged(text: Editable?) {
        addressText = text.orElse("").toString()
        setStyle(confirmButton)
      }
      override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {}
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