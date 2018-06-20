package io.goldstone.blockchain.module.home.profile.chain.nodeselection.view

import android.support.v4.app.Fragment
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.orTrue
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ChainText
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.crypto.CryptoID
import io.goldstone.blockchain.crypto.CryptoName
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.model.NodeSelectionCell
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.model.NodeSelectionSectionCell
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter.NodeSelectionPresenter
import org.jetbrains.anko.*

/**
 * @date 2018/6/20 8:59 PM
 * @author KaySaith
 */
class NodeSelectionFragment : BaseFragment<NodeSelectionPresenter>() {
	
	private val isMainnet by lazy { arguments?.getBoolean(ArgumentKey.isMainnet) }
	private val nodeList: (isMainnet: Boolean) -> ArrayList<Pair<String, String>> = {
		if (it) mainnetNodeList
		else testnetNodeList
	}
	private val testnetNodeList = arrayListOf(
		Pair(CryptoName.eth, ChainText.ropsten),
		Pair(CryptoName.eth, ChainText.kovan),
		Pair(CryptoName.eth, ChainText.rinkeby),
		Pair(CryptoName.etc, ChainText.morden)
	)
	private val mainnetNodeList = arrayListOf(
		Pair(CryptoName.eth, ChainText.goldStoneMain),
		Pair(CryptoName.eth, ChainText.infuraMain),
		Pair(CryptoName.etc, ChainText.etcMain)
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
				val nodes = nodeList(isMainnet.orTrue())
				nodes.distinctBy { it.first }.forEach { chain ->
					// Section Header
					when (chain.first) {
						CryptoName.eth -> NodeSelectionSectionCell(context).ethType().into(this)
						else -> NodeSelectionSectionCell(context).etcType().into(this)
					}
					// Nodes of One Chain
					val chainChild = nodes.filter { it.first == chain.first }
					// Generate cell with chain and node data
					chainChild.forEachIndexed { index, pair ->
						var isSelected = false
						if (pair.second == presenter.getDefaultOrCurrentChainName(
								isMainnet.orTrue(),
								getChainIDByName(chain.first)
							)
						) {
							selectedNode += pair
							isSelected = true
						}
						NodeSelectionCell(context).setData(pair.second, isSelected, index).click {
							clearAllRadio(chainChild.size, getChainIDByName(chain.first))
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
				}.click {
					isMainnet?.let {
						selectedNode.forEach {
							if (it.first == CryptoName.eth) presenter.updateERC20TestChainID(it.second)
							else presenter.updateETCTestChainID(it.second)
						}
						presenter.updateDatabaseThenJump(it)
					}
				}.into(this)
			}
		}
	}
	
	private fun getChainIDByName(name: String): Int {
		return when (name) {
			CryptoName.eth -> CryptoID.eth
			else -> CryptoID.etc
		}
	}
	
	private fun clearAllRadio(maxIndex: Int, type: Int) {
		val start = when (type) {
			CryptoID.etc -> 10
			else -> 0
		}
		(start until maxIndex).forEach {
			container.findViewById<NodeSelectionCell>(it)?.clearRadio()
		}
	}
}