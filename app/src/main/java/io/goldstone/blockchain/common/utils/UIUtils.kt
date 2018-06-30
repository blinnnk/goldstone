package io.goldstone.blockchain.common.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.orElse
import com.blinnnk.extension.orFalse
import com.blinnnk.extension.otherwise
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R.drawable.*
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WalletNameText
import io.goldstone.blockchain.kernel.network.GoldStoneAPI

/**
 * @date 21/03/2018 9:07 PM
 * @author KaySaith
 */
object UIUtils {
	
	// easy to set gradient color
	fun setGradientColor(
		startColor: Int,
		endColor: Int,
		width: Float = ScreenSize.Width.toFloat(),
		height: Float = ScreenSize.Height.toFloat()
	) =
		LinearGradient(
			0f, 0f, width, height, startColor, endColor, Shader.TileMode.CLAMP
		)
	
	fun subtractThenHalf(
		first: Int,
		second: Int
	) = (first - second) / 2
	
	fun generateAvatar(id: Int): Int {
		val avatars = arrayListOf(
			avatar_1, avatar_2, avatar_3, avatar_4, avatar_5, avatar_6, avatar_7, avatar_8, avatar_9,
			avatar_10, avatar_11, avatar_12, avatar_13, avatar_14, avatar_15, avatar_16, avatar_17
		)
		val finalID = if (id > 0) id - 1 else id
		return avatars[finalID % 17]
	}
	
	fun generateDefaultName(): String {
		val name = arrayListOf(
			WalletNameText.Owl,
			WalletNameText.Bear,
			WalletNameText.Elephant,
			WalletNameText.Rhinoceros,
			WalletNameText.Frog,
			WalletNameText.Koala,
			WalletNameText.Fox,
			WalletNameText.Monkey,
			WalletNameText.Giraffle,
			WalletNameText.Penguin,
			WalletNameText.Wolf,
			WalletNameText.Bull,
			WalletNameText.Leopard,
			WalletNameText.Deer,
			WalletNameText.Raccoon,
			WalletNameText.Lion,
			WalletNameText.Hippo
		)
		val walletID =
			if (Config.getMaxWalletID() == 100) 0
			else Config.getMaxWalletID()
		return name[(walletID) % 17]
	}
}

object TimeUtils {
	// 将时间戳转化为界面显示的时间格式的工具
	fun formatDate(timeStamp: String): String {
		val stamp = if (timeStamp.contains(".")) timeStamp.substringBefore(".").toLong()
		else timeStamp.toLong()
		return DateUtils.formatDateTime(
			GoldStoneAPI.context, stamp.toMillsecond(), DateUtils.FORMAT_SHOW_YEAR
		) + " " + DateUtils.formatDateTime(
			GoldStoneAPI.context, stamp.toMillsecond(), DateUtils.FORMAT_SHOW_TIME
		)
	}
	
	// 将时间戳转化为界面显示的时间格式的工具
	fun formatDate(timeStamp: Long): String {
		val time = timeStamp.toMillsecond()
		return DateUtils.formatDateTime(
			GoldStoneAPI.context, time, DateUtils.FORMAT_SHOW_YEAR
		) + " " + DateUtils.formatDateTime(
			GoldStoneAPI.context, time, DateUtils.FORMAT_SHOW_TIME
		)
	}
}

fun String.toMillsecond(): Long {
	val timestamp = toLongOrNull().orElse(0L)
	return when {
		count() == 10 -> timestamp * 1000
		count() < 13 -> timestamp * Math.pow(10.0, (13 - count()).toDouble()).toLong()
		count() > 13 -> timestamp / Math.pow(10.0, (count() - 13).toDouble()).toLong()
		else -> timestamp
	}
}

fun Long.toMillsecond(): Long {
	return toString().toMillsecond()
}

fun Activity.transparentStatus() {
	TinyNumberUtils.allFalse(
		packageManager.hasSystemFeature("com.oppo.feature.screen.heteromorphism"),
		hasNotchInScreen(),
		isTargetDevice(DeviceName.nokiaX6).orFalse(),
		isTargetDevice(DeviceName.xiaomi8).orFalse(),
		detectnochScreenInAndroidP().orFalse()
	) isTrue {
		Config.updateNotchScreenStatus(false)
		setTransparentStatusBar()
	} otherwise {
		Config.updateNotchScreenStatus(true)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
			window.statusBarColor = Color.BLACK
		}
	}
}

@SuppressLint("ObsoleteSdkInt")
fun Activity.setTransparentStatusBar() {
	if (Build.VERSION.SDK_INT >= 19) {
		window.decorView.systemUiVisibility =
			View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	}
	if (Build.VERSION.SDK_INT >= 21) {
		window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
		window.statusBarColor = Color.TRANSPARENT
	}
}

private fun isTargetDevice(name: String): Boolean? {
	val myDevice = BluetoothAdapter.getDefaultAdapter()
	return try {
		val deviceName = myDevice.name
		name.contains(deviceName, true)
	} catch (error: Exception) {
		null
	}
}

private fun Activity.detectnochScreenInAndroidP(): Boolean? {
	return if (Build.VERSION.SDK_INT >= 28) {
		View(this).rootWindowInsets.displayCutout.safeInsetTop > 30.uiPX()
	} else {
		null
	}
}

// 华为适配齐刘海的判断
fun Activity.hasNotchInScreen(): Boolean {
	var ret = false
	try {
		val cl = classLoader
		val hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
		val get = hwNotchSizeUtil.getMethod("hasNotchInScreen")
		ret = get.invoke(hwNotchSizeUtil) as Boolean
	} catch (e: ClassNotFoundException) {
		Log.e("test", "hasNotchInScreen ClassNotFoundException")
	} catch (e: NoSuchMethodException) {
		Log.e("test", "hasNotchInScreen NoSuchMethodException")
	} catch (e: Exception) {
		Log.e("test", "hasNotchInScreen Exception")
	} finally {
		return ret
	}
}