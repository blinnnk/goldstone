package io.goldstone.blinnnk.module.home.wallet.walletsettings.addressmanager.model

import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.language.CreateWalletText
import io.goldstone.blinnnk.common.language.ImportWalletText
import io.goldstone.blinnnk.common.language.WalletSettingsText
import io.goldstone.blinnnk.common.language.WalletText
import io.goldstone.blinnnk.crypto.multichain.ChainType
import io.goldstone.blinnnk.crypto.multichain.CryptoName
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/11/18
 */
data class GridIconTitleModel(
	val imageResource: Int,
	val name: String,
	val chainType: ChainType?
) : Serializable {
	companion object {
		fun getModels(): ArrayList<GridIconTitleModel> {
			return arrayListOf(
				GridIconTitleModel(R.drawable.eth_creator_icon, CryptoName.eth, ChainType.ETH),
				GridIconTitleModel(R.drawable.etc_creator_icon, CryptoName.etc, ChainType.ETC),
				GridIconTitleModel(R.drawable.btc_creator_icon, CryptoName.btc, ChainType.BTC),
				GridIconTitleModel(R.drawable.ltc_creator_icon, CryptoName.ltc, ChainType.LTC),
				GridIconTitleModel(R.drawable.bch_creator_icon, CryptoName.bch, ChainType.BCH),
				GridIconTitleModel(R.drawable.eos_creator_icon, CryptoName.eos, ChainType.EOS)
			)
		}

		fun getMenuModels(
			hasDefaultCell: Boolean = true,
			isBCH: Boolean = false
		): ArrayList<GridIconTitleModel> {
			return arrayListOf(
				GridIconTitleModel(R.drawable.default_icon, WalletText.setDefaultAddress, null),
				GridIconTitleModel(R.drawable.qr_code_icon, WalletText.qrCode, null),
				GridIconTitleModel(R.drawable.keystore_icon, WalletSettingsText.exportKeystore, null),
				GridIconTitleModel(R.drawable.private_key_icon, WalletSettingsText.exportPrivateKey, null),
				GridIconTitleModel(R.drawable.bch_address_convert_icon, WalletText.getBCHLegacyAddress, null)
			).apply {
				// 如果当前 `Cell` 就是默认地址, 不限时设置默认地址的选项
				if (!hasDefaultCell) remove(find { it.name == WalletText.setDefaultAddress })
				// 如果当前 `Cell` 不是 `BCH` 那么不限时转换 `BCH` 地址的选项
				if (!isBCH) remove(find { it.name == WalletText.getBCHLegacyAddress })
			}
		}

		fun getWalletManagementMenu(): ArrayList<GridIconTitleModel> {
			return arrayListOf(
				GridIconTitleModel(R.drawable.create_wallet_icon, CreateWalletText.create, null),
				GridIconTitleModel(R.drawable.import_wallet_icon, ImportWalletText.importWallet, null),
				GridIconTitleModel(R.drawable.watch_only_icon, ImportWalletText.importWatchWallet, null)
			)
		}
	}
}