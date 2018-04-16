package io.goldstone.blockchain.module.home.profile.contacts.contractinput.presenter

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orElse
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

  fun addContact(nameInput: EditText, addressInput: EditText, confirmButton: RoundButton) {

    if (nameInput.text.isEmpty()) {
      fragment.context?.alert("You must enter a contact name")
    }

    if (addressInput.text.isEmpty()) {
      fragment.context?.alert("You must enter a wallet address")
    }

    var nameText = ""
    nameInput.addTextChangedListener(object: TextWatcher{
      override fun afterTextChanged(text: Editable?) {
        nameText = text.orElse("").toString()
      }
      override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {}
    })

    var addressText = ""
    addressInput.addTextChangedListener(object: TextWatcher{
      override fun afterTextChanged(text: Editable?) {
        addressText = text.orElse("").toString()
      }
      override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {}
    })

    if (nameText.count() * addressText.count() != 0 && WalletUtils.isValidAddress(addressText)) {
      confirmButton.setBlueStyle()
    }

    ContactTable.insertContact(
      ContactTable(
      0,
      "",
      nameInput.text.toString(),
      addressInput.text.toString())
    ) {
      fragment.getParentFragment<ProfileOverlayFragment> {
        presenter.popFragmentFrom<ContractInputFragment>()
      }
    }
  }
}