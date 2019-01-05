package io.goldstone.blinnnk.module.common.tokenpayment.deposit.view

import android.content.Context
import io.goldstone.blinnnk.common.component.GradientType
import io.goldstone.blinnnk.common.component.edittext.ValueInputView

/**
 * @date 2018/5/8 11:29 AM
 * @author KaySaith
 */

class DepositInputView(context: Context) : ValueInputView(context) {

	init {
		gradientView.setStyle(GradientType.CrystalGreen, gradientViewHeight)
	}

}