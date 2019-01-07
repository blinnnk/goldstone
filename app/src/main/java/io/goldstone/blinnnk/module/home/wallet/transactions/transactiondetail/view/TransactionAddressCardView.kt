package io.goldstone.blinnnk.module.home.wallet.transactions.transactiondetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import com.blinnnk.util.observing
import io.goldstone.blinnnk.common.component.GSCard
import io.goldstone.blinnnk.common.component.cell.buttonSquareCell
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.*
import io.goldstone.blinnnk.crypto.utils.CryptoUtils
import io.goldstone.blinnnk.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blinnnk.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
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
					val addresses = it.info.split(",")
					addresses.forEach { address ->
						buttonSquareCell {
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
						}
					}
				}
			}
		}
	}

	init {
		resetCardElevation(ShadowSize.Cell)
		setContentPadding(10.uiPX(), 20.uiPX(), PaddingSize.overlay, PaddingSize.overlay)
	}
}