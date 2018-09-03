package io.goldstone.blockchain.crypto.eos

data class EOSAuthorization(
	val actor: String,
	val permission: String
) {
	companion object {
		private fun createObject(authorization: EOSAuthorization): String {
			return "{\"actor\":\"${authorization.actor}\",\"permission\":\"${authorization.permission}\"}"
		}

		fun createMultiAuthorizationObjects(vararg authorizations: EOSAuthorization): String {
			var objects = ""
			authorizations.forEach {
				objects += createObject(it) + ","
			}
			return "[${objects.substringBeforeLast(",")}]"
		}
	}
}