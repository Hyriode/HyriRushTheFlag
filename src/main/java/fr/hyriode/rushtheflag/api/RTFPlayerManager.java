package fr.hyriode.rushtheflag.api;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class RTFPlayerManager {

    private final RTFAPI api;


    public RTFPlayerManager(RTFAPI api) {
        this.api = api;
    }

    public void sendPlayer(RTFPlayer player) {
        final Jedis jedis = this.api.getJedisPool().getResource();
        try {
            jedis.set(this.getRedisKey(player.getUuid().toString()), new Gson().toJson(player));
        } finally {
            jedis.close();
        }
    }

    public void removePlayer(RTFPlayer player) {
        final Jedis jedis = this.api.getJedisPool().getResource();
        try {
            jedis.del(this.getRedisKey(player.getUuid().toString()));
        } finally {
            jedis.close();
        }
    }

    public RTFPlayer getPlayer(UUID uuid) {
        final Jedis jedis = this.api.getJedisPool().getResource();
        try {
            final String json = jedis.get(this.getRedisKey(uuid.toString()));
            return new Gson().fromJson(json, RTFPlayer.class);
        } finally {
            jedis.close();
        }
    }

    private String getRedisKey(String uuid) {
        return this.api.getRedisKey() + "players:" + uuid;
    }

}