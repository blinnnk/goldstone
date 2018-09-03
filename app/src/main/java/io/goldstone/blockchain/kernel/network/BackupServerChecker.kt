package io.goldstone.blockchain.kernel.network

import com.blinnnk.extension.isTrue
import com.blinnnk.extension.safeGet
import com.blinnnk.util.TinyNumberUtils
import io.goldstone.blockchain.common.utils.GoldStoneWebSocket
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.WebUrl
import org.json.JSONObject

/**
 * @date 2018/6/12 8:27 PM
 * @author KaySaith
 */
object BackupServerChecker {

	/**
	 * 有一些原因会导致， 我们的核心 `Server` 不可用, 当出现这种情况的时候客户端需要
	 * 有策略检测并切换到备份网络。
	 */
	fun checkBackupStatusByException(error: Exception) {
		if (
			error.toString().contains("timeout")
			|| error.toString().contains("StringIndexOutOfBoundsException")
			|| error.toString().contains("java.lang.Exception")
		) {
			checkWhetherNeedToSwitchToBackupServer(
				{
					// Error Callback
					LogUtil.error("CheckWhetherNeedToSwitchToBackupServer", it)
				}
			) {
				isTrue {
					APIPath.updateServerUrl(WebUrl.backUpServer)
					GoldStoneWebSocket.updateSocketUrl(WebUrl.backUpSocket)
				}
			}
		}
	}

	/**
	 * 业务中会在入口的常贵接口请求中判断超时， 如果超时后就会执行这个函数来检查
	 * 是否需要把业务切换到备份网络。
	 */
	private fun checkWhetherNeedToSwitchToBackupServer(
		errorCallback: (Exception) -> Unit,
		hold: Boolean.() -> Unit
	) {
		RequisitionUtil.requestData<String>(
			APIPath.serverStatus,
			"",
			true,
			errorCallback,
			isEncrypt = true
		) {
			hold(TinyNumberUtils.isTrue(JSONObject(this[0]).safeGet("inuse")))
		}
	}
}