package io.goldstone.blinnnk.module.common.tokendetail.tokendetailcenter.view

import android.graphics.Color
import android.support.v4.app.Fragment
import android.widget.RelativeLayout
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import io.goldstone.blinnnk.common.base.basefragment.BaseFragment
import io.goldstone.blinnnk.common.component.ViewPagerMenu
import io.goldstone.blinnnk.common.language.TokenDetailText
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.UMengEvent
import io.goldstone.blinnnk.crypto.multichain.isEOS
import io.goldstone.blinnnk.module.common.tokendetail.tokendetail.event.FilterButtonDisplayEvent
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailcenter.presenter.TokenDetailCenterPresenter
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blinnnk.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.onPageChangeListener


/**
 * @author KaySaith
 * @date  2018/09/10
 */
class TokenDetailCenterFragment : BaseFragment<TokenDetailCenterPresenter>() {

	val token by lazy {
		arguments?.get(ArgumentKey.tokenDetail) as? WalletDetailCellModel
	}
	override val pageTitle: String get() = token?.symbol?.symbol.orEmpty()
	private lateinit var menuBar: ViewPagerMenu
	private lateinit var viewPager: TokenDetailCenterViewPager
	private val menuTitles by lazy {
		val secondMenuTitle =
			if (token?.contract.isEOS()) TokenDetailText.assets else TokenDetailText.information
		arrayListOf(TokenDetailText.transactionList, secondMenuTitle)
	}

	override val presenter = TokenDetailCenterPresenter(this)

	private var isTokenDetailPage = true

	override fun onResume() {
		super.onResume()
		getParentFragment<TokenDetailOverlayFragment> {
			headerTitle = token?.symbol?.symbol.orEmpty()
		}
	}

	override fun AnkoContext<Fragment>.initView() {
		relativeLayout {
			lparams(matchParent, matchParent)
			menuBar = ViewPagerMenu(context)
			menuBar.setColor(Spectrum.deepBlue, Spectrum.lightBlue, Color.TRANSPARENT)
			menuBar.into(this)
			viewPager = TokenDetailCenterViewPager(this@TokenDetailCenterFragment)
			addView(viewPager, RelativeLayout.LayoutParams(ScreenSize.heightWithOutHeader, matchParent))
			viewPager.apply {
				// `MenuBar` 点击选中动画和内容更换
				menuBar.setMenuTitles(menuTitles) { button, id ->
					button.onClick {
						currentItem = id
						menuBar.moveUnderLine(menuBar.getUnitWidth() * currentItem)
						button.preventDuplicateClicks()
						UMengEvent.add(context, UMengEvent.Click.TokenDetail.tabBar)
					}
				}
				setMargins<RelativeLayout.LayoutParams> {
					topMargin = menuBar.layoutParams.height
				}

				// `MenuBar` 滑动选中动画
				onPageChangeListener {
					onPageScrolled { position, percent, _ ->
						menuBar.moveUnderLine(menuBar.getUnitWidth() * (percent + position))
					}
					onPageSelected {
						isTokenDetailPage = it == 0
					}
				}
			}
		}
	}

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		// `TokenDetailFragment` 的 左上角的 `Filter Button` 显示控制
		EventBus.getDefault().post(FilterButtonDisplayEvent(!hidden && isTokenDetailPage))
	}
}