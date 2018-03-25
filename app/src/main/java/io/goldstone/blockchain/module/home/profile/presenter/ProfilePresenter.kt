package io.goldstone.blockchain.module.home.profile.presenter

import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.home.profile.model.ProfileModel
import io.goldstone.blockchain.module.home.profile.view.ProfileFragment

/**
 * @date 25/03/2018 10:52 PM
 * @author KaySaith
 */

class ProfilePresenter(
  override val fragment: ProfileFragment
  ) : BaseRecyclerPresenter<ProfileFragment, ProfileModel>() {

}