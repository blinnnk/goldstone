package io.goldstone.blockchain.module.common.tokenpayment.deposit.view

import android.content.Context
import io.goldstone.blockchain.common.component.GradientType
import io.goldstone.blockchain.common.component.ValueInputView

/**
 * @date 2018/5/8 11:29 AM
 * @author KaySaith
 */

class DepositInputView(context: Context) : ValueInputView(context) {

	init {
		gradientView.setStyle(GradientType.CrystalGreen, gradientViewHeight)
	}

}