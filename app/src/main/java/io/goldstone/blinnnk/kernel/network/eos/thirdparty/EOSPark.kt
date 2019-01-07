package io.goldstone.blinnnk.kernel.network.eos.thirdparty

import com.blinnnk.extension.getRandom
import io.goldstone.blinnnk.crypto.eos.account.EOSAccount


/**
 * @author KaySaith
 * @date  2018/11/27
 */
object EOSPark {
	private val apikey: () -> String = {
		eosParkKeys.getRandom()
	}
	val getAccountBalance: (account: EOSAccount) -> String = {
		"https://api.eospark.com/api?module=account&action=get_token_list&apikey=${apikey()}&account=${it.name}"
	}
	val getTransactionByTXID: (txID: String) -> String = {
		"https://api.eospark.com/api?module=transaction&action=get_transaction_detail_info&apikey=${apikey()}&trx_id=$it"
	}
}

// `EtherScan` 对 `Key` 的限制很大, 用随机的办法临时解决, 降低测试的时候出问题的概率
private val eosParkKeys = listOf(
	"5887d6dd674a58eae3fe81ab08fb4aa7",
	"be079bd8edbca0e7df380d39fb9cc3a2",
	"405889915148da211da20f385a8f55c8",
	"7e472eb57111a6b099e467374faf9b06",
	"2ae1ed37928a4094e9ecd673d64d0d00",
	"610c5cab996ef4299c22cece3a5a877d",
	"3129db89f87b8f108ad1412103cadbc5",
	"65ef574c62ac2a7c120e129e2e8d9b3c",
	"f44f8856ca8cab20839f0dcddd93bfe6",
	"36f45fb00df9da15bc4c7911e8c0df91",
	"ea960a9f81edf9e6b514aa24ad7e1fb0",
	"6121fa229a24647d3c4cb4ebfcae3235",
	"682df6fc3a97acb16bdb971e53597eb5",
	"d387ab382bd07427efbcb31ca2ea479c",
	"0581dcabecaca24565778cfdfad34dcf",
	"b7521ee0520208be6eb1852b550d80b7",
	"930af9bc51771b6c50b3270708f6deef",
	"d6e4e78746237c5cdfbf9ef049c5a9c5",
	"c740ee4f2814413d7adf4942b2ea2ee2",
	"6c562cfba56b3e774c64efe6b074b495",
	"ac3d20c1ad274fa8f1a405c37bed911d",
	"2d7133290bed2abac3447d4755821cdb",
	"026e0cb993b91ad6d809c98dd3708ec6",
	"0874d019053fe5f45342bb2b5956855c",
	"3d24288126d6bbf77e15bd0238e931c0",
	"13f93c1d7f4ce5efcf601cea2d589664",
	"20a66457bf1593e84e38114fd8b5a6a2",
	"e05606a19785c19d309aefa1da6530ff",
	"63f0ae4a26ae3ee937b07f8d0b5d1d85",
	"a0d65cfcdc436d0e952c7cd591b22e04",
	"43ff84cbea37b6203254a0e80252f867",
	"b6e000d51e23a36f7e92dbb84c05c2e0",
	"afa1d1ae2dcd2d550a650c08ecbf550c",
	"a7e89920878ef470465823d911a57e1c",
	"13f60bd6bbb7985fe1db183f872098c6",
	"4c5769067a296278820e0e61e039a27e",
	"7d43d6a67336537090be6297a3f441f4",
	"657710364130939e870b4cce5bfed0d7",
	"5a22ac35be616218357c7f7d66a6cf0b",
	"07246ed537eeb13e6c10dbda94024722",
	"1b66b3df070cd5e675a3dedf075679c9",
	"01232118ec70f4b1daa77105cc05eec9",
	"7871e0591fd3ef225fb6e80c0a401eac",
	"c99ceea39c190afb80783a64649999e4",
	"1e660d549960578c4833d0822b7e3a04",
	"3c26a7b229c8f915928af29bf1e0f7d7",
	"423ec66038f67d062c6f783da33e5b80",
	"326903d2867f7600340a19a3f261b6b7",
	"274fb01bc20a202bfd459726229df69e",
	"a302850749be97ae5b1a44f244dc3b65",
	"0b5ebc4369ca7f9c462caba9ad6f75ec",
	"1c7294c3f67e72ee819ddf2c4f5a4e91",
	"b8a3703463c3f64e82dd70d8a4f422e8",
	"f0244dd68b50034d2796be6a871b8e72",
	"7bb73131011d5317c64cad293b21a9c4",
	"d769e649005dfe70e28b788a34375b41",
	"760cd2d81419a37ec0a975776edc2ae7",
	"85a8ceaa3e6f6fe27638a3f457f17de9",
	"d4330c6f5ce37181068c45109b9796e2",
	"284ac9a9076cb04eca1e807189a084a5",
	"bfbfe08f923fdd28b586130df7b071a8",
	"153be174d6743f709ae8331c45c51d21",
	"c88528ec8f4488b5cb6cd16d5aebab87",
	"ea0bb99c34eae9d44b58f0b978900cd1",
	"90c9333e2e1f3034ef17eb8e745bd34a",
	"01177de64df10b7337538e020026931c",
	"1e6791ff1ca5de8aecf8516dad1118f5",
	"9543a518fdb80014bb28714324660dac",
	"bc959fb82c9af88867243bf0a22ac547",
	"053d9b2403d73205778eafbcd0932642",
	"fa2f483d3e05e4cf87bb157424e7d4ca",
	"17a7a01c43240f4eadc0001f774071fe",
	"01676f1f8b76aa6f0448d94f1203fa59",
	"79b1de0401c95387db76087ceb85cab3",
	"de054c3ec2b723870ce089b1817e5e9a",
	"4f40c3cd9ac8400126ed6ee8f21edc6d",
	"308dc105a42c4fcce5a6553b93687378",
	"0355a1ffa0289d39a67f225a222610a6",
	"d6be3dc8c888a399d334aad8faef2fec",
	"e65297f15430e7763c97b495fddcb6bd",
	"186c487634cb58f9ee0e4e3959768b2c",
	"b79fe09685a660d403a42804b46deb36",
	"a7fb2f70a112c15d15a9949ea5cf527d",
	"67feaecc5211ed9e897a679768ffe59e",
	"d7038dc07129a397a268b0efe1fd6192",
	"c379a7a4532aeebd3ab9c77d5c463aa1",
	"1d5c20894d3500efdd4ad4daa506cb51",
	"4dbdae808c8c5535afeb65028f8098a6",
	"3c5e3ca36d65d1b34b38dcc781bb536b",
	"00a705eb7ff49444fc04c6cafe8d1a9f",
	"7716c2f6b15ef6879c77b3962002c8d9",
	"a7d9e638fd5e8bf1c3baeb2bfe5b012a",
	"75a590cc58babb843bff4c3095a68092",
	"8a1a06b1614c4cc5f69b2d5e82ceb542",
	"0d06d02777e0d3edbae0b16b9b75c677",
	"775a1154db14b438c9280d95bccca1a1",
	"14a635960f0ce180bd541b9e3e9c4d30",
	"2f704f94ea22ad86275b78ee041ba68d",
	"f5c9dbbe03a1333f483a6ff1a60c8816",
	"6578c56a0f7f12e23f06bae85bb3dd48"
)