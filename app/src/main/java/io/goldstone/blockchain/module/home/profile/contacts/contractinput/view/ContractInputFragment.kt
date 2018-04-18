package io.goldstone.blockchain.module.home.profile.contacts.contractinput.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.component.WalletEditText
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.presenter.ContractInputPresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 16/04/2018 1:13 PM
 * @author KaySaith
 */

class ContractInputFragment : BaseFragment<ContractInputPresenter>() {

  private val nameInput by lazy { RoundInput(context!!) }
  private val addressInput by lazy { WalletEditText(context!!) }
  private val confirmButton by lazy { RoundButton(context!!) }

  override val presenter = ContractInputPresenter(this)

  override fun AnkoContext<Fragment>.initView() {
    verticalLayout {
      lparams(matchParent, matchParent)
      nameInput.apply {
        text = "Contact Name"
        setTextInput()
        setMargins<LinearLayout.LayoutParams> { topMargin = 40.uiPX() }
      }.into(this)

      addressInput.apply {
        setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
        hint = "Enter Address That You Want To Store"
      }.into(this)

      confirmButton.apply {
        text = CommonText.confirm
        marginTop = 20.uiPX()
        setGrayStyle()
      }.click {
          presenter.addContact()
        }.into(this)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    presenter.setConfirmButtonStyle(nameInput, addressInput, confirmButton)
  }

}