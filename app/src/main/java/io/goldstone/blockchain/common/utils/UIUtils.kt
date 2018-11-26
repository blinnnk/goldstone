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
import com.blinnnk.extension.orFalse
import com.blinnnk.extension.otherwise
import com.blinnnk.extension.toMillisecond
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.TinyNumberUtils
import io.goldstone.blockchain.common.language.WalletNameText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.value.DeviceName
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import java.text.SimpleDateFormat
import java.util.*


/**
 * @date 21/03/2018 9:07 PM
 * @author KaySaith
 * @rewriteDate 26/07/2018 5:47 PM
 * @reWriter wcx
 * @description 調整头像对应名称的顺序 , 删除generateAvatar方法
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

	fun generateDefaultName(): String {
		val name = arrayListOf(
			WalletNameText.Cat,
			WalletNameText.Bull,
			WalletNameText.Elephant,
			WalletNameText.Deer,
			WalletNameText.Fox,
			WalletNameText.Frog,
			WalletNameText.Giraffle,
			WalletNameText.Hippo,
			WalletNameText.Leopard,
			WalletNameText.Koala,
			WalletNameText.Lion,
			WalletNameText.Monkey,
			WalletNameText.Owl,
			WalletNameText.Penguin,
			WalletNameText.Raccoon,
			WalletNameText.Rhinoceros,
			WalletNameText.Wolf
		)
		val walletID =
			if (SharedWallet.getMaxWalletID() == 100) 0
			else SharedWallet.getMaxWalletID()
		return name[Math.abs(walletID) % name.size]
	}
}

object TimeUtils {

	// 将时间戳转化为界面显示的时间格式的工具
	fun formatDate(timeStamp: Long): String {
		val time = timeStamp.toMillisecond()
		return DateUtils.formatDateTime(
			GoldStoneAPI.context, time, DateUtils.FORMAT_SHOW_YEAR
		) + " " + DateUtils.formatDateTime(
			GoldStoneAPI.context, time, DateUtils.FORMAT_SHOW_TIME
		)
	}

	@SuppressLint("SimpleDateFormat")
		/**
		 * @date: 2018/8/22
		 * @author: yanglihai
		 * @description: 把日期转换成月日，例如 8/15
		 */
	fun formatMdDate(date: Long): String {
		val simpleDateFormat = SimpleDateFormat("M/d")
		return simpleDateFormat.format(java.util.Date(date))
	}

	@SuppressLint("SimpleDateFormat")
		/**
		 * @date: 2018/8/22
		 * @author: yanglihai
		 * @description: 把日期转换成日期+时间，例如8-25 12:00
		 */
	fun formatMdHmDate(date: Long): String {
		val simpleDateFormat = SimpleDateFormat("M-d HH:mm")
		return simpleDateFormat.format(java.util.Date(date))
	}
	
	/**
	 * @date: 2018/8/22
	 * @author: yanglihai
	 * @description: 把日期转换成日期+时间
	 */
	@SuppressLint("SimpleDateFormat")
	fun formatYMdHmDate(date: Long): String {
		val formatter = SimpleDateFormat("yyyy/MM/dd    HH:mm")
		return formatter.format(java.util.Date(date))
	}

	@SuppressLint("SimpleDateFormat")
	fun getUtcTime(time: Long): Long {
		val calendar = Calendar.getInstance()
		calendar.timeInMillis = time
		val timezone = calendar.timeZone
		var offset = timezone.rawOffset
		if (timezone.inDaylightTime(Date())) {
			offset += timezone.dstSavings
		}
		val offsetHours = offset / 1000 / 60 / 60
		val offsetMinutes = offset / 1000 / 60 % 60
		calendar.add(Calendar.HOUR_OF_DAY, -offsetHours)
		calendar.add(Calendar.MINUTE, -offsetMinutes)
		return calendar.time.time
	}
}

fun Long.toMillisecond(): Long {
	return toString().toMillisecond()
}

fun Activity.transparentStatus() {
	TinyNumberUtils.allFalse(
		try {
			packageManager.hasSystemFeature("com.oppo.feature.screen.heteromorphism")
		} catch (error: Exception) {
			LogUtil.error("hasSystemFeature", error)
			false
		},
		hasNotchInScreen(),
		isTargetDevice(DeviceName.nokiaX6).orFalse(),
		isTargetDevice(DeviceName.xiaomi8).orFalse(),
		detectNotchScreenInAndroidP().orFalse()
	) isTrue {
		SharedWallet.updateNotchScreenStatus(false)
		setTransparentStatusBar()
	} otherwise {
		SharedWallet.updateNotchScreenStatus(true)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
			window.statusBarColor = Spectrum.deepBlue
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

private fun Activity.detectNotchScreenInAndroidP(): Boolean? {
	return if (Build.VERSION.SDK_INT >= 28) {
		try {
			View(this).rootWindowInsets.displayCutout?.safeInsetTop ?: 0 > 30.uiPX()
		} catch (error: Exception) {
			LogUtil.error("detectNotchScreenInAndroidP", error)
			null
		}
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