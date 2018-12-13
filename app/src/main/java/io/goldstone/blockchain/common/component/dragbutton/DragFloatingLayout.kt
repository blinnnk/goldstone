package io.goldstone.blockchain.common.component.dragbutton

import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.RelativeLayout
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize


/**
 * @date: 2018-12-10.
 * @author: yangLiHai
 * @description:
 */
class DragFloatingLayout(context: Context): RelativeLayout(context) {
  private val viewWidth = ScreenSize.Width
  private val viewHeight = ScreenSize.Height
  
  private val floatingButton = DragFloatingButton(context)
  private val buttonWidth = 600
  private val framePadding = 20 // floatingButton 距离边距的距离
  private val scaleRatio =  0.6f // 缩放比率（最终要缩放到的原比例）
	private val animationDuration: Long = 500 // 动画执行的时间
  
  init {
    floatingButton.layoutParams = RelativeLayout.LayoutParams(buttonWidth, buttonWidth)
		floatingButton.setMargins<RelativeLayout.LayoutParams> {
			leftMargin = (viewWidth - buttonWidth) / 2
			topMargin = (viewHeight - buttonWidth) / 2
		}
    addView(floatingButton)
    addView(Button(context).apply {
      setOnClickListener { postProgress() }
    })
  }
  
  override fun dispatchTouchEvent(event: MotionEvent): Boolean {
    if (floatingButton.isTouching) {
      if (event.action == MotionEvent.ACTION_MOVE) {
        (floatingButton.layoutParams as? RelativeLayout.LayoutParams)?.apply {
          val leftMargin = (event.x - floatingButton.xDistance).toInt()
          val topMargin = (event.y - floatingButton.yDistance).toInt()
          setMargins(leftMargin, topMargin, 0, 0)
          floatingButton.layoutParams = this
        }
      } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
        
        val startX = (floatingButton.layoutParams as? RelativeLayout.LayoutParams)?.leftMargin ?: 0
        val endX =  if (event.x > this@DragFloatingLayout.width / 2) {
          this@DragFloatingLayout.width - floatingButton.layoutParams.width - framePadding
        } else {
          framePadding
        }
        
        val animator = ValueAnimator.ofInt(startX, endX)
        animator.duration = animationDuration
        animator.interpolator = OvershootInterpolator()
        animator.addUpdateListener {
          (floatingButton.layoutParams as? RelativeLayout.LayoutParams)?.apply {
            setMargins( it.animatedValue as Int, topMargin, 0, 0)
            floatingButton.layoutParams = this
          }
        }
        (floatingButton.layoutParams as? RelativeLayout.LayoutParams)?.topMargin?.let {
          if (it < framePadding || it > viewHeight - floatingButton.layoutParams.width - framePadding)  {
						// 如果超过上下的边距
            val yAnimator = ValueAnimator.ofInt(it, if (it < framePadding) framePadding else viewHeight - floatingButton.layoutParams.width - framePadding)
            yAnimator.duration = animationDuration
            yAnimator.addUpdateListener { value ->
              (floatingButton.layoutParams as? RelativeLayout.LayoutParams)?.apply {
                setMargins(leftMargin, value.animatedValue as Int, 0, 0)
                floatingButton.layoutParams = this
              }
            }
            yAnimator.start()
          }
        }
        animator.start()
      }
    }
    return super.dispatchTouchEvent(event)
  }
  
  private var progress = 3
  private val progressHandler = Handler()
  private val progressRunnable = Runnable {
    progress --
    floatingButton.progressView.text = "$progress"
    postProgress()
  }
  
  private fun doProgressAnimator() {
    
    val startLeft = (viewWidth - buttonWidth) / 2f - framePadding // 开始的x坐标
    val startTop =( viewHeight - buttonWidth) / 2f // 开始的y坐标
    val translateY = viewHeight - framePadding - buttonWidth * scaleRatio - startTop // 需要位移的y值
    val animator = ValueAnimator.ofFloat(1f, 0f)
    animator.duration = animationDuration
    animator.interpolator = DecelerateInterpolator()
    animator.addUpdateListener {
      (floatingButton.layoutParams as? RelativeLayout.LayoutParams)?.apply {
        val ratio = it.animatedValue as Float
        setMargins(
          (framePadding + startLeft * ratio).toInt(),
          (startTop + translateY * (1 - ratio)).toInt(),
          0,
          0
        )
        val animateWidth = ((1 - scaleRatio) * ratio + scaleRatio) * buttonWidth
        this.width = animateWidth.toInt()
        this.height = animateWidth.toInt()
        floatingButton.layoutParams = this
        floatingButton.progressView.layoutParams.let { progressViewParams ->
					progressViewParams.width = this.width
					progressViewParams.height = this.height
          floatingButton.progressView.layoutParams = progressViewParams
        }
      }
    }
    animator.start()
		progress = 3
    
  }
  
	// 执行倒计时(给外部调用的)
	fun postProgress() {
    if (progress > 0) {
      progressHandler.postDelayed(progressRunnable, 1000)
    } else {
      doProgressAnimator()
    }
  }
  
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    progressHandler.removeCallbacksAndMessages(null)
  }
}







