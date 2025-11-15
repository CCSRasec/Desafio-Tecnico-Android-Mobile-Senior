package com.seidor.data.model

data class UserApi(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val address: AddressApi,
    val phone: String,
    val website: String,
    val company: CompanyApi
)

data class AddressApi(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    val geo: GeoApi
)

data class GeoApi(
    val lat: String,
    val lng: String
)

data class CompanyApi(
    val name: String,
    val catchPhrase: String,
    val bs: String
)