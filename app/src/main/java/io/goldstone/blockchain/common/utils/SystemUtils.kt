@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.common.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

/**
 * @date 2018/6/4 12:29 AM
 * @author KaySaith
 */
object SystemUtils {
	
	@Throws(PackageManager.NameNotFoundException::class)
	private fun getPackageInfo(context: Context): PackageInfo {
		return context.packageManager.getPackageInfo(context.packageName, 0)
	}
	
	fun getVersionName(context: Context): String {
		try {
			return getPackageInfo(context).versionName
		} catch (e: PackageManager.NameNotFoundException) {
			e.printStackTrace()
		}
		return ""
	}
	
	fun getVersionCode(context: Context): Int {
		try {
			return getPackageInfo(context).versionCode
		} catch (e: PackageManager.NameNotFoundException) {
			e.printStackTrace()
		}
		return 0
	}
}