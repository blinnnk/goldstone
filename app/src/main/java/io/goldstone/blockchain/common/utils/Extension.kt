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
import android.view.WindowManager
import android.widget.TextView
import com.blinnnk.animation.fallOut
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick

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

fun <T: View> T.into(parent: ViewGroup) {
  parent.addView(this)
}

fun <T: TextView> T.setTitle(title: SpannableString): T {
  text = title
  return this
}

fun <T: View> T.isHidden(): T {
  this.visibility = View.GONE
  return this
}

fun <T: View> T.click(callback: (T) -> Unit): T {
  onClick { callback(this@click) }
  return this
}

fun View.addTopLRCorner(radius: Float, backgroundColor: Int) {
  val shape = GradientDrawable().apply {
    cornerRadii = floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f)
    shape = GradientDrawable.RECTANGLE
    setSize(matchParent, matchParent)
    setColor(backgroundColor)
  }
  backgroundDrawable = shape
}

/**
 * 有虚拟操作栏的手机 例如 `SamSung S8, S9` 无法通过常规方法获取真是的屏幕高度
 * 这里增加两个方法用来获取真是屏幕高度
 */

fun Context.getRealScreenHeight(): Int {
  val displaySize = Point()
  (this as? Activity)?.windowManager?.defaultDisplay?.getRealSize(displaySize)
  return displaySize.y
}

fun Context.getScreenHeightWithoutStatusBar(): Int {
  val displaySize = Point()
  (this as? Activity)?.windowManager?.defaultDisplay?.getRealSize(displaySize)
  return displaySize.y - ScreenSize.statusBarHeight
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

fun Activity.hideStatusBar() {
  //取消状态栏
  window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

/**
 * @description  使用中方便的为 `View` 设置 `LayoutParams` 并且返回一个类型明确的 `LayoutParams`
 * 通过 `Lambda` 对齐进一步操作.
 */

inline fun<T: ViewGroup.MarginLayoutParams> View.setMargins(block: T.() -> Unit) {
  (layoutParams as? T).let {
    if (it != null) {
      block(it)
    } else {
      return
    }
  }
}

/**
 * @description 删除 `Son` 级别的 `Fragment`
 */

inline fun<T: Fragment> T.removeChildFragment(fragment: Fragment, callback: () -> Unit = {}) {
  childFragmentManager.beginTransaction().remove(fragment).commit()
  callback()
}

fun<T: Fragment> T.hideChildFragment(fragment: Fragment) {
  childFragmentManager.beginTransaction().hide(fragment).commit()
}

fun<T: Fragment> T.showChildFragment(fragment: Fragment) {
  childFragmentManager.beginTransaction().show(fragment).commit()
}