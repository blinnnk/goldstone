package io.goldstone.blockchain.module.home.profile.fingerprintsetting.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.UiThread
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.gsfragment.GSFragment
import io.goldstone.blockchain.common.component.DescriptionView
import io.goldstone.blockchain.common.component.cell.GraySquareCell
import io.goldstone.blockchain.common.component.cell.SwitchCell
import io.goldstone.blockchain.common.component.cell.graySquareCell
import io.goldstone.blockchain.common.component.cell.switchCell
import io.goldstone.blockchain.common.component.overlay.Dashboard
import io.goldstone.blockchain.common.component.overlay.LoadingView
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.common.utils.FingerPrintManager
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
import io.goldstone.blockchain.crypto.utils.KeystoreInfo
import io.goldstone.blockchain.module.home.profile.fingerprintsetting.contract.FingerprintSettingContract
import io.goldstone.blockchain.module.home.profile.fingerprintsetting.presenter.FingerprintSettingPresenter
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import javax.crypto.Cipher


/**
 * @author KaySaith
 * @date  2018/12/18
 */
class FingerprintSettingFragment : GSFragment(), FingerprintSettingContract.GSView {
	override val pageTitle: String = "Fingerprint Settings"
	override lateinit var presenter: FingerprintSettingContract.GSPresenter
	private lateinit var switchCell: SwitchCell
	private lateinit var fingerprintManager: FingerPrintManager
	private lateinit var loadingView: LoadingView

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
			scrollView {
				lparams(matchParent, matchParent)
				verticalLayout {
					loadingView = LoadingView(context)
					fingerprintManager = FingerPrintManager(context)
					gravity = Gravity.CENTER_HORIZONTAL
					topPadding = 60.uiPX()
					imageView {
						layoutParams = LinearLayout.LayoutParams(120.uiPX(), 120.uiPX())
						imageResource = R.drawable.fingerprint_icon
						setColorFilter(Spectrum.white)
						padding = 15.uiPX()
						addCorner(60.uiPX(), Spectrum.green)
					}
					DescriptionView(context).isFingerprint().into(this)
					switchCell = switchCell {
						hasTopLine = true
						horizontalSize = PaddingSize.content.toFloat()
					}
					switchCell.setMargins<LinearLayout.LayoutParams> {
						topMargin = 20.uiPX()
					}
				}
			}
		}.view
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter = FingerprintSettingPresenter(this)
		presenter.start()
		presenter.getUsedStatus { isChecked ->
			switchCell.setSelectedStatus(isChecked)
			if (!isChecked) updateButtonStatus()
			else turnOffFingerprint()
		}
	}

	override fun showError(error: Throwable) {
		ErrorDisplayManager(error).show(context)
	}

	override fun onDestroy() {
		super.onDestroy()
		fingerprintManager.removeHandler()
	}

	private fun turnOffFingerprint() {
		with(switchCell) {
			setTitle("Using Fingerprint Payment")
			clickEvent = Runnable {
				Dashboard(context) {
					showAlert(
						"Turn Off Fingerprint",
						"are you sure that you decide tto turn off your fingerprint payment function"
					) {
						presenter.turnOffFingerprintPayment {
							dialog.dismiss()
							context.alert(CommonText.succeed)
						}
					}
				}
			}
		}
	}

	private fun updateButtonStatus() {
		when {
			fingerprintManager.checker().isValid() || !fingerprintManager.checker().isUnsupportedDevice() -> {
				with(switchCell) {
					setTitle("USE FINGERPRINT PAYMENT")
					clickEvent = Runnable {
						loadingView.show()
						Dashboard(context) {
							showAlertView(
								"Permission Verify",
								"verify you identity before you setting fingerprint function",
								true
							) { passwordInput ->
								// 校验 `Keystore` 密码来验证身份
								val password = passwordInput?.text.toString()
								// 如果是 `Bip44` 钱包返回 `mnemonic` 如果是多链钱包返回 `root private key`
								presenter.getSecret(password) { secret ->
									launchUI {
										loadingView.remove()
										showFingerprintDashboard(context, false) { cipher, error ->
											if (cipher.isNotNull() && error.isNone()) {
												val fingerEncryptKey =
													JavaKeystoreUtil(KeystoreInfo.isFingerPrinter(cipher)).encryptData(secret)
												presenter.updateFingerEncryptKey(fingerEncryptKey) {
													SharedWallet.updateFingerprint(true)
													context.alert(CommonText.succeed)
												}
											} else if (cipher.isNull() && error.isNone()) {
												// Fingerprint cryptoObject 在部分机型返回 null
												// 这个时候就直接调用 Keystore 加密而不在额外用 Finger 返回的 cipher 做为条件
												val fingerEncryptKey =
													JavaKeystoreUtil(KeystoreInfo.isMnemonic()).encryptData(secret)
												presenter.updateFingerEncryptKey(fingerEncryptKey) {
													SharedWallet.updateFingerprint(true)
													context.alert(CommonText.succeed)
												}
											} else showError(error)
										}
									}
								}
							}
						}
					}
				}
			}
			fingerprintManager.checker().isUnsupportedDevice() -> {
				switchCell.setTitle("UNSUPPORTED DEVICE")
				switchCell.setSelectedStatus(false)
				switchCell.clickEvent = Runnable {
					Dashboard(switchCell.context) {
						showAlert(
							"Unsupported Fingerprint",
							"Unfortunately, your device does not support fingerprint payment."
						) {
							switchCell.setSelectedStatus(false)
						}
					}
				}
			}
			else -> {
				switchCell.setTitle("GO TO SET FINGERPRINT")
				switchCell.setSelectedStatus(false)
				switchCell.clickEvent = Runnable {
					val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
					startActivity(intent)
					switchCell.setSelectedStatus(false)
				}
			}
		}
	}

	companion object {
		/**
		 * 由于 Android 在部分机型的 Fingerprint CryptObject 返回的 Cipher 会为 null
		 * 但实际是成功的.
		 * 所以这里做了一些处理判断, 采用不同的 Keystore 加密方式
		 * 需要注意的是, 通过指纹的根本条件是 error.isNone()
		 */
		@UiThread
		fun showFingerprintDashboard(
			context: Context,
			showPasswordButton: Boolean,
			usePasswordEvent: () -> Unit = {},
			hold: (cipher: Cipher?, error: GoldStoneError) -> Unit
		) {
			val manager = FingerPrintManager(context)
			var passwordButton: GraySquareCell? = null
			val fingerView = LinearLayout(context).apply {
				orientation = LinearLayout.VERTICAL
				layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
				gravity = Gravity.CENTER_HORIZONTAL
				setPadding(PaddingSize.content, PaddingSize.content, PaddingSize.content, 0)
				imageView {
					padding = 10.uiPX()
					imageResource = R.drawable.fingerprint_icon
					setColorFilter(Spectrum.white)
					layoutParams = LinearLayout.LayoutParams(90.uiPX(), 90.uiPX())
					addCorner(45.uiPX(), Spectrum.green)
				}
				if (showPasswordButton) {
					passwordButton = graySquareCell {
						layoutParams = LinearLayout.LayoutParams(matchParent, 50.uiPX())
						setTitle("Use Password")
						showArrow()
					}
					passwordButton?.setMargins<LinearLayout.LayoutParams> {
						topMargin = 30.uiPX()
					}
				}
			}
			Dashboard(context) {
				showAttentionDashboard(
					"Fingerprint Detected",
					"put you finger on your sensor, then we can detect you fingerprint",
					fingerView
				) {
					manager.removeHandler()
				}
				manager.observing { cipher, error ->
					hold(cipher, error)
					manager.removeHandler()
					dialog.dismiss()
				}
				passwordButton?.onClick {
					usePasswordEvent()
					dialog.dismiss()
					passwordButton?.preventDuplicateClicks()
				}
			}
		}
	}
}