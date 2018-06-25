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
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.component.WalletEditText
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.ContactText
import io.goldstone.blockchain.common.value.ProfileText
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.presenter.ContactInputPresenter
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 16/04/2018 1:13 PM
 * @author KaySaith
 */
class ContactInputFragment : BaseFragment<ContactInputPresenter>() {
	
	private val nameInput by lazy { RoundInput(context!!) }
	private val addressInput by lazy { WalletEditText(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	override val presenter = ContactInputPresenter(this)
	
	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
			nameInput.apply {
				title = ContactText.contactName
				setTextInput()
				setMargins<LinearLayout.LayoutParams> { topMargin = 40.uiPX() }
			}.into(this)
			
			addressInput.apply {
				setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
				hint = ContactText.hint
			}.into(this)
			
			confirmButton.apply {
				text = CommonText.confirm
				setGrayStyle(20.uiPX())
			}.click {
				presenter.addContact()
			}.into(this)
			
			presenter.getAddressIfExist(addressInput)
		}
	}
	
	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(view, savedInstanceState)
		presenter.setConfirmButtonStyle(nameInput, addressInput, confirmButton)
	}
	
	override fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
		getParentFragment<ProfileOverlayFragment> {
			headerTitle = ProfileText.contacts
			presenter.popFragmentFrom<ContactInputFragment>()
		}
	}
}