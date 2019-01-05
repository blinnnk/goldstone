package io.goldstone.blinnnk.module.common.tokendetail.eosactivation.authorizatitonmanagement.view

import android.content.Context
import android.widget.LinearLayout
import io.goldstone.blinnnk.common.component.cell.graySquareCell
import io.goldstone.blinnnk.common.component.title.sessionTitle
import io.goldstone.blinnnk.common.language.EOSAccountText
import io.goldstone.blinnnk.common.sharedpreference.SharedAddress
import io.goldstone.blinnnk.common.value.PaddingSize
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.padding


/**
 * @author KaySaith
 * @date  2018/12/26
 */
class AuthorizationManagementHeaderView(context: Context) : LinearLayout(context) {

	init {
		orientation = LinearLayout.VERTICAL
		sessionTitle {
			setTitle(EOSAccountText.accountInformation)
			leftPadding = PaddingSize.device
		}
		padding = PaddingSize.device
		bottomPadding = 0
		graySquareCell {
			setTitle(EOSAccountText.account)
			setSubtitle(SharedAddress.getCurrentEOSAccount().name)
		}
		sessionTitle {
			setTitle(EOSAccountText.permissionList)
			leftPadding = PaddingSize.device
		}
	}
}