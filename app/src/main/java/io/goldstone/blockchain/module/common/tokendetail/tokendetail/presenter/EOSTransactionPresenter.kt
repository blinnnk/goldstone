package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import org.jetbrains.anko.runOnUiThread


/**
 * @author KaySaith
 * @date  2018/09/13
 */

val current = System.currentTimeMillis()