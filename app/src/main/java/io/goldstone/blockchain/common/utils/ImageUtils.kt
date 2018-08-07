package io.goldstone.blockchain.common.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.goldstone.blockchain.kernel.network.GoldStoneAPI

/**
 * @date 22/03/2018 12:32 AM
 * @author KaySaith
 */

private const val fadeDuration = 200

fun <T> ImageView.glideImage(imagePath: T?) {
	Glide
		.with(context)
		.load(imagePath)
		.transition(DrawableTransitionOptions().crossFade(fadeDuration))
		.into(this)
}
