package io.goldstone.blockchain.module.home.profile.aboutus.presenter

import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.module.home.profile.aboutus.view.AboutUsFragment

/**
 * @date 2018/5/12 7:51 PM
 * @author KaySaith
 */

class AboutUsPresenter(
	override val fragment: AboutUsFragment
	) : BasePresenter<AboutUsFragment>() {
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.setIntroContent("You supply the storage, compute, and networking resources that power our entire decentralized web. Adding new content? Use PiedPiperCoin to store your data across thousands of devices, making it quick and easy to access.")
		fragment.setProductIntroContent("You supply the storage, compute, and networking resources that power our entire decentralized web. Adding new content? Use PiedPiperCoin to store your data across thousands of devices, making it quick and easy to access.")
		recoveryFragmentHeight()
	}
}