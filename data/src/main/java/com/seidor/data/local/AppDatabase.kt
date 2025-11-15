package com.seidor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.seidor.data.local.dao.user.UserDao
import com.seidor.data.model.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}