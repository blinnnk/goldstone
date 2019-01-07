package io.goldstone.blinnnk.module.common.tokendetail.tokendetail.event


/**
 * @author KaySaith
 * @date  2018/11/20
 */

/**
 * 转账监听到足够的块并确认不可逆后, 需要通过这个 Event Bus
 * 更新 `TokenDetail` 列面 里面账单的 `Pending` 状态
 */
data class TokenDetailEvent(val hasConfirmed: Boolean)

/**
 * 这个是当 `ParentFragment` 隐藏或恢复的时候控制 `Filter Button` 的
 * 事件显示的通知
 */
data class FilterButtonDisplayEvent(val status: Boolean)
