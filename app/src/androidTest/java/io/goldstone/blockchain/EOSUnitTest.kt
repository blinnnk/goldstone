@file:Suppress("DEPRECATION")

package io.goldstone.blockchain

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.subgraph.orchid.encoders.Hex
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.DefaultPath
import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.eos.account.EosPrivateKey
import io.goldstone.blockchain.crypto.eos.accountregister.*
import io.goldstone.blockchain.crypto.eos.ecc.EcDsa
import io.goldstone.blockchain.crypto.eos.ecc.Sha256
import io.goldstone.blockchain.crypto.eos.eosram.EOSRamModel
import io.goldstone.blockchain.crypto.eos.header.TransactionHeader
import io.goldstone.blockchain.crypto.eos.netcpumodel.EOSNetCPUModel
import io.goldstone.blockchain.crypto.eos.transaction.*
import io.goldstone.blockchain.crypto.litecoin.BaseKeyPair
import io.goldstone.blockchain.module.home.home.view.MainActivity
import junit.framework.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * @date 2018/6/9 7:25 PM
 * @author KaySaith
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class EOSUnitTest {

	@Rule
	@JvmField
	val mActivityRule = ActivityTestRule(MainActivity::class.java)
	private val position = this.javaClass.simpleName

	@Test
	fun generateEOSPublicKey() {
		val expectResult = BaseKeyPair("EOS5Yw8KiBABxExQvLPj6XTBp3paLFh3G2YsmKYHSAcytLBujzWad", "L28YYpvzabFvEdb7nxVgZwE6Tbz8oEoZLuVQn6Aijopd2PQbqmHE")
		val mnemonic = "card eager cotton tag rally include order cheap soda october giggle easy"
		val path = DefaultPath.eosPath
		val keyPair = EOSWalletUtils.generateKeyPair(mnemonic, path)
		val compareResult = keyPair == expectResult
		LogUtil.debug("$position generateEOSPublicKey", "$keyPair")
		Assert.assertTrue("generateEOSPublicKey get Incorrect Result", compareResult)
	}

	@Test
	fun encryptEOSTransactionInfo() {
		val expectResult = "00a6823403ea30550000000000ea3055400d03000000000004454f5300000000026464"
		val transactionInfo = EOSTransactionInfo(
			"eosio.token",
			"eosio",
			200000L,
			"dd"
		)
		val transactionInfoCode = transactionInfo.serialize()
		val compareResult = transactionInfoCode == expectResult
		LogUtil.debug("$position encryptEOSTransactionInfo", transactionInfoCode)
		Assert.assertTrue("Encrypt TransactionInfo get Incorrect Result", compareResult)
	}

	@Test
	fun generateAction() {
		val expectResult = " [{\"account\":\"eosio.token\",\"authorization\":[{\"actor\":\"eosio.token\",\"permission\":\"active\"},{\"actor\":\"eosio.token\",\"permission\":\"active\"}],\"data\":\"00a6823403ea30550000000000ea3055400d03000000000004454f5300000000026464\",\"name\":\"transfer\"},{\"account\":\"eosio.token\",\"authorization\":[{\"actor\":\"eosio.token\",\"permission\":\"active\"},{\"actor\":\"eosio.token\",\"permission\":\"active\"}],\"data\":\"00a6823403ea30550000000000ea3055400d03000000000004454f5300000000026464\",\"name\":\"transfer\"}]\n"
		val authorization = EOSAuthorization("eosio.token", EOSActor.Active)
		val authorizationObjects = EOSAuthorization.createMultiAuthorizationObjects(authorization, authorization)
		val action = EOSAction(
			"eosio.token",
			"00a6823403ea30550000000000ea3055400d03000000000004454f5300000000026464",
			"transfer",
			authorizationObjects
		)
		val result = EOSAction.createMultiActionObjects(action, action)
		val compareResult = result == expectResult
		LogUtil.debug("$position generateAction", result)
		Assert.assertTrue("Generate Actions Get Wrong Result", compareResult)
	}

	@Test
	fun serializedAction() {
//		val accountName = EOSUtils.getLittleEndianCode("eosio")
//		val method = EOSUtils.getLittleEndianCode("newaccount")
		// val actor = EOSUtils.getLittleEndianCode("kingofdragon") // 302933372dcaa683
		// val serializePermission = EOSUtils.getLittleEndianCode("active") // 00000000a8ed3232
		// val key = EOSUtils.getLittleEndianCode("wellwellwell") // 10a3e2312a1ea3e2
//		val pubKey = EosPublicKey("EOS55Lavobz5yaEWgXVtqwswrLMRbbvfUUwRCnJcsdQqGVvj6PrGr").bytes.toNoPrefixHexString()
		val dataByte = Hex.decode("302933372dcaa683c0e9c49c4ecce9c450c300000000000004454f5300000000")
		System.out.println(EOSUtils.getVariableUInt(dataByte.size))
	}

	@Test
	fun generateAuthorizations() {
		val expectResult = "[{\"actor\":\"eosio.token\",\"permission\":\"active\"},{\"actor\":\"eosio.token\",\"permission\":\"active\"}]"
		val authorization = EOSAuthorization("eosio.token", EOSActor.Active)
		val result = EOSAuthorization.createMultiAuthorizationObjects(authorization, authorization)
		val compareResult = result == expectResult
		LogUtil.debug("$position generateAuthorizations", result)
		Assert.assertTrue("Generate Authorizations Get Wrong Result", compareResult)
	}

	@Test
	fun generateUnSignedTransaction() {
		val expectResult = "{\"available_keys\":[\"EOS69UvbnXLnE3Kmzv7VkPbXnD1FQZjcv9DAxrASAXCPY1PYN2RZu\"],\"transaction\":{\"actions\":[{\"account\":\"eosio.token\",\"authorization\":[{\"actor\":\"eosio.token\",\"permission\":\"active\"}],\"data\":\"00a6823403ea30550000000000ea3055400d03000000000004454f5300000000026464\",\"name\":\"transfer\"}],\"context_free_actions\":[],\"context_free_data\":[],\"delay_sec\":0,\"expiration\":\"2018-05-29T15:50:20\",\"max_kcpu_usage\":0,\"max_net_usage_words\":0,\"ref_block_num\":31531,\"ref_block_prefix\":1954897243,\"signatures\":[]}}"
		val authorization = EOSAuthorization("eosio.token", EOSActor.Active)
		val authorizationObjects = EOSAuthorization.createMultiAuthorizationObjects(authorization)
		val action = EOSAction(
			"eosio.token",
			"00a6823403ea30550000000000ea3055400d03000000000004454f5300000000026464",
			"transfer",
			authorizationObjects
		)
		val transaction = UnSignedTransaction(
			UnSignedTransaction.prepareAvailableKeys(
				"EOS69UvbnXLnE3Kmzv7VkPbXnD1FQZjcv9DAxrASAXCPY1PYN2RZu"
			),
			EOSAction.createMultiActionObjects(action),
			"",
			"",
			0,
			"2018-05-29T15:50:20",
			0,
			0,
			31531,
			1954897243,
			""
		)
		val result = transaction.createObject()
		LogUtil.debug("$position generateUnSignedTransaction", result)
		val compareResult = result == expectResult
		Assert.assertTrue("Generate UnSignedTransaction Get Wrong Result", compareResult)
	}

	@Test
	fun getRefBlockPrefix() {
		val expectResult = 1954897243
		val headBlockID = "00007b2beeb3e1fb5b5d8574f7719149b550a73ed06939b7a8ba741bc82ad367"
		val result = EOSUtils.getRefBlockPrefix(headBlockID)
		val compareResult = result == expectResult
		LogUtil.debug("$position getRefBlockPrefix", "$result")
		Assert.assertTrue("Get Wrong Ref Block Prefix Result", compareResult)
	}

	@Test
	fun getRefBlockNumber() {
		val expectResult = 31531
		val headBlockID = "00007b2beeb3e1fb5b5d8574f7719149b550a73ed06939b7a8ba741bc82ad367"
		val result = EOSUtils.getRefBlockNumber(headBlockID)
		val compareResult = result == expectResult
		LogUtil.debug("$position getRefBlockNumber", "$result")
		Assert.assertTrue("Get Wrong Ref Block Number Result", compareResult)
	}

	@Test
	fun expirationDateToCode() {
		val expectResult = "1527580250"
		val expirationDate = "2018-05-29T15:50:20" // UTC
		val result = EOSUtils.getExpirationCode(expirationDate)
		val compareResult = result == expectResult
		LogUtil.debug("$position expirationDateToCode", result)
		Assert.assertTrue("Get Wrong ExpirationDateCode Result", compareResult)
	}

	@Test
	fun serializeUnSignedTransaction() {
		val data = "302933372dcaa683205c9cce4fe3bae6020000000000000004454f53000000000a74657374207472616e73"
		val authorization = EOSAuthorization("kingofdragon", EOSActor.Active)
		val authorizationObjects = EOSAuthorization.createMultiAuthorizationObjects(authorization)
		val action = EOSAction("eosio.token", data, "transfer", authorizationObjects)
		val serializedExpirationDate = EOSUtils.getExpirationCode(1535958970)
		val serializedRefBlockNumber = EOSUtils.getRefBlockNumberCode(12873742)
		val serializeRefBlockPrefix = EOSUtils.getRefBlockPrefixCode(1738495360)
		val serializableMaxNetUsageWords = EOSUtils.getVariableUInt(0)
		val serializableMaxKCpuUsage = EOSUtils.getVariableUInt(0)
		val serializableDelaySecond = EOSUtils.getVariableUInt(0)
		val contextFreeActions = listOf<String>()
		val serializableContextFreeActions = EOSUtils.getVariableUInt(contextFreeActions.size)
		val actions = listOf(action)
		val serializableActionSize = EOSUtils.getVariableUInt(actions.size)
		val serializeAccountName = EOSUtils.getLittleEndianCode("eosio.token")
		val serializeMethod = EOSUtils.getLittleEndianCode("transfer")
		val authorizations = listOf(authorization)
		val serializeAuthorizationSize = EOSUtils.getVariableUInt(authorizations.size)
		val serializeActorName = EOSUtils.getLittleEndianCode("kingofdragon")
		val serializePermission = EOSUtils.getLittleEndianCode("active")
		val serializeDataByteLength = EOSUtils.getVariableUInt(Hex.decode(data).size)
		val serializeTransactionExtension = "00"
		LogUtil.debug("$position serializeUnSignedTransaction",
			"serializedRefBlockNumber: $serializedRefBlockNumber \n" +
				"serializedExpirationDate: $serializedExpirationDate \n" +
				"serializeRefBlockPrefix: $serializeRefBlockPrefix \n" +
				"serializeMaxNetUsageWords: $serializableMaxNetUsageWords \n" +
				"serializableMaxKCpuUsage: $serializableMaxKCpuUsage \n" +
				"serializableDelaySecond: $serializableDelaySecond \n" +
				"serializableContextFreeActions: $serializableContextFreeActions \n" +
				"serializeActionSize: $serializableActionSize \n" +
				"serializeAccountName: $serializeAccountName \n" +
				"serializeMethod: $serializeMethod \n" +
				"serializeAuthorizationSize: $serializeAuthorizationSize \n" +
				"serializePermission: $serializePermission \n" +
				"serializeDataByteLength: $serializeDataByteLength"
		)
		val serializedCode =
			"serializeCode: " +
				serializedExpirationDate + serializedRefBlockNumber + serializeRefBlockPrefix +
				serializableMaxNetUsageWords + serializableMaxKCpuUsage + serializableDelaySecond + serializableContextFreeActions +
				serializableActionSize + serializeAccountName + serializeMethod + serializeAuthorizationSize + serializeActorName +
				serializePermission + serializeDataByteLength + data + serializeTransactionExtension
		LogUtil.debug("$position serializeUnSignedTransaction", serializedCode)
	}

	@Test
	fun digestForSignature() {
		val expectResult = "SIG_K1_KiKNEc71CkZpunpcrNa31kV9cg5JPrpAPp7SfSoweu7XbKMCwEoFrkLqysunhJc8kYPEW94UNnQ5SEuLeFKKfAoRRrUVLZ"
		val hash = Sha256.from(Hex.decode("038f4b0fc8ff18a4f0842a8f0564611f6e96e8535901dd45e43ac8691a1c4dcabadf8c5b0e7080559f67000000000100a6823403ea3055000000572d3ccdcd01302933372dcaa68300000000a8ed32322b302933372dcaa683205c9cce4fe3bae6020000000000000004454f53000000000a74657374207472616e73000000000000000000000000000000000000000000000000000000000000000000"))
		val eosPrivateKey = EosPrivateKey("5KQXER65zxzRcN1zsJpx6JjdP2kfHcPdrhendoXYY9MTyrLnXDv")
		val result = eosPrivateKey.sign(hash).toString()
		val compareResult = result == expectResult
		Assert.assertTrue("Get Wrong Signed Result", compareResult)
	}

	@Test
	fun serializedTransaction() {
		val transactionInfo = EOSTransactionInfo(
			"kingofdragon",
			"wuxianyinli2",
			2,
			"test trans"
		)
		val transactionInfoCode = transactionInfo.serialize()
		val header = TransactionHeader(ExpirationType.FiveMinute, 12873742, 1738495360)
		val authorization = EOSAuthorization("kingofdragon", EOSActor.Active)
		val authorizationObjects = EOSAuthorization.createMultiAuthorizationObjects(authorization)
		val action = EOSAction("eosio.token", transactionInfoCode, "transfer", authorizationObjects)
		EOSTransactionUtils.serialize(
			EOSChain.Test,
			header,
			listOf(action),
			listOf(authorization),
			transactionInfoCode
		).let {
			LogUtil.debug("$position serializedTransaction", "serialization: $it")
		}
	}

	@Test
	fun createEOSNewAccountObject() {
		val expectResult = "{\"account\":\"eosio\",\"name\":\"newaccount\",\"authorization\":[{\"actor\":\"kingofdragon\",\"permission\":\"active\"}],\"data\":{\"creator\":\"kingofdragon\",\"name\":\"snowsnowsnow\",\"owner\":{\"threshold\":1,\"keys\":[{\"key\":\"EOS55Lavobz5yaEWgXVtqwswrLMRbbvfUUwRCnJcsdQqGVvj6PrGr\",\"weight\":1},{\"key\":\"EOS55Lavobz5yaEWgXVtqwswrLMRbbvfUUwRCnJcsdQqGVvj6PrGr\",\"weight\":2}],\"accounts\":[],\"waits\":[]},\"active\":{\"threshold\":1,\"keys\":[{\"key\":\"EOS55Lavobz5yaEWgXVtqwswrLMRbbvfUUwRCnJcsdQqGVvj6PrGr\",\"weight\":1}],\"accounts\":[],\"waits\":[]}},\"hex_data\":\"\"}"
		val owners = listOf(
			ActorKey("EOS55Lavobz5yaEWgXVtqwswrLMRbbvfUUwRCnJcsdQqGVvj6PrGr", 1),
			ActorKey("EOS55Lavobz5yaEWgXVtqwswrLMRbbvfUUwRCnJcsdQqGVvj6PrGr", 2)
		)
		val actives = listOf(
			ActorKey("EOS55Lavobz5yaEWgXVtqwswrLMRbbvfUUwRCnJcsdQqGVvj6PrGr", 1)
		)
		val account = listOf<AccountActor>()
		val authorization = EOSAuthorization("kingofdragon", EOSActor.Active)
		val authorizations = listOf(authorization)
		val accountInfo = EOSNewAccountModel(
			authorizations,
			"kingofdragon",
			"snowsnowsnow",
			1,
			owners,
			account,
			1,
			actives,
			account
		)
		val result = accountInfo.createObject()
		val compareResult = result == expectResult
		LogUtil.debug("$position createEOSNewAccountObject", result)
		Assert.assertTrue("Get Wrong EOS New Account JSONObject Result", compareResult)
	}

	@Test
	fun createBuyRamObject() {
		val authorization = EOSAuthorization("kingofdragon", EOSActor.Active)
		val authorizations = listOf(authorization)
		val ramModel = EOSRamModel(
			authorizations,
			"kingofdragon",
			"snowsnowsnow",
			50000
		)
		System.out.println(ramModel.createObject())
	}

	@Test
	fun createCPUNetObject() {
		val authorization = EOSAuthorization("kingofdragon", EOSActor.Active)
		val authorizations = listOf(authorization)
		val netCPUModel = EOSNetCPUModel(
			authorizations,
			"kingofdragon",
			"snowsnowsnow",
			50000,
			50000,
			false
		)
		System.out.println(netCPUModel.createObject())
	}

	@Test
	fun serializeRegisterModels() {
		val blockNumber = EOSUtils.getRefBlockNumber("00ca333674b90de693c1da1a2bf10c2af2a6f4c85cf655bff837be750c034a08")
		val prefix = EOSUtils.getRefBlockPrefix("00ca333674b90de693c1da1a2bf10c2af2a6f4c85cf655bff837be750c034a08")
		val header = TransactionHeader(
			ExpirationType.FiveMinute,
			blockNumber,
			prefix
		)
		val authorization = EOSAuthorization("kingofdragon", EOSActor.Active)
		val authorizations = listOf(authorization)
		/** NEW Account Model*/
		val owners = listOf(
			ActorKey("EOS69UvbnXLnE3Kmzv7VkPbXnD1FQZjcv9DAxrASAXCPY1PYN2RZu", 1)
		)
		val actives = listOf(
			ActorKey("EOS69UvbnXLnE3Kmzv7VkPbXnD1FQZjcv9DAxrASAXCPY1PYN2RZu", 1)
		)
		val account = listOf<AccountActor>()
		val accountInfo = EOSNewAccountModel(
			authorizations,
			"kingofdragon",
			"xxrkissleo11",
			1,
			owners,
			account,
			1,
			actives,
			account
		)
		val buyRamModel = EOSRamModel(
			authorizations,
			"kingofdragon",
			"xxrkissleo11",
			50000
		)
		val netCPUModel = EOSNetCPUModel(
			authorizations,
			"kingofdragon",
			"xxrkissleo11",
			50000,
			50000,
			false
		)
		System.out.println(
			EOSRegisterUtil.getRegisterSerializedCode(EOSChain.Test, header, accountInfo, buyRamModel, netCPUModel, false)
		)
	}

	@Test
	fun signPackedData() {
		val packedData = "038f4b0fc8ff18a4f0842a8f0564611f6e96e8535901dd45e43ac8691a1c4dca8cad8f5b363393c1da1a00000000030000000000ea305500409e9a2264b89a01302933372dcaa68300000000a8ed323266302933372dcaa6831002551163076fef01000000010002a5bd18039fb67451d9c192fba2b64fe988284cce252b7ff0840604ad9c21bb450100000001000000010002a5bd18039fb67451d9c192fba2b64fe988284cce252b7ff0840604ad9c21bb45010000000000000000ea3055000000004873bd3e01302933372dcaa68300000000a8ed323220302933372dcaa6831002551163076fef50c300000000000004454f53000000000000000000ea305500003f2a1ba6a24a01302933372dcaa68300000000a8ed323231302933372dcaa6831002551163076fef50c300000000000004454f530000000050c300000000000004454f530000000000000000000000000000000000000000000000000000000000000000000000000000"
		System.out.println(
			EosPrivateKey("5KQXER65zxzRcN1zsJpx6JjdP2kfHcPdrhendoXYY9MTyrLnXDv").sign(Sha256.from(Hex.decode(packedData)))
		)
	}
}


