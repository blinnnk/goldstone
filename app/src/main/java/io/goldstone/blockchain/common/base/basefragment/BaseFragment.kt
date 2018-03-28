package io.goldstone.blockchain.common.base.basefragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.support.v4.UI

/**
 * @date 22/03/2018 2:57 AM
 * @author KaySaith
 */

abstract class BaseFragment<out T : BasePresenter<BaseFragment<T>>> : Fragment() {

  abstract val presenter: T

  abstract fun AnkoContext<Fragment>.initView()

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    presenter.onFragmentAttach()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    presenter.onFragmentCreateView()
    return UI {
      initView()
    }.view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    presenter.onFragmentViewCreated()
  }

  override fun onHiddenChanged(hidden: Boolean) {
    super.onHiddenChanged(hidden)
    if (!hidden) {
      presenter.onFragmentShowFromHidden()
    }
  }

  override fun onDetach() {
    super.onDetach()
    presenter.onFragmentDetach()
  }

  override fun onResume() {
    super.onResume()
    presenter.onFragmentResume()
  }

}