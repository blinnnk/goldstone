package io.goldstone.blinnnk.module.home.wallet.walletsettings.addressmanager.event

import io.goldstone.blinnnk.crypto.multichain.ChainType


/**
 * @author KaySaith
 * @date  2018/11/26
 */
data class DefaultAddressUpdateEvent(val chainType: ChainType)