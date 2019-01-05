package io.goldstone.blinnnk.module.home.wallet.tokenmanagement.tokenmanagementlist.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.safeGet
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/11/15
 * @Description
 * 按照约定的规则拆分数据
 * Multiple data split with `,` Single data split with `|`
 * [0] `Name` [1] `Icon Url` [2] `Link Url`
 *
 */
data class SocialMediaModel(
	val name: String,
	val iconURL: String,
	val url: String
) : Serializable {
	constructor(content: String) : this(
		content.split("|").firstOrNull().orEmpty(),
		content.split("|").safeGet(1).orEmpty(),
		content.split("|").safeGet(2).orEmpty()
	)

	fun generateObject(): String {
		return "$name|$iconURL|$url"
	}

	companion object {
		fun generateList(content: String): List<SocialMediaModel> {
			return when {
				content.isEmpty() -> listOf()
				content.contains(",") -> content.split(",").map { SocialMediaModel(it) }
				else -> listOf(SocialMediaModel(content))
			}
		}
	}
}

class SocialMediaConverter {
	@TypeConverter
	fun revertString(content: String): List<SocialMediaModel> {
		return SocialMediaModel.generateList(content)
	}

	@TypeConverter
	fun convertListString(content: List<SocialMediaModel>): String {
		var stringContent = ""
		content.forEach {
			stringContent += "${it.generateObject()},"
		}
		return if (stringContent.isEmpty()) stringContent
		else stringContent.substringBeforeLast(",")
	}
}