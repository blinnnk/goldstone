package io.goldstone.blockchain.module.common.tokendetail.tokendetailcenter.view

import android.support.v4.app.Fragment
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.ViewPagerMenu
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.crypto.multichain.isEOS
import io.goldstone.blockchain.module.common.tokendetail.tokendetailcenter.presenter.TokenDetailCenterPresenter
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.onPageChangeListener


/**
 * @author KaySaith
 * @date  2018/09/10
 */
class TokenDetailCenterFragment : BaseFragment<TokenDetailCenterPresenter>() {

	val token by lazy {
		arguments?.get(ArgumentKey.tokenDetail) as? WalletDetailCellModel
	}

	private val menuBar by lazy { ViewPagerMenu(context!!) }
	private val viewPager by lazy { TokenDetailCenterViewPager(this) }
	private val menuTitles by lazy {
		val secondMenuTitle =
			if (token?.contract.isEOS()) "Asset" else "Information"
		arrayListOf("Transaction List", secondMenuTitle)
	}
	override val presenter = TokenDetailCenterPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		relativeLayout {
			lparams(matchParent, matchParent)
			menuBar.into(this)
			addView(viewPager, RelativeLayout.LayoutParams(ScreenSize.heightWithOutHeader, matchParent))
			viewPager.apply {
				// `MenuBar` 点击选中动画和内容更换
				menuBar.setMemnuTitles(menuTitles) { button, id ->
					button.onClick {
						currentItem = id
						menuBar.moveUnderLine(menuBar.getUnitWidth() * currentItem)
						button.preventDuplicateClicks()
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
				}
			}
		}
	}
}