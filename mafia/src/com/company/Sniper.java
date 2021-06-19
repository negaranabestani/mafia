package com.company;

import java.util.Scanner;

public class Sniper extends Citizen{
    public Sniper(String name) {
        super(name);
    }

    public void choose(){
        System.out.println("do you want to shoot?");
        Scanner c=new Scanner(System.in);
        String s=c.nextLine();
        if (s.equals("yes")){
            s=c.nextLine();
        }
        gameService.sendRoleAction(s);
    }

    @Override
    public void doAction() {
        choose();
    }
}
