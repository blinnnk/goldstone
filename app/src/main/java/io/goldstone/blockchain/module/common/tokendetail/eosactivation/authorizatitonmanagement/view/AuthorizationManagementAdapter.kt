package io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.view

import android.content.Context
import android.view.View
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.model.AuthorizationManagementModel


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

	override fun generateFooter(context: Context) = View(context)

	override fun generateHeader(context: Context) =
		AuthorizationManagementHeaderView(context)

	override fun AuthorizationManagementCell.bindCell(data: AuthorizationManagementModel, position: Int) {
		model = data
		// 如果只有 1 个 Owner Key 的话那么禁止对其操作隐藏操作按钮
		if (
			dataSet.filter { EOSActor.getActorByValue(it.permission).isOwner() }.size == 1 &&
			EOSActor.getActorByValue(data.permission).isOwner()
		) {
			showEditIcons(false)
		} else showEditIcons(true)
	}

}