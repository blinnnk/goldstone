package io.goldstone.blinnnk.common.value

import android.content.Context
import com.umeng.analytics.MobclickAgent


/**
 * @author KaySaith
 * @date  2019/01/07
 */
object UMengEvent {
	object Click {
		object Common {
			const val importWatchWallet = "common_click_importWatchWallet"  //通用_点击导入观察钱包按钮
			const val importWallet = "common_click_importWallet"  //通用_点击导入钱包按钮
			const val createWallet = "common_click_createWallet"  //通用_点击创建钱包按钮
			const val send = "common_click_send"  //通用_点击收款
			const val deposit = "common_click_deposit"  //通用_点击转账
		}

		object Wallet {
			const val walletButton = "wallet_click_walletButton"  //钱包_点击圆形钱包按钮
			const val addTokenButton = "wallet_click_addTokenButton"  //钱包_点击添加token按钮
			const val notificationButton = "wallet_click_notificationButton"  //钱包_点击通知按钮
			const val tokenCell = "wallet_click_tokenCell"  //钱包_点击token条目
		}
		object TokenManage {
			const val switchButton = "tokenManage_click_switchButton"  //Token管理_点击显示/关闭我的Token
			const val searchInput = "tokenManage_click_searchInput"  //Token管理_点击搜索token输入框
		}

		object TokenDetail {
			const val eosTools = "tokenDetail_click_eosTools"  //Token详情_点击EOS工具按钮
			const val thirdPartyCheckDetail = "tokenDetail_click_thirdPartyCheckDetail"  //Token详情_点击第三方浏览器查看详情
			const val toCopyAddress = "tokenDetail_click_toCopyAddress"  //Token详情_点击复制地址
			const val tabBar = "tokenDetail_click_tabBar"  //Token详情_点击顶部tabBar
			const val transactionCell = "tokenDetail_click_transactionCell"  //Token详情_点击转账条目
			const val send = "tokenDetail_click_send"  //Token详情_点击发送按钮
			const val deposit = "tokenDetail_click_deposit"  //Token详情_点击接收按钮
			const val filter = "tokenDetail_click_filter"  //Token详情_点击筛选按钮
			const val eosDelegateBandwidth = "tokenDetail_click_eosDelegateBandwidth"  //Token详情_点击EOS代理带宽cell
			const val eosResourceProcessBar = "tokenDetail_click_eosResourceProcessBar"  //Token详情_点击EOS资源进度条
			const val eosAccountRegister = "tokenDetail_click_eosAccountRegister"  //Token详情_点击EOS资源注册
			const val filterAlertButton = "tokenDetail_click_filterAlertButton"  //Token详情_点击筛选弹窗按钮
			const val eosAccountList = "tokenDetail_click_eosAccountList"  //Token详情_点击EOS公钥账号列表
		}

		object DappCenter {
			const val dapp = "dappCenter_click_dapp"  //Dapp中心_点击Dapp
			const val allRecommendation = "dappCenter_click_allRecommendation"  //Dapp中心_点击查看全部推荐
			const val allDapps = "dappCenter_click_allDapps"  //Dapp中心_点击查看全部Dapp
			const val browerInput = "dappCenter_click_browerInput"  //Dapp中心_点击浏览器输入框
			const val popMenu = "browerPage_click_popMenu"  //Dapp浏览器_点击悬浮按钮
		}

		const val dappCenter = "dappCenter"
	}

	object Page {
		const val launchPage = "启动首页"
		const val tokenTransactionList = "token账单列表"
		const val tokenAssetDetail = "token资产详情"
		const val wallet = "钱包界面"
		const val market = "市场界面"
		const val dappCenter = "Dapp中心"
		const val settings = "设置"
		const val notifications = "通知中心"
		const val transactionDetail = "账单详情"
		const val sendAddress = "填写发送地址界面"
		const val sendAmount = "填写发送金额界面"
		const val sendMeta = "填写发送备注界面"
		const val gasCustom = "燃气自定义"
		const val gasSelection = "燃气选项列表"
		const val currencySettings = "货币设置"
		const val contactList = "通讯录列表"
		const val contactDetail = "通讯录详情"
		const val languageSettings = "语言设置"
		const val pairList = "交易对管理"
		const val pairSearch = "交易对搜索"
		const val marketFilter = "交易对搜索的市场筛选"
		const val tokenList = "token管理"
		const val tokenSearch = "token搜索"
		const val quotationDetail = "交易对详情"
		const val marketCapRank = "市值排行"
		const val addWalletWindow = "新增钱包弹窗"
	}

	fun add(context: Context?, eventName: String, parameter: String = "") {
		MobclickAgent.onEvent(context, eventName, parameter)
	}
}