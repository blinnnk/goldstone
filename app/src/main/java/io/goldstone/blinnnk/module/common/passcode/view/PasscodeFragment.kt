package io.goldstone.blinnnk.module.common.passcode.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blinnnk.common.base.basefragment.BaseFragment
import io.goldstone.blinnnk.common.component.GradientType
import io.goldstone.blinnnk.common.component.GradientView
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.*
import io.goldstone.blinnnk.module.common.passcode.presenter.PasscodePresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.textColor

/**
 * @date 23/04/2018 11:04 AM
 * @author KaySaith
 */
class PasscodeFragment : BaseFragment<PasscodePresenter>() {

	lateinit var container: RelativeLayout
	override val pageTitle: String = "PIN Code"
	private val keyboard by lazy { NumberKeyboard(context!!) }
	private val passcodeInput by lazy { PasscodeInput(context!!) }
	private var failedAttention: TextView? = null
	override val presenter = PasscodePresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		container = relativeLayout {
			isClickable = true
			lparams(matchParent, matchParent)
			GradientView(context).apply {
				setStyle(GradientType.Blue)
				lparams(matchParent, matchParent)
			}.into(this)

			passcodeInput.apply {
				y += ScreenSize.Height * 0.18f
			}.into(this)

			keyboard.into(this)
			keyboard.apply {
				centerInHorizontal()
				alignParentBottom()
				y -= ScreenSize.Height * 0.12f
				setKeyboardClickEventByFrozenStatus()
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		activity?.apply { SoftKeyboard.hide(this) }
	}

	fun disableKeyboard(status: Boolean) {
		keyboard.disableKeyboard(status)
	}

	fun resetHeaderStyle() {
		keyboard.resetCode()
		passcodeInput.swipe()
	}

	fun showFailedAttention(content: String) {
		if (failedAttention.isNotNull()) failedAttention?.text = content
		else {
			failedAttention = TextView(context).apply {
				y += 30.uiPX()
				layoutParams = RelativeLayout.LayoutParams(matchParent, 20.uiPX())
				textSize = fontSize(12)
				textColor = Spectrum.red
				typeface = GoldStoneFont.medium(context)
				text = content
				gravity = Gravity.CENTER_HORIZONTAL
			}
			failedAttention?.into(container)
		}
	}

	fun recoveryAfterFreeze() {
		failedAttention?.let {
			container.removeView(it)
		}
		keyboard.setKeyboardClickEventByFrozenStatus()
		failedAttention = null
		resetHeaderStyle()
	}

	// 检查是否处于冻结状态
	private fun NumberKeyboard.setKeyboardClickEventByFrozenStatus() {
		presenter.isFrozenStatus { isFrozen ->
			checkCode = Runnable {
				if (!isFrozen) {
					disableKeyboard(false)
					if (getEnteredCode().length == Count.pinCode) presenter.unlockOrAlert(getEnteredCode())
					if (getEnteredCode().isEmpty()) passcodeInput.recoveryStyle()
					else passcodeInput.setEnteredStyle(getEnteredCode().lastIndex)
				}
			}
		}
	}

	companion object {
		fun show(fragment: Fragment) {
			if (fragment.activity?.supportFragmentManager?.findFragmentByTag(FragmentTag.pinCode).isNull())
				fragment.activity?.addFragment<PasscodeFragment>(ContainerID.main, FragmentTag.pinCode)
		}
	}
}