package com.example.services.redis

import io.ktor.server.config.*
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection

class RedisService(private val config: ApplicationConfig) {
    private lateinit var redisClient: RedisClient
    private lateinit var connection: StatefulRedisConnection<String, String>

    fun configureRedis(redisUrl: String = config.property("redis.server.url").getString()) {
        redisClient = RedisClient.create(redisUrl)
        connection = redisClient.connect()
    }

    fun get(key: String) : String? {
        return connection.sync().get(key)
    }

    fun set(key: String, value: String) {
        connection.sync().set(key, value)
    }
}