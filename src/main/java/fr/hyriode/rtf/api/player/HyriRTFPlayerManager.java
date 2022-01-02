package fr.hyriode.rtf.api.player;

import fr.hyriode.rtf.api.HyriRTFAPI;

import java.util.UUID;
import java.util.function.Function;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:27
 */
public class HyriRTFPlayerManager {

    private static final Function<UUID, String> REDIS_KEY = uuid -> HyriRTFAPI.REDIS_KEY + "players:" + uuid.toString();

    private final HyriRTFAPI api;

    public HyriRTFPlayerManager(HyriRTFAPI api) {
        this.api = api;
    }

    public HyriRTFPlayer getPlayer(UUID uuid) {
        final String json = this.api.getFromRedis(REDIS_KEY.apply(uuid));

        if (json != null) {
            return HyriRTFAPI.GSON.fromJson(json, HyriRTFPlayer.class);
        }
        return null;
    }

    public void sendPlayer(HyriRTFPlayer player) {
        this.api.redisRequest(jedis -> jedis.set(REDIS_KEY.apply(player.getUniqueId()), HyriRTFAPI.GSON.toJson(player)));
    }

    public void removePlayer(UUID uuid) {
        this.api.redisRequest(jedis -> jedis.del(REDIS_KEY.apply(uuid)));
    }

}
