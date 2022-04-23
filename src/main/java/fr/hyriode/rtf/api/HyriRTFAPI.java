package fr.hyriode.rtf.api;

import com.google.gson.Gson;
import fr.hyriode.rtf.api.player.HyriRTFPlayerManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:09
 */
public class HyriRTFAPI {

    public static final Gson GSON = new Gson();
    private final HyriRTFPlayerManager playerManager;

    public HyriRTFAPI() {
        this.playerManager = new HyriRTFPlayerManager(this);
    }

    public HyriRTFPlayerManager getPlayerManager() {
        return this.playerManager;
    }
}
