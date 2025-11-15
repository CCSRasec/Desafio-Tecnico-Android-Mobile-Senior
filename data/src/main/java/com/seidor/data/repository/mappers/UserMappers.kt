package com.seidor.data.repository.mappers

import com.seidor.data.model.UserApi
import com.seidor.data.model.UserEntity
import com.seidor.domain.model.Address
import com.seidor.domain.model.Company
import com.seidor.domain.model.Geo
import com.seidor.domain.model.User


// Model -> Entity
fun UserApi.toEntity(now: Long): UserEntity = UserEntity(
    id = id,
    name = name,
    username = username,
    email = email,
    street = address.street,
    suite = address.suite,
    city = address.city,
    zipcode = address.zipcode,
    lat = address.geo.lat,
    lng = address.geo.lng,
    phone = phone,
    website = website,
    companyName = company.name,
    companyCatchPhrase = company.catchPhrase,
    companyBs = company.bs,
    cacheTimestamp = now
)

// Entity -> Domain
fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    username = username,
    email = email,
    address = Address(
        street = street,
        suite = suite,
        city = city,
        zipcode = zipcode,
        geo = Geo(lat = lat, lng = lng)
    ),
    phone = phone,
    website = website,
    company = Company(
        name = companyName,
        catchPhrase = companyCatchPhrase,
        bs = companyBs
    )
)