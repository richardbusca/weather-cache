package com.example.services.redis

import io.ktor.server.config.*
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands

class RedisService(private val config: ApplicationConfig) {
    private lateinit var redisClient: RedisClient
    private lateinit var connection: StatefulRedisConnection<String, String>

    fun configureRedis(redisUrl: String = config.property("redis.server.url").getString()) {
        redisClient = RedisClient.create(redisUrl)
        connection = redisClient.connect()
    }

    fun getSyncCommands(): RedisCommands<String, String> = connection.sync()

    fun get(key: String) : String{
        return getSyncCommands().get(key)
    }

    fun set(key: String, value: String) {
        getSyncCommands().set(key, value)
    }

    fun shutdown() {
        connection.close()
        redisClient.shutdown()
    }
}