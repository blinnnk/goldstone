package io.goldstone.blockchain.common.component.dragbutton

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.*
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import io.goldstone.blockchain.common.utils.click
import org.jetbrains.anko.wrapContent


/**
 * @date: 2018-12-10.
 * @author: yangLiHai
 * @description:
 */
class DragFloatingLayout(context: Context): RelativeLayout(context) {
	private val viewWidth = ScreenSize.Width
	private val viewHeight = ScreenSize.Height
	private val mainButton = DragFloatingButton(context)
	private val childLayout = RelativeLayout(context)
	private val buttonWidth = 600
	private val framePadding = 20 // floatingButton 距离边距的距离
	private val scaleRatio =  0.4f // 缩放比率（最终要缩放到的原比例）
	private val childWidth = 200
	private var childStatus = FloatingButtonStatus.CENTER
	private var animatorDuration: Long = 500
	private var eventDownX = 0
	private var eventDownY = 0
	
	private var clickLimitDistance = 10  // 判定是否处于moving状态，moving状态不可展示子view
	
	init {
		mainButton.layoutParams = RelativeLayout.LayoutParams(buttonWidth, buttonWidth)
		mainButton.setMargins<LayoutParams> {
			leftMargin = (viewWidth - buttonWidth) / 2
			topMargin = (viewHeight - buttonWidth) / 2
		}
		childLayout.visibility = View.GONE
		childLayout.layoutParams = LayoutParams(wrapContent, wrapContent)
		addView(childLayout)
		addView(mainButton)
		mainButton.click {
			showChildFloatingButton()
		}
		
	}
	
	override fun dispatchTouchEvent(event: MotionEvent): Boolean {
		if (event.action == MotionEvent.ACTION_DOWN) {
			eventDownX = event.x.toInt()
			eventDownY = event.y.toInt()
		}
		if (mainButton.isTouching && childLayout.visibility == View.GONE) {
			if (event.action == MotionEvent.ACTION_MOVE) {
				val distanceX = eventDownX - event.x.toInt()
				val distanceY = eventDownY - event.y.toInt()
				if (Math.abs(distanceX) > clickLimitDistance || Math.abs(distanceY) > clickLimitDistance) {
					childStatus = FloatingButtonStatus.MOVING
				}
				(mainButton.layoutParams as? RelativeLayout.LayoutParams)?.apply {
					val leftMargin = (event.x - mainButton.xDistance).toInt()
					val topMargin = (event.y - mainButton.yDistance).toInt()
					setMargins(leftMargin, topMargin, 0, 0)
					mainButton.layoutParams = this
				}
			} else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
				animateAfterTouchUp(event)
			}
		}
		return super.dispatchTouchEvent(event)
	}
	
	fun updateProgressText(text: String, isFinish: Boolean) {
		mainButton.progressView.text = text
		if (isFinish) {
			doEndAnimator()
		}
	}
	
	private fun animateAfterTouchUp(event: MotionEvent) {
		val startX = (mainButton.layoutParams as? RelativeLayout.LayoutParams)?.leftMargin ?: 0
		val endX =  if (event.x > this@DragFloatingLayout.width / 2) {
			this@DragFloatingLayout.width - mainButton.layoutParams.width - framePadding
		} else {
			framePadding
		}
		
		val animator = ValueAnimator.ofInt(startX, endX)
		animator.duration = animatorDuration
		animator.interpolator = OvershootInterpolator()
		animator.addUpdateListener {
			(mainButton.layoutParams as? RelativeLayout.LayoutParams)?.apply {
				leftMargin = it.animatedValue as Int
				mainButton.layoutParams = this
			}
		}
		
		animator.addListener(object : Animator.AnimatorListener {
			override fun onAnimationRepeat(animation: Animator?) {}
			override fun onAnimationCancel(animation: Animator?) {}
			override fun onAnimationStart(animation: Animator?) {}
			override fun onAnimationEnd(animation: Animator?) {
				childStatus = if (endX > this@DragFloatingLayout.width / 2 ) FloatingButtonStatus.RIGHT else FloatingButtonStatus.LEFT
			}
		})
		(mainButton.layoutParams as? RelativeLayout.LayoutParams)?.topMargin?.let {
			if (it < framePadding || it > viewHeight - mainButton.layoutParams.width - framePadding)  {
				val yAnimator = ValueAnimator.ofInt(it, if (it < framePadding) framePadding else viewHeight - mainButton.layoutParams.width - framePadding)
				yAnimator.duration = animatorDuration
				yAnimator.addUpdateListener { value ->
					(mainButton.layoutParams as? RelativeLayout.LayoutParams)?.apply {
						setMargins(leftMargin, value.animatedValue as Int, 0, 0)
						mainButton.layoutParams = this
					}
				}
				yAnimator.start()
			}
		}
		animator.start()
	}
	
	fun doEndAnimator() {
		val startLeft = (viewWidth - buttonWidth) / 2f - framePadding // 开始的x坐标
		val startTop =( viewHeight - buttonWidth) / 2f // 开始的y坐标
		val translateY = viewHeight - framePadding - buttonWidth * scaleRatio - startTop // 需要位移的y值
		val animator = ValueAnimator.ofFloat(1f, 0f)
		animator.duration = animatorDuration
		animator.interpolator = DecelerateInterpolator()
		animator.addUpdateListener {
			(mainButton.layoutParams as? RelativeLayout.LayoutParams)?.apply {
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
				mainButton.layoutParams = this
				mainButton.progressView.layoutParams.let { progressParams ->
					progressParams.width = this.width
					progressParams.height = this.height
					mainButton.progressView.layoutParams = progressParams
				}
			}
		}
		animator.start()
		childStatus = FloatingButtonStatus.LEFT
		
	}
	
	fun addChildFloatingButton(text: String, hold: TextView.() -> Unit) {
		childLayout.addView(TextView(context).apply {
			this.text = text
			setTextColor(Color.BLACK)
			setTextSize(TypedValue.COMPLEX_UNIT_SP,28f)
			layoutParams = LayoutParams(childWidth, (buttonWidth * scaleRatio).toInt())
			gravity = Gravity.CENTER
			setSingleLine(true)
			setBackgroundColor(if (childLayout.childCount % 2 == 0) Color.RED else Color.BLUE)
			hold()
		})
	}
	
	private fun showChildFloatingButton() {
		if (childStatus == FloatingButtonStatus.CENTER
			|| childStatus == FloatingButtonStatus.MOVING
			|| childLayout.childCount == 0
			|| childLayout.visibility == View.VISIBLE) {
			childLayout.visibility = View.GONE
			return
		}
		// 做动画之前先要让他们全部隐藏
		val hideMargin = when (childStatus) {
			FloatingButtonStatus.LEFT -> -childWidth
			FloatingButtonStatus.RIGHT -> childWidth * childLayout.childCount
			else -> 0
		}
		for (index in 0 until childLayout.childCount) {
			childLayout.getChildAt(index)?.let {
				(it.layoutParams as? RelativeLayout.LayoutParams)?.apply {
					setMargins(hideMargin, 0, 0, 0)
					it.layoutParams = this
				}
			}
		}
		childLayout.visibility = View.VISIBLE
		var finallyLeftMargin = 0
		if (childStatus == FloatingButtonStatus.LEFT) {
			finallyLeftMargin = framePadding + mainButton.width
		} else if (childStatus == FloatingButtonStatus.RIGHT) {
			finallyLeftMargin = viewWidth - framePadding - mainButton.width - childWidth * childLayout.childCount
		}
		(childLayout.layoutParams as? RelativeLayout.LayoutParams)?.apply {
			width = childWidth * childLayout.childCount
			setMargins(finallyLeftMargin, mainButton.y.toInt(), 0, 0)
			childLayout.layoutParams = this
		}
		
		val startX = when (childStatus) {
			FloatingButtonStatus.LEFT -> -childWidth
			FloatingButtonStatus.RIGHT -> childWidth * childLayout.childCount
			else -> 0
		}
		
		for (index in 0 until childLayout.childCount) {
			val endX = childWidth * index
			val child = childLayout.getChildAt(index)
			val animator = ValueAnimator.ofInt(startX, endX)
			animator.duration = animatorDuration + index * 100
			animator.interpolator = OvershootInterpolator()
			animator.addUpdateListener {
				(child?.layoutParams as? RelativeLayout.LayoutParams)?.apply {
					val leftMargin = it.animatedValue as Int
					setMargins(leftMargin, 0, 0, 0)
					child.layoutParams = this
				}
			}
			animator.start()
		}
		
	}
	
	private enum class FloatingButtonStatus {
		CENTER, LEFT, RIGHT, MOVING
	}
	
}







