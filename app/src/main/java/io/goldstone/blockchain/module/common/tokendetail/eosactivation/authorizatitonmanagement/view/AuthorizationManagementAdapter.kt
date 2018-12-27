package io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.view

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.model.AuthorizationManagementModel
import org.jetbrains.anko.matchParent


/**
 * @author KaySaith
 * @date  2018/12/26
 */
class AuthorizationManagementAdapter(
	override val dataSet: ArrayList<AuthorizationManagementModel>,
	private val editAction: (publicKey: String, actor: EOSActor) -> Unit,
	private val deleteAction: (publicKey: String, actor: EOSActor) -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<AuthorizationManagementModel, AuthorizationManagementHeaderView, AuthorizationManagementCell, View>() {
	override fun generateCell(context: Context) =
		AuthorizationManagementCell(context, editAction, deleteAction)

	override fun generateFooter(context: Context) = View(context).apply {
		layoutParams = LinearLayout.LayoutParams(matchParent, 20.uiPX())
	}

	override fun generateHeader(context: Context) =
		AuthorizationManagementHeaderView(context)

	override fun AuthorizationManagementCell.bindCell(data: AuthorizationManagementModel, position: Int) {
		model = data
		click {
			context.clickToCopy(data.publicKey)
		}
		// 如果只有 1 个 Owner Key 的话那么禁止对其操作隐藏操作按钮
		if (
			dataSet.filter { EOSActor.getActorByValue(it.permission).isOwner() }.size == 1 &&
			EOSActor.getActorByValue(data.permission).isOwner()
		) {
			showEditIcons(false)
		} else showEditIcons(true)
	}

}