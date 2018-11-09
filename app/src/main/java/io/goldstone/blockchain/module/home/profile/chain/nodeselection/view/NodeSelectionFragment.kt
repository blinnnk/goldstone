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
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.multichain.node.ChainNodeTable
import io.goldstone.blockchain.crypto.multichain.node.ChainURL
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.model.NodeCell
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.model.NodeSelectionCell
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter.NodeSelectionPresenter
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
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
	private val confirmButton by lazy {
		RoundButton(context!!)
	}
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
					confirmButton.apply {
						text = CommonText.confirm
						setBlueStyle(30.uiPX())
					}.click {
						confirmNodeSelection()
					}.into(this)
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
			val dao = GoldStoneDataBase.database.chainNodeDao()
			if (isMainnet) dao.getMainnet() else dao.getTestnet()
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
					clearAllSelectedStatus(node.chainType)
					cell.selectRadio()
					selectedNode.add(node)
				}.into(this)
			}
			callback()
		}
	}

	private fun clearAllSelectedStatus(type: Int) {
		val starID = getChainTypeID(type)
		(starID until starID + 10).forEach {
			container.findViewById<NodeSelectionCell>(it)?.clearRadio()
		}
		selectedNode.removeAll { it.chainType == type }
	}

	private fun getChainTypeID(type: Int) = when {
		ChainType(type).isETH() -> 10
		ChainType(type).isBTC() -> 20
		ChainType(type).isLTC() -> 30
		ChainType(type).isBCH() -> 40
		ChainType(type).isEOS() -> 50
		else -> 60
	}

	private fun confirmNodeSelection() {
		SharedValue.updateIsTestEnvironment(fromMainnetSetting == false)
		selectedNode.forEach { node ->
			when {
				ChainType(node.chainType).isETH() ->
					presenter.updateERC20ChainID(ChainURL(node))
				ChainType(node.chainType).isBTC() ->
					presenter.updateBTCChainID(ChainURL(node))
				ChainType(node.chainType).isBCH() ->
					presenter.updateBCHChainID(ChainURL(node))
				ChainType(node.chainType).isLTC() ->
					presenter.updateLTCChainID(ChainURL(node))
				ChainType(node.chainType).isEOS() ->
					presenter.updateEOSChainID(ChainURL(node))
				else -> presenter.updateETCChainID(ChainURL(node))
			}
		}
		updateLocalNodeSelectedStatus {
			activity?.jump<SplashActivity>()
		}
	}

	private fun updateLocalNodeSelectedStatus(callback: () -> Unit) {
		doAsync {
			val dao = GoldStoneDataBase.database.chainNodeDao()
			dao.clearIsUsedStatus(if (fromMainnetSetting.orFalse()) 0 else 1)
			selectedNode.forEach {
				dao.updateIsUsedByURL(it.url, true)
			}
			uiThread { callback() }
		}
	}
}