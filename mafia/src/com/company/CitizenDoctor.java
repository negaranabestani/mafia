package com.company;

import com.company.interfaces.Doctor;

import java.util.Scanner;

public class CitizenDoctor extends Citizen implements Doctor {
    Boolean selfHeal=false;
    public CitizenDoctor(String name) {
        super(name);
    }

    @Override

    public void heal(String name){
        if (name.equals(this.name)&&!healed){
            healed=true;
            gameService.sendRoleAction(name);
        }else if (name.equals(this.name)&&healed){
            choosePlayer();
        }else
            gameService.sendRoleAction(name);

    }

    @Override
    public void choosePlayer(){
        System.out.println("whom do you want to heal?");
        String name;
        Scanner sc=new Scanner(System.in);
        name=sc.nextLine();
        heal(name);
    }

    @Override
    public void doAction() {
        choosePlayer();
    }
}
