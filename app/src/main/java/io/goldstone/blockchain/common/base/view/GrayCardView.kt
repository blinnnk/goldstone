package io.goldstone.blockchain.common.base.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import io.goldstone.blockchain.common.component.GSCard
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent


/**
 * @author KaySaith
 * @date  2018/09/10
 */

@SuppressLint("ViewConstructor")
open class GrayCardView(context: Context) : GSCard(context) {

	var container = verticalLayout {
		gravity = Gravity.CENTER_HORIZONTAL
		lparams(matchParent, wrapContent)
	}

	init {
	  this.setCardBackgroundColor(GrayScale.whiteGray)
		this.setContentPadding(PaddingSize.content, PaddingSize.content, PaddingSize.content, PaddingSize.content)
	}

	fun addContent(hold: LinearLayout.() -> Unit) {
		hold(container)
	}

}