package io.goldstone.blockchain.module.home.profile.contacts.component

import com.blinnnk.extension.addFragmentAndSetArgument
import com.blinnnk.extension.into
import com.blinnnk.extension.removeChildFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.overlay.ContentScrollOverlayView
import io.goldstone.blockchain.common.language.ContactText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.home.profile.contacts.contracts.view.ContactFragment


/**
 * @author KaySaith
 * @date  2018/09/24
 */
fun <T : BaseOverlayFragment<*>> T.showContactDashboard(chainType: ChainType, hold: (address: String) -> Unit) {
	getContainer().apply {
		object : ContentScrollOverlayView(context) {
			override fun remove() {
				super.remove()
				childFragmentManager.fragments.find { it is ContactFragment }?.let {
					removeChildFragment(it)
				}
				if (this@showContactDashboard is TokenDetailOverlayFragment) {
					setTitle(TokenDetailText.tradingRAM)
				}
			}
		}.apply {
			setTitle(ContactText.contactName)
			addContent {
				addFragmentAndSetArgument<ContactFragment>(
					ContainerID.contentOverlay
				).apply {
					this.chainType = chainType.id
					this.clickCellEvent = Runnable {
						selectedAddress?.let(hold)
						remove()
					}
				}
			}
		}.into(this)
	}
}