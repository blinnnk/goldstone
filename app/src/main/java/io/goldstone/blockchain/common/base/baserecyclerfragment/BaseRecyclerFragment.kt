package io.goldstone.blockchain.common.base.baserecyclerfragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.common.component.EmptyView
import io.goldstone.blockchain.common.component.overlay.TopMiniLoadingView
import io.goldstone.blockchain.common.utils.getMainActivity

/**
 * @date 23/03/2018 3:46 PM
 * @author KaySaith
 */
abstract class BaseRecyclerFragment<out T : BaseRecyclerPresenter<BaseRecyclerFragment<T, D>, D>, D>
	: GSRecyclerFragment<D>() {

	/**
	 * @description
	 * 每一个 `Fragment` 都会配备一个 `Presenter` 来进行数据及UI 的控制, 这个 `Presenter`
	 * 必须是配套的 [BaseRecyclerPresenter] `Fragment` 和 `Presenter` 之间
	 * 有固定的约定实现协议, 来更方便安全和便捷的使用.
	 */
	abstract val presenter: T

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		presenter.onFragmentAttach()
	}

	override fun onStart() {
		super.onStart()
		presenter.onFragmentStart()
	}

	override fun onDetach() {
		super.onDetach()
		presenter.onFragmentDetach()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		presenter.onFragmentDestroy()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		presenter.onFragmentCreate()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		presenter.onFragmentCreateView()
		return super.onCreateView(inflater, container, savedInstanceState)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter.onFragmentViewCreated()
	}

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		presenter.onFragmentHiddenChanged(hidden)
		presenter.onFragmentShowFromHidden()
	}

	override fun onDestroy() {
		super.onDestroy()
		// 如果键盘在显示那么销毁键盘
		activity?.apply { SoftKeyboard.hide(this) }
	}

	override fun onResume() {
		super.onResume()
		getMainActivity()?.sendAnalyticsData(this::class.java.simpleName)
		presenter.onFragmentResume()
	}

	private var emptyLayout: EmptyView? = null
}