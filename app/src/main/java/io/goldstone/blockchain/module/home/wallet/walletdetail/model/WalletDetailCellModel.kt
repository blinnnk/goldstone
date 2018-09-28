package io.goldstone.blockchain.module.home.wallet.walletdetail.model

import io.goldstone.blockchain.crypto.eos.EOSWalletType
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.multichain.isEOS
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.MyTokenWithDefaultTable
import java.io.Serializable
import java.math.BigInteger

/**
 * @date 23/03/2018 11:57 PM
 * @author KaySaith
 */
data class WalletDetailCellModel(
	var iconUrl: String = "",
	var symbol: String = "",
	var tokenName: String = "",
	var decimal: Int = 0,
	var count: Double = 0.0,
	var price: Double = 0.0,
	var currency: Double = 0.0,
	var contract: TokenContract,
	var weight: Int = 0,
	var chainID: String,
	var eosWalletType: EOSWalletType
) : Serializable {

	constructor(
		data: MyTokenWithDefaultTable,
		eosWalletType: EOSWalletType
	) : this(
		data.iconUrl,
		data.symbol,
		data.tokenName,
		data.decimal,
		data.count,
		data.price,
		data.currency,
		TokenContract(data.contract),
		data.weight,
		data.chainID,
		if (TokenContract(data.contract).isEOS()) eosWalletType else EOSWalletType.None
	)

	constructor(
		data: DefaultTokenTable,
		amount: BigInteger,
		eosWalletType: EOSWalletType
	) : this(
		data.iconUrl,
		data.symbol,
		data.name,
		data.decimals,
		CryptoUtils.toCountByDecimal(amount, data.decimals),
		data.price,
		CryptoUtils.toCountByDecimal(amount, data.decimals) * data.price,
		TokenContract(data.contract),
		data.weight,
		data.chainID,
		if (TokenContract(data.contract).isEOS()) eosWalletType else EOSWalletType.None
	)

	constructor(
		data: DefaultTokenTable,
		balance: Double,
		eosWalletType: EOSWalletType
	) : this(
		data.iconUrl,
		data.symbol,
		data.name,
		data.decimals,
		balance,
		data.price,
		balance * data.price,
		TokenContract(data.contract),
		data.weight,
		data.chainID,
		if (TokenContract(data.contract).isEOS()) eosWalletType else EOSWalletType.None
	)
}