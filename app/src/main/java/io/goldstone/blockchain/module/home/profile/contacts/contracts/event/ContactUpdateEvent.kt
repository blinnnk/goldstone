package io.goldstone.blockchain.module.home.profile.contacts.contracts.event


/**
 * @author KaySaith
 * @date  2018/11/20
 */

/**
 * 新增或编辑完毕通讯录后, 通知这个界面刷新数据
 */
data class ContactUpdateEvent(val hasChanged: Boolean)