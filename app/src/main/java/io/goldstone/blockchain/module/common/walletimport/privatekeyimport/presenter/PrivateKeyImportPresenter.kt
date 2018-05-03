package io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter

import android.support.v4.app.Fragment
import android.widget.EditText
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.crypto.getWalletByPrivateKey
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view.PrivateKeyImportFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import org.web3j.crypto.WalletUtils

/**
 * @date 23/03/2018 2:13 AM
 * @author KaySaith
 */

class PrivateKeyImportPresenter(
	override val fragment: PrivateKeyImportFragment
) : BasePresenter<PrivateKeyImportFragment>() {

	fun importWalletByPrivateKey(
		privateKeyInput: EditText,
		passwordInput: EditText,
		repeatPasswordInput: EditText,
		isAgree: Boolean,
		nameInput: EditText,
		hintInput: EditText
	) {
		privateKeyInput.text.isEmpty() isTrue {
			fragment.context?.alert("privateKey is not correct")
			return
		}
		CreateWalletPresenter.checkInputValue(
			nameInput.text.toString(),
			passwordInput.text.toString(),
			repeatPasswordInput.text.toString(),
			isAgree,
			fragment.context
		) { passwordValue, walletName ->
			importWallet(
				privateKeyInput.text.toString(),
				passwordValue,
				walletName,
				fragment,
				hintInput.text?.toString()
			)
		}
	}

	companion object {

		/**
		 * 导入 `keystore` 是先把 `keystore` 解密成 `private key` 在存储, 所以这个方法是公用的
		 */
		fun importWallet(
			privateKey: String, password: String, name: String, fragment: Fragment, hint: String? = null
		) {
			// `Metamask` 的私钥有的时候回是 63 位的导致判断有效性的时候回出错这里弥补上
			// 默认去除多余的空格
			val currentPrivateKey = (if (privateKey.length == 63) "0$privateKey" else privateKey).replace(" ", "")
			// 首先检查私钥地址是否合规
			if (!WalletUtils.isValidPrivateKey(currentPrivateKey)) {
				fragment.context?.alert(ImportWalletText.unvalidPrivateKey)
				return
			}
			// 解析私钥并导入钱包
			fragment.context?.getWalletByPrivateKey(privateKey, password) { address ->
				WalletTable.getWalletByAddress(address!!) {
					it.isNull() isTrue {
						// 在数据库记录钱包信息
						WalletTable.insertAddress(address, name, hint) {
							// 创建钱包并获取默认的 `token` 信息
							CreateWalletPresenter.generateMyTokenInfo(address) {
								fragment.activity?.jump<SplashActivity>()
							}
							// 注册钱包地址用于发送 `Push`
							XinGePushReceiver.registerWalletAddressForPush()
						}
					} otherwise {
						fragment.context?.alert(ImportWalletText.existAddress)
					}
				}
			}
		}

	}
}