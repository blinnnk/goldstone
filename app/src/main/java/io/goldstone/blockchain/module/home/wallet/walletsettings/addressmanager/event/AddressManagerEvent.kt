package io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.event

import io.goldstone.blockchain.crypto.multichain.ChainType


/**
 * @author KaySaith
 * @date  2018/11/26
 */
data class DefaultAddressUpdateEvent(val chainType: ChainType)