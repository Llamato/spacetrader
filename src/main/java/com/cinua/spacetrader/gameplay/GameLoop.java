package com.cinua.spacetrader.gameplay;

import com.cinua.spacetrader.gamedata.Savegame;
import com.cinua.spacetrader.view.GameView;

public class GameLoop{
    public static final int success = 0;
    public static final int invalidSavegame = 1;
    public static final int invalidView = 2;

    public static int start(Savegame gameData, GameView gameView){
        if(gameData == null){
            return invalidSavegame;
        }if(gameView == null){
            return invalidView;
        }
        gameView.msgOut("Welcome back, " + gameData.data.player.getName());
        int gameResult = loop(gameData, gameView);
        return stop();
    }

    public static int loop(Savegame gameData, GameView gameView){
        boolean gameRunning = true;
        while(gameRunning){

            gameRunning = false;
        }
        return success;
    }

    public static int stop(){
        return success;
    }
}
