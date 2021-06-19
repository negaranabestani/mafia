package com.company;

import java.util.Scanner;

public class Psychologist extends Citizen {

    public Psychologist(String name) {
        super(name);
    }
    public void shutPlayer(){
        gameService.sendRoleAction(choosePlayer());
    }
    public String choosePlayer(){
        System.out.println("do you want to shut?");
        String name;
        Scanner sc=new Scanner(System.in);
        name=sc.nextLine();
        if (name.equals("yes")){
            System.out.println("who?");
            name=sc.nextLine();
        }
        return name;
    }

    @Override
    public void doAction() {
        shutPlayer();
    }
}
