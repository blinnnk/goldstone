package io.goldstone.blockchain.module.common.tokendetail.tokenasset.view

import android.os.Build
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.GrayCardView
import io.goldstone.blockchain.common.component.ProgressView
import io.goldstone.blockchain.common.component.SessionTitleView
import io.goldstone.blockchain.common.component.cell.GraySquareCell
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.common.tokendetail.tokenasset.presenter.TokenAssetPresenter
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/09/10
 */

class TokenAssetFragment : BaseFragment<TokenAssetPresenter>() {

	private val authorizationCell by lazy {
		GraySquareCell(context!!).apply {
			showArrow()
			setTitle("AUTHORITY MANAGEMENT")
			setSubtitle("kaysaith1522")
		}
	}

	private val assetCard by lazy {
		GrayCardView(context!!).apply {
			setCardParams(ScreenSize.widthWithPadding, 255.uiPX())
		}
	}

	private val ramAssetCell by lazy {
		ProgressView(context!!).apply {
			setTitle("RAM")
			setSubtitle("1.502 EOS")
			setLeftValue(50, "KB AVAILABLE")
			setRightValue(219, "KB TOTAL")
		}
	}

	private val cpuAssetCell by lazy {
		ProgressView(context!!).apply {
			setTitle("CPU")
			setSubtitle("2.589 EOS")
			setLeftValue(89, "SEC AVAILABLE")
			setRightValue(219, "SEC TOTAL")
		}
	}

	private val netAssetCell by lazy {
		ProgressView(context!!).apply {
			setTitle("NET")
			setSubtitle("2.47 EOS")
			setLeftValue(102, "KB AVAILABLE")
			setRightValue(128, "KB TOTAL")
		}
	}

	override val presenter = TokenAssetPresenter(this)
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				topPadding = 10.uiPX()
				lparams(matchParent, wrapContent)
				gravity = Gravity.CENTER_HORIZONTAL
				SessionTitleView(context).setTitle("ACCOUNT MANAGEMENT").into(this)
				authorizationCell.into(this)
				SessionTitleView(context).setTitle("RESOURCES").into(this)
				assetCard.apply {
					addView(ramAssetCell)
					addView(cpuAssetCell)
					addView(netAssetCell)
				}.into(this)
				SessionTitleView(context).setTitle("ASSET TOOLS").into(this)
				linearLayout {
					lparams(ScreenSize.widthWithPadding, wrapContent)
					listOf(
						Pair(R.drawable.cpu_icon, "DELEGATE CPU\nREFUND CPU"),
						Pair(R.drawable.net_icon, "DELEGATE NET\nREFUND NET"),
						Pair(R.drawable.ram_icon, "BUY RAM\nSELL RAM")
					).forEachIndexed { index, pair ->
						val cardWidth = (ScreenSize.widthWithPadding - 10.uiPX()) / 3
						GrayCardView(context).apply {
							x = 5.uiPX() * index * 1f
							setCardParams(cardWidth, 130.uiPX())
							getContainer().apply {
								imageView {
									setColorFilter(GrayScale.gray)
									scaleType = ImageView.ScaleType.CENTER_INSIDE
									imageResource = pair.first
									layoutParams = RelativeLayout.LayoutParams(cardWidth, 80.uiPX())
								}
								textView(pair.second) {
									textSize = fontSize(11)
									textColor = GrayScale.midGray
									typeface = GoldStoneFont.black(context)
									layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
									gravity = Gravity.CENTER_HORIZONTAL
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
										lineHeight = 13.uiPX()
									}
								}
							}
						}.into(this)
					}
				}
			}
		}
	}
}