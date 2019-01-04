package io.goldstone.blockchain.common.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.goldstone.blockchain.GoldStoneApp

/**
 * @date 22/03/2018 12:32 AM
 * @author KaySaith
 */

private const val fadeDuration = 200

fun <T> ImageView.glideImage(imagePath: T?) {
	// 这里的 `Context` 应该用 `Application` 的 `Context`, 不然在多线程下的 Context
	// 可能会丢失 `Context`
	Glide
		.with(GoldStoneApp.appContext.applicationContext)
		.load(imagePath)
		.transition(DrawableTransitionOptions().crossFade(fadeDuration))
		.into(this)
}