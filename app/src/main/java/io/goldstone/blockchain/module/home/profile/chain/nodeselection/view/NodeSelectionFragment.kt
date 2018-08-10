package io.goldstone.blockchain.module.home.profile.chain.nodeselection.view

import android.support.v4.app.Fragment
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.jump
import com.blinnnk.extension.orTrue
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ChainNameID
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.CryptoName
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.model.NodeSelectionCell
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.model.NodeSelectionSectionCell
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter.NodeSelectionPresenter
import org.jetbrains.anko.*

/**
 * @date 2018/6/20 8:59 PM
 * @author KaySaith
 */
class NodeSelectionFragment : BaseFragment<NodeSelectionPresenter>() {

	private val fromMainnetSetting by lazy {
		arguments?.getBoolean(ArgumentKey.isMainnet)
	}
	private val nodeList: (isMainnet: Boolean) -> ArrayList<Pair<String, String>> = {
		if (it) mainnetNodeList
		else testnetNodeList
	}
	private val testnetNodeList = arrayListOf(
		Pair(CryptoName.eth, ChainText.ropsten),
		Pair(CryptoName.eth, ChainText.infuraRopsten),
		Pair(CryptoName.eth, ChainText.kovan),
		Pair(CryptoName.eth, ChainText.infuraKovan),
		Pair(CryptoName.eth, ChainText.rinkeby),
		Pair(CryptoName.eth, ChainText.infuraRinkeby),
		Pair(CryptoName.etc, ChainText.goldStoneEtcMorderTest),
		Pair(CryptoName.etc, ChainText.etcMorden),
		Pair(CryptoName.btc, ChainText.btcTest)
	)
	private val mainnetNodeList = arrayListOf(
		Pair(CryptoName.eth, ChainText.goldStoneMain),
		Pair(CryptoName.eth, ChainText.infuraMain),
		Pair(CryptoName.etc, ChainText.goldStoneEtcMain),
		Pair(CryptoName.etc, ChainText.etcMainGasTracker),
		Pair(CryptoName.btc, ChainText.btcMain)
	)
	private val confirmButton by lazy {
		RoundButton(context!!)
	}
	private lateinit var container: LinearLayout
	private val selectedNode = arrayListOf<Pair<String, String>>()
	override val presenter = NodeSelectionPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			container = verticalLayout {
				lparams(matchParent, matchParent)
				leftPadding = PaddingSize.device
				rightPadding = PaddingSize.device
				val nodes = nodeList(fromMainnetSetting.orTrue())
				nodes.distinctBy { it.first }.forEach { chain ->
					// Section Header
					when (chain.first) {
						CryptoName.eth -> NodeSelectionSectionCell(context).ethType().into(this)
						CryptoName.btc -> NodeSelectionSectionCell(context).btcType().into(this)
						else -> NodeSelectionSectionCell(context).etcType().into(this)
					}
					// Nodes of One Chain
					val chainChild = nodes.filter {
						it.first.equals(chain.first, true)
					}
					// Generate cell with chain and node data
					chainChild.forEachIndexed { index, pair ->
						var isSelected = false
						if (pair.second == presenter.getDefaultOrCurrentChainName(
								fromMainnetSetting.orTrue(),
								getChainTypeByName(chain.first)
							)
						) {
							selectedNode += pair
							isSelected = true
						}
						/**
						 * `ID` 用 `10` 为波段进行区分, ETH 0～10 ， ETC 10~20
						 */
						val id = index + when {
							pair.first.equals(CryptoName.etc, true) -> 10
							pair.first.equals(CryptoName.btc, true) -> 20
							else -> 0
						}
						NodeSelectionCell(context).setData(pair.second, isSelected, id).click { it ->
							clearAllRadio(chainChild.size, getChainTypeByName(chain.first))
							it.selectRadio()
							selectedNode.find {
								it.first.equals(chain.first, true)
							}?.let {
								selectedNode.remove(it)
							}
							selectedNode.add(pair)
						}.into(this)
					}
				}
				confirmButton.apply {
					text = CommonText.confirm
					setBlueStyle(50.uiPX())
				}.click { _ ->
					fromMainnetSetting?.let { it ->
						// 更新是否是测试环境的参数
						Config.updateIsTestEnvironment(!it)
						selectedNode.forEach { pair ->
							when {
								pair.first.equals(CryptoName.eth, true) ->
									presenter.updateERC20ChainID(pair.second)
								pair.first.equals(CryptoName.btc, true) ->
									presenter.updateBTCChainID(pair.second)
								else -> presenter.updateETCChainID(pair.second)
							}
						}
						AppConfigTable.updateChainInfo(
							it,
							ChainNameID.getChainNameIDByName(
								selectedNode.find {
									it.first.equals(CryptoName.etc, true)
								}?.second.orEmpty()
							),
							ChainNameID.getChainNameIDByName(
								selectedNode.find {
									it.first.equals(CryptoName.eth, true)
								}?.second.orEmpty()
							),
							ChainNameID.getChainNameIDByName(
								selectedNode.find {
									it.first.equals(CryptoName.btc, true)
								}?.second.orEmpty()
							)
						) {
							activity?.jump<SplashActivity>()
						}
					}
				}.into(this)
			}
		}
	}

	private fun getChainTypeByName(name: String): ChainType {
		return when (name) {
			CryptoName.eth -> ChainType.ETH
			CryptoName.btc -> ChainType.BTC
			else -> ChainType.ETC
		}
	}

	private fun clearAllRadio(maxIndex: Int, type: ChainType) {
		val start = when (type) {
			ChainType.ETC -> 10
			ChainType.BTC -> 20
			else -> 0
		}
		(start until maxIndex + start).forEach {
			container.findViewById<NodeSelectionCell>(it)?.clearRadio()
		}
	}
}