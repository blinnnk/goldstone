package io.goldstone.blockchain.common.value

/**
 * @date 2018/5/25 8:14 PM
 * @author KaySaith
 */
enum class ChainID(val id: String) {
	
	Main("1"),
	Ropsten("3"),
	Rinkeby("4"),
	Kovan("42"),
	ETCMain("61"),
	ETCTest("62");
	
	companion object {
		fun getAllChainID(): ArrayList<String> {
			return arrayListOf(
				ChainID.Main.id,
				ChainID.Ropsten.id,
				ChainID.Kovan.id,
				ChainID.Rinkeby.id,
				ChainID.ETCTest.id,
				ChainID.ETCMain.id
			)
		}
		
		fun getChainNameByID(chainID: String): String {
			return when (chainID) {
				Main.id -> ChainText.goldStoneMain
				Ropsten.id -> ChainText.ropsten
				Ropsten.id -> ChainText.infuraRopsten
				Kovan.id -> ChainText.kovan
				Rinkeby.id -> ChainText.rinkeby
				ETCMain.id -> ChainText.goldStoneEtcMain
				ETCMain.id -> ChainText.etcMainGasTracker
				ETCTest.id -> ChainText.morden
				else -> ChainText.goldStoneMain
			}
		}
		
		fun getChainIDByName(name: String): String {
			return when (name) {
				// GoldStone ERC Node
				ChainText.goldStoneMain -> Main.id
				ChainText.ropsten -> Ropsten.id
				ChainText.kovan -> Kovan.id
				ChainText.rinkeby -> Rinkeby.id
				// Infura ERC Node
				ChainText.infuraMain -> Main.id
				ChainText.infuraRopsten -> Ropsten.id
				ChainText.infuraKovan -> Kovan.id
				ChainText.infuraRinkeby -> Rinkeby.id
				// ETC Node
				ChainText.morden -> ETCTest.id
				ChainText.goldStoneEtcMain -> ETCMain.id
				ChainText.etcMainGasTracker -> ETCMain.id
				else -> Main.id
			}
		}
	}
}