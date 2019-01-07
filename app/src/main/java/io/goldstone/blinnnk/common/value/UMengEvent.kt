package io.goldstone.blinnnk.common.value

import android.content.Context
import com.umeng.analytics.MobclickAgent


/**
 * @author KaySaith
 * @date  2019/01/07
 */
object UMengEvent {
	object Click {
		const val dappCenter = "dappCenter"
	}

	fun add(context: Context?, eventName: String) {
		MobclickAgent.onEvent(context, eventName, currentChannel.value)
	}
}