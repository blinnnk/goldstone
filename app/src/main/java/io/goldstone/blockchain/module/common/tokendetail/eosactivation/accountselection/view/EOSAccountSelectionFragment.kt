package io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AttentionTextView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.overlay.LoadingView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.crypto.eos.accountregister.AccountActor
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.presenter.EOSAccountSelectionPresenter
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/09/11
 */
class EOSAccountSelectionFragment : BaseFragment<EOSAccountSelectionPresenter>() {
	private val attentionText by lazy { AttentionTextView(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	private lateinit var container: LinearLayout
	private lateinit var loadingView: LinearLayout
	private var selectedIndex = 0

	private var accountActors: List<AccountActor> by observing(listOf()) {
		accountActors.forEachIndexed { index, actor ->
			container.apply {
				EOSAccountCell(context).apply {
					id = index // for clearing radio checked status
					setRadioStatus(index == 0)
					setAccountInfo(actor.name, actor.permission.value)
				}.click {
					clearRadiosStatus(accountActors.size)
					it.setRadioStatus(true)
					selectedIndex = it.id
				}.into(this)
			}
		}
	}
	override val presenter = EOSAccountSelectionPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams(matchParent, matchParent)
				gravity = Gravity.CENTER_HORIZONTAL
				attentionText.apply {
					isCenter()
					setPadding(15.uiPX(), 30.uiPX(), 15.uiPX(), 20.uiPX())
					layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
					text = "Found multiple EOS accounts under this EOS public key, please select an account as your default account."
				}.into(this)
				container = verticalLayout {
					lparams(matchParent, matchParent)
					gravity = Gravity.CENTER_HORIZONTAL
					loadingView = verticalLayout {
						visibility = View.GONE
						lparams(matchParent, 200.uiPX())
						gravity = Gravity.CENTER
						LoadingView.addLoadingCircle(this, 50.uiPX())
						textView("loading account info from chain") {
							topPadding = 20.uiPX()
							layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent)
							textSize = fontSize(12)
							typeface = GoldStoneFont.heavy(context)
							textColor = GrayScale.midGray
						}
					}
				}
				confirmButton.apply {
					text = CommonText.confirm.toUpperCase()
					setBlueStyle(20.uiPX())
				}.click {
					container.findViewById<EOSAccountCell>(selectedIndex)?.getName()?.apply {
						presenter.setEOSDefaultName(this)
					}
				}.into(this)
			}
		}
	}

	fun setAccountNameList(actors: List<AccountActor>) {
		accountActors = actors
	}

	fun showLoadingView(status: Boolean) {
		if (status) {
			loadingView.visibility = View.VISIBLE
			confirmButton.visibility = View.GONE
		} else {
			loadingView.visibility = View.GONE
			confirmButton.visibility = View.VISIBLE
		}
	}

	private fun clearRadiosStatus(cellCount: Int) {
		(0 until cellCount).forEach {
			container.findViewById<EOSAccountCell>(it)?.setRadioStatus(false)
		}
	}

}