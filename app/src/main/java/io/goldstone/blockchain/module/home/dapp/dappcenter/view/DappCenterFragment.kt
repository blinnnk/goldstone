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
import io.goldstone.blockchain.common.component.SearchBar
import io.goldstone.blockchain.common.component.ViewPagerMenu
import io.goldstone.blockchain.common.component.gsCard
import io.goldstone.blockchain.common.component.searchBar
import io.goldstone.blockchain.common.component.title.sessionTitle
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.isEmptyThen
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
class DAPPCenterFragment : GSFragment(), DAppCenterContract.GSView {

	override val pageTitle: String = "Dapp Center"

	override lateinit var presenter: GoldStonePresenter
	private lateinit var searchBar: SearchBar
	private lateinit var recommendDAPP: RecommendDappView
	private lateinit var menuBar: ViewPagerMenu
	private lateinit var newAPP: DAPPRecyclerView
	private lateinit var latestUsed: DAPPRecyclerView

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
					searchBar = searchBar {
						layoutParams = LinearLayout.LayoutParams(ScreenSize.card, wrapContent)
						clearFocus()
					}
					sessionTitle {
						setTitle("Recommend DAPP", Spectrum.white)
						setSubtitle(
							"(5)",
							"Check All (5)",
							Spectrum.opacity5White,
							Spectrum.white
						)
					}
					linearLayout {
						lparams(matchParent, wrapContent)
						recommendDAPP = RecommendDappView(context) {
							showDAppBrowserFragment()
						}
						recommendDAPP.into(this)
						bottomPadding = 10.uiPX()
					}

					gsCard {
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
							latestUsed = DAPPRecyclerView(context) {

							}
							viewPager {
								layoutParams = LinearLayout.LayoutParams(matchParent, ScreenSize.fullHeight)
								adapter = ViewPagerAdapter(listOf(newAPP, latestUsed))
								val titles = listOf("NEW DAPP", "LATEST USED")
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
					}
				}
			}
		}.view
	}

	override fun showRecommendDAPP(data: ArrayList<DAPPModel>) = launchUI {
		recommendDAPP.setData(data)
	}

	override fun showNewDAPP(data: ArrayList<DAPPModel>) = launchUI {
		newAPP.setData(data)
	}

	override fun showLatestUsed(data: ArrayList<DAPPModel>) {
		latestUsed.setData(data)
	}

	private fun showDAppBrowserFragment() {
		// 测试使用之后完成逻辑需要删除替换
		val url = searchBar.getContent() isEmptyThen "http://192.168.64.2/site/dapp/index.html"
		getMainActivity()?.addFragmentAndSetArguments<DAppBrowserFragment>(ContainerID.main) {
			putString("webURL", url)
		}
		getMainActivity()?.hideHomeFragment()
	}

}