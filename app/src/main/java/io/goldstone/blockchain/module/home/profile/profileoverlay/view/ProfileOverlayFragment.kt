package io.goldstone.blockchain.module.home.profile.profileoverlay.view

import android.view.View
import android.view.ViewGroup
import com.blinnnk.extension.into
import com.blinnnk.extension.setCenterInVertical
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.profile.profileoverlay.presenter.ProfileOverlayPresenter

/**
 * @date 26/03/2018 12:55 AM
 * @author KaySaith
 */

class ProfileOverlayFragment : BaseOverlayFragment<ProfileOverlayPresenter>() {

  val title by lazy { arguments?.getString(ArgumentKey.profileTitle) }

  private val addContactButton by lazy { RoundButton(context!!) }

  override val presenter = ProfileOverlayPresenter(this)

  override fun ViewGroup.initView() {

    headerTitle = title.orEmpty()
    presenter.showTargetFragmentByTitle(title.orEmpty())

    addContactButton.apply {
      visibility = View.GONE
      setSmallButton(Spectrum.green)
      text = CommonText.create.toUpperCase()
    }.into(overlayView.header)
    addContactButton.setCenterInVertical()
  }

  fun showAddContactButton(status: Boolean = true, hold: RoundButton.() -> Unit = {}) {
    if (status) {
      addContactButton.visibility = View.VISIBLE
      hold(addContactButton)
    }
    else addContactButton.visibility = View.GONE
  }

}