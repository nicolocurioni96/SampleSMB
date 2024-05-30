package com.nicolocurioni.samplesmb.shared

enum class Secrets(val secretValue: String) {

    sambaHost(""),
    sambaPort(""),
    sambaShare(""),
    sambaUsername(""),
    sambaPassword("");

    val value = secretValue
}