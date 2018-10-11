package io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.Language.EOSRAMText
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.crypto.multichain.ChainNameID
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.CryptoName
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.model.NodeSelectionCell
import io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorygeneralview.view.EOSMemoryTransactionHistoryFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramrank.view.RankFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.view.EOSRAMPriceTrendFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradepercent.view.RAMTradePercentFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.present.TraderMemoryDetailPresenter
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
class TraderMemoryDetailFragment : BaseFragment<TraderMemoryDetailPresenter>() {
	override val pageTitle: String
		get() = EOSRAMText.ramTradeRoom
	override val presenter: TraderMemoryDetailPresenter = TraderMemoryDetailPresenter(this)

	@SuppressLint("ResourceType", "CommitTransaction")
	override fun AnkoContext<Fragment>.initView() {
		AppConfigTable.getAppConfig {
			it?.apply {
				if (!isMainnet) {
					context?.toast("您当前在使用Jungle测试网络\n" +
						"目前内存行情仅支持EOS主网，当前显示的是主网数据")
				}
			}
		}
		relativeLayout {
			scrollView {
				layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
				verticalLayout {
					layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
					gravity = Gravity.CENTER_HORIZONTAL

					frameLayout {
						id = ElementID.chartView
					}.lparams(matchParent, wrapContent)
					addFragmentAndSetArgument<EOSRAMPriceTrendFragment>(ElementID.chartView) {
					}

					frameLayout {
						id = ElementID.rankList
					}.lparams(matchParent, 50.uiPX() * 11)
					addFragmentAndSetArgument<RankFragment>(ElementID.rankList) {}

					// 内存交易记录列表
					frameLayout {
						layoutParams = LinearLayout.LayoutParams(matchParent, 400.uiPX()).apply {
							topMargin = 10.uiPX()
							bottomMargin = 10.uiPX()
						}
						id = ElementID.eosMemoryTransactionHistoryList
					}
					addFragmentAndSetArgument<EOSMemoryTransactionHistoryFragment>(
						ElementID.eosMemoryTransactionHistoryList
					) {
					}

					frameLayout {
						id = ElementID.pieChart
					}
					addFragmentAndSetArgument<RAMTradePercentFragment>(ElementID.pieChart) {}

				}
			}

			linearLayout {
				val roundButton = RoundButton(context)
				roundButton.into(this)
				roundButton.apply {
					text = "买入/卖出 RAM"
					y -= 10.uiPX()
					setBlueStyle(20.uiPX(), ScreenSize.widthWithPadding - 40.uiPX())
					onClick { _ ->
						AppConfigTable.getAppConfig {
							it?.apply {
								if (!isMainnet) {
									switchTheMainNetworkPopupWindow()
								} else {
									presenter.merchandiseRAM()
								}
							}
						}
					}
				}
			}.apply {
				setAlignParentBottom()
				setCenterInHorizontal()
			}
		}
	}

	private fun switchTheMainNetworkPopupWindow() {
		context?.let {
			GoldStoneDialog.show(it) {
				showButtons("切换到主网") {
					switchToTheMainNetwork()
					GoldStoneDialog.remove(context)
				}
				setImage(R.drawable.network_browken_banner)
				setContent(
					"抱歉,EOS内存交易所\n目前只支持主网", "或者,想要直接切换到主网进行内存\n买卖吗"
				)
			}
		}
	}

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
	private val selectedNode = arrayListOf<Pair<String, String>>()

	fun switchToTheMainNetwork() {
		val nodes = mainnetNodeList
		nodes.distinctBy { it.first }.forEach { chain ->
			// Nodes of Main or Test Chain
			val chainChild = nodes.filter {
				it.first.equals(chain.first, true)
			}
			// Generate cell with chain and node data
			chainChild.forEachIndexed { _, pair ->
				if (pair.second == presenter.getCurrentChainName(
						true,
						getChainTypeByName(chain.first)
					)
				) {
					selectedNode += pair
				}

			}
		}

		true.let { fromMainnet ->
			// 更新是否是测试环境的参数
			Config.updateIsTestEnvironment(fromMainnet == false)
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

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		presenter.onHiddenChanged(hidden)
	}

}