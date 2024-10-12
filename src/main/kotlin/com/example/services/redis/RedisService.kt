package com.example.services.redis

import io.ktor.server.config.*
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection

/**
 * Service for interacting with Redis to store and retrieve data.
 *
 * @property config The application configuration containing Redis server details.
 */
class RedisService(private val config: ApplicationConfig) {
    private lateinit var redisClient: RedisClient
    private lateinit var connection: StatefulRedisConnection<String, String>

    /**
     * Configures the Redis client with the provided Redis server URL.
     *
     * @param redisUrl The URL of the Redis server.
     *                 Defaults to the URL specified in the application configuration.
     */
    fun configureRedis(redisUrl: String = config.property("redis.server.url").getString()) {
        redisClient = RedisClient.create(redisUrl)
        connection = redisClient.connect()
    }

    /**
     * Retrieves the value associated with the specified key from Redis.
     *
     * @param key The key for which to retrieve the value.
     * @return The value associated with the key, or `null` if the key does not exist.
     */
    fun get(key: String) : String? {
        return connection.sync().get(key)
    }

    /**
     * Stores the specified value in Redis with the given key.
     *
     * @param key The key to associate with the value.
     * @param value The value to be stored.
     */
    fun set(key: String, value: String) {
        connection.sync().set(key, value)
    }
}