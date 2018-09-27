package io.goldstone.blockchain.module.common.walletimport.walletimportcenter.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.setUnderline
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.title.AttentionTextView
import io.goldstone.blockchain.common.component.title.ExplanationTitle
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.language.QAText
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.WebUrl
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.common.walletimport.walletimportcenter.presenter.WalletImportCenterPresenter
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/09/06
 */

class WalletImportCenterFragment : BaseFragment<WalletImportCenterPresenter>() {

	override val pageTitle: String = ImportWalletText.importWallet
	private val attentionText by lazy { AttentionTextView(context!!) }
	private val supportedChainMenu by lazy { SupportedChainMenu(context!!) }
	override val presenter = WalletImportCenterPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				gravity = Gravity.CENTER_HORIZONTAL
				lparams {
					width = matchParent
					height = matchParent
				}
				attentionText.apply {
					isCenter()
					setPadding(15.uiPX(), 30.uiPX(), 15.uiPX(), 20.uiPX())
					layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
					text = ImportWalletText.importWalletDescription
				}.into(this)
				supportedChainMenu.into(this)
				// Method Cells
				// 导入助记词
				WalletImportMethodCell(context)
					.apply { setMnemonicType() }
					.click { presenter.showMnemonicImportFragment() }
					.into(this)
				// 导入私钥
				WalletImportMethodCell(context)
					.apply { setPrivateKeyType() }
					.click { presenter.showPrivateKeyImportFragment() }
					.into(this)
				// 导入 KeyStore
				WalletImportMethodCell(context)
					.apply { setKeystoreType() }
					.click { presenter.showKeystoreImportFragment() }
					.into(this)
				// QA Link
				ExplanationTitle(context).apply {
					text = QAText.whatIsMnemonic.setUnderline()
				}.click {
					getParentFragment<WalletImportFragment> {
						NetworkUtil.hasNetworkWithAlert(context) isTrue {
							presenter.showTargetFragment<WebViewFragment>(
								Bundle().apply {
									putString(ArgumentKey.webViewUrl, WebUrl.whatIsMnemonic)
									putString(ArgumentKey.webViewName, QAText.whatIsMnemonic)
								}
							)
						}
					}
				}.into(this)
			}
		}
	}
}