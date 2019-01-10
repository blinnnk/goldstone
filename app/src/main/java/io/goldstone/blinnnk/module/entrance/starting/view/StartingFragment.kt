package io.goldstone.blinnnk.module.entrance.starting.view

import android.support.v4.app.Fragment
import android.view.Gravity
import com.blinnnk.extension.alignParentBottom
import com.blinnnk.extension.centerInHorizontal
import com.blinnnk.extension.into
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.base.basefragment.BaseFragment
import io.goldstone.blinnnk.common.component.button.RoundButton
import io.goldstone.blinnnk.common.component.overlay.LoadingView
import io.goldstone.blinnnk.common.language.CreateWalletText
import io.goldstone.blinnnk.common.language.ImportWalletText
import io.goldstone.blinnnk.common.language.SplashText
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.common.value.PaddingSize
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.UMengEvent
import io.goldstone.blinnnk.common.value.fontSize
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blinnnk.module.entrance.starting.presenter.StartingPresenter
import org.jetbrains.anko.*

/**
 * @date 21/03/2018 10:15 PM
 * @author KaySaith
 */
class StartingFragment : BaseFragment<StartingPresenter>() {

	override val pageTitle: String = "Starting"
	override val presenter = StartingPresenter(this)
	private val createButton by lazy { RoundButton(context!!) }
	private val importButton by lazy { RoundButton(context!!) }
	private val logoSize = 220.uiPX()

	override fun AnkoContext<Fragment>.initView() {
		relativeLayout {
			lparams(matchParent, matchParent)
			imageView {
				imageResource = R.drawable.gold_stone_logo
			}.lparams {
				width = logoSize
				height = logoSize
				centerHorizontally()
				topMargin = (ScreenSize.Height * 0.2).toInt()
			}
			// Intro
			verticalLayout {
				lparams(matchParent, wrapContent)
				gravity = Gravity.CENTER_HORIZONTAL

				textView(SplashText.goldStone) {
					textSize = fontSize(21)
					textColor = Spectrum.white
					typeface = GoldStoneFont.black(context)
					gravity = Gravity.CENTER_HORIZONTAL
				}

				textView(SplashText.slogan) {
					gravity = Gravity.CENTER_HORIZONTAL
					textSize = fontSize(12)
					typeface = GoldStoneFont.medium(context)
					textColor = Spectrum.opacity5White
					leftPadding = PaddingSize.content
					rightPadding = PaddingSize.content
				}
			}.lparams {
				centerHorizontally()
				topMargin = (ScreenSize.Height * 0.2).toInt() + logoSize
			}

			load {
				WalletTable.dao.rowCount()
			} then { walletCount ->
				if (walletCount == 0) {
					// 本地没有钱包的情况下显示登录和导入按钮
					verticalLayout {
						gravity = Gravity.CENTER_HORIZONTAL
						createButton.apply {
							text = CreateWalletText.create.toUpperCase()
							marginTop = 0
							setDarkStyle()
						}.click {
							presenter.showCreateWalletFragment()
							UMengEvent.add(context, UMengEvent.Click.Common.createWallet, UMengEvent.Page.launchPage)
						}.into(this)

						importButton.apply {
							text = ImportWalletText.importWallet.toUpperCase()
							setDarkStyle()
						}.click {
							presenter.showImportWalletFragment()
							UMengEvent.add(context, UMengEvent.Click.Common.importWallet, UMengEvent.Page.launchPage)
						}.into(this)
					}.lparams {
						height = (ScreenSize.Height * 0.1).toInt() + importButton.layoutParams.height * 2
						alignParentBottom()
						width = matchParent
					}
				} else {
					presenter.updateWalletInfoForUserInfo(walletCount)
					LoadingView.addLoadingCircle(
						this,
						30.uiPX(),
						Spectrum.white
					) {
						alignParentBottom()
						centerInHorizontal()
						y -= 100.uiPX()
					}
				}
			}
		}
	}
}