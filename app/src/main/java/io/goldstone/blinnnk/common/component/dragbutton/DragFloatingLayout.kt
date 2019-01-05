package io.goldstone.blinnnk.common.component.dragbutton

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.view.*
import android.view.animation.OvershootInterpolator
import android.widget.RelativeLayout
import com.blinnnk.animation.scale
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.*
import com.blinnnk.uikit.ScreenSize
import io.goldstone.blinnnk.common.utils.click
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick


/**
 * @date: 2018-12-10.
 * @author: yangLiHai
 * @description:
 */
class DragFloatingLayout(context: Context) : RelativeLayout(context) {
	private val viewWidth = ScreenSize.Width
	private var viewHeight = ScreenSize.Height + ScreenSize.statusBarHeight
	private val mainButton = DragFloatingButton(context)
	private val childLayout = RelativeLayout(context)
	private val buttonWidth = 60.uiPX()
	private val framePadding = 20.uiPX() // floatingButton 距离边距的距离
	private var mainButtonStatus = FloatingButtonStatus.LEFT
	private var animatorDuration: Long = 500
	private var eventDownX = 0
	private var eventDownY = 0
	private var clickLimitDistance = 10.uiPX()  // 判定是否处于moving状态，moving状态不可展示子view
	
	init {
		z = 1f
		layoutParams = LayoutParams(viewWidth, matchParent)
		post {
			viewHeight = height
		}
		
		mainButton.layoutParams = RelativeLayout.LayoutParams(buttonWidth, buttonWidth).apply {
			alignParentBottom()
		}
		mainButton.setMargins<RelativeLayout.LayoutParams> {
			leftMargin = framePadding
			bottomMargin = framePadding
		}
		mainButton.showIcon()
		childLayout.visibility = View.GONE
		childLayout.layoutParams = LayoutParams(wrapContent, wrapContent)
		addView(childLayout)
		addView(mainButton)
		mainButton.click {
			it.scale()
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
					mainButtonStatus = FloatingButtonStatus.MOVING
				}
				if (mainButtonStatus == FloatingButtonStatus.MOVING) {
					(mainButton.layoutParams as? RelativeLayout.LayoutParams)?.apply {
						val leftMargin = (event.x - mainButton.xDistance).toInt()
						val topMargin = (event.y - mainButton.yDistance).toInt()
						removeRule(ALIGN_PARENT_BOTTOM)
						setMargins(leftMargin, topMargin, 0, 0)
						mainButton.layoutParams = this
					}
				}
			} else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
				animateAfterTouchUp(event)
			}
		}
		
		val dispatchResult = super.dispatchTouchEvent(event)
		
		var childTouching = false
		for (index in 0 until childLayout.childCount) {
			if (!childTouching) childTouching = (childLayout.getChildAt(index) as DragFloatingButton).isTouching
		}
		
		if (event.action == MotionEvent.ACTION_DOWN
			&& !mainButton.isTouching
			&& childLayout.visibility == View.VISIBLE
			&& !childTouching) {
			hideChildFloatingButton()
			return true
		}
		return dispatchResult
	}

	private fun animateAfterTouchUp(event: MotionEvent) {
		if (mainButtonStatus != FloatingButtonStatus.MOVING) return
		val startX = (mainButton.layoutParams as? RelativeLayout.LayoutParams)?.leftMargin ?: 0
		val endX = if (event.x > viewWidth / 2) {
			viewWidth - mainButton.layoutParams.width - framePadding
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
				mainButtonStatus = if (endX > this@DragFloatingLayout.width / 2) FloatingButtonStatus.RIGHT else FloatingButtonStatus.LEFT
			}
		})
		(mainButton.layoutParams as? RelativeLayout.LayoutParams)?.topMargin?.let {
			if (it < framePadding || it > viewHeight - mainButton.layoutParams.width - framePadding) {
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

	fun addSubButton(vararg model: DragButtonModel) {
		model.forEach { dragModel ->
			childLayout.addView(
				DragFloatingButton(context).apply {
					layoutParams = LayoutParams(buttonWidth, buttonWidth)
					showIcon(dragModel.icon, dragModel.color)
					onClick {
						dragModel.event()
						scale()
						preventDuplicateClicks()
					}
				}
			)
		}
	}

	private fun showChildFloatingButton() {
		if (mainButtonStatus == FloatingButtonStatus.CENTER
			|| mainButtonStatus == FloatingButtonStatus.MOVING
			|| childLayout.childCount == 0
			|| childLayout.visibility == View.VISIBLE) {
			hideChildFloatingButton()
			return
		}
		// 做动画之前先要让他们全部隐藏
		val hideMargin = when (mainButtonStatus) {
			FloatingButtonStatus.LEFT -> 0
			FloatingButtonStatus.RIGHT -> buttonWidth * (childLayout.childCount + 1)
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
		if (mainButtonStatus == FloatingButtonStatus.LEFT) {
			finallyLeftMargin = framePadding
		} else if (mainButtonStatus == FloatingButtonStatus.RIGHT) {
			finallyLeftMargin = viewWidth - framePadding - mainButton.width - buttonWidth * (childLayout.childCount + 1)
		}
		(childLayout.layoutParams as? RelativeLayout.LayoutParams)?.apply {
			width = buttonWidth * (childLayout.childCount + 2)
			setMargins(finallyLeftMargin, mainButton.y.toInt(), 0, 0)
			childLayout.layoutParams = this
		}

		val startX = when (mainButtonStatus) {
			FloatingButtonStatus.LEFT -> 0
			FloatingButtonStatus.RIGHT -> buttonWidth * (childLayout.childCount + 1)
			else -> 0
		}

		for (index in 0 until childLayout.childCount) {
			val endX =  buttonWidth * (index + 1)
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
	
	private fun hideChildFloatingButton() {
		if (childLayout.visibility != View.VISIBLE) {
			return
		}
		
		val endX = when (mainButtonStatus) {
			FloatingButtonStatus.LEFT -> 0
			FloatingButtonStatus.RIGHT -> buttonWidth * (childLayout.childCount + 1)
			else -> 0
		}
		
		for (index  in 0 until childLayout.childCount) {
			val child = childLayout.getChildAt(index)
			val startX =  buttonWidth * (index + 1)
			val animator = ValueAnimator.ofInt(startX, endX)
			animator.duration = animatorDuration - 100
			animator.addUpdateListener {
				(child?.layoutParams as? RelativeLayout.LayoutParams)?.apply {
					val leftMargin = it.animatedValue as Int
					setMargins(leftMargin, 0, 0, 0)
					child.layoutParams = this
				}
			}
			animator.start()
		}
		
		childLayout.postDelayed( {
			childLayout.visibility = View.GONE
		}, animatorDuration + (childLayout.childCount - 1) * 100)
		
	}

	private enum class FloatingButtonStatus {
		CENTER, LEFT, RIGHT, MOVING
	}

}







