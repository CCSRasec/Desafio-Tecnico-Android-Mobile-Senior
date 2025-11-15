package com.seidor.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val username: String,
    val email: String,

    // address
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    val lat: String,
    val lng: String,

    val phone: String,
    val website: String,

    // company
    val companyName: String,
    val companyCatchPhrase: String,
    val companyBs: String,

    // para controle de refresh/ordenacao local
    val cacheTimestamp: Long
)