package io.goldstone.blockchain.common.base.baseoverlayfragment.overlayview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.EditTextWithButton
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.*

/**
 * @date 22/03/2018 2:37 AM
 * @author KaySaith
 */

class OverlayHeaderLayout(context: Context) : RelativeLayout(context) {

  var title: TextView

  val closeButton by lazy {
    HeaderIcon(context).apply {
      id = ElementID.closeButton
      imageResource = R.drawable.close_icon
      setRightPosition()
    }
  }

  val backButton by lazy {
    HeaderIcon(context).apply {
      id = ElementID.backButton
      imageResource = R.drawable.back
      setLeftPosition()
    }
  }

  private val addButton by lazy {
    HeaderIcon(context).apply {
      id = ElementID.addButton
      imageResource = R.drawable.add_icon
      setLeftPosition()
    }
  }

  private val searchButton by lazy {
    HeaderIcon(context).apply {
      id = ElementID.searchButton
      imageResource = R.drawable.search_icon
      setLeftPosition()
    }
  }

  private val searchInput by lazy {
    EditTextWithButton(context)
  }

  private val headerHeight = 65.uiPX()
  private val paint = Paint()

  init {

    setWillNotDraw(false)

    layoutParams = RelativeLayout.LayoutParams(ScreenSize.Width, headerHeight)

    title = textView {
      textColor = GrayScale.black
      textSize = FontSize.header
      typeface = GoldStoneFont.heavy(context)
      gravity = Gravity.CENTER
      layoutParams = RelativeLayout.LayoutParams(matchParent, headerHeight)
    }

    paint.color = GrayScale.lightGray
    paint.isAntiAlias = true
    paint.style = Paint.Style.FILL

  }

  fun showCloseButton(isShow: Boolean) {
    findViewById<ImageView>(ElementID.closeButton).apply {
      if (isShow) {
        isNull().isTrue { addView(closeButton) }
      } else {
        isNull().isFalse { removeView(this) }
      }
    }
  }

  fun showBackButton(isShow: Boolean, setClickEvent: ImageView.() -> Unit = {}) {
    findViewById<ImageView>(ElementID.backButton).let {
      it.isNull().isTrue {
        isShow.isTrue {
          backButton.click { setClickEvent(backButton) }.into(this)
        }
      } otherwise {
        isShow.isTrue {
          backButton.click { setClickEvent(backButton) }
        } otherwise {
          removeView(it)
        }
      }
    }
  }

  fun showAddButton(isShow: Boolean, setClickEvent: ImageView.() -> Unit = {}) {
    findViewById<ImageView>(ElementID.addButton).let {
      it.isNull().isTrue {
        isShow.isTrue {
          addButton.click { setClickEvent(addButton) }.into(this)
        }
      } otherwise {
        isShow.isTrue {
          addButton.click { setClickEvent(addButton) }
        } otherwise {
          removeView(it)
        }
      }
    }
  }

  fun setKeyboardConfirmEvent(action: EditText.() -> Unit) {
    searchInput.onPressKeyboardEnterButton { action(searchInput.editText)  }
  }

  fun showSearchButton(isShow: Boolean, setClickEvent: ImageView.() -> Unit = {}) {
    if (isShow) {
      searchButton
        .click {
          setClickEvent(searchButton)
        }
        .into(this)
    } else findViewById<ImageView>(ElementID.searchButton)?.let {
      removeView(it)
    }
  }

  fun showSearchInput(isShow: Boolean = true, cancelEvent: () -> Unit = {}) {

    showCloseButton(!isShow)

    isShow.isFalse {
      title.visibility = View.VISIBLE
      searchInput.visibility = View.GONE
      return
    }

    // 复用的 `OverlayFragment Header` 首先隐藏常规 `Title`
    title.visibility = View.GONE
    findViewById<EditTextWithButton>(ElementID.searchInput).let {
      it.isNull().isTrue {
        searchInput
          .apply {
            requestFocus()
            setCancelButton {
              showSearchInput(false)
              cancelEvent()
              // 取消搜索后自动清空搜索框里面的内容
              searchInput.editText.text.clear()
            }
          }
          .into(this)
      } otherwise {
        it.visibility = View.VISIBLE
      }
    }
  }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    canvas?.drawLine(
      PaddingSize.device.toFloat(),
      height - BorderSize.default,
      (ScreenSize.Width - PaddingSize.device).toFloat(),
      height - BorderSize.default, paint
    )
    canvas?.save()
  }

}

class HeaderIcon(context: Context) : ImageView(context) {

  private val iconSize = 30.uiPX()

  init {
    setColorFilter(GrayScale.lightGray)
    scaleType = ImageView.ScaleType.CENTER_INSIDE
    addTouchRippleAnimation(Color.TRANSPARENT, Spectrum.blue, RippleMode.Round)
  }

  fun setLeftPosition() {
    layoutParams = RelativeLayout.LayoutParams(iconSize, iconSize).apply {
      topMargin = 18.uiPX()
      leftMargin = 15.uiPX()
      alignParentLeft()
    }
  }

  fun setRightPosition() {
    layoutParams = RelativeLayout.LayoutParams(iconSize, iconSize).apply {
      topMargin = 18.uiPX()
      rightMargin = 15.uiPX()
      alignParentRight()
    }
  }

}