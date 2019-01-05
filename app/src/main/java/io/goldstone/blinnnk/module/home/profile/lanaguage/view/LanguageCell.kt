package io.goldstone.blinnnk.module.home.profile.lanaguage.view

import android.content.Context
import com.blinnnk.util.observing
import io.goldstone.blinnnk.common.base.basecell.BaseRadioCell
import io.goldstone.blinnnk.common.language.HoneyLanguage
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.module.home.profile.lanaguage.model.LanguageModel

/**
 * @date 26/03/2018 6:42 PM
 * @author KaySaith
 */
class LanguageCell(context: Context) : BaseRadioCell(context) {

	var model: LanguageModel by observing(LanguageModel()) {
		title.text = model.name
		checkedStatus = SharedWallet.getCurrentLanguageCode() == HoneyLanguage.getLanguageCode(model.name)
	}
}