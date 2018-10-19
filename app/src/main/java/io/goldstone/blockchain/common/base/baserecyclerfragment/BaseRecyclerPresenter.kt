package io.goldstone.blockchain.common.base.baserecyclerfragment

import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.crypto.utils.getObjectMD5HexString

/**
 * @date 23/03/2018 3:46 PM
 * @author KaySaith
 */
abstract class BaseRecyclerPresenter<out T : BaseRecyclerFragment<BaseRecyclerPresenter<T, D>, D>, D> {

	abstract val fragment: T

	open fun updateData() {}
	open fun afterUpdateAdapterDataSet(recyclerView: BaseRecyclerView) {}

	/**
	 * @description
	 * 在依赖的 `Fragment` 的对应的生命周期提供的依赖方法
	 * @param
	 * [fragment] 这个就是依赖的 `Fragment` 实体
	 * */
	open fun onFragmentAttach() {}

	open fun onFragmentCreate() {}
	open fun onFragmentCreateView() {}
	open fun onFragmentDetach() {}
	open fun onFragmentDestroy() {}

	private var isLoadingData = false
	open fun onFragmentViewCreated() {
		updateData()
		fragment.addRecyclerLoadMoreListener {
			if (!isLoadingData) loadMore()
			isLoadingData = true
		}
	}

	open fun onFragmentStart() {}
	open fun onFragmentShowFromHidden() {}
	open fun onFragmentResume() {}
	open fun onFragmentHiddenChanged(isHidden: Boolean) {}

	fun showBottomLoading(status: Boolean) {
		fragment.recyclerView.apply {
			val bottomIndex = adapter?.itemCount.orZero() - 1
			getItemAtAdapterPosition<BottomLoadingView>(bottomIndex) {
				if (status) it.show() else it.hide()
			}
		}
		isLoadingData = false
	}

	/**
	 * 1. 异步获取新数据
	 * 2. 获取到的数据更新 adapter 的数据以及 asyncData 的数据
	 * 		 2.1  asyncData.addAll(newData)
	 *		 2.2 getAdapter<Type>().dataSet = asyncData
	 * 3. recycler adapter notifyItemRangeChanged 增量更新
	 */
	open fun loadMore() {
		// 显示底部的 `LoadingView`
		showBottomLoading(true)
	}

	// 适配的是有 `Header`， `Footer` 的 `adapter`
	inline fun <reified T : HoneyBaseAdapterWithHeaderAndFooter<D, *, *, *>> diffAndUpdateAdapterData(newData: ArrayList<D>) {
		fragment.getAdapter<T>()?.apply {
			// Comparison the data, if they are different then update adapter
			diffDataSetChanged(dataSet, newData) {
				it.isFalse {
					fragment.asyncData?.clear()
					fragment.asyncData?.addAll(newData)
					if (newData.isNotEmpty()) {
						fragment.removeEmptyView()
					} else {
						fragment.showEmptyView()
					}
					dataSet.clear()
					dataSet.addAll(newData)
					notifyDataSetChanged()
				}
			}
		}
	}

	// 适配的是有 `Header`， `Footer` 的 `adapter`
	inline fun <reified T : HoneyBaseAdapter<D, *>> diffAndUpdateSingleCellAdapterData(newData: ArrayList<D>) {
		fragment.getAdapter<T>()?.apply {
			// Comparison the data, if they are different then update adapter
			diffDataSetChanged(dataSet, newData) {
				it.isFalse {
					fragment.asyncData?.clear()
					fragment.asyncData?.addAll(newData)
					if (newData.isNotEmpty()) {
						fragment.removeEmptyView()
					} else {
						fragment.showEmptyView()
					}
					dataSet.clear()
					dataSet.addAll(newData)
					notifyDataSetChanged()
				}
			}
		}
	}

	inline fun diffDataSetChanged(
		oldData: ArrayList<D>,
		newData: ArrayList<D>,
		hold: (Boolean) -> Unit
	) {
		hold(oldData.getObjectMD5HexString() == newData.getObjectMD5HexString())
	}
}