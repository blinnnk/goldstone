package io.goldstone.blockchain.module.common.passcode.view

import android.annotation.SuppressLint
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
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.GradientType
import io.goldstone.blockchain.common.component.GradientView
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.common.passcode.presenter.PasscodePresenter
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
				setCenterInHorizontal()
				setAlignParentBottom()
				y -= ScreenSize.Height * 0.12f
				setKeyboardClickEventByFrozenStatus()
			}
		}
	}

	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(view, savedInstanceState)
		activity?.apply { SoftKeyboard.hide(this) }
	}

	fun resetHeaderStyle() {
		keyboard.resetCode()
		passcodeInput.swipe()
	}

	@SuppressLint("SetTextI18n")
	fun showFailedAttention(content: String) {
		failedAttention.isNull() isFalse {
			failedAttention?.text = content
		} otherwise {
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

	fun recoveryAfterFrezon() {
		failedAttention?.let { container.removeView(it) }
		keyboard.setKeyboardClickEventByFrozenStatus()
		failedAttention = null
		resetHeaderStyle()
	}

	private fun NumberKeyboard.setKeyboardClickEventByFrozenStatus() {
		// 检查是否处于冻结状态
		presenter.isFrozenStatus { isFrozen ->
			checkCode = Runnable {
				if (isFrozen) return@Runnable
				presenter.unlockOrAlert(getEnteredCode()) {
					getEnteredCode().isEmpty() isTrue {
						passcodeInput.recoveryStyle()
					} otherwise {
						passcodeInput.setEnteredStyle(getEnteredCode().lastIndex)
					}
				}
			}
		}
	}
}