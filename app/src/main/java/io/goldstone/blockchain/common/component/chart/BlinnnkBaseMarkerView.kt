package example.cat.com.candlechartdemo.ktd

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.IMarker
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import io.goldstone.blockchain.R
import org.jetbrains.anko.*
import java.lang.ref.WeakReference

/**
 * @date: 2018/8/7.
 * @author: yanglihai
 * @description:
 */
open class BlinnnkBaseMarkerView(context: Context) : RelativeLayout(context), IMarker {
  private var mOffset: MPPointF? = MPPointF()
  private val mOffset2 = MPPointF()
  private var mWeakChart: WeakReference<Chart<*>>? = null
  
  lateinit var textViewContent: TextView
  
  /**
   * Constructor. Sets up the MarkerView with a custom layout resource.
   *
   * @param context
   * @param layoutResource the layout resource to use for the MarkerView
   */
  init {
    with(this) {
      relativeLayout {
        backgroundResource = R.drawable.btn_round_gray
        textViewContent = textView {
          textSize = 10f
          textColor = Color.BLACK
        }.lparams(wrapContent, wrapContent) {
          margin = 10
        }
        measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
          View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        layout(0, 0, measuredWidth, measuredHeight)
      }
    }
    
  }
  
  /**
   * Sets the layout resource for a custom MarkerView.
   *
   * @param layoutResource
   */
  private fun setupLayoutResource(layoutResource: Int) {
    
    val inflated = LayoutInflater.from(getContext()).inflate(layoutResource, this)
    
    inflated.setLayoutParams(RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
      RelativeLayout.LayoutParams.WRAP_CONTENT))
    inflated.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
      View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
    
    // measure(getWidth(), getHeight());
    inflated.layout(0, 0, inflated.getMeasuredWidth(), inflated.getMeasuredHeight())
  }
  
  fun setOffset(offset: MPPointF) {
    mOffset = offset
    
    if (mOffset == null) {
      mOffset = MPPointF()
    }
  }
  
  fun setOffset(
    offsetX: Float,
    offsetY: Float
  ) {
    mOffset!!.x = offsetX
    mOffset!!.y = offsetY
  }
  
  override fun getOffset(): MPPointF? {
    return mOffset
  }
  
  fun setChartView(chart: Chart<*>) {
    mWeakChart = WeakReference<Chart<*>>(chart)
  }
  
  fun getChartView(): Chart<*>? {
    return if (mWeakChart == null) null else mWeakChart!!.get()
  }
  
  override fun getOffsetForDrawingAtPoint(
    posX: Float,
    posY: Float
  ): MPPointF {
    
    val offset = getOffset()
    mOffset2.x = offset!!.x
    mOffset2.y = offset.y
    
    val chart = getChartView()
    
    val width = getWidth().toFloat()
    val height = getHeight().toFloat()
    
    if (posX + mOffset2.x < 0) {
      mOffset2.x = -posX
    } else if (chart != null && posX + width + mOffset2.x > chart.width) {
      mOffset2.x = chart.width.toFloat() - posX - width
    }
    
    if (posY + mOffset2.y < 0) {
      mOffset2.y = -posY
    } else if (chart != null && posY + height + mOffset2.y > chart.height) {
      mOffset2.y = chart.height.toFloat() - posY - height
    }
    
    return mOffset2
  }
  
  override fun refreshContent(
    e: Entry,
    highlight: Highlight
  ) {
    
    measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
      View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
    layout(0, 0, getMeasuredWidth(), getMeasuredHeight())
    
  }
  
  override fun draw(
    canvas: Canvas,
    posX: Float,
    posY: Float
  ) {
    
    val offset = getOffsetForDrawingAtPoint(posX, posY)
    
    val saveId = canvas.save()
    // translate to the correct position and draw
    canvas.translate(posX + offset.x, posY + offset.y)
    draw(canvas)
    canvas.restoreToCount(saveId)
  }
}
