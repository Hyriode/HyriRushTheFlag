package fr.hyriode.rushtheflag.api;

import redis.clients.jedis.JedisPool;

public class RTFAPI {

    private final RTFPlayerManager playerManager;
    private final JedisPool jedisPool;

    public RTFAPI(JedisPool jedisPool) {
        this.playerManager = new RTFPlayerManager(this);
        this.jedisPool = jedisPool;
    }

    public String getRedisKey() {
        return "rtf:";
    }

    public RTFPlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }
}
