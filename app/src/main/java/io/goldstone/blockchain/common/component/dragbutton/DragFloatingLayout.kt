package io.goldstone.blockchain.common.component.dragbutton

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.RelativeLayout
import com.blinnnk.animation.scale
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.click
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.wrapContent


/**
 * @date: 2018-12-10.
 * @author: yangLiHai
 * @description:
 */
class DragFloatingLayout(context: Context) : RelativeLayout(context) {
	private val viewWidth = ScreenSize.Width
	private val viewHeight = ScreenSize.Height
	private val mainButton = DragFloatingButton(context)
	private val childLayout = RelativeLayout(context)
	private val buttonWidth = 70.uiPX()
	private val framePadding = 20.uiPX() // floatingButton 距离边距的距离
	private var childStatus = FloatingButtonStatus.CENTER
	private var animatorDuration: Long = 500
	private var eventDownX = 0
	private var eventDownY = 0
	private var clickLimitDistance = 10  // 判定是否处于moving状态，moving状态不可展示子view

	init {
		z = 1f
		mainButton.layoutParams = RelativeLayout.LayoutParams(buttonWidth, buttonWidth)
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

	private fun animateAfterTouchUp(event: MotionEvent) {
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
				childStatus = if (endX > this@DragFloatingLayout.width / 2) FloatingButtonStatus.RIGHT else FloatingButtonStatus.LEFT
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
		if (childStatus == FloatingButtonStatus.CENTER
			|| childStatus == FloatingButtonStatus.MOVING
			|| childLayout.childCount == 0
			|| childLayout.visibility == View.VISIBLE) {
			childLayout.visibility = View.GONE
			return
		}
		// 做动画之前先要让他们全部隐藏
		val hideMargin = when (childStatus) {
			FloatingButtonStatus.LEFT -> -buttonWidth
			FloatingButtonStatus.RIGHT -> buttonWidth * childLayout.childCount
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
			finallyLeftMargin = viewWidth - framePadding - mainButton.width - buttonWidth * childLayout.childCount
		}
		(childLayout.layoutParams as? RelativeLayout.LayoutParams)?.apply {
			width = buttonWidth * childLayout.childCount
			setMargins(finallyLeftMargin, mainButton.y.toInt(), 0, 0)
			childLayout.layoutParams = this
		}

		val startX = when (childStatus) {
			FloatingButtonStatus.LEFT -> -buttonWidth
			FloatingButtonStatus.RIGHT -> buttonWidth * childLayout.childCount
			else -> 0
		}

		for (index in 0 until childLayout.childCount) {
			val endX = buttonWidth * index
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







