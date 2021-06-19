package com.company;

import com.company.services.GameService;

public class Main {
public static Game game=new Game();
public static GameService gameService=new GameService();
    public static void main(String[] args) {
        gameService.run();
        game.run();

    }
}
