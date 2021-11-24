package fr.hyriode.rushtheflag.api;

public class RTFAPI {

    private final RTFPlayerManager playerManager;

    public RTFAPI() {
        this.playerManager = new RTFPlayerManager(this);
    }

    public String getRedisKey() {
        return "rtf:";
    }

    public RTFPlayerManager getPlayerManager() {
        return this.playerManager;
    }

}
