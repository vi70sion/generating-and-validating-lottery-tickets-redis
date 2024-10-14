package org.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.util.Set;

public class RedisService {

    private final JedisPool jedisPool;

    public RedisService(String host, int port) {
        this.jedisPool = new JedisPool(host, port);
    }

    public Set<String> getKeys() {
        Jedis jedis = jedisPool.getResource();
        Set<String> keys = jedis.keys("TicketNumbers_*");
        return keys;
    }

    // Išsaugome String vertę tiesiogiai
    public void put(String key, String numbersAsJson) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, numbersAsJson);  // Nereikia serializuoti
        }
    }

    // Gauname String vertę tiesiogiai
    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);  // Nereikia deserializuoti
        }
    }

    public void close() {
        jedisPool.close();
    }
}