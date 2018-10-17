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
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.crypto.multichain.ChainNameID
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.CryptoName
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.model.NodeCell
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.model.NodeSelectionCell
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter.NodeSelectionPresenter
import org.jetbrains.anko.*

/**
 * @date 2018/6/20 8:59 PM
 * @author KaySaith
 */
class NodeSelectionFragment : BaseFragment<NodeSelectionPresenter>() {

	override val pageTitle: String = ChainText.nodeSelection
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
		Pair(CryptoName.eth, ChainText.infuraKovan),
		Pair(CryptoName.eth, ChainText.infuraRinkeby),
		Pair(CryptoName.etc, ChainText.goldStoneEtcMordenTest),
		Pair(CryptoName.etc, ChainText.etcMorden),
		Pair(CryptoName.btc, ChainText.btcTest),
		Pair(CryptoName.ltc, ChainText.ltcTest),
		Pair(CryptoName.bch, ChainText.bchTest),
		Pair(CryptoName.eos, ChainText.eosTest)
	)
	private val mainnetNodeList = arrayListOf(
		Pair(CryptoName.eth, ChainText.goldStoneMain),
		Pair(CryptoName.eth, ChainText.infuraMain),
		Pair(CryptoName.etc, ChainText.goldStoneEtcMain),
		Pair(CryptoName.etc, ChainText.etcMainGasTracker),
		Pair(CryptoName.btc, ChainText.btcMain),
		Pair(CryptoName.ltc, ChainText.ltcMain),
		Pair(CryptoName.bch, ChainText.bchMain),
		Pair(CryptoName.eos, ChainText.eosMain)
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
				bottomPadding = PaddingSize.device
				val nodes = nodeList(fromMainnetSetting.orTrue())
				nodes.distinctBy { it.first }.forEach { chain ->
					// Section Header
					when (chain.first) {
						CryptoName.eth -> NodeCell(context).ethType().into(this)
						CryptoName.btc -> NodeCell(context).btcType().into(this)
						CryptoName.ltc -> NodeCell(context).ltcType().into(this)
						CryptoName.bch -> NodeCell(context).bchType().into(this)
						CryptoName.eos -> NodeCell(context).eosType().into(this)
						else -> NodeCell(context).etcType().into(this)
					}
					// Nodes of Main or Test Chain
					val chainChild = nodes.filter {
						it.first.equals(chain.first, true)
					}
					// Generate cell with chain and node data
					chainChild.forEachIndexed { index, pair ->
						var isSelected = false
						if (pair.second == presenter.getCurrentChainName(
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
							pair.first.equals(CryptoName.ltc, true) -> 30
							pair.first.equals(CryptoName.bch, true) -> 40
							pair.first.equals(CryptoName.eos, true) -> 50
							else -> 0
						}
						NodeSelectionCell(context).apply {
							// 这个是画下划线的判断
							if (index == chainChild.lastIndex) isLast = true
						}.setData(pair.second, isSelected, id).click { it ->
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
					setBlueStyle(30.uiPX())
				}.click {
					fromMainnetSetting?.let { fromMainnet ->
						// 更新是否是测试环境的参数
						SharedValue.updateIsTestEnvironment(fromMainnet == false)
						selectedNode.forEach { pair ->
							when {
								pair.first.equals(CryptoName.eth, true) ->
									presenter.updateERC20ChainID(pair.second)
								pair.first.equals(CryptoName.btc, true) ->
									presenter.updateBTCChainID(pair.second)
								pair.first.equals(CryptoName.bch, true) ->
									presenter.updateBCHChainID(pair.second)
								pair.first.equals(CryptoName.ltc, true) ->
									presenter.updateLTCChainID(pair.second)
								pair.first.equals(CryptoName.eos, true) ->
									presenter.updateEOSChainID(pair.second)
								else -> presenter.updateETCChainID(pair.second)
							}
						}
						AppConfigTable.updateChainInfo(
							fromMainnet,
							etcChainNameID = ChainNameID.getChainNameIDByName(
								selectedNode.find { node ->
									node.first.equals(CryptoName.etc, true)
								}?.second.orEmpty()
							),
							ethSeriesID = ChainNameID.getChainNameIDByName(
								selectedNode.find { node ->
									node.first.equals(CryptoName.eth, true)
								}?.second.orEmpty()
							),
							btcChainNameID = ChainNameID.getChainNameIDByName(
								selectedNode.find { node ->
									node.first.equals(CryptoName.btc, true)
								}?.second.orEmpty()
							),
							bchChainNameID = ChainNameID.getChainNameIDByName(
								selectedNode.find { node ->
									node.first.equals(CryptoName.bch, true)
								}?.second.orEmpty()
							),
							ltcChainNameID = ChainNameID.getChainNameIDByName(
								selectedNode.find { node ->
									node.first.equals(CryptoName.ltc, true)
								}?.second.orEmpty()
							),
							eosChainNameID = ChainNameID.getChainNameIDByName(
								selectedNode.find { node ->
									node.first.equals(CryptoName.eos, true)
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
			CryptoName.ltc -> ChainType.LTC
			CryptoName.bch -> ChainType.BCH
			CryptoName.eos -> ChainType.EOS
			else -> ChainType.ETC
		}
	}

	private fun clearAllRadio(maxIndex: Int, type: ChainType) {
		val start = when (type) {
			ChainType.ETC -> 10
			ChainType.BTC -> 20
			ChainType.LTC -> 30
			ChainType.BCH -> 40
			ChainType.EOS -> 50
			else -> 0
		}
		(start until maxIndex + start).forEach {
			container.findViewById<NodeSelectionCell>(it)?.clearRadio()
		}
	}
}