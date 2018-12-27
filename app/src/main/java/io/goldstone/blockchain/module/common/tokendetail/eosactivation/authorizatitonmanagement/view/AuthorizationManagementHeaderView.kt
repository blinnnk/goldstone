package io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.view

import android.content.Context
import android.widget.LinearLayout
import io.goldstone.blockchain.common.component.cell.graySquareCell
import io.goldstone.blockchain.common.component.title.sessionTitle
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.value.PaddingSize
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
			setTitle("Account Information")
			leftPadding = PaddingSize.device
		}
		padding = PaddingSize.device
		graySquareCell {
			setTitle("ACCOUNT")
			setSubtitle(SharedAddress.getCurrentEOSAccount().name)
		}
		sessionTitle {
			setTitle("Authorization List")
			leftPadding = PaddingSize.device
		}
	}
}