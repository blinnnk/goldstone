package io.goldstone.blockchain.module.entrance.starting.view

import android.support.v4.app.Fragment
import android.view.Gravity
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.extension.setCenterInHorizontal
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.language.CreateWalletText
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.overlay.LoadingView
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.language.SplashText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.starting.presenter.StartingPresenter
import org.jetbrains.anko.*

/**
 * @date 21/03/2018 10:15 PM
 * @author KaySaith
 */
class StartingFragment : BaseFragment<StartingPresenter>() {

	override val presenter = StartingPresenter(this)
	private val createButton by lazy { RoundButton(context!!) }
	private val importButton by lazy { RoundButton(context!!) }
	private val logoSize = 220.uiPX()

	override fun AnkoContext<Fragment>.initView() {
		relativeLayout {
			lparams(matchParent, matchParent)
			// Logo
			imageView {
				glideImage(R.drawable.gold_stone_logo)
			}.lparams {
				width = logoSize
				height = logoSize
				centerHorizontally()
				topMargin = (ScreenSize.Height * 0.2).toInt()
			}
			// Intro
			verticalLayout {
				textView(SplashText.goldStone) {
					textSize = fontSize(21)
					textColor = Spectrum.white
					typeface = GoldStoneFont.black(context)
					gravity = Gravity.CENTER_HORIZONTAL
				}

				textView(SplashText.slogan) {
					textSize = fontSize(12)
					typeface = GoldStoneFont.medium(context)
					textColor = Spectrum.opacity5White
				}
			}.lparams {
				centerHorizontally()
				topMargin = (ScreenSize.Height * 0.2).toInt() + logoSize
			}

			WalletTable.getAll {
				// 本地没有钱包的情况下显示登录和导入按钮
				if (isEmpty()) {
					verticalLayout {
						gravity = Gravity.CENTER_HORIZONTAL
						createButton.apply {
							text = CreateWalletText.create.toUpperCase()
							marginTop = 0
							setDarkStyle()
						}.click {
							presenter.showCreateWalletFragment()
						}.into(this)

						importButton.apply {
							text = ImportWalletText.importWallet.toUpperCase()
							marginTop = PaddingSize.content
							setDarkStyle()
						}.click {
							NetworkUtil.hasNetworkWithAlert(
								context, AlertText.importWalletNetwork
							)
							presenter.showImportWalletFragment()
						}.into(this)
					}.lparams {
						height = (ScreenSize.Height * 0.135).toInt() + importButton.layoutParams.height * 2
						alignParentBottom()
						width = matchParent
					}
				} else {
					presenter.updateWalletInfoForUserInfo(this)
					LoadingView.addLoadingCircle(
						this@relativeLayout,
						30.uiPX(),
						Spectrum.white
					) {
						setAlignParentBottom()
						setCenterInHorizontal()
						y -= 100.uiPX()
					}
				}
			}
		}
	}
}