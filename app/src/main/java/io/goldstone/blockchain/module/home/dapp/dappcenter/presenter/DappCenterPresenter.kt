package io.goldstone.blockchain.module.home.dapp.dappcenter.presenter

import io.goldstone.blockchain.module.home.dapp.dappcenter.contract.DAppCenterContract
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPModel


/**
 * @author KaySaith
 * @date  2018/11/29
 */
class DAppCenterPresenter(
	private val dappView: DAppCenterContract.GSView
) : DAppCenterContract.GSPresenter {

	override fun start() {
		setDAPPRecommendData()
	}

	private fun setDAPPRecommendData() {
		dappView.showRecommendDAPP(
			arrayListOf(
				DAPPModel("https://static-cdn.jtvnw.net/ttv-boxart/Clash%20of%20Clans.jpg", "Clash Of Clans", "really good-playing tower game named clash of clan"),
				DAPPModel("https://techcrunch.com/wp-content/uploads/2013/11/mv_oct13_01.png", "Monument Valley", "rant eos bandwidth resource cheap"),
				DAPPModel("https://wi-images.condecdn.net/image/J41Zk0Yg8ky/crop/810/f/gmail_logomax-2800x2800.jpg", "Decentralize Mail", "really good-playing tower game named clash of clan")
			)
		)
	}

}