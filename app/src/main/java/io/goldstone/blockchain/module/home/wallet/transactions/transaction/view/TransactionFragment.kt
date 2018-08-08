package io.goldstone.blockchain.module.home.wallet.transactions.transaction.view

import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.ViewPagerMenu
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.WalletType
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.presenter.TransactionPresenter
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.onPageChangeListener

/**
 * @date 24/03/2018 2:37 AM
 * @author KaySaith
 */
class TransactionFragment : BaseOverlayFragment<TransactionPresenter>() {

	var isETCListShown: Runnable? = null
	var isBTCListShown: Runnable? = null
	private val menuBar by lazy {
		ViewPagerMenu(context!!)
	}
	private val viewPager by lazy {
		TransactionViewPager(this)
	}
	override val presenter = TransactionPresenter(this)
	private val menuTitles = when (Config.getCurrentWalletType()) {
		WalletType.BTCTestOnly.content, WalletType.BTCOnly.content ->
			arrayListOf(CryptoSymbol.btc())
		WalletType.ETHERCAndETCOnly.content ->
			arrayListOf(CryptoSymbol.eth + "/" + CryptoSymbol.erc, CryptoSymbol.etc)
		else ->
			arrayListOf(CryptoSymbol.eth + "/" + CryptoSymbol.erc, CryptoSymbol.btc(), CryptoSymbol.etc)
	}

	override fun ViewGroup.initView() {
		headerTitle = TransactionText.transaction
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

				if (menuTitles.size > 1) {
					onPageSelected {
						if (it == menuTitles.lastIndex - 1) isBTCListShown?.run()
						if (it == menuTitles.lastIndex) isETCListShown?.run()
					}
				}
			}
		}
	}

	fun showMenuBar(isShow: Boolean) {
		menuBar.visibility = if (isShow) View.VISIBLE else View.GONE
	}

	companion object {
		val viewPagerSize = when (Config.getCurrentWalletType()) {
			WalletType.BTCTestOnly.content, WalletType.BTCOnly.content -> 1
			WalletType.ETHERCAndETCOnly.content -> 2
			else -> 3
		}
	}
}