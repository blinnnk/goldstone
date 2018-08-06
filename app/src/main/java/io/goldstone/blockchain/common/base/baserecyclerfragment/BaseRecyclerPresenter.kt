package io.goldstone.blockchain.common.base.baserecyclerfragment

import android.support.v7.widget.RecyclerView
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.isFalse
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.crypto.utils.getObjectMD5HexString

/**
 * @date 23/03/2018 3:46 PM
 * @author KaySaith
 */
abstract class BaseRecyclerPresenter<out T : BaseRecyclerFragment<BaseRecyclerPresenter<T, D>, D>, D> {
	
	abstract val fragment: T
	
	open fun updateData() {
		// Do Something
	}
	
	open fun afterUpdateAdapterDataset(recyclerView: BaseRecyclerView) {
	}
	
	/**
	 * @description
	 * 在依赖的 `Fragment` 的对应的生命周期提供的依赖方法
	 * @param
	 * [fragment] 这个就是依赖的 `Fragment` 实体
	 * */
	open fun onFragmentAttach() {
		// Do Something
	}
	
	open fun onFragmentCreate() {
		// Do Something
	}
	
	open fun onFragmentCreateView() {
		// Do Something
	}
	
	open fun onFragmentDetach() {
	}
	
	open fun onFragmentDestroy() {
		// Do Something
	}
	
	open fun onFragmentViewCreated() {
		updateData()
	}
	
	open fun onFragmentStart() {
		// 当 `ViewCreated`后执行更新数据函数
	}
	
	open fun onFragmentShowFromHidden() {
		// Do Something
	}
	
	open fun onFragmentResume() {}
	
	open fun onFragmentHiddenChanged(isHidden: Boolean) {}
	
	/** 获取依赖的 `Adapter` */
	inline fun <reified T : RecyclerView.Adapter<*>> getAdapter() =
		fragment.recyclerView.adapter as? T
	
	// 适配的是有 `Header`， `Footer` 的 `adapter`
	inline fun <reified T : HoneyBaseAdapterWithHeaderAndFooter<D, *, *, *>> diffAndUpdateAdapterData(
		newData: ArrayList<D>
	) {
		getAdapter<T>()?.apply {
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
		getAdapter<T>()?.apply {
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