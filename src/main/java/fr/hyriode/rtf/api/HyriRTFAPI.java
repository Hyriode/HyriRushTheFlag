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

    public static final String REDIS_KEY = "rtf:";
    public static final Gson GSON = new Gson();

    private final HyriRTFPlayerManager playerManager;

    private final LinkedBlockingQueue<Consumer<Jedis>> redisRequests;
    private final Thread redisRequestsThread;

    private final JedisPool jedisPool;

    public HyriRTFAPI(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.redisRequests = new LinkedBlockingQueue<>();
        this.redisRequestsThread = new Thread(() -> {
            try {
                final Consumer<Jedis> request = this.redisRequests.take();

                try (final Jedis jedis = this.getRedisResource()) {
                    if (jedis != null) {
                        request.accept(jedis);
                    }
                }
            } catch (InterruptedException ignored) {}
        }, "RTF API - Redis processor");
        this.playerManager = new HyriRTFPlayerManager(this);
    }

    public void start() {
        this.redisRequestsThread.start();
    }

    public void stop() {
        this.redisRequestsThread.interrupt();
    }

    public JedisPool getJedisPool() {
        return this.jedisPool;
    }

    public Jedis getRedisResource() {
        return this.jedisPool.getResource();
    }

    public void redisRequest(Consumer<Jedis> request) {
        this.redisRequests.add(request);
    }

    public String getFromRedis(String key) {
        try (final Jedis jedis = this.getRedisResource()) {
            if (jedis != null) {
                return jedis.get(key);
            }
        }
        return null;
    }

    public HyriRTFPlayerManager getPlayerManager() {
        return this.playerManager;
    }
}
