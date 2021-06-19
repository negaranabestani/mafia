package com.company.models;

import com.company.Game;
import com.company.enumorators.GameStatus;

public class GameRuntimeInformation {
    public GameStatus status;
    public GameRuntimeInformation(GameStatus state){
        status=state;
    }
}

