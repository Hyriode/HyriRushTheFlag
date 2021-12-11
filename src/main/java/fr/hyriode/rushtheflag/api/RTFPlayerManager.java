package fr.hyriode.rushtheflag.api;

import com.google.gson.Gson;

import java.util.UUID;

public class RTFPlayerManager {

    private final RTFAPI api;

    public RTFPlayerManager(RTFAPI api) {
        this.api = api;
    }

    public void sendPlayer(RTFPlayer player) {
        this.api.getJedisPool().getResource().set(this.getRedisKey(player.getUuid().toString()), new Gson().toJson(player));
        this.api.getJedisPool().getResource().getClient().close();
    }

    public void removePlayer(RTFPlayer player) {
        this.api.getJedisPool().getResource().del(this.getRedisKey(player.getUuid().toString()));
        this.api.getJedisPool().getResource().getClient().close();
    }

    public RTFPlayer getPlayer(UUID uuid) {
        final String json = this.api.getJedisPool().getResource().get(this.getRedisKey(uuid.toString()));

        this.api.getJedisPool().getResource().getClient().close();

        return new Gson().fromJson(json, RTFPlayer.class);
    }

    private String getRedisKey(String uuid) {
        return this.api.getRedisKey() + "players:" + uuid;
    }

}