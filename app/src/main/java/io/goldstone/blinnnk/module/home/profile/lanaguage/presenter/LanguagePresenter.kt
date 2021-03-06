package io.goldstone.blinnnk.module.home.profile.lanaguage.presenter

import com.blinnnk.extension.jump
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blinnnk.common.language.AlertText
import io.goldstone.blinnnk.common.language.HoneyLanguage
import io.goldstone.blinnnk.common.sandbox.SandBoxManager
import io.goldstone.blinnnk.kernel.commontable.AppConfigTable
import io.goldstone.blinnnk.module.entrance.splash.view.SplashActivity
import io.goldstone.blinnnk.module.home.profile.lanaguage.model.LanguageModel
import io.goldstone.blinnnk.module.home.profile.lanaguage.view.LanguageFragment
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

@Suppress("DEPRECATION")
/**
 * @date 26/03/2018 6:42 PM
 * @author KaySaith
 */
class LanguagePresenter(
	override val fragment: LanguageFragment
) : BaseRecyclerPresenter<LanguageFragment, LanguageModel>() {

	override fun updateData() {
		fragment.asyncData = arrayListOf(
			LanguageModel(HoneyLanguage.English.language),
			LanguageModel(HoneyLanguage.Chinese.language),
			LanguageModel(HoneyLanguage.Japanese.language),
			LanguageModel(HoneyLanguage.Russian.language),
			LanguageModel(HoneyLanguage.Korean.language),
			LanguageModel(HoneyLanguage.TraditionalChinese.language)
		)
	}

	fun setLanguage(language: String, hold: Boolean.() -> Unit) {
		fragment.context?.apply {
			alert(
				AlertText.switchLanguage,
				AlertText.switchLanguageConfirmText
			) {
				yesButton {
					updateLanguageValue(language) {
						jumpAndReset()
					}
					hold(true)
				}
				noButton {
					hold(false)
				}
			}.show()
		}
	}

	private fun updateLanguageValue(language: String, callback: () -> Unit) {
		load {
			val code = when (language) {
				HoneyLanguage.English.language -> HoneyLanguage.English.code
				HoneyLanguage.Chinese.language -> HoneyLanguage.Chinese.code
				HoneyLanguage.Japanese.language -> HoneyLanguage.Japanese.code
				HoneyLanguage.Russian.language -> HoneyLanguage.Russian.code
				HoneyLanguage.Korean.language -> HoneyLanguage.Korean.code
				HoneyLanguage.TraditionalChinese.language -> HoneyLanguage.TraditionalChinese.code
				else -> HoneyLanguage.English.code
			}
			SandBoxManager.updateLanguage(code)
			AppConfigTable.dao.updateLanguageCode(code)
		} then {
			callback()
		}
	}

	private fun jumpAndReset() {
		fragment.activity?.jump<SplashActivity>()
		// 杀掉进程
		android.os.Process.killProcess(android.os.Process.myPid())
		System.exit(0)
	}
}

