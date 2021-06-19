package com.company;
import com.company.enumorators.GameStatus;
import com.company.enumorators.PlayerStatus;
import com.company.services.GameService;

import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.TableRowSorter;
import java.util.Scanner;

public class Game implements Runnable {
    GameService gameService;
    Player player;
    Thread showChat;
    Thread getChat;
    boolean chatRunning = false;

    public void register() {
        System.out.println("write your name!");
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();
        while (!gameService.corm(name).equals("null")){
            System.out.println("player with this name exists!");
            name = sc.nextLine();
        }
        System.out.println("your role: " + gameService.newPlayer(name));
        player = gameService.getPlayer();

    }

    @Override
    public void run() {
        gameService = Main.gameService;
        register();
    }
}
