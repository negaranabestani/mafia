package com.company;

import com.company.interfaces.Doctor;

import java.util.Scanner;

public class LectorDoctor extends Mafia implements Doctor {


    public LectorDoctor(String name){
        super(name);
    }

    @Override
    public void heal(String name) {
        if (name.equals(this.name)&&!healed){
            healed=true;
            gameService.sendRoleAction(this.name);
        }else if (healed&&name.equals(this.name)){
            choosePlayer();
        }else
            gameService.sendRoleAction(name);
    }

    @Override
    public void choosePlayer() {
        System.out.println("whom do you want to heal?");
        String name;
        Scanner sc=new Scanner(System.in);
        name=sc.nextLine();
        System.out.println(gameService.corm(name));
        if (gameService.corm(name).equals("m"))
        heal(name);
        else {
            System.out.println("choose a mafia!");
            choosePlayer();
        }
    }

    @Override
    public void doAction() {
        choosePlayer();
    }
}
