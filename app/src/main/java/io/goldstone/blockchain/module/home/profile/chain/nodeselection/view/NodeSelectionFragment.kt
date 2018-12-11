package io.goldstone.blockchain.module.home.profile.chain.nodeselection.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.jump
import com.blinnnk.extension.orFalse
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.TinyNumberUtils
import com.blinnnk.util.getParentFragment
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.button.roundButton
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.multichain.node.ChainNodeTable
import io.goldstone.blockchain.crypto.multichain.node.ChainURL
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.model.NodeCell
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.model.NodeSelectionCell
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter.NodeSelectionPresenter
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
	private lateinit var confirmButton: RoundButton
	// ChainType and Node URL -  "194/https://www.eos.com"
	private var selectedNode: ArrayList<ChainNodeTable> = arrayListOf()
	private lateinit var container: LinearLayout
	override val presenter = NodeSelectionPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			container = verticalLayout {
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, matchParent)
				bottomPadding = PaddingSize.device
				showNodeList(fromMainnetSetting.orFalse()) {
					confirmButton = roundButton {
						text = CommonText.confirm
						setBlueStyle(30.uiPX())
					}.click {
						confirmNodeSelection()
					}
				}
			}
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<ProfileOverlayFragment>()?.presenter
			?.popFragmentFrom<NodeSelectionFragment>()
	}

	private fun LinearLayout.showNodeList(isMainnet: Boolean, callback: () -> Unit) {
		load {
			if (isMainnet) ChainNodeTable.dao.getMainnet()
			else ChainNodeTable.dao.getTestnet()
		} then { nodeList ->
			fun showChainSectionHeader(node: ChainNodeTable) = when {
				ChainType(node.chainType).isETH() -> NodeCell(context).ethType().into(this)
				ChainType(node.chainType).isBTC() -> NodeCell(context).btcType().into(this)
				ChainType(node.chainType).isLTC() -> NodeCell(context).ltcType().into(this)
				ChainType(node.chainType).isBCH() -> NodeCell(context).bchType().into(this)
				ChainType(node.chainType).isEOS() -> NodeCell(context).eosType().into(this)
				else -> NodeCell(context).etcType().into(this)
			}
			// 用来做 `UI` 波段, 当用户点击 `Radio Clear` 其他 选中样式的时候使用
			var chainTypeID = 0
			var chainType: Int? = null
			nodeList.forEachIndexed { index, node ->
				// 内存中更新用户当前正在使用的节点
				if (TinyNumberUtils.isTrue(node.isUsed)) selectedNode.add(node)
				if (node.chainType != chainType) {
					showChainSectionHeader(node)
					chainTypeID = getChainTypeID(node.chainType)
				}
				chainType = node.chainType
				NodeSelectionCell(context).setData(
					node.name,
					TinyNumberUtils.isTrue(node.isUsed),
					chainTypeID + index
				).click { cell ->
					setSelectedStatus(node.chainType, nodeList.size, cell.id)
					selectedNode.add(node)
				}.into(this)
			}
			callback()
		}
	}

	private fun setSelectedStatus(type: Int, dataCount: Int, id: Int) {
		val starID = getChainTypeID(type)
		(starID until starID + dataCount).forEach {
			container.findViewById<NodeSelectionCell>(it)?.setSelectedStatus(it == id)
		}
		selectedNode.removeAll { it.chainType == type }
	}

	private fun getChainTypeID(type: Int) = when {
		ChainType(type).isETH() -> 100
		ChainType(type).isBTC() -> 200
		ChainType(type).isLTC() -> 300
		ChainType(type).isBCH() -> 400
		ChainType(type).isEOS() -> 500
		ChainType(type).isETC() -> 600
		else -> 1000
	}

	private fun confirmNodeSelection() {
		SharedValue.updateIsTestEnvironment(fromMainnetSetting == false)
		selectedNode.forEach { node ->
			when {
				ChainType(node.chainType).isETH() ->
					presenter.updateERC20Chain(ChainURL(node))
				ChainType(node.chainType).isBTC() ->
					presenter.updateBTCChain(ChainURL(node))
				ChainType(node.chainType).isBCH() ->
					presenter.updateBCHChain(ChainURL(node))
				ChainType(node.chainType).isLTC() ->
					presenter.updateLTCChain(ChainURL(node))
				ChainType(node.chainType).isEOS() -> {
					presenter.updateEOSChain(ChainURL(node))
				}
				else -> presenter.updateETCChain(ChainURL(node))
			}
		}
		updateLocalNodeSelectedStatus {
			activity?.jump<SplashActivity>()
		}
	}

	private fun updateLocalNodeSelectedStatus(callback: () -> Unit) {
		GlobalScope.launch(Dispatchers.Default) {
			ChainNodeTable.dao.clearIsUsedStatus(if (fromMainnetSetting.orFalse()) 0 else 1)
			selectedNode.forEach {
				ChainNodeTable.dao.updateIsUsed(it.id, 1, it.chainID)
			}
			launchUI(callback)
		}
	}
}