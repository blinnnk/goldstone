package io.goldstone.blockchain.common.base.basefragment

/**
 * @date 22/03/2018 2:58 AM
 * @author KaySaith
 */

abstract class BasePresenter<out T : BaseFragment<*>> {

  abstract val fragment: T

  open fun onFragmentAttach() {
    // Do Something When fragment Attach
  }

  open fun onFragmentCreateView() {
    // Do Something When fragment Attach
  }

  open fun onFragmentViewCreated() {
    // Do Something
  }

  open fun onFragmentDetach() {
    // Do Something
  }

  open fun onFragmentResume() {
    // Do Something
  }

}