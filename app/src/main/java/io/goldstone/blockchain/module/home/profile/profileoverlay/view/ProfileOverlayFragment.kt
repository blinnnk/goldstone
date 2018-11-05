package io.goldstone.blockchain.module.home.profile.profileoverlay.view

import android.view.ViewGroup
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.model.ContactModel
import io.goldstone.blockchain.module.home.profile.profileoverlay.presenter.ProfileOverlayPresenter

/**
 * @date 26/03/2018 12:55 AM
 * @author KaySaith
 */
class ProfileOverlayFragment : BaseOverlayFragment<ProfileOverlayPresenter>() {

	val title by lazy {
		arguments?.getString(ArgumentKey.profileTitle)
	}
	val contactAddressModel by lazy {
		arguments?.getSerializable(ArgumentKey.addressModel) as? ContactModel
	}
	override val presenter = ProfileOverlayPresenter(this)

	override fun ViewGroup.initView() {
		headerTitle = title.orEmpty()
		presenter.showTargetFragmentByTitle(title.orEmpty())
	}

}