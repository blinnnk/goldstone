package io.goldstone.blockchain.module.home.profile.profile.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.profile.profile.model.ProfileModel

/**
 * @date 25/03/2018 10:54 PM
 * @author KaySaith
 */

class ProfileAdapter(
  override val dataSet: ArrayList<ProfileModel>,
  private val callback: (ProfileCell, Int) -> Unit
  ) : HoneyBaseAdapter<ProfileModel, ProfileCell>() {

  override fun generateCell(context: Context) = ProfileCell(context)

  override fun ProfileCell.bindCell(data: ProfileModel, position: Int) {
    model = data
    callback(this@bindCell, position)
  }

}