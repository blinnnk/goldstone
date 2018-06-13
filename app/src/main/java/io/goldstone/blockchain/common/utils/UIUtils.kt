package io.goldstone.blockchain.common.utils

import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.format.DateUtils
import com.blinnnk.uikit.ScreenSize
import io.goldstone.blockchain.R.drawable.*
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WalletNameText
import io.goldstone.blockchain.kernel.network.GoldStoneAPI

/**
 * @date 21/03/2018 9:07 PM
 * @author KaySaith
 */
object UIUtils {
	
	// easy to set gradient color
	fun setGradientColor(
		startColor: Int,
		endColor: Int,
		width: Float = ScreenSize.Width.toFloat(),
		height: Float = ScreenSize.Height.toFloat()
	) =
		LinearGradient(
			0f, 0f, width, height, startColor, endColor, Shader.TileMode.CLAMP
		)
	
	fun subtractThenHalf(
		first: Int,
		second: Int
	) = (first - second) / 2
	
	fun generateAvatar(id: Int): Int {
		val avatars = arrayListOf(
			avatar_1, avatar_2, avatar_3, avatar_4, avatar_5, avatar_6, avatar_7, avatar_8, avatar_9,
			avatar_10, avatar_11, avatar_12, avatar_13, avatar_14, avatar_15, avatar_16, avatar_17
		)
		val finalID = if (id > 0) id - 1 else id
		return avatars[finalID % 17]
	}
	
	fun generateDefaultName(): String {
		val name = arrayListOf(
			WalletNameText.Owl,
			WalletNameText.Bear,
			WalletNameText.Elephant,
			WalletNameText.Rhinoceros,
			WalletNameText.Frog,
			WalletNameText.Koala,
			WalletNameText.Fox,
			WalletNameText.Monkey,
			WalletNameText.Giraffle,
			WalletNameText.Penguin,
			WalletNameText.Wolf,
			WalletNameText.Bull,
			WalletNameText.Leopard,
			WalletNameText.Deer,
			WalletNameText.Raccoon,
			WalletNameText.Lion,
			WalletNameText.Hippo
		)
		val walletID =
			if (Config.getMaxWalletID() == 100) 0
			else Config.getMaxWalletID()
		return name[(walletID) % 17]
	}
}

object TimeUtils {
	// 将时间戳转化为界面显示的时间格式的工具
	fun formatDate(timeStamp: String): String {
		val stamp = if (timeStamp.contains(".")) timeStamp.substringBefore(".").toLong()
		else timeStamp.toLong()
		return DateUtils.formatDateTime(
			GoldStoneAPI.context, stamp * 1000, DateUtils.FORMAT_SHOW_YEAR
		) + " " + DateUtils.formatDateTime(
			GoldStoneAPI.context, stamp * 1000, DateUtils.FORMAT_SHOW_TIME
		)
	}
	
	// 将时间戳转化为界面显示的时间格式的工具
	fun formatDate(timeStamp: Long): String {
		return DateUtils.formatDateTime(
			GoldStoneAPI.context, timeStamp * 1000, DateUtils.FORMAT_SHOW_YEAR
		) + " " + DateUtils.formatDateTime(
			GoldStoneAPI.context, timeStamp * 1000, DateUtils.FORMAT_SHOW_TIME
		)
	}
}