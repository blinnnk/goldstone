package io.goldstone.blockchain.kernel.network.common

import com.blinnnk.extension.isNull
import com.blinnnk.extension.safeGet
import com.blinnnk.util.TinyNumberUtils
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.utils.GoldStoneWebSocket
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
	fun checkBackupStatusByException(error: GoldStoneError) {
		if (
			error.message.contains("timeout")
			|| error.message.contains("StringIndexOutOfBoundsException")
			|| error.message.contains("java.lang.Exception")
		) {
			checkWhetherNeedToSwitchToBackupServer { needSwitch, switchError ->
				if (!needSwitch.isNull() && switchError.isNone()) {
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
		hold: (needSwitch: Boolean?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			APIPath.serverStatus,
			"",
			true,
			isEncrypt = true
		) { result, error ->
			if (!result.isNull() && error.isNone()) {
				hold(TinyNumberUtils.isTrue(JSONObject(result!!.firstOrNull()).safeGet("inuse")), error)
			} else hold(null, error)
		}
	}
}