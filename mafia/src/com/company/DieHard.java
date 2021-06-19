package com.company;
import java.util.Scanner;

public class DieHard extends Citizen{
    int askRoleNum;
    public DieHard(String name) {
        super(name);
        askRoleNum=0;
    }

    public void askOutedPlayerRole(String answer){
        gameService.sendRoleAction(answer);
    }

    @Override
    public void doAction() {
        System.out.println("do you want to check roles?");
        Scanner c=new Scanner(System.in);
        String s=c.nextLine();
        askOutedPlayerRole(s);
    }
}
