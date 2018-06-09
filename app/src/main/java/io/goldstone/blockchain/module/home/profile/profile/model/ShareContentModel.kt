package io.goldstone.blockchain.module.home.profile.profile.model

import com.blinnnk.extension.safeGet
import org.json.JSONObject

/**
 * @date 2018/6/9 7:55 PM
 * @author KaySaith
 */
data class ShareContentModel(
	val title: String,
	val content: String,
	val url: String
) {
	
	constructor(data: JSONObject) : this(
		data.safeGet("title"),
		data.safeGet("content"),
		data.safeGet("url")
	)
}