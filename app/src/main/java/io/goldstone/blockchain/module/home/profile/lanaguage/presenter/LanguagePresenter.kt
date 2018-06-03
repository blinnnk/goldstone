package io.goldstone.blockchain.module.home.profile.lanaguage.presenter

import com.blinnnk.extension.jump
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.AlertText
import io.goldstone.blockchain.common.value.HoneyLanguage
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.profile.lanaguage.model.LanguageModel
import io.goldstone.blockchain.module.home.profile.lanaguage.view.LanguageFragment
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
			LanguageModel(HoneyLanguage.Korean.language)
		)
	}
	
	fun setLanguage(
		language: String,
		hold: Boolean.() -> Unit
	) {
		fragment.context?.apply {
			alert(
				AlertText.switchLanguage,
				AlertText.switchLanguageConfirmText
			) {
				yesButton {
					updateLanguageValue(language)
					hold(true)
				}
				noButton {
					hold(false)
				}
			}.show()
		}
	}
	
	private fun updateLanguageValue(language: String) {
		val code = when (language) {
			HoneyLanguage.English.language -> HoneyLanguage.English.code
			HoneyLanguage.Chinese.language -> HoneyLanguage.Chinese.code
			HoneyLanguage.Japanese.language -> HoneyLanguage.Japanese.code
			HoneyLanguage.Russian.language -> HoneyLanguage.Russian.code
			HoneyLanguage.Korean.language -> HoneyLanguage.Korean.code
			else -> HoneyLanguage.English.code
		}
		
		AppConfigTable.updateLanguage(code) {
			jumpAndReset()
		}
	}
	
	private fun jumpAndReset() {
		
		fragment.activity?.jump<SplashActivity>()
		// 杀掉进程
		android.os.Process.killProcess(android.os.Process.myPid())
		System.exit(0)
	}
}

