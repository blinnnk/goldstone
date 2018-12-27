package io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.view

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basecell.BaseRadioCell
import io.goldstone.blockchain.common.base.basecell.radioCell
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.common.component.cell.graySquareCell
import io.goldstone.blockchain.common.component.edittext.WalletEditText
import io.goldstone.blockchain.common.component.overlay.Dashboard
import io.goldstone.blockchain.common.component.overlay.LoadingView
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.language.EOSAccountText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.base.showDialog
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.contract.AuthorizationManagementContract
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.model.AuthorizationManagementModel
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.presenter.AuthorizationManagementPresenter
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.presenter.PaymentDetailPresenter
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.presenter.PrivatekeyActionType
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.wrapContent


/**
 * @author KaySaith
 * @date  2018/12/26
 */
class AuthorizationManagementFragment
	: GSRecyclerFragment<AuthorizationManagementModel>(), AuthorizationManagementContract.GSView {

	override val pageTitle: String = "Authorization Management"
	private val overlayFragment by lazy {
		getParentFragment<TokenDetailOverlayFragment>()
	}
	private val loadingView by lazy {
		LoadingView(context!!)
	}

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<AuthorizationManagementModel>?
	) {
		recyclerView.adapter = AuthorizationManagementAdapter(
			asyncData.orEmptyArray(),
			editAction = { publicKey, actor ->
				updatePermissionKey(publicKey, actor)
			},
			deleteAction = { publickey, actor ->
				deletePermissionKey(publickey, actor)
			}
		)
	}

	override lateinit var presenter: AuthorizationManagementContract.GSPresenter

	override fun showError(error: Throwable) {
		ErrorDisplayManager(error).show(context)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		asyncData = arrayListOf()
		overlayFragment?.showAddButton(true, false) {
			updatePermissionKey("", EOSActor.Active)
		}
		presenter = AuthorizationManagementPresenter(this)
		presenter.start()
	}

	override fun updateData(data: ArrayList<AuthorizationManagementModel>) = launchUI {
		updateAdapterDataSet<AuthorizationManagementAdapter>(data)
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		overlayFragment?.presenter?.popFragmentFrom<AuthorizationManagementFragment>()
	}

	private fun deletePermissionKey(publicKey: String, actor: EOSActor) {
		Dashboard(context!!) {
			showAlert(
				"Delete Permission Key",
				"are you sure to delete this key's permission in this account",
				cancelAction = {
					dismiss()
				},
				confirmAction = {
					PaymentDetailPresenter.getPrivatekey(
						context!!,
						ChainType.EOS,
						PrivatekeyActionType.SignData,
						cancelEvent = {
							loadingView.remove()
						},
						confirmEvent = {
							loadingView.show()
						}
					) { privateKey, error ->
						launchUI {
							loadingView.remove()
							if (privateKey.isNotNull() && error.isNone()) {
								presenter.updateAuthorization(
									publicKey,
									actor,
									EOSPrivateKey(privateKey),
									true
								) { response, responseError ->
									launchUI {
										if (response.isNotNull() && responseError.isNone()) {
											response.showDialog(context!!)
											presenter.refreshAuthList()
										} else showError(responseError)
									}
								}
							} else showError(error)
						}
					}
				}
			)
		}
	}

	private fun updatePermissionKey(key: String, actor: EOSActor) {
		showAddKeyDashboard(key, actor) { publicKey, eosActor ->
			PaymentDetailPresenter.getPrivatekey(
				context!!,
				ChainType.EOS,
				PrivatekeyActionType.SignData,
				cancelEvent = {
					loadingView.remove()
				},
				confirmEvent = {
					loadingView.show()
				}
			) { privateKey, error ->
				launchUI {
					loadingView.remove()
					if (privateKey.isNotNull() && error.isNone()) {
						presenter.updateAuthorization(
							publicKey,
							eosActor,
							EOSPrivateKey(privateKey),
							false
						) { response, responseError ->
							launchUI {
								if (response.isNotNull() && responseError.isNone()) {
									response.showDialog(context!!)
									presenter.refreshAuthList()
								} else showError(responseError)
							}
						}
					} else showError(error)
				}
			}
		}
	}

	private fun showAddKeyDashboard(
		publicKey: String,
		actor: EOSActor,
		confirmAction: (publicKey: String, actor: EOSActor) -> Unit
	) {
		var selectedPermission = EOSActor.Active
		val activeCell: BaseRadioCell
		val ownerCell: BaseRadioCell
		val keyInput: WalletEditText
		val editorView = LinearLayout(context).apply {
			orientation = LinearLayout.VERTICAL
			layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
			padding = PaddingSize.content
			graySquareCell {
				layoutParams = LinearLayout.LayoutParams(matchParent, 45.uiPX())
				setTitle(EOSAccountText.account)
				setSubtitle(SharedAddress.getCurrentEOSAccount().name)
			}
			keyInput = WalletEditText(context).apply {
				hint = EOSAccountText.enterPublicKey
				if (publicKey.isNotEmpty()) setText(publicKey)
				layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
			}
			keyInput.into(this)
			keyInput.setMargins<LinearLayout.LayoutParams> {
				topMargin = 15.uiPX()
				bottomMargin = 15.uiPX()
			}

			ownerCell = radioCell {
				setTitle(EOSActor.Owner.value)
			}

			activeCell = radioCell {
				setTitle(EOSActor.Active.value)
				setSwitchStatusBy(true)
			}

			ownerCell.click {
				activeCell.setSwitchStatusBy(false)
				it.setSwitchStatusBy(true)
				selectedPermission = EOSActor.Owner
			}

			if (actor.isOwner()) {
				activeCell.setSwitchStatusBy(false)
				ownerCell.setSwitchStatusBy(true)
				selectedPermission = EOSActor.Owner
			}

			activeCell.click {
				ownerCell.setSwitchStatusBy(false)
				it.setSwitchStatusBy(true)
				selectedPermission = EOSActor.Active
			}
		}

		Dashboard(context!!) {
			showDashboard(
				EOSAccountText.addNewPermissionTitle,
				EOSAccountText.addNewPermissionDescription,
				editorView,
				hold = {
					if (!EOSWalletUtils.isValidAddress(keyInput.getContent())) {
						showError(AccountError.InvalidAddress)
					} else confirmAction(keyInput.getContent(), selectedPermission)
					dismiss()
				}
			) {
				dismiss()
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		overlayFragment?.showAddButton(false) {}
	}

}