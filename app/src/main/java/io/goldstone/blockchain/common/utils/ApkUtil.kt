package io.goldstone.blockchain.common.utils

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import java.io.File

/**
 * @date: 2018/8/10.
 * @author: yanglihai
 */
object ApkUtil {
	
	/**
	 * @author: yanglihai
	 * @param apkUri 需要安装的apk
	 * @description: 安装apk的（适配了各个版本）
	 */
	fun installApk(apkFile: File) {
		val intent = Intent(Intent.ACTION_VIEW)
		// 由于没有在Activity环境下启动Activity,设置下面的标签
		intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 判读版本是否在7.0以上
			// 添加这一句表示对目标应用临时授权该Uri所代表的文件
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
			// 参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
			val apkUri = FileProvider.getUriForFile(GoldStoneAPI.context, "io.goldstone.blockchain.provider", apkFile)
			intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
		} else {
			intent.setDataAndType(Uri.fromFile(apkFile),  "application/vnd.android.package-archive")
		}
		GoldStoneAPI.context.startActivity(intent)
	}
}