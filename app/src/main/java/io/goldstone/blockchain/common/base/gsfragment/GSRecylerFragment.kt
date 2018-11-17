package io.goldstone.blockchain.common.base.gsfragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.*
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.component.EmptyType
import io.goldstone.blockchain.common.component.EmptyView
import io.goldstone.blockchain.common.component.overlay.TopMiniLoadingView
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.HomeSize
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.view.AddressSelectionFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.contacts.contracts.view.ContactFragment
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationFragment
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.view.QuotationManagementFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.view.QuotationSearchFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view.NotificationListFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.view.TokenSearchFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI

/**
 * @date 23/03/2018 3:46 PM
 * @author KaySaith
 */
abstract class GSRecyclerFragment<D> : Fragment() {

	abstract val pageTitle: String
	lateinit var wrapper: RelativeLayout
	lateinit var recyclerView: BaseRecyclerView
	private lateinit var topMiniLoading: TopMiniLoadingView
	/**
	 * @description
	 * 当 `RecyclerView` 需要异步数据更新列表的时候, 通常网络的业务都会是异步的,就可以通过在异步为
	 * asyncData 赋值来达到 `RecyclerView` 更新数据的效果.
	 */
	var asyncData: ArrayList<D>? by observing(null) {
		recyclerView.adapter.apply {
			if (this == null) {
				setRecyclerViewAdapter(recyclerView, asyncData)
			} else notifyDataSetChanged()
		}

		afterUpdateAdapterDataSet(recyclerView)
		/** 如果数据返回空的显示占位图 */
		asyncData?.let { setEmptyViewBy(it) }
	}

	open fun afterUpdateAdapterDataSet(recyclerView: BaseRecyclerView) {}

	/**
	 * `RecyclerFragment` 的默认 `LayoutManager` 是 `LinearLayoutManager`. 提供了下面这个
	 * 可被修改的方法来更改 内嵌的 `RecyclerView` 的 `LayoutManager`
	 */
	open fun setRecyclerViewLayoutManager(recyclerView: BaseRecyclerView) {}

	open fun observingRecyclerViewScrollState(state: Int) {
		if (state == 1) activity?.apply { SoftKeyboard.hide(this) }
	}

	open fun observingRecyclerViewScrolled(dx: Int, dy: Int) {}
	open fun observingRecyclerViewVerticalOffset(offset: Int, range: Int) {}
	open fun observingRecyclerViewHorizontalOffset(offset: Int) {}

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


	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		setPageTitle()
		return UI {
			// 这个高度判断是解决少数虚拟键盘高度可以手动隐藏的, 例如 `Samsung S8, S9`
			val wrapperHeight = when {
				activity?.navigationBarIsHidden() == true ->
					context?.getRealScreenHeight().orZero() - HomeSize.tabBarHeight
				this !is BaseOverlayFragment<*> ->
					ScreenSize.Height + ScreenSize.statusBarHeight - HomeSize.tabBarHeight
				else -> ScreenSize.Height + ScreenSize.statusBarHeight - HomeSize.headerHeight
			}
			wrapper = relativeLayout {
				topMiniLoading = TopMiniLoadingView(context)
				topMiniLoading.visibility = View.GONE
				layoutParams = setRecyclerViewParams(matchParent, wrapperHeight)
				recyclerView = BaseRecyclerView(context)
				setRecyclerViewLayoutManager(recyclerView)
				addView(recyclerView, RelativeLayout.LayoutParams(matchParent, matchParent))
				addView(topMiniLoading)
			}
		}.view
	}

	var isLoadingData = false

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
				/** [newState] `1` 开始滑动, `0` 停止滑动 `2` 加速滑动 */
				super.onScrollStateChanged(recyclerView, newState)
				observingRecyclerViewScrollState(newState)
			}

			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				observingRecyclerViewScrolled(dx, dy)
				observingRecyclerViewVerticalOffset(
					recyclerView.computeVerticalScrollOffset().orZero(),
					recyclerView.computeVerticalScrollRange().orZero()
				)
				observingRecyclerViewHorizontalOffset(
					recyclerView.computeHorizontalScrollOffset().orZero()
				)
			}
		})
		addRecyclerLoadMoreListener {
			if (!isLoadingData) flipPage()
			isLoadingData = true
		}
		getMainActivity()?.backEvent = Runnable {
			setBackEvent(getMainActivity())
		}
	}

	open fun flipPage() {}

	fun addRecyclerLoadMoreListener(callback: () -> Unit) {
		recyclerView.addOnScrollListener(
			object : RecyclerView.OnScrollListener() {
				override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
					recyclerView.layoutManager?.apply {
						var lastPosition = 0
						when (this) {
							is LinearLayoutManager -> lastPosition = findLastVisibleItemPosition()
							is GridLayoutManager -> lastPosition = findLastVisibleItemPosition()
							is StaggeredGridLayoutManager -> {
								val arrays = IntArray(spanCount)
								findLastVisibleItemPositions(arrays)
								lastPosition = arrays.max()!!
							}
						}
						if (lastPosition >= itemCount - 1) callback()
					}
				}
			}
		)
	}

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		// 如果键盘在显示那么销毁键盘
		activity?.apply { SoftKeyboard.hide(this) }
		if (!hidden) {
			setPageTitle()
			// 重置回退栈事件
			getMainActivity()?.backEvent = Runnable {
				setBackEvent(getMainActivity())
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		// 如果键盘在显示那么销毁键盘
		activity?.apply { SoftKeyboard.hide(this) }
	}

	override fun onResume() {
		super.onResume()
		getMainActivity()?.sendAnalyticsData(this::class.java.simpleName)
	}

	private var emptyLayout: EmptyView? = null

	/**
	 * `Inside loadingView` 非阻碍式的 `Loading`
	 */
	open fun showLoadingView(status: Boolean) {
		topMiniLoading.visibility = if (status )View.VISIBLE else View.GONE
	}

	open fun emptyClickEvent() {}

	open fun showEmptyView() {
		// 如果已经存在 `emptyLayout` 跳出
		emptyLayout.isNotNull { return }
		emptyLayout = EmptyView(wrapper.context).apply {
			when (this@GSRecyclerFragment) {
				is TokenDetailFragment -> setStyle(EmptyType.TokenDetail)
				is ContactFragment, is AddressSelectionFragment -> setStyle(EmptyType.Contact)
				is TokenSearchFragment -> setStyle(EmptyType.Search)
				is QuotationSearchFragment -> setStyle(EmptyType.QuotationSearch)
				is QuotationManagementFragment -> setStyle(EmptyType.QuotationManagement)
				is QuotationFragment -> setStyle(EmptyType.Quotation)
				is WalletDetailFragment -> setStyle(EmptyType.WalletDetail)
				is NotificationListFragment -> setStyle(EmptyType.NotificationList)
				else -> setStyle(EmptyType.TransactionDetail)
			}
		}

		emptyLayout?.onClick {
			emptyClickEvent()
			emptyLayout?.preventDuplicateClicks()
		}

		// Quotation 的空占位图是需要点击执行功能的所以层级需要高一级
		if (this@GSRecyclerFragment is QuotationFragment) {
			wrapper.addView(emptyLayout)
		} else {
			wrapper.addView(emptyLayout, 0)
		}
		if (this@GSRecyclerFragment !is TokenDetailFragment
			&& this@GSRecyclerFragment !is QuotationFragment
		) emptyLayout?.centerInParent()
	}

	open fun removeEmptyView() {
		emptyLayout?.apply {
			wrapper.removeView(this)
			wrapper.requestLayout()
			wrapper.invalidate()
			emptyLayout = null
		}
	}

	private fun setEmptyViewBy(data: ArrayList<D>) {
		if (data.isEmpty()) showEmptyView()
		else removeEmptyView()
	}

	open fun updateBackEvent() {
		// 重置回退栈事件
		getMainActivity()?.backEvent = Runnable {
			setBackEvent(getMainActivity())
		}
	}

	open fun recoveryBackEvent() {
		getMainActivity()?.apply {
			backEvent = Runnable {
				setBackEvent(this)
			}
		}
	}

	open fun setBackEvent(mainActivity: MainActivity?) {
		val parent = parentFragment
		if (parent is BaseOverlayFragment<*>) {
			parent.presenter.removeSelfFromActivity()
			// 如果阻碍 `Loading` 存在也一并销毁
			mainActivity?.removeLoadingView()
		}
	}

	/** 获取依赖的 `Adapter` */
	inline fun <reified T : RecyclerView.Adapter<*>> getAdapter() = recyclerView.adapter as? T

	inline fun <reified T : HoneyBaseAdapterWithHeaderAndFooter<D, *, *, *>> updateAdapterData(newData: ArrayList<D>, callback: () -> Unit = {}) {
		getAdapter<T>()?.apply {
			// Comparison the data, if they are different then update adapter
			asyncData?.clear()
			asyncData?.addAll(newData)
			if (newData.isNotEmpty()) removeEmptyView()
			else showEmptyView()
			dataSet.clear()
			dataSet.addAll(newData)
			notifyDataSetChanged()
			callback()
		}
	}

	inline fun <reified T : HoneyBaseAdapter<D, *>> updateSingleCellAdapterData(newData: ArrayList<D>, callback: () -> Unit = {}) {
		getAdapter<T>()?.apply {
			// Comparison the data, if they are different then update adapter
			asyncData?.clear()
			asyncData?.addAll(newData)
			if (newData.isNotEmpty()) removeEmptyView()
			else showEmptyView()
			dataSet.clear()
			dataSet.addAll(newData)
			notifyDataSetChanged()
			callback()
		}
	}

	private fun setPageTitle() {
		val parent = parentFragment
		if (parent is BaseOverlayFragment<*>) {
			parent.headerTitle = pageTitle
		}
	}
}