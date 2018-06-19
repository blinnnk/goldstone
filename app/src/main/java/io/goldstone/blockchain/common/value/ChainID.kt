package io.goldstone.blockchain.common.value

/**
 * @date 2018/5/25 8:14 PM
 * @author KaySaith
 */
enum class ChainID(val id: String) {
	
	Main("1"),
	Ropstan("3"),
	Rinkeby("4"),
	Kovan("42");
	
	companion object {
		fun getAllChainID(): ArrayList<String> {
			return arrayListOf(
				ChainID.Main.id,
				ChainID.Ropstan.id,
				ChainID.Kovan.id,
				ChainID.Rinkeby.id
			)
		}
		
		fun getChainNameByID(chainID: String): String {
			return when (chainID) {
				Main.id -> ChainText.goldStoneMain
				Ropstan.id -> ChainText.ropsten
				Kovan.id -> ChainText.kovan
				Rinkeby.id -> ChainText.rinkeby
				else -> ChainText.goldStoneMain
			}
		}
	}
}