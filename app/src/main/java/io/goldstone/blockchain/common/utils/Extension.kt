@file:Suppress("UNCHECKED_CAST")

package io.goldstone.blockchain.common.utils

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.drawable.GradientDrawable
import android.support.v4.app.Fragment
import android.text.SpannableString
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.blinnnk.animation.fallOut
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import com.blinnnk.uikit.ScreenSize

/**
 * @date 21/03/2018 11:12 PM
 * @author KaySaith
 */

/**
 * `View` 的便捷链式调用的方法
 * */

fun <T: View> T.assignWidth(width: Int): T {
  layoutParams.width = width
  return this
}

fun <T: View> T.assignHeight(height: Int): T {
  layoutParams.height = height
  return this
}

fun <T: View> T.click(callback: (T) -> Unit): T {
  onClick { callback(this@click) }
  return this
}


/**
 * @description
 * 这个函数是用来安全移除黑蜜的主题 `ContentOverlay` 的。
 * 因为黑蜜的设计大多数的内容场景都是在悬浮层上实现的。所以有自己特殊的构造结构
 * 详情见 [visioncore.heyhoney.common.base.overlay.HoneyContentOverlay]
 * 为此从 `Activity` 写在 `Fragment` 的时候要先要实现对应 `View` 上的动画.
 * 为此设置了这个便捷的函数.
 * @param [animationView] 需要做动画移除的 `subView`
 */

fun Fragment.removeSelfWithAnimation(animationView: ViewGroup?, callback: () -> Unit = { }) {
  activity?.let {
    animationView?.fallOut {
      it.supportFragmentManager.beginTransaction().remove(this).commit()
      callback()
    }
  }
}

