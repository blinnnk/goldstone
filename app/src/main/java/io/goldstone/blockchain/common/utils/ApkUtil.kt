package io.goldstone.blockchain.common.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.content.FileProvider
import com.blinnnk.extension.isEvenCount
import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.utils.toCryptHexString
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import java.io.File
import java.math.BigInteger

/**
 * @date: 2018/8/10.
 * @author: yanglihai
 */
object ApkUtil {

	/**
	 * @author: yanglihai
	 * [apkFile] 需要安装的apk
	 * @description: 安装apk的（适配了各个版本）
	 */
	fun installApk(apkFile: File) {
		val intent = Intent(Intent.ACTION_VIEW)
		// 由于没有在Activity环境下启动Activity,设置下面的标签
		intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
		// 判读版本是否在7.0以上
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			// 添加这一句表示对目标应用临时授权该 Uri 所代表的文件
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
			// 参数 1 上下文, 参数 2 Provider 主机地址和配置文件中保持一致参数 3 共享的文件
			val apkUri = FileProvider.getUriForFile(GoldStoneAPI.context, "io.goldstone.blockchain.provider", apkFile)
			intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
		} else {
			intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
		}
		GoldStoneAPI.context.startActivity(intent)
	}

	@SuppressLint("HardwareIds")
	fun generateGoldStoneID(): String {
		val deviceID = Settings.Secure.getString(GoldStoneAPI.context.contentResolver, Settings.Secure.ANDROID_ID)
		val registerTime = System.currentTimeMillis()
		val deviceIDCode = deviceID.toCryptHexString()
		val registerTimeCode = BigInteger.valueOf(registerTime).toString(16)
		val finalCode = deviceIDCode + registerTimeCode
		return EOSUtils.toLittleEndian(if (!finalCode.isEvenCount()) finalCode + "0" else finalCode)
	}
}