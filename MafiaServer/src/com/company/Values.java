package com.company;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * kips the values of the game
 */
public class Values {
    ArrayList<ClientHandler>attend=new ArrayList<>();
    ArrayList<ClientHandler> allPlayers=new ArrayList<>();
    ArrayList<ClientHandler>allCitizen=new ArrayList<>();
    ArrayList<ClientHandler>allMafia=new ArrayList<>();
    ArrayList<String>roleNames=new ArrayList<>();
    ArrayList<ClientHandler>dead=new ArrayList<>();
    ClientHandler doctor=null;
    ClientHandler lector=null;
    ClientHandler godFatherRole=null;
    ClientHandler detective=null;
    ClientHandler mayor=null;
    ClientHandler sniper=null;
    ClientHandler dieHard=null;
    ClientHandler psychologist=null;
    ClientHandler godFather=null;
    boolean dieHardGotShot;
    boolean godfatherAction=false;
    boolean allActions=false;
    boolean mayorAllowance=false;
    boolean voteDone=false;
    int dieHardAction=0;
    int playerNum=0;
    ClientHandler votedPlayer;
    ArrayList<ClientHandler> killedPlayer=new ArrayList<>();
    HashMap<String,Integer>votes=new HashMap<>();
    File chatFile;
    String gameState="test";
    int actionDone=0;
    ArrayList<String>chats=new ArrayList<>();
public Values(){
    roleNames.add("doctor");
    roleNames.add("lector");
    roleNames.add("detective");
    roleNames.add("mayor");
    roleNames.add("sniper");
    roleNames.add("dieHard");
    roleNames.add("psychologist");
    roleNames.add("godFather");
    chats.add("chat started");
    dieHardGotShot=false;
}


}
