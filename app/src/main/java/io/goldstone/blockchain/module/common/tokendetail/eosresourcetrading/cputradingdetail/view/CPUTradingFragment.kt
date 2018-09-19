package io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.cputradingdetail.view

import android.support.v4.app.Fragment
import android.view.Gravity
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.SessionTitleView
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.TradingCardView
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.cputradingdetail.presenter.CPUTradingPresenter
import org.jetbrains.anko.*
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/18
 */
class CPUTradingFragment : BaseFragment<CPUTradingPresenter>() {

	private val cpuTitle by lazy {
		SessionTitleView(context!!).setTitle(TokenDetailText.delegateCPUTitle)
	}

	private val netTitle by lazy {
		SessionTitleView(context!!).setTitle(TokenDetailText.refundCPUTitle)
	}

	private val cpuTradingCard by lazy {
		TradingCardView(context!!).apply { setAccountHint(Config.getCurrentEOSName()) }
	}

	private val netTradingCard by lazy {
		TradingCardView(context!!).apply { setAccountHint(Config.getCurrentEOSName()) }
	}

	override val presenter = CPUTradingPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams(matchParent, matchParent)
				gravity = Gravity.CENTER_HORIZONTAL
				topPadding = 20.uiPX()
				cpuTitle.into(this)
				cpuTitle.setSubtitle("0.0027 ", "Current Price: 0.0027 EOS/MS/Day", Spectrum.blue)
				cpuTradingCard.into(this)
				netTitle.into(this)
				netTitle.setSubtitle("0.0019", "Current Price: 0.0019 EOS/Byte/Day", Spectrum.blue)
				netTradingCard.into(this)
			}
		}
	}

	fun setCPUUsage(cpuWeight: String, cpuAvailable: BigInteger, total: BigInteger) {
		cpuTradingCard.setProcessValue(TokenDetailText.cpu, cpuWeight, cpuAvailable, total)
	}

	fun setNETUsage(netWeight: String, netAvailable: BigInteger, total: BigInteger) {
		netTradingCard.setProcessValue(TokenDetailText.cpu, netWeight, netAvailable, total)
	}

}