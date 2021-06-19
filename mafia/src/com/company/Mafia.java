package com.company;

import java.util.Scanner;

public class Mafia extends Player{
    public boolean godFatherRole;
    public Mafia(String name) {
        super(name);
        checkRole=true;
        godFatherRole=false;
    }
    public void shootGun(){
        System.out.println("who do you want to kill?");

    }
    public void choosePlayer(){
        shootGun();
        String name;
        Scanner sc=new Scanner(System.in);
        name=sc.nextLine();
        gameService.sendRoleAction(name);
        System.out.println("action sent");
    }
    public void godFatherJob(){
        choosePlayer();
    }

}
