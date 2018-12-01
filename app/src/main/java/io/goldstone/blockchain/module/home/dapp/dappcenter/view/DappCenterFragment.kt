package io.goldstone.blockchain.module.home.dapp.dappcenter.view

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.gsfragment.GSFragment
import io.goldstone.blockchain.common.base.view.ViewPagerAdapter
import io.goldstone.blockchain.common.component.GSCard
import io.goldstone.blockchain.common.component.SearchBar
import io.goldstone.blockchain.common.component.ViewPagerMenu
import io.goldstone.blockchain.common.component.title.SessionTitleView
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.home.dapp.dappbrowser.view.DAppBrowserFragment
import io.goldstone.blockchain.module.home.dapp.dappcenter.contract.DAppCenterContract
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPModel
import io.goldstone.blockchain.module.home.dapp.dappcenter.presenter.DAppCenterPresenter
import io.goldstone.blockchain.module.home.dapp.dappcenter.view.applist.DAPPRecyclerView
import io.goldstone.blockchain.module.home.dapp.dappcenter.view.recommend.RecommendDappView
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.onPageChangeListener
import org.jetbrains.anko.support.v4.viewPager


/**
 * @author KaySaith
 * @date  2018/11/29
 */
class DAppCenterFragment : GSFragment(), DAppCenterContract.GSView {

	override val pageTitle: String = "Dapp Center"

	override lateinit var presenter: GoldStonePresenter
	private lateinit var searchBar: SearchBar
	private lateinit var recommendDAPP: RecommendDappView
	private lateinit var menuBar: ViewPagerMenu
	private lateinit var newAPP: DAPPRecyclerView
	private lateinit var myLatestAPP: DAPPRecyclerView

	override fun showError(error: Throwable) {
		safeShowError(error)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter = DAppCenterPresenter(this)
		presenter.start()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
			scrollView {
				lparams(matchParent, matchParent)
				verticalLayout {
					lparams(matchParent, matchParent)
					gravity = Gravity.CENTER_HORIZONTAL
					topPadding = 30.uiPX()
					searchBar = SearchBar(context).apply {
						layoutParams = LinearLayout.LayoutParams(ScreenSize.card, wrapContent)
					}
					addView(searchBar)
					SessionTitleView(context)
						.setTitle("Recommend DAPP", Spectrum.white)
						.setSubtitle(
							"(5)",
							"Check All (5)",
							Spectrum.opacity5White,
							Spectrum.white
						)
						.into(this)
					linearLayout {
						lparams(matchParent, wrapContent)
						recommendDAPP = RecommendDappView(context) {
							showDAppBrowserFragment()
						}
						recommendDAPP.into(this)
						bottomPadding = 10.uiPX()
					}

					GSCard(context).apply {
						lparams(ScreenSize.card, matchParent)
						verticalLayout {
							lparams(matchParent, matchParent)
							menuBar = ViewPagerMenu(
								context,
								ScreenSize.card,
								GrayScale.black,
								12
							)
							menuBar.setColor(Color.TRANSPARENT, Spectrum.blue, GrayScale.lightGray)
							menuBar.into(this)
							minimumHeight = 200.uiPX()
							newAPP = DAPPRecyclerView(context) {

							}
							myLatestAPP = DAPPRecyclerView(context) {

							}
							newAPP.setData(
								arrayListOf(
									DAPPModel(
										"https://cloudia.hnonline.sk/r740x/d70a807d25d6d0a760e73bce845753d7.jpg", "Neowin", "today is an amazing sunday"
									),
									DAPPModel(
										"https://img.logonews.cn/uploads/2015/04/2015042406311937.png", "Tumblr", "sharing photos and you videos by you will"
									),
									DAPPModel(
										"https://previews.123rf.com/images/nearbirds/nearbirds1603/nearbirds160300008/53831381-jungle-shamans-mobile-game-user-interface-play-window-screen-vector-illustration-for-web-mobile-vide.jpg", "Snamans", "sharing photos and you videos by you will"
									),
									DAPPModel(
										"https://www.touchtapplay.com/wp-content/uploads/2015/12/Crashlands.png", "Crashlands", "Role Playing Game Crashlands To Launch On The App Store Next Month"
									)
								)
							)
							viewPager {
								layoutParams = LinearLayout.LayoutParams(matchParent, 500.uiPX())
								adapter = ViewPagerAdapter(listOf(newAPP, myLatestAPP))
								val titles = listOf("NEW DAPP", "MY LATEST USED")
								menuBar.setMenuTitles(titles) { button, id ->
									button.onClick {
										currentItem = id
										menuBar.moveUnderLine(menuBar.getUnitWidth() * currentItem)
										button.preventDuplicateClicks()
									}
								}
								// `MenuBar` 滑动选中动画
								onPageChangeListener {
									onPageScrolled { position, percent, _ ->
										menuBar.moveUnderLine(menuBar.getUnitWidth() * (percent + position))
									}
								}
							}
						}
					}.into(this)

				}
			}
		}.view
	}

	override fun showRecommendDAPP(data: ArrayList<DAPPModel>) = launchUI {
		recommendDAPP.setData(data)
	}

	private fun showDAppBrowserFragment() {
		getMainActivity()?.addFragmentAndSetArguments<DAppBrowserFragment>(ContainerID.main)
		getMainActivity()?.hideHomeFragment()
	}

}