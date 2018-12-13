package io.goldstone.blockchain.common.component.dragbutton

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.*
import android.widget.*

/**
 * @date: 2018-12-10.
 * @author: yangLiHai
 * @description:
 */
class DragFloatingButton(context: Context): LinearLayout(context) {
  
  var isTouching = false
  var xDistance = 0f // 按下的点距离左侧的距离
  var yDistance = 0f // 按下的点距离上边的距离
  
  val progressView = TextView(context)
  
  init {
    setBackgroundColor(Color.GREEN)
    progressView.apply {
      layoutParams = LayoutParams(600, 600)
      setTextColor(Color.BLACK)
      setTextSize(TypedValue.COMPLEX_UNIT_SP,28f)
      gravity = Gravity.CENTER
      text = "3"
    }
    addView(progressView)
    isClickable = true
  }
  
  @SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent): Boolean {
    if (event.action == MotionEvent.ACTION_DOWN) {
			isTouching = true
      xDistance = event.x
      yDistance = event.y
    } else if (event.action == MotionEvent.ACTION_UP) {
			isTouching = false
    }
    return super.onTouchEvent(event)
  }
  
}
