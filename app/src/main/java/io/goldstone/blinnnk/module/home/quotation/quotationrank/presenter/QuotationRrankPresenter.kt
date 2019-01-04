package io.goldstone.blinnnk.module.home.quotation.quotationrank.presenter

import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blinnnk.common.language.HoneyLanguage
import io.goldstone.blinnnk.common.language.currentLanguage
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.common.thread.launchDefault
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.kernel.network.common.GoldStoneAPI
import io.goldstone.blinnnk.module.home.home.presneter.SilentUpdater
import io.goldstone.blinnnk.module.home.profile.currency.view.CurrencySymbol
import io.goldstone.blinnnk.module.home.quotation.quotationrank.contract.QuotationRankContract
import io.goldstone.blinnnk.module.home.quotation.quotationrank.model.QuotationRankTable
import java.math.BigDecimal


/**
 * @author KaySaith
 * @date  2019/01/02
 */
class QuotationRankPresenter(
	private val rankView: QuotationRankContract.GSView
) : QuotationRankContract.GSPresenter {
	private var lastRank = 0

	override fun start() {
		getGlobalData()
		loadFirstPage()
	}

	private fun getGlobalData() {
		GoldStoneAPI.getGlobalRankData { model, error ->
			launchUI {
				if (model.isNotNull() && error.isNone()) {
					rankView.showHeaderData(model)
				} else {
					rankView.showError(error)
				}
			}
		}
	}

	override fun loadFirstPage() {
		rankView.showLoadingView(true)
		launchDefault {
			val localData = QuotationRankTable.dao.getAll()
			if (localData.isEmpty()) SilentUpdater.updateQuotationRank {
				load {
					QuotationRankTable.dao.getAll()
				} then {
					rankView.showLoadingView(false)
					lastRank = it.lastOrNull()?.rank ?: 0
					rankView.updateData(it.toArrayList())
				}
			} else launchUI {
				lastRank = localData.last().rank
				rankView.showLoadingView(false)
				rankView.updateData(localData)
			}
		}
	}

	override fun loadMore() {
		rankView.showBottomLoading(true)
		GoldStoneAPI.getQuotationRankList(lastRank) { data, error ->
			launchUI {
				if (!data.isNullOrEmpty() && error.isNone()) {
					rankView.updateData(data)
					if (data.isNotEmpty()) {
						lastRank = data.last().rank
					}
				} else if (error.hasError()) {
					rankView.showError(error)
				}
				rankView.showBottomLoading(false)
			}
		}
	}


	companion object {

		enum class NumberUnit(val value: BigDecimal, private val chineseSymbol: String, private val englishSymbol: String) {
			Thousand(BigDecimal(Math.pow(10.0, 3.0)), "千", "T"),
			Million(BigDecimal(Math.pow(10.0, 6.0)), "百万", "M"),
			Billion(BigDecimal(Math.pow(10.0, 9.0)), "十亿", "B"),
			TenThousand(BigDecimal(Math.pow(10.0, 4.0)), "万", "W"),
			HundredMillion(BigDecimal(Math.pow(10.0, 8.0)), "亿", "Y");

			fun getUnit(): String {
				return when (currentLanguage) {
					HoneyLanguage.English.code, HoneyLanguage.Russian.code -> this.englishSymbol
					else -> this.chineseSymbol
				}
			}

			fun calculate(volume: BigDecimal): String {
				val result = volume.divide(value, 3, BigDecimal.ROUND_HALF_UP)
				return result.toPlainString() + getUnit()
			}
		}

		fun parseVolumeText(text: String): String {
			val volume = BigDecimal(text)
			fun getCurrencySymbol(): String {
				return CurrencySymbol.getSymbol(SharedWallet.getCurrencyCode())
			}
			return getCurrencySymbol() + if (currentLanguage == HoneyLanguage.English.code || currentLanguage == HoneyLanguage.Russian.code) {
				when {
					volume > NumberUnit.Billion.value ->
						NumberUnit.Billion.calculate(volume)
					volume > NumberUnit.Million.value ->
						NumberUnit.Million.calculate(volume)
					volume > NumberUnit.Thousand.value ->
						NumberUnit.Thousand.calculate(volume)
					else -> {
						volume.setScale(3, BigDecimal.ROUND_HALF_UP)
					}
				}
			} else when {
				volume > NumberUnit.HundredMillion.value ->
					NumberUnit.HundredMillion.calculate(volume)
				volume > NumberUnit.TenThousand.value ->
					NumberUnit.TenThousand.calculate(volume)
				else -> {
					volume.setScale(3, BigDecimal.ROUND_HALF_UP)
				}
			}
		}
	}
}