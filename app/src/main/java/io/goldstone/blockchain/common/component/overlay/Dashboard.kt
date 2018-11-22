package io.goldstone.blockchain.common.component.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.customListAdapter
import com.afollestad.materialdialogs.list.getRecyclerView
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNCHECKED_CAST")
@SuppressLint("ViewConstructor")
/**
 * @date 2018/7/11 1:32 PM
 * @author KaySaith
 */

class Dashboard(context: Context, hold: Dashboard.() -> Unit) {
	val dialog = MaterialDialog(context)

	init {
		dialog.window.setLayout(ScreenSize.overlayContentWidth, wrapContent)
		val shape = GradientDrawable().apply {
			cornerRadius = CornerSize.small
			shape = GradientDrawable.RECTANGLE
			setSize(matchParent, matchParent)
			setColor(Spectrum.white)
		}
		dialog.window.setBackgroundDrawable(shape)
		hold(this)
	}

	fun dismiss() = dialog.dismiss()

	fun <T, C : View> showList(title: String, adapter: HoneyBaseAdapter<T, C>) {
		with(dialog) {
			title(text = title)
			customListAdapter(adapter)
			negativeButton(text = CommonText.gotIt)
			show()
		}
	}

	fun <T, C : View> showGrid(title: String, adapter: HoneyBaseAdapter<T, C>) {
		with(dialog) {
			title(text = title)
			customListAdapter(adapter)
			getRecyclerView()?.layoutManager =
				GridLayoutManager(context, 3, 1, false)
			negativeButton(text = CommonText.cancel)
			show()
		}
	}

	fun <T : View> showDashboard(title: String, customView: T, message: String, hold: (T) -> Unit, cancelAction: () -> Unit) {
		with(dialog) {
			cancelOnTouchOutside(false)
			title(text = title)
			message(text = message)
			customView(view = customView)
			positiveButton(text = CommonText.confirm) {
				hold(customView)
				dialog.dismiss()
			}
			negativeButton(text = CommonText.cancel) {
				cancelAction()
			}
			show()
		}
	}

	fun showAlert(
		title: String,
		message: String,
		positiveButtonTitle: String = CommonText.confirm,
		cancelAction: () -> Unit = {},
		confirmAction: () -> Unit
	) {
		with(dialog) {
			title(text = title)
			message(text = message)
			positiveButton(text = positiveButtonTitle) {
				confirmAction()
			}
			negativeButton(text = CommonText.cancel) {
				cancelAction()
			}
			show()
		}
	}

	fun showMultiChoice(
		title: String,
		data: List<String>,
		defaultIndexes: IntArray,
		confirmAction: (List<String>) -> Unit
	) {
		var selectedItems = listOf<String>()
		with(dialog) {
			title(text = title)
			listItemsMultiChoice(
				items = data,
				initialSelection = defaultIndexes,
				waitForPositiveButton = false
			) { _, _, items ->
				selectedItems = items
				dialog.setActionButtonEnabled(WhichButton.POSITIVE, items.isNotEmpty())
			}
			positiveButton(text = CommonText.confirm) {
				confirmAction(selectedItems)
			}
			negativeButton(text = CommonText.cancel)
			show()
		}
	}
}