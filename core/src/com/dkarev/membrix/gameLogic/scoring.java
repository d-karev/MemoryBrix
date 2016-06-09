package com.dkarev.membrix.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.dkarev.membrix.MemoryBrixMain;

import java.util.ArrayList;

/**
 * Created by Dobi on 8.6.2016 г..
 */
public class scoring {

    private static final float coefElapsedTime = (float)0.1;
    private static final float coefClicks = (float)0.15;
    private static final float coefOccurence = (float)3.8;
    private static final float coefDiversity = (float)3.1;
    private static final float coefFinal = 100000;

    private static final int maxStoredScores = 5;

    private static final String scoreBoardFileName = "memBrixScoreBoard.brs";

    private scoring() {

    }

    public static float calculateScore(scoreEntry entry) {
        float result = coefFinal;
        result *= entry.getOccurence() * coefOccurence;
        result *= entry.getDiversity() * coefDiversity;
        result /= entry.getClicks() * coefClicks;
        result /= entry.getElapsedTime() * coefElapsedTime;
        return result;
    }

    public static boolean isHighScore(scoreEntry entry) {
        boolean result = false;
        float score = calculateScore(entry);

        ArrayList<scoreEntry> scoreBoard = getScoreBoard();

        if (scoreBoard.size() < maxStoredScores) {
            result = true;
        } else
            for (scoreEntry e: scoreBoard) {
                if (calculateScore(e) < score) {
                    result = true;
                    break;
                }
            }

        return result;
    }

    public static ArrayList<scoreEntry> getScoreBoard() {
        ArrayList<scoreEntry> result = new ArrayList<scoreEntry>();
        FileHandle fh = Gdx.files.local(scoreBoardFileName);
        if (fh.exists()) {
            Json json = new Json();
            result = json.fromJson(ArrayList.class, scoreEntry.class, fh);
        }
        return result;
    }

    public static boolean addScoreToBoard(scoreEntry entry) {
        Gdx.app.log(MemoryBrixMain.NAME, "Updating high scores...");

        if (!isHighScore(entry)) {
            Gdx.app.log(MemoryBrixMain.NAME, "Entry is not a high score!");
            return false;
        }

        FileHandle fh = Gdx.files.local(scoreBoardFileName);
        ArrayList<scoreEntry> oldScoreBoard = getScoreBoard();

        ArrayList<scoreEntry> newScoreBoard = new ArrayList<scoreEntry>();

        float score = calculateScore(entry);
        boolean entryAdded = false;

        if (oldScoreBoard.size() > 0)
            for (int k = 0; k < oldScoreBoard.size(); k++) {
                scoreEntry old = oldScoreBoard.get(k);

                if (calculateScore(old) >= score || entryAdded)
                    newScoreBoard.add(old);
                else {
                    newScoreBoard.add(entry);
                    entryAdded = true;
                }

                if (newScoreBoard.size() >= maxStoredScores)
                    break;
            }
        else
            newScoreBoard.add(entry);

        Json json = new Json();
        String jasonScore = json.toJson(newScoreBoard, ArrayList.class, scoreEntry.class);
        fh.writeString(jasonScore, false);

        Gdx.app.log(MemoryBrixMain.NAME, "Score board was updated!");
        return true;
    }

    public static void clearScoreBoard() {
        FileHandle fh = Gdx.files.local(scoreBoardFileName);
        if (fh.exists())
            fh.delete();
    }
}
