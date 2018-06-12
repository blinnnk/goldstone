package io.goldstone.blockchain.kernel.network

import com.blinnnk.extension.isTrue
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.utils.GoldStoneWebSocket
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.WebUrl
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TinyNumber
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
		) {
			checkWhetherNeedToSwitchToBackupServer(
				{
					// Error Callback
					LogUtil.error("CheckWhetherNeedToSwitchToBackupServer", it)
				}
			) {
				isTrue {
					System.out.println("hello2")
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
		System.out.println("hello3")
		RequisitionUtil.requestData<String>(
			APIPath.serverStatus,
			"",
			true,
			errorCallback = errorCallback
		) {
			System.out.println("hello4 ___ $this")
			JSONObject(this[0]).safeGet("inuse").toIntOrNull()?.let {
				hold(it == TinyNumber.True.value)
				System.out.println("hello5 $it")
			}
		}
	}
}