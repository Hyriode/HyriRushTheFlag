package fr.hyriode.rushtheflag.api;

import com.google.gson.Gson;
import fr.hyriode.hyriapi.HyriAPI;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class RTFPlayerManager {

    private final RTFAPI api;

    public RTFPlayerManager(RTFAPI api) {
        this.api = api;
    }

    public void sendPlayer(RTFPlayer player) {
        final Jedis jedis = HyriAPI.get().getRedisResource();

        jedis.set(this.getRedisKey(player.getUuid().toString()), new Gson().toJson(player));
        jedis.close();
    }

    public void removePlayer(RTFPlayer player) {
        final Jedis jedis = HyriAPI.get().getRedisResource();

        jedis.del(this.getRedisKey(player.getUuid().toString()));
        jedis.close();
    }

    public RTFPlayer getPlayer(UUID uuid) {
        final Jedis jedis = HyriAPI.get().getRedisResource();
        final String json = jedis.get(this.getRedisKey(uuid.toString()));

        jedis.close();

        return new Gson().fromJson(json, RTFPlayer.class);
    }

    private String getRedisKey(String uuid) {
        return this.api.getRedisKey() + "players:" + uuid;
    }

}