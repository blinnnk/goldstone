package io.goldstone.blockchain.module.home.dapp.dapplist.event


/**
 * @author KaySaith
 * @date  2018/12/10
 */
/**
 *  `DAPPListDisplayEvent` 在这个页面打开 `DAPPBrowser`
 *  再在 `DAPPBrowser` 关闭界面返回后的 `栈` 显示控制
 */
data class DAPPListDisplayEvent(val isShown: Boolean)