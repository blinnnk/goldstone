package io.goldstone.blockchain.module.home.quotation.quotationmanagement.event


/**
 * @author KaySaith
 * @date  2018/11/20
 */
/**
 *  Pair Update Event 是在搜索 Pair 后返回到 Pair 管理界面
 *  采用的 Event 通知更新的场景
 */
data class PairUpdateEvent(val needUpdate: Boolean)

/**
 * Quotation Update Event 是在 Pair 管理界面增删 Pair 后
 * 在行情列表里面的通知更新方法
 */
data class QuotationUpdateEvent(val needUpdate: Boolean)