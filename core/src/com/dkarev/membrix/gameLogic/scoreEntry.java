package com.dkarev.membrix.gameLogic;

/**
 * Created by Dobi on 8.6.2016 г..
 */
public class scoreEntry {

    private long elapsedTime = 0;
    private int clicks = 0;
    private int occurence = 0;
    private int diversity = 0;
    public String player = "";

    public long getElapsedTime() {
        return elapsedTime;
    }

    public int getOccurence() {
        return occurence;
    }

    public int getDiversity() {
        return diversity;
    }

    public int getClicks() {
        return clicks;
    }

    public scoreEntry(
            long elapsedTime,
            int clicks,
            int occurence,
            int diversity,
            String player) {
        this.elapsedTime = elapsedTime;
        this.clicks = clicks;
        this.occurence = occurence;
        this.diversity = diversity;
        this.player = player;
    }

    public scoreEntry() {

    }
}
