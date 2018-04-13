package io.goldstone.blockchain.common.base.baserecyclerfragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.component.EmptyType
import io.goldstone.blockchain.common.component.EmptyView
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view.PaymentValueDetailFragment
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.support.v4.UI

/**
 * @date 23/03/2018 3:46 PM
 * @author KaySaith
 */

abstract class BaseRecyclerFragment<out T : BaseRecyclerPresenter<BaseRecyclerFragment<T, D>, D>, D> :
  Fragment() {

  lateinit var wrapper: RelativeLayout
  lateinit var recyclerView: BaseRecyclerView

  /**
   * @description
   * 当 `RecyclerView` 需要异步数据更新列表的时候, 通常网络的业务都会是异步的,就可以通过在异步为
   * asyncData 赋值来达到 `RecyclerView` 更新数据的效果.
   */
  var asyncData: ArrayList<D>? by observing(null) {
    recyclerView.adapter.apply {
      isNull().isTrue {
        setRecyclerViewAdapter(recyclerView, asyncData)
      } otherwise {
        notifyDataSetChanged()
      }
    }

    /** 如果数据返回时空的显示占位图 */
    asyncData?.isEmpty()?.isTrue {
      wrapper.showEmptyView()
    }

    /** 当数据返回后在这个方法根据数据的数量决定如何做伸展动画 */
    setSlideUpAnimation()
  }

  /**
   * @description
   * 每一个 `Fragment` 都会配备一个 `Presenter` 来进行数据及UI 的控制, 这个 `Presenter`
   * 必须是配套的 [BaseRecyclerPresenter] `Fragment` 和 `Presenter` 之间
   * 有固定的约定实现协议, 来更方便安全和便捷的使用.
   */
  abstract val presenter: T

  /**
   * `RecyclerFragment` 的默认 `LayoutManager` 是 `LinearLayoutManager`. 提供了下面这个
   * 可被修改的方法来更改 内嵌的 `RecyclerView` 的 `LayoutManager`
   */
  open fun setRecyclerViewLayoutManager(recyclerView: BaseRecyclerView) {
    // Do Something
  }

  open fun observingRecyclerViewScrollState(state: Int) {
    // Do Something
  }

  open fun observingRecyclerViewScrolled(dx: Int, dy: Int) {
    // Do Something
  }

  open fun observingRecyclerViewVerticalOffset(offset: Int) {
    // Do Something
  }

  open fun observingRecyclerViewHorizontalOffset(offset: Int) {
    // Do Something
  }

  /**
   * 当设定 `Cell` 的单元高度后,就会在 `OnViewCreated` 的时机执行 `updateParentContentLayoutHeight`
   * 来动画伸展出初始界面.
   */
  open fun setSlideUpWithCellHeight(): Int? = null

  open fun setSlideUpAnimation() {
    // 如果有父级 `ParentFragment` 就可以在 `Presenter` 执行这个方法
    setSlideUpWithCellHeight().let {
      it.isNull().isTrue {
        presenter.updateParentContentLayoutHeight()
      } otherwise {
        presenter.updateParentContentLayoutHeight(asyncData?.size.orZero(), it.orZero())
      }
    }
  }

  /**
   * @description
   * `RecyclerFragment` 把 `RecyclerView` 内置在 `Fragment` 中所以协议提供了一个强制实现的
   * 方法必须指定一个 `Adapter` 这个方法是初始化 `Adapter` 数据的. 只会在 `Adapter` 为空的时候进行
   * 处理。
   * @param
   * [recyclerView] 这个就是内嵌的 `RecyclerView` 的实体, 作为参数传递主要是在继承场景中不用寻找隐形的 `RecyclerView`
   * 可以直接调用参数联动。
   * [asyncData] 这个数据是在 `Presenter` 里面实现好后返回到这里的实体.
   */
  abstract fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<D>?)

  /**
   * 默认的尺寸是填充屏幕, 这个方法提供了修改的功能
   */
  open fun setRecyclerViewParams(width: Int, height: Int): RelativeLayout.LayoutParams =
    RelativeLayout.LayoutParams(width, height)

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    presenter.onFragmentAttach()
  }

  override fun onDetach() {
    super.onDetach()
    presenter.onFragmentDetach()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    presenter.onFragmentCreate()
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    presenter.onFragmentCreateView()
    return UI {
      wrapper = relativeLayout {
        layoutParams = setRecyclerViewParams(matchParent, matchParent)
        recyclerView = BaseRecyclerView(context)
        setRecyclerViewLayoutManager(recyclerView)
        addView(recyclerView, RelativeLayout.LayoutParams(matchParent, matchParent))
      }
    }.view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    presenter.onFragmentViewCreated()

    // 监听 `RecyclerView` 滑动
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

      override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
        /** [newState] `1` 开始滑动, `0` 停止滑动 `2` 加速滑动 */
        super.onScrollStateChanged(recyclerView, newState)
        observingRecyclerViewScrollState(newState)
      }

      override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        observingRecyclerViewScrolled(dx, dy)
        observingRecyclerViewVerticalOffset(recyclerView?.computeVerticalScrollOffset().orZero())
        observingRecyclerViewHorizontalOffset(recyclerView?.computeHorizontalScrollOffset().orZero())
      }
    })
  }

  override fun onHiddenChanged(hidden: Boolean) {
    super.onHiddenChanged(hidden)
    if (!hidden) {
      presenter.onFragmentShowFromHidden()
    }
  }

  private var emptyLayout: EmptyView? = null

  open fun ViewGroup.showEmptyView() {
    emptyLayout = EmptyView(context).apply {
      when (this@BaseRecyclerFragment) {
        is PaymentValueDetailFragment -> return
        is TokenDetailFragment -> setStyle(EmptyType.TokenDetail)
        else -> setStyle(EmptyType.TransactionDetail)
      }
    }
    emptyLayout?.into(this)
    if (this@BaseRecyclerFragment !is TokenDetailFragment) emptyLayout?.setCenterInParent()
  }

}