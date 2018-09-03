package io.goldstone.blockchain.crypto.eos

data class EOSAction(
	val account: String,
	val cryptoData: String,
	val methodName: String,
	val authorizationObjects: String
) {
	companion object {
		private fun createObject(action: EOSAction): String {
			return "{\"account\":\"${action.account}\",\"authorization\":${action.authorizationObjects},\"data\":\"${action.cryptoData}\",\"name\":\"${action.methodName}\"}"
		}

		fun createMultiActionObjects(vararg actions: EOSAction): String {
			var actionObjects = ""
			actions.forEach {
				actionObjects += createObject(it) + ","
			}
			return "[${actionObjects.substringBeforeLast(",")}]"
		}
	}
}