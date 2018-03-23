package io.goldstone.blockchain.common.base.baserecyclerfragment

/**
 * @date 23/03/2018 3:46 PM
 * @author KaySaith
 */

abstract class BaseRecyclerPresenter<out T : BaseRecyclerFragment<BaseRecyclerPresenter<T, D>, D>, D> {

  abstract val fragment: T

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
    // Do Something
  }

  open fun onFragmentViewCreated() {
    // Do Something
  }

}