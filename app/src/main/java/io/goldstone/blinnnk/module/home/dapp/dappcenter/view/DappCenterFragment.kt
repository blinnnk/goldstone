package io.goldstone.blinnnk.module.home.dapp.dappcenter.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS
import android.widget.LinearLayout
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.base.gsfragment.GSFragment
import io.goldstone.blinnnk.common.base.view.ViewPagerAdapter
import io.goldstone.blinnnk.common.component.ViewPagerMenu
import io.goldstone.blinnnk.common.component.gsCard
import io.goldstone.blinnnk.common.component.overlay.Dashboard
import io.goldstone.blinnnk.common.component.title.SessionTitleView
import io.goldstone.blinnnk.common.component.title.sessionTitle
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.language.DappCenterText
import io.goldstone.blinnnk.common.language.ProfileText
import io.goldstone.blinnnk.common.thread.launchDefault
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.common.utils.getMainActivity
import io.goldstone.blinnnk.common.utils.safeShowError
import io.goldstone.blinnnk.common.value.*
import io.goldstone.blinnnk.kernel.commontable.FavoriteTable
import io.goldstone.blinnnk.module.home.dapp.dappbrowser.view.PreviousView
import io.goldstone.blinnnk.module.home.dapp.dappcenter.contract.DAppCenterContract
import io.goldstone.blinnnk.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blinnnk.module.home.dapp.dappcenter.presenter.DAppCenterPresenter
import io.goldstone.blinnnk.module.home.dapp.dappcenter.view.applist.DAPPRecyclerView
import io.goldstone.blinnnk.module.home.dapp.dappcenter.view.recommend.RecommendDappView
import io.goldstone.blinnnk.module.home.dapp.dapplist.model.DAPPType
import io.goldstone.blinnnk.module.home.dapp.dappoverlay.view.DAPPOverlayFragment
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.nestedScrollView
import org.jetbrains.anko.support.v4.onPageChangeListener
import org.jetbrains.anko.support.v4.themedViewPager
import org.jetbrains.anko.topPadding
import org.jetbrains.anko.verticalLayout


/**
 * @author KaySaith
 * @date  2018/11/29
 */
class DAPPCenterFragment : GSFragment(), DAppCenterContract.GSView {

	override val pageTitle: String = ProfileText.dappCenter

	override lateinit var presenter: DAppCenterContract.GSPresenter
	private lateinit var searchBar: SearchBar
	private lateinit var recommendDAPP: RecommendDappView
	private lateinit var menuBar: ViewPagerMenu
	private lateinit var newAPP: DAPPRecyclerView
	private lateinit var latestUsed: DAPPRecyclerView
	private lateinit var recommendedSession: SessionTitleView

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
					isFocusableInTouchMode = true
					descendantFocusability = FOCUS_BLOCK_DESCENDANTS
					lparams(matchParent, matchParent)
					gravity = Gravity.CENTER_HORIZONTAL
					topPadding = 30.uiPX()
					bottomPadding = 55.uiPX()
					searchBar = SearchBar(context).click {
						showDAPPExplorerFragment()
						UMengEvent.add(context, UMengEvent.Click.DappCenter.browerInput)
					}
					searchBar.into(this)
					recommendedSession = sessionTitle {
						setTitle(DappCenterText.recommendDapp, Spectrum.white)
						setSubtitle(
							"(--)",
							"${CommonText.checkAll} (--)",
							Spectrum.opacity5White,
							Spectrum.white
						) {
							showDAPPListDetailFragment(DAPPType.Recommend)
							UMengEvent.add(context, UMengEvent.Click.DappCenter.dapp, "推荐")
						}
					}
					recommendDAPP = RecommendDappView(
						context,
						clickCellEvent = {
							showAttentionOrElse(context, id) {
								getMainActivity()?.showDappBrowserFragment(
									url,
									PreviousView.DAPPCenter,
									backgroundColor,
									this@DAPPCenterFragment
								)
								presenter.setUsedDAPPs()
							}
							UMengEvent.add(context, UMengEvent.Click.DappCenter.dapp, "推荐")
						}
					)
					recommendDAPP.into(this)
					recommendDAPP.setMargins<LinearLayout.LayoutParams> {
						bottomMargin = 5.uiPX()
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
									showAttentionOrElse(context, id) {
										getMainActivity()?.showDappBrowserFragment(
											url,
											PreviousView.DAPPCenter,
											backgroundColor,
											this@DAPPCenterFragment
										)
										presenter.setUsedDAPPs()
									}
									UMengEvent.add(context, UMengEvent.Click.DappCenter.dapp, "最新")
								},
								checkAllEvent = {
									showDAPPListDetailFragment(DAPPType.New)
									UMengEvent.add(context, UMengEvent.Click.DappCenter.allDapps, "最新")
								}
							)
							latestUsed = DAPPRecyclerView(
								context,
								clickCellEvent = {
									getMainActivity()?.showDappBrowserFragment(
										url,
										PreviousView.DAPPCenter,
										backgroundColor,
										this@DAPPCenterFragment
									)
									UMengEvent.add(context, UMengEvent.Click.DappCenter.dapp, "最近使用")
								},
								checkAllEvent = {
									showDAPPListDetailFragment(DAPPType.Latest)
									UMengEvent.add(context, UMengEvent.Click.DappCenter.allDapps, "最近使用")
								}
							)
							themedViewPager {
								layoutParams = LinearLayout.LayoutParams(matchParent, 998.uiPX())
								adapter = ViewPagerAdapter(listOf(newAPP, latestUsed))
								val titles = listOf(DappCenterText.newDapp, DappCenterText.recentDapp)
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

	override fun showRecommendedSession(count: Int) {
		recommendedSession.setSubtitle(
			"($count)",
			"${CommonText.checkAll} ($count)",
			Spectrum.opacity5White,
			Spectrum.white
		) {
			showDAPPListDetailFragment(DAPPType.Recommend)
		}
	}

	override fun showAllDAPP(data: ArrayList<DAPPTable>) = launchUI {
		newAPP.setData(data)
	}

	override fun showLatestUsed(data: ArrayList<DAPPTable>) {
		latestUsed.setData(data)
	}

	// 这个会报漏给搜索界面的点击更新事件
	override fun refreshLatestUsed() {
		presenter.setUsedDAPPs()
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		activity?.getHomeFragment()?.apply {
			presenter.showWalletDetailFragment()
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

	private fun showDAPPExplorerFragment() {
		getMainActivity()?.apply {
			addFragmentAndSetArguments<DAPPOverlayFragment>(ContainerID.main) {
				putSerializable(ArgumentKey.dappType, DAPPType.Explorer)
			}
			hideHomeFragment()
		}
	}

	companion object {
		fun showAttentionOrElse(context: Context, dappID: String, callback: () -> Unit) {
			DAPPTable.getDAPPUsedStatus(dappID) { isUsed ->
				if (isUsed) callback()
				else Dashboard(context) {
					showAlert(
						DappCenterText.thirdPartDappAlertTitle,
						DappCenterText.thirdPartDappAlertDescription
					) {
						launchDefault {
							FavoriteTable.updateDAPPUsedStatus(dappID) {
								launchUI(callback)
							}
						}
					}
				}
			}
		}
	}
}