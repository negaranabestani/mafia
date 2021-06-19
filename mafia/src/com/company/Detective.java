package com.company;

import java.util.*;

public class Detective extends Player {
    HashMap<Player,String>knownRoles;
    public Detective(String name) {
        super(name);
        checkRole=false;
        knownRoles=new HashMap<>();
    }
    void askRole(String name){
        gameService.sendRoleAction(name);
    }
    public void choosePlayer(){
        System.out.println("whom do you want to know about?");
        String name;
        Scanner sc=new Scanner(System.in);
        name=sc.nextLine();
        askRole(name);
    }

    @Override
    public void doAction() {
        choosePlayer();
    }
}
