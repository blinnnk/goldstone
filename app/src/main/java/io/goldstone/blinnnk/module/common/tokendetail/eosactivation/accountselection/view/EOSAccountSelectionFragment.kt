package io.goldstone.blinnnk.module.common.tokendetail.eosactivation.accountselection.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blinnnk.common.base.basefragment.BaseFragment
import io.goldstone.blinnnk.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blinnnk.common.component.button.RoundButton
import io.goldstone.blinnnk.common.component.overlay.LoadingView
import io.goldstone.blinnnk.common.component.title.AttentionTextView
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.language.EOSAccountText
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.fontSize
import io.goldstone.blinnnk.crypto.eos.accountregister.AccountActor
import io.goldstone.blinnnk.module.common.tokendetail.eosactivation.accountselection.presenter.EOSAccountSelectionPresenter
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/09/11
 */
class EOSAccountSelectionFragment : BaseFragment<EOSAccountSelectionPresenter>() {

	override val pageTitle: String = EOSAccountText.accountNameSelection
	private val defaultActorName by lazy { arguments?.getString(ArgumentKey.defaultEOSAccountName) }
	private val attentionText by lazy { AttentionTextView(context!!) }
	private val confirmButton by lazy { RoundButton(context!!) }
	private lateinit var container: LinearLayout
	private lateinit var loadingView: LinearLayout
	private var defaultIndex: Int? = null

	private var accountActors: List<Pair<AccountActor, Boolean>> by observing(listOf()) {
		accountActors.forEachIndexed { index, actor ->
			EOSAccountCell(container.context).apply {
				id = index // for clearing radio checked status
				if (actor.first.name == defaultActorName && defaultIndex.isNull()) {
					setRadioStatus(true)
					defaultIndex = index
				}
				setAccountInfo(actor.first.name, actor.first.permission.value, actor.second)
			}.click {
				clearRadiosStatus(accountActors.size)
				it.setRadioStatus(true)
				defaultIndex = it.id
			}.into(container)
		}
	}
	override val presenter = EOSAccountSelectionPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams(matchParent, wrapContent)
				gravity = Gravity.CENTER_HORIZONTAL
				attentionText.apply {
					isCenter()
					setPadding(15.uiPX(), 30.uiPX(), 15.uiPX(), 20.uiPX())
					layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
					text = EOSAccountText.multipleAccountHint
				}.into(this)
				container = verticalLayout {
					lparams(matchParent, wrapContent)
					gravity = Gravity.CENTER_HORIZONTAL
					loadingView = verticalLayout {
						visibility = View.GONE
						lparams(matchParent, 200.uiPX())
						gravity = Gravity.CENTER
						LoadingView.addLoadingCircle(this, 60.uiPX())
						textView(EOSAccountText.loadingAccountInfo) {
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
					container.findViewById<EOSAccountCell>(defaultIndex.orZero())?.getName()?.apply {
						if (isNotEmpty()) presenter.setEOSDefaultName(this)
					}
				}.into(this)
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter.showAvailableNames()
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		if (parent is BaseOverlayFragment<*>) {
			parent.presenter.popFragmentFrom<EOSAccountSelectionFragment>()
		}
	}

	fun setAccountNameList(actors: List<Pair<AccountActor, Boolean>>) {
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