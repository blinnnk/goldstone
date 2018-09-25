package io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbyfriend.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbyfriend.view.RegisterByFriendFragment
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbysmartcontract.detail.view.SmartContractRegisterDetailFragment
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbysmartcontract.register.view.SmartContractRegisterFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import org.jetbrains.anko.runOnUiThread


/**
 * @author KaySaith
 * @date  2018/09/25
 */
class RegisterByFriendPresenter(
	override val fragment: RegisterByFriendFragment
) : BasePresenter<RegisterByFriendFragment>() {
}