package io.goldstone.blinnnk.common.thread

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * @author KaySaith
 * @date  2018/11/10
 */

fun launchUI(callback: () -> Unit) {
	GlobalScope.launch(Dispatchers.Main) { callback() }
}

fun launchDefault(callback: () -> Unit) {
	GlobalScope.launch(Dispatchers.Default) { callback() }
}

