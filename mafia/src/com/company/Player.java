package com.company;


import com.company.enumorators.PlayerStatus;
import com.company.services.GameService;
import java.util.Scanner;

public class Player {
    GameService gameService;
    public String name;
    public boolean checkRole;
    public boolean healed;
    public boolean talkative;
    public boolean psychAllows;
    public PlayerStatus status;
    public int voteNumbers;
    public Player(String name){
        this.name=name;
        checkRole=true;
        status=PlayerStatus.Asleep;
        healed=false;
        voteNumbers=0;
        gameService=Main.gameService;
    }
    public void setTalkative(boolean value){
        talkative=value;
    }
    public void vote(){
        System.out.println("vote the one you want out:");
        String name;
        Scanner sc=new Scanner(System.in);
        name=sc.nextLine();
        gameService.sendVote(name);
    }
    public void talk(){
        //System.out.println(talkative);
        if (talkative&&psychAllows){
            String s;
            //System.out.print(name+": ");
            Scanner sc=new Scanner(System.in);
            if (!talkative)
                return;
            s=sc.nextLine();
            if (this instanceof Mafia&&((Mafia) this).godFatherRole){
                if (s.equals("action")){
                    talkative=false;
                    ((Mafia) this).godFatherJob();
                    //gameService.chatStop();
                }else if (talkative)
                    gameService.sendLog(s);

            }else if (talkative)
                gameService.sendLog(s);
        }

    }
    public Integer tellPlayerNum(){
        System.out.println("how many players are there going to be?");
        Scanner sc=new Scanner(System.in);
        int s=sc.nextInt();
        //sc.nextLine();
        return s;
    }
    public void godSays(String log){
        System.out.println("God: "+log);
    }
    public void doAction(){

    }



}

