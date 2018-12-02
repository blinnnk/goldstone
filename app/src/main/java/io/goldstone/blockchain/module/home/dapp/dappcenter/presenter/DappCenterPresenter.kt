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
		setNewDAPP()
		setLatestUsedDAPP()
	}

	private fun setDAPPRecommendData() {
		dappView.showRecommendDAPP(
			arrayListOf(
				DAPPModel(
					"https://static-cdn.jtvnw.net/ttv-boxart/Clash%20of%20Clans.jpg",
					"Clash Of Clans",
					"really good-playing tower game named clash of clan",
					listOf()
				),
				DAPPModel(
					"https://techcrunch.com/wp-content/uploads/2013/11/mv_oct13_01.png",
					"Monument Valley",
					"rant eos bandwidth resource cheap",
					listOf()
				),
				DAPPModel(
					"https://wi-images.condecdn.net/image/J41Zk0Yg8ky/crop/810/f/gmail_logomax-2800x2800.jpg",
					"Decentralize Mail",
					"really good-playing tower game named clash of clan",
					listOf()
				)
			)
		)
	}

	private fun setLatestUsedDAPP() {
		dappView.showLatestUsed(arrayListOf())
	}

	private fun setNewDAPP() {
		dappView.showNewDAPP(
			arrayListOf(
				DAPPModel(
					"https://cloudia.hnonline.sk/r740x/d70a807d25d6d0a760e73bce845753d7.jpg",
					"Neowin",
					"today is an amazing sunday",
					listOf("Game")
				),
				DAPPModel(
					"https://img.logonews.cn/uploads/2015/04/2015042406311937.png",
					"Tumblr",
					"sharing photos and you videos by you will",
					listOf("Life Style")
				),
				DAPPModel(
					"https://previews.123rf.com/images/nearbirds/nearbirds1603/nearbirds160300008/53831381-jungle-shamans-mobile-game-user-interface-play-window-screen-vector-illustration-for-web-mobile-vide.jpg",
					"Snamans",
					"sharing photos and you videos by you will",
					listOf("Game", "EOS Chain")
				),
				DAPPModel(
					"https://www.touchtapplay.com/wp-content/uploads/2015/12/Crashlands.png",
					"Crashlands",
					"Role Playing Game Crashlands To Launch On The App Store Next Month",
					listOf("Game", "EOS Chain")
				),
				DAPPModel(
					"https://www.magneticmag.com/.image/c_limit%2Ccs_srgb%2Cq_auto:good%2Cw_700/MTQzNDkwNDU1MDE3ODkwOTU5/spotify-1360002_1280jpg.webp",
					"SPotify",
					"Role Playing Game Crashlands To Launch On The App Store Next Month",
					listOf("Music", "EOS Chain")
				)
			)
		)
	}

}