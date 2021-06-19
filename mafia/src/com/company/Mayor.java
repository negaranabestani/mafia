package com.company;

import java.util.Scanner;

public class Mayor extends Citizen {

    public Mayor(String name) {
        super(name);
    }


    public void makeDecision(){
        System.out.println("do you want to cancel knocking out? yes/no");
        Scanner c=new Scanner(System.in);
        String s=c.nextLine();
        gameService.sendRoleAction(s);
    }

    @Override
    public void doAction() {
        makeDecision();
    }
}
