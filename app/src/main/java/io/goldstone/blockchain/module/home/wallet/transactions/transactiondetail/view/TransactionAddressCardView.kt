package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.GSCard
import io.goldstone.blockchain.common.component.cell.GraySquareCellWithButtons
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel.Companion.convertMultiToOrFromAddresses
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/11/07
 * @description
 * 获取本地的所有联系人, 然后和参数地址比对是否存在本地, 来决定是否显示
 * 添加联系人的快捷按钮.
 */
@SuppressLint("ViewConstructor")
class TransactionAddressCardView(context: Context, addAction: (address: String) -> Unit) : GSCard(context) {
	var model: List<TransactionDetailModel> by observing(listOf()) {
		verticalLayout {
			id = ElementID.cardLayout
			ContactTable.getAllContactAddresses { contacts ->
				model.forEach {
					textView {
						setPadding(5.uiPX(), 5.uiPX(), 5.uiPX(), 5.uiPX())
						layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
						textColor = Spectrum.deepBlue
						textSize = fontSize(12)
						typeface = GoldStoneFont.black(context)
						text = it.description
					}
					val addresses = convertMultiToOrFromAddresses(it.info)
					addresses.forEach { address ->
						GraySquareCellWithButtons(context).apply {
							val isExisted = contacts.any { contact ->
								contact.equals(address, true)
							}
							if (isExisted) showOnlyCopyButton {
								context.clickToCopy(address)
							} else showCopyAndAddButton(
								copyAction = { context.clickToCopy(address) },
								addAction = { addAction(address) }
							)
							setTitle(CryptoUtils.scaleMiddleAddress(address))
						}.into(this)
					}
				}
			}
		}
	}

	init {
		setContentPadding(10.uiPX(), 20.uiPX(), PaddingSize.overlay, PaddingSize.overlay)
	}
}