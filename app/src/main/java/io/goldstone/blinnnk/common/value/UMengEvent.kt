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
			// 通用_点击导入观察钱包按钮
			const val importWatchWallet = "common_click_importWatchWallet"
			// 通用_点击导入钱包按钮
			const val importWallet = "common_click_importWallet"
			// 通用_点击创建钱包按钮
			const val createWallet = "common_click_createWallet"
			// 通用_点击收款
			const val send = "common_click_send"
			// 通用_点击转账
			const val deposit = "common_click_deposit"
			// 通用_点击第三方浏览器查看详情
			const val thirdPartyCheckDetail = "common_click_thirdPartyCheckDetail"
			// 通用_点击复制地址
			const val copyAddress = "common_click_copyAddress"
			// 通用_点击保存二维码到相册
			const val saveQRToAlbum = "common_click_saveQRToAlbum"
			// 通用_点击分享
			const val share = "common_click_share"
		}

		object Wallet {
			// 钱包_点击圆形钱包按钮
			const val walletButton = "wallet_click_walletButton"
			// 钱包_点击添加token按钮
			const val addTokenButton = "wallet_click_addTokenButton"
			// 钱包_点击通知按钮
			const val notificationButton = "wallet_click_notificationButton"
			// 钱包_点击token条目
			const val tokenCell = "wallet_click_tokenCell"
		}

		object WalletManage {
			// 钱包列表_点击钱包卡片
			const val walletCard = "walletList_click_walletCard"
			// 钱包列表_点击添加钱包
			const val add = "walletList_click_add"
		}

		object WalletDetail {
			// 钱包详情_点击查看全部地址
			const val viewAllAddress = "walletDetail_click_viewAllAddress"
			// 钱包详情_点击钱包名称
			const val name = "walletDetail_click_name"
			// 钱包详情_点击修改密码
			const val changePassword = "walletDetail_click_changePassword"
			// 钱包详情_点击删除钱包
			const val delete = "walletDetail_click_delete"
			// 钱包详情_点击密码提示
			const val passwordHint = "walletDetail_click_passwordHint"
			// 钱包详情_点击点击cell
			const val cell = "walletDetail_click_cell"
			// 全部地址_点击更多按钮
			const val more = "allAddress_click_more"
			// 全部地址_点击添加子地址
			const val addSubAddress = "allAddress_click_addSubAddress"
			// 全部地址_点击某链全部子地址
			const val checkAll = "allAddress_click_checkAll"
			// 全部地址更多操作_点击查看二维码
			const val QRCode = "allAddressMore_click_QRCode"
			// 全部地址更多操作_点击导出Keystore
			const val keystore = "allAddressMore_click_keystore"
			// 全部地址更多操作_点击导出私钥
			const val privateKey = "allAddressMore_click_privateKey"
			// 全部地址更多操作_点击BCH转换为Legacy格式
			const val convertToLegacy = "allAddressMore_click_convertToLegacy"
			// 全部地址更多操作_点击设置为默认地址
			const val setAsDefault = "allAddressMore_click_setAsDefault"
		}

		object TokenManage {
			// Token管理_点击显示关闭我的Token
			const val switchButton = "tokenManage_click_switchButton"
			// Token管理_点击搜索token输入框
			const val searchInput = "tokenManage_click_searchInput"
		}

		object TokenDetail {
			// Token详情_点击第三方浏览器查看详情
			const val thirdPartyCheckDetail = "tokenDetail_click_thirdPartyCheckDetail"
			// Token详情_点击复制地址
			const val toCopyAddress = "tokenDetail_click_toCopyAddress"
			// Token详情_点击顶部tabBar
			const val tabBar = "tokenDetail_click_tabBar"
			// Token详情_点击转账条目
			const val transactionCell = "tokenDetail_click_transactionCell"
			// Token详情_点击发送按钮
			const val send = "tokenDetail_click_send"
			// Token详情_点击接收按钮
			const val deposit = "tokenDetail_click_deposit"
			// Token详情_点击筛选按钮
			const val filter = "tokenDetail_click_filter"
			// Token详情_点击筛选弹窗按钮
			const val filterAlertButton = "tokenDetail_click_filterAlertButton"
			// Token详情_点击EOS工具按钮
			const val eosTools = "tokenDetail_click_eosTools"
			// Token详情_点击EOS代理带宽cell
			const val eosDelegateBandwidth = "tokenDetail_click_eosDelegateBandwidth"
			// Token详情_点击EOS资源进度条
			const val eosResourceProcessBar = "tokenDetail_click_eosResourceProcessBar"
			// Token详情_点击EOS资源注册
			const val eosAccountRegister = "tokenDetail_click_eosAccountRegister"
			// Token详情_点击EOS公钥账号列表
			const val eosAccountList = "tokenDetail_click_eosAccountList"
		}

		object EOSRNoAccount {
			// 无EOS账户_点击复制公钥
			const val copyPublicKey = "noEosAccount_click_copyPublicKey"
			// 无EOS账户_点击合约创建
			const val viaContract = "noEosAccount_click_viaContract"
			// 无EOS账户_点击好友创建
			const val viaFriend = "noEosAccount_click_viaFriend"
		}

		object EOSViaContractGuide {
			// EOS合约创建账号引导_点击检查账号名是否可用
			const val checkName = "viaContractGuide_click_checkName"
			// EOS合约创建账号引导_点击复制备注
			const val copyMemo = "viaContractGuide_click_copyMemo"
			// EOS合约创建账号引导_点击复制合约账号
			const val copyContractName = "viaContractGuide_click_copyContractName"
		}

		object EOSViaFriendGuide {
			// EOS好友创建账号引导_点击检查账号名是否可用
			const val checkName = "viaFriendGuide_click_checkName"
			// EOS好友创建账号引导_点击复制备注
			const val copyMemo = "viaFriendGuide_click_copyMemo"
		}

		object EOSRegister {
			// EOS注册账号_点击高级设置
			const val advancedSettings = "eosRegister_click_advancedSettings"
			// EOS注册账号_点击确认
			const val confirm = "eosRegister_click_confirm"
		}

		object EOSResource {
			// EOS带宽列表_点击带宽条目
			const val bandwidthCell = "eosDelegateList_click_bandwidthCell"
			// EOS带宽列表_点击确认赎回
			const val confirmRefund = "eosDelegateList_click_confirmRefund"
			// EOS资源交易_点击买入确认按钮
			const val buyConfirmButton = "eosResourceTrade_click_buyConfirmButton"
			// EOS资源交易_点击卖出确认按钮
			const val sellConfirmButton = "eosResourceTrade_click_sellConfirmButton"
			// EOS资源交易_点击通讯录按钮
			const val contactButton = "eosResourceTrade_click_contactButton"
		}

		object EOSAccountList {
			// EOS账号列表_点击账号cell
			const val accountCell = "eosAccountList_click_accountCell"
			// EOS账号列表_点击确认按钮
			const val confirm = "eosAccountList_click_confirm"
		}

		object EOSPermission {
			// EOS权限管理_点击删除权限
			const val edit = "eosPermissionList_click_edit"
			// EOS权限管理_点击编辑权限
			const val delet = "eosPermissionList_click_delet"
			// EOS权限管理_点击新增权限
			const val add = "eosPermissionList_click_add"
			// EOS权限管理_点击点击权限cell
			const val permissionCell = "eosPermissionList_click_permissionCell"
			// EOS权限编辑弹窗_点击按钮
			const val confirmEidt = "eosPermissionAddWindow_click_confirmEidt"
			// EOS权限编辑弹窗_点击角色单选按钮
			const val switchType = "eosPermissionAddWindow_click_switchType"
		}

		object DappCenter {
			// Dapp中心_点击Dapp
			const val dapp = "dappCenter_click_dapp"
			// Dapp中心_点击查看全部推荐
			const val allRecommendation = "dappCenter_click_allRecommendation"
			// Dapp中心_点击查看全部Dapp
			const val allDapps = "dappCenter_click_allDapps"
			// Dapp中心_点击浏览器输入框
			const val browerInput = "dappCenter_click_browerInput"
			// Dapp浏览器_点击悬浮按钮
			const val popMenu = "browerPage_click_popMenu"
		}

		const val dappCenter = "dappCenter"

		object Market {
			// 市场_点击管理交易对按钮
			const val managePair = "market_click_managePair"
			// 市场_点击交易对卡片
			const val pairCard = "market_click_pairCard"
			// 市场_点击排行按钮
			const val rankButton = "market_click_rankButton"
			// 市场排行_点击token条目
			const val tokenItem = "marketCapRank_click_tokenItem"
			// 市场交易对管理_点击搜索输入框
			const val searchInput = "marketPairList_click_searchInput"
			// 市场交易对管理_点击开关
			const val switch = "marketPairList_click_switch"
		}

		object Profile {
			// 设置_点击钱包管理
			const val walletManage = "profile_click_walletManage"
			// 设置_点击节点
			const val chainNode = "profile_click_chainNode"
			// 设置_点击PIN
			const val PIN = "profile_click_PIN"
			// 设置_点击指纹支付
			const val fingerPayment = "profile_click_fingerPayment"
			// 设置_点击货币
			const val currency = "profile_click_currency"
			// 设置_点击语言
			const val language = "profile_click_language"
			// 设置_点击通讯录
			const val contacts = "profile_click_contacts"
			// 设置_点击关于我们
			const val aboutUs = "profile_click_aboutUs"
			// 设置_点击帮助中心
			const val helpCenter = "profile_click_helpCenter"
			// 设置_点击隐私条款
			const val privacyPolicy = "profile_click_privacyPolicy"
			// 设置_点击版本号
			const val version = "profile_click_version"
			// 设置_点击用户协议
			const val agreement = "profile_click_agreement"
			// 主网测试网选择_点击主网测试网选择
			const val mainOrTest = "chainNodeList_click_mainOrTest"
			// 节点_点击节点
			const val nodeItem = "chainNodeList_click_nodeItem"
			// 节点_点击确认切换节点按钮
			const val confirmSwitchNodeButton = "chainNodeList_click_confirmSwitchNodeButton"
			// 货币_点击条目
			const val currencyItem = "currency_click_currencyItem"
			// 语言_点击条目
			const val languageItem = "language_click_languageItem"
			// PIN_点击开关功能按钮
			const val switchPIN = "PIN_click_switchPIN"
			// PIN_点击确认按钮
			const val confirmPIN = "PIN_click_confirmPIN"
			// 指纹支付_点击开关功能按钮
			const val switchFingerPay = "fingerPayment_click_switchFingerPay"
			// 指纹支付_点击确认按钮
			const val confirmFingerPay = "fingerPayment_click_confirmFingerPay"
			// 通讯录_点击添加
			const val addContact = "contacts_click_addContact"
			// 通讯录_点击删除
			const val deleteContact = "contacts_click_deleteContact"
			// 通讯录_点击确认编辑按钮
			const val confirmEditContact = "contacts_click_confirmEditContact"
		}
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
		const val walletDetail = "钱包设置界面"
		const val allAddresses = "钱包所有地址界面"
		const val allAddressesOfSingleChain = "某链所有地址界面"
		const val eosContractRegisterGuide = "EOS合约注册引导"
		const val eosFriendRegisterGuide = "EOS好友注册引导"
		const val eosAcountRegister = "EOS账号注册"
		const val eosBandwidthList = "EOS带宽列表"
		const val eosBandwidthRefund = "EOS带宽赎回"
		const val eosRamTrade = "RAM交易"
		const val eosCpuTrade = "CPU交易"
		const val eosNetTrade = "NET交易"
	}

	fun add(context: Context?, eventName: String, parameter: String = "") {
		MobclickAgent.onEvent(context, eventName, parameter)
	}
}