package me.sebaarkadia.practice.settings;

public class Settings
{
    private boolean scoreboard;
    private boolean duelRequests;
    private boolean publicChat;
    private boolean time;
    private boolean message;
    private boolean seeall;
    private boolean mod;
    
    public Settings() {
        this.scoreboard = true;
        this.duelRequests = true;
        this.publicChat = true;
        this.time = true;
        this.message = true;
        this.mod = false;
    }
    
    public boolean isScoreboard() {
        return this.scoreboard;
    }
    
    public boolean isMod() {
        return this.mod;
    }
    
    public boolean isSeeAll() {
        return this.seeall;
    }
    
    public boolean isDuelRequests() {
        return this.duelRequests;
    }
    
    public boolean isPublicChat() {
        return this.publicChat;
    }
    
    public boolean isTime() {
        return this.time;
    }
    
    public boolean isMessage() {
        return this.message;
    }
    
    public void setScoreboard(final boolean scoreboard) {
        this.scoreboard = scoreboard;
    }
    
    public void setSeeAll(final boolean seeall) {
        this.seeall = seeall;
    }
    
    public void setMod(final boolean mod) {
        this.mod = mod;
    }
    
    public void setDuelRequests(final boolean duelRequests) {
        this.duelRequests = duelRequests;
    }
    
    public void setPublicChat(final boolean publicChat) {
        this.publicChat = publicChat;
    }
    
    public void setTime(final boolean time) {
        this.time = time;
    }
    
    public void setMessage(final boolean message) {
        this.message = message;
    }
}
