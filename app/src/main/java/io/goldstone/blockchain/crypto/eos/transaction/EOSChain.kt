package io.goldstone.blockchain.crypto.eos.transaction

import io.goldstone.blockchain.common.value.ChainID

enum class EOSChain(val id: String) {
	Main(ChainID.EOSMain.id), Test(ChainID.EOSTest.id)
}