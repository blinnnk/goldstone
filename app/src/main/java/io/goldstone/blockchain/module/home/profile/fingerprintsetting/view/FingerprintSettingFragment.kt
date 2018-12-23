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
import android.widget.TextView
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
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.FingerprintPaymentText
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.common.utils.FingerPrintManager
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
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
	override val pageTitle: String = ProfileText.fingerprintSettings
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
		presenter = FingerprintSettingPresenter()
		presenter.start()
		switchCell.setSelectedStatus(SharedWallet.hasFingerprint())
		resetButtonEvent()
	}

	override fun showError(error: Throwable) {
		ErrorDisplayManager(error).show(context)
	}

	override fun onDestroy() {
		super.onDestroy()
		fingerprintManager.removeHandler()
	}

	private fun resetButtonEvent() {
		if (!SharedWallet.hasFingerprint()) updateButtonStatus()
		else turnOffFingerprint()
	}

	private fun turnOffFingerprint() {
		with(switchCell) {
			setTitle(FingerprintPaymentText.buttonStatusEnabled)
			clickEvent = Runnable {
				Dashboard(context) {
					showAlert(
						FingerprintPaymentText.turnOffAlertTitle,
						FingerprintPaymentText.turnOffAlertDescription
					) {
						presenter.turnOffFingerprintPayment {
							dismiss()
							context.alert(CommonText.succeed)
							resetButtonEvent()
						}
					}
				}
			}
		}
	}

	private fun updateButtonStatus() {
		when {
			fingerprintManager.checker().isValid() -> {
				with(switchCell) {
					setTitle(FingerprintPaymentText.buttonStatusUnset)
					clickEvent = Runnable {
						loadingView.show()
						Dashboard(context) {
							showAlertView(
								FingerprintPaymentText.permissionVerifyAlertTitle,
								FingerprintPaymentText.permissionVerifyAlertDescription,
								true,
								cancelAction = {
									loadingView.remove()
									switchCell.setSelectedStatus(SharedWallet.hasFingerprint())
								}
							) { passwordInput ->
								// 校验 `Keystore` 密码来验证身份
								val password = passwordInput?.text.toString()
								// 如果是 `Bip44` 钱包返回 `mnemonic` 如果是多链钱包返回 `root private key`
								presenter.getSecret(password) { secret, error ->
									launchUI {
										loadingView.remove()
										if (secret.isNotNull() && error.isNone()) {
											showFingerprintDashboard(
												context,
												false,
												cancelAction = {
													switchCell.setSelectedStatus(SharedWallet.hasFingerprint())
												}
											) { cipher ->
												if (cipher.isNotNull()) {
													val fingerEncryptKey =
														JavaKeystoreUtil(KeystoreInfo.isFingerPrinter(cipher)).encryptData(secret)
													presenter.updateFingerEncryptKey(fingerEncryptKey) {
														SharedWallet.updateFingerprint(true)
														context.alert(CommonText.succeed)
														resetButtonEvent()
													}
												} else {
													// Fingerprint cryptoObject 在部分机型返回 null
													// 这个时候就直接调用 Keystore 加密而不在额外用 Finger 返回的 cipher 做为条件
													val fingerEncryptKey =
														JavaKeystoreUtil(KeystoreInfo.isMnemonic()).encryptData(secret)
													presenter.updateFingerEncryptKey(fingerEncryptKey) {
														SharedWallet.updateFingerprint(true)
														context.alert(CommonText.succeed)
														resetButtonEvent()
													}
												}
											}
										} else {
											showError(error)
											SharedWallet.updateFingerprint(false)
											resetButtonEvent()
										}
									}
								}
							}
						}
					}
				}
			}
			fingerprintManager.checker().isUnsupportedDevice() -> {
				switchCell.setTitle(FingerprintPaymentText.buttonStatusUnsupport)
				switchCell.setSelectedStatus(false)
				switchCell.clickEvent = Runnable {
					Dashboard(switchCell.context) {
						showAlert(
							FingerprintPaymentText.unsupported,
							FingerprintPaymentText.unsupportedDescription
						) {
							switchCell.setSelectedStatus(false)
						}
					}
				}
			}
			else -> {
				switchCell.setTitle(FingerprintPaymentText.goToSetFingerprint)
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
			cancelAction: () -> Unit = {},
			hold: (cipher: Cipher?) -> Unit
		) {
			val manager = FingerPrintManager(context)
			var passwordButton: GraySquareCell? = null
			var description: TextView
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
				description = textView {
					layoutParams = LinearLayout.LayoutParams(matchParent, 35.uiPX())
					gravity = Gravity.CENTER or Gravity.BOTTOM
					textSize = fontSize(12)
					textColor = GrayScale.midGray
					typeface = GoldStoneFont.medium(context)
					text = FingerprintPaymentText.detecting
				}
				if (showPasswordButton) {
					passwordButton = graySquareCell {
						layoutParams = LinearLayout.LayoutParams(matchParent, 50.uiPX())
						setTitle(FingerprintPaymentText.usePassword)
						showArrow()
					}
					passwordButton?.setMargins<LinearLayout.LayoutParams> {
						topMargin = 30.uiPX()
					}
				}
			}
			Dashboard(context) {
				showAttentionDashboard(
					FingerprintPaymentText.authenticationAlertTitle,
					FingerprintPaymentText.authenticationAlertDescription,
					fingerView,
					cancelAction = {
						manager.removeHandler()
						dismiss()
						cancelAction()
					}
				)
				manager.observing { cipher, error ->
					// `cipher` 是 `null` `error.isNone()` 也可以传出去
					if (error.hasError()) {
						description.text = error.message
						description.textColor = Spectrum.lightRed
					} else {
						hold(cipher)
						manager.removeHandler()
						dismiss()
					}
				}
				passwordButton?.onClick {
					usePasswordEvent()
					dismiss()
					manager.removeHandler()
					passwordButton?.preventDuplicateClicks()
				}
			}
		}
	}
}