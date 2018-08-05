package io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter

import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.common.value.WalletType
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.bitcoin.exportBase58PrivateKey
import io.goldstone.blockchain.crypto.getPrivateKey
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view.PrivateKeyExportFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.uiThread

/**
 * @date 06/04/2018 1:02 AM
 * @author KaySaith
 */
class PrivateKeyExportPresenter(
	override val fragment: PrivateKeyExportFragment
) : BasePresenter<PrivateKeyExportFragment>() {
	
	private val address by lazy {
		fragment.arguments?.getString(ArgumentKey.address)
	}
	private val isBTCAddress by lazy {
		fragment.arguments?.getBoolean(ArgumentKey.isBTCAddress)
	}
	
	fun getPrivateKeyByAddress(
		password: String,
		hold: String?.() -> Unit
	) {
		if (password.isEmpty()) {
			fragment.toast(ImportWalletText.exportWrongPassword)
			hold("")
			return
		}
		
		fragment.activity?.apply { SoftKeyboard.hide(this) }
		
		address?.let {
			val isSingleChainWallet =
				!Config.getCurrentWalletType().equals(WalletType.MultiChain.content, true)
			if (isBTCAddress == true) getBTCPrivateKeyByAddress(
				it,
				password,
				isSingleChainWallet,
				hold
			)
			else getETHERCorETCPrivateKeyByAddress(
				it,
				password,
				isSingleChainWallet,
				hold
			)
		}
	}
	
	private fun getETHERCorETCPrivateKeyByAddress(
		address: String,
		password: String,
		isSingleChainWallet: Boolean,
		hold: String.() -> Unit
	) {
		doAsync {
			fragment.context?.getPrivateKey(
				address,
				password,
				false,
				isSingleChainWallet,
				{
					uiThread { hold("") }
				},
				hold
			)
		}
	}
	
	private fun getBTCPrivateKeyByAddress(
		address: String,
		password: String,
		isSingleChainWallet: Boolean,
		hold: String?.() -> Unit
	) {
		doAsync {
			val isTest = BTCUtils.isValidTestnetAddress(address)
			fragment.context?.exportBase58PrivateKey(
				address,
				password,
				isSingleChainWallet,
				isTest,
				hold
			)
		}
	}
}