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
import io.goldstone.blockchain.common.component.ViewPagerMenu
import io.goldstone.blockchain.common.component.gsCard
import io.goldstone.blockchain.common.component.overlay.Dashboard
import io.goldstone.blockchain.common.component.title.sessionTitle
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.home.dapp.dappbrowser.view.DAppBrowserFragment
import io.goldstone.blockchain.module.home.dapp.dappcenter.contract.DAppCenterContract
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blockchain.module.home.dapp.dappcenter.presenter.DAppCenterPresenter
import io.goldstone.blockchain.module.home.dapp.dappcenter.view.applist.DAPPRecyclerView
import io.goldstone.blockchain.module.home.dapp.dappcenter.view.recommend.RecommendDappView
import io.goldstone.blockchain.module.home.dapp.dapplist.model.DAPPType
import io.goldstone.blockchain.module.home.dapp.dapplistdetail.view.DAPPOverlayFragment
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.nestedScrollView
import org.jetbrains.anko.support.v4.onPageChangeListener
import org.jetbrains.anko.support.v4.viewPager


/**
 * @author KaySaith
 * @date  2018/11/29
 */
class DAPPCenterFragment : GSFragment(), DAppCenterContract.GSView {

	override val pageTitle: String = "Dapp Center"

	override lateinit var presenter: DAppCenterContract.GSPresenter
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
			nestedScrollView {
				lparams(matchParent, matchParent)
				verticalLayout {
					isFocusable = true
					isFocusableInTouchMode = true
					lparams(matchParent, matchParent)
					gravity = Gravity.CENTER_HORIZONTAL
					topPadding = 30.uiPX()
					bottomPadding = 55.uiPX()
					searchBar = SearchBar(context).click {
						showDAPPExplorerFragment()
					}
					searchBar.into(this)
					sessionTitle {
						setTitle("Recommend DAPP", Spectrum.white)
						setSubtitle(
							"(5)",
							"Check All (5)",
							Spectrum.opacity5White,
							Spectrum.white
						) {
							showDAPPListDetailFragment(DAPPType.Recommend)
						}
					}
					linearLayout {
						lparams(matchParent, wrapContent)
						recommendDAPP = RecommendDappView(context) {
							showDAPPBrowserFragment(url)
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
							newAPP = DAPPRecyclerView(
								context,
								clickCellEvent = {
									showAttentionOrElse(id) {
										showDAPPBrowserFragment(url)
										presenter.setUsedDAPPs()
									}
								},
								checkAllEvent = {
									showDAPPListDetailFragment(DAPPType.New)
								}
							)
							latestUsed = DAPPRecyclerView(
								context,
								clickCellEvent = {
									showDAPPBrowserFragment(url)
								},
								checkAllEvent = {
									showDAPPListDetailFragment(DAPPType.Latest)
								}
							)
							viewPager {
								layoutParams = LinearLayout.LayoutParams(matchParent, 900.uiPX())
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

	override fun showRecommendDAPP(data: ArrayList<DAPPTable>) = launchUI {
		recommendDAPP.setData(data)
	}

	override fun showAllDAPP(data: ArrayList<DAPPTable>) = launchUI {
		newAPP.setData(data)
	}

	override fun showLatestUsed(data: ArrayList<DAPPTable>) {
		latestUsed.setData(data)
	}

	private fun showAttentionOrElse(dappID: String, callback: () -> Unit) {
		presenter.getDAPPUsedStatus(dappID) { isUsed ->
			if (isUsed) callback()
			else Dashboard(context!!) {
				showAlert(
					"Attention About DAPP Terms",
					"once you decide to use third-party DAPP, it means you have to read their term/privacy, that will be necessary for you"
				) {
					presenter.updateDAPPUsedStatus(dappID)
					callback()
				}
			}
		}
	}

	private fun showDAPPListDetailFragment(type: DAPPType) {
		getMainActivity()?.apply {
			addFragmentAndSetArguments<DAPPOverlayFragment>(ContainerID.main) {
				putSerializable(ArgumentKey.dappType, type)
			}
			hideHomeFragment()
		}
	}

	private fun showDAPPBrowserFragment(url: String) {
		// 测试使用之后完成逻辑需要删除替换
		getMainActivity()?.apply {
			addFragmentAndSetArguments<DAppBrowserFragment>(
				ContainerID.main,
				FragmentTag.dappBrowser
			) {
				putString("webURL", url)
			}
			hideHomeFragment()
		}
	}

	private fun showDAPPExplorerFragment() {
		getMainActivity()?.apply {
			addFragmentAndSetArguments<DAPPOverlayFragment>(ContainerID.main) {
				putSerializable(ArgumentKey.dappType, DAPPType.Explorer)
			}
			hideHomeFragment()
		}
	}
}