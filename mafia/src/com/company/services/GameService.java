package com.company.services;
import com.company.enumorators.GameStatus;
import com.company.enumorators.PlayerStatus;
import com.company.*;
import com.company.interfaces.Doctor;
import com.company.models.GameRuntimeInformation;
import java.io.*;
import java.net.Socket;
class ServerListener extends Thread{
    Socket socket;
    GameService gameService=Main.gameService;
    DataInputStream codeIn;
    public ServerListener(Socket socket,DataInputStream in){
        this.socket=socket;
        codeIn=in;
    }

    @Override
    public void run() {
        try {
            String code;
            while (gameService.runtimeInformation.status!=GameStatus.Ended){
                code=codeIn.readUTF();
                if (code.equals("state")){
                    gameService.getState();
                }
                else if (code.equals("player_state")){
                    gameService.getPlayerState();
                }
                else if (code.equals("player_num")){
                    gameService.sendPlayerNum(gameService.getPlayer().tellPlayerNum());
                }
                else if (code.equals("healed")){
                    gameService.player.healed=true;
                }else if (code.equals("detectiveAnswer")){
                    gameService.player.godSays(codeIn.readUTF());
                }else if (code.equals("dieHardAnswer")){
                    String s=codeIn.readUTF();
                    while (!s.equals("done")){
                        gameService.player.godSays(s);
                        s=codeIn.readUTF();
                    }
                }else if (code.equals("shut_up")){
                    gameService.player.psychAllows=false;
                    gameService.player.godSays("you can not talk!");
                }else if (code.equals("do_action")){
                    gameService.player.doAction();
                }else if (code.equals("godfather_role")){
                    gameService.player.godSays("you are in charge of godfather's job ");
                    ((Mafia)gameService.player).godFatherRole=true;
                }else if (code.equals("knocked_out")){
                    String s=codeIn.readUTF();
                    while (!s.equals("done")){
                        gameService.player.godSays(s);
                        s=codeIn.readUTF();
                    }
                }
                //System.out.println(gameService.player.status);
            }
        }catch (IOException i){

        }

    }
}
public class GameService implements Runnable{
    Thread showChat;
    Thread getChat;
    boolean chatRunning=true;
    int registerPort=2300;
    int serverListenerPort=3200;
    int chatPort=12300;
    ObjectOutputStream oOut;
    ObjectInputStream oIn;
    DataOutputStream chatOut;
    DataInputStream chatIn;
    DataInputStream serverIn;
    DataOutputStream serverOut;
    String ip="127.0.0.1";
    Player player;
    GameRuntimeInformation runtimeInformation=new GameRuntimeInformation(GameStatus.Waiting);
    Socket socket;
    Socket chatSocket;
    Socket serverListenerSocket;
    public Player getPlayer(){
        return player;
    }
    public void getState() {
        String state;
        try {
            state=serverIn.readUTF();
            setState(state);
        }
        catch (IOException i){

        }
    }
    public void sendVote(String name){
        try {
            serverOut.writeUTF("vote");
            serverOut.writeUTF(name);
        }catch (IOException i){

        }
    }
    public void getPlayerState(){
        String state;
        try {
            state=serverIn.readUTF();
            setPlayerState(state);
        }
        catch (IOException i){

        }
    }
    private void setState(String state){
        System.out.println(state);
        if (state.equals("day"))

                runtimeInformation.status=GameStatus.Day;

        else if (state.equals("night"))
            runtimeInformation.status=GameStatus.Night;
        else if (state.equals("ended")){
            runtimeInformation.status=GameStatus.Ended;
            try {
                System.out.println(serverIn.readUTF());
            }catch (IOException i){

            }
        }
        else if (state.equals("voting")){
            runtimeInformation.status=GameStatus.Voting;
            player.talkative=false;
            player.vote();
        }
    }
    private void setPlayerState(String state){
        switch (state) {
            case "awake":
                player.status = PlayerStatus.Awake;
                //chatStart();
                player.setTalkative(true);
                break;
            case "asleep":
                player.status = PlayerStatus.Asleep;
                //chatStop();
                player.setTalkative(false);
                break;
            case "dead":
                if (!player.healed)
                    player.status = PlayerStatus.Dead;
                else if (!(this instanceof Doctor))
                    player.healed = false;
                break;
        }
        System.out.println(player.status);
//        System.out.println(player.talkative);


    }
    public String corm(String name){
        try {
            serverOut.writeUTF("corm");
            serverOut.writeUTF(name);
            String type=serverIn.readUTF();
           // System.out.println(type);
            return type;
        }catch (IOException i){

        }
        return null;
    }
    public GameRuntimeInformation getRuntimeInfo(){
        return runtimeInformation;
    }
    public String newPlayer(String name)  {
        try {
            //System.out.println("Connected");
            serverOut.writeUTF("new_player");
            //oOut.reset();
            //System.out.println("sent cod");
            String type=serverIn.readUTF();
            System.out.println(type);
            creatPlayer(type,name);
            //System.out.println("created "+player.getClass());
            serverOut.writeUTF(name);
            //oOut.reset();
            Thread serverListener=new ServerListener(serverListenerSocket,serverIn);
            serverListener.start();
            return type;
            //System.out.println("sent");

        }
        catch (IOException i){
            System.out.println(i);
        }
        return null;
    }
    public void sendLog(String log){
        try {
            chatOut.writeUTF(log);
        }catch (IOException i){

        }
    }
    public String receiveChat(){
        try {
            return chatIn.readUTF();
        }catch (IOException i){

        }
        return null;
    }
    public void sendPlayerNum(int num){
        try {
            serverOut.writeInt(num);
        }
        catch (IOException i){

        }
    }
    private void creatPlayer(String type,String name){

        if (type.equals("doctor"))
            player=new CitizenDoctor(name);
        else if (type.equals("lector"))
            player=new LectorDoctor(name);
        else if (type.equals("detective"))
            player=new Detective(name);
        else if (type.equals("mayor"))
            player=new Mayor(name);
        else if (type.equals("sniper"))
            player=new Sniper(name);
        else if (type.equals("dieHard"))
            player=new DieHard(name);
        else if (type.equals("psychologist"))
            player=new Psychologist(name);
        else if (type.equals("godFather"))
            player=new GodFather(name);
        else if (type.equals("citizen"))
            player=new Citizen(name);
        else if (type.equals("mafia"))
            player=new Mafia(name);
        showChat.start();
        getChat.start();
    }
    public void sendRoleAction(String action){
        player.talkative=false;
        try {
            serverOut.writeUTF("action");
            serverOut.writeUTF(action);
        }catch (IOException i){

        }
    }

    @Override
    public void run() {
        try {
            //socket=new Socket(ip,registerPort);
            chatSocket=new Socket(ip,chatPort);
            serverListenerSocket=new Socket(ip,serverListenerPort);
//            oOut=new ObjectOutputStream(socket.getOutputStream());
//            oOut.flush();
//            oIn= new ObjectInputStream(socket.getInputStream());
            chatIn=new DataInputStream(chatSocket.getInputStream());
            chatOut=new DataOutputStream(chatSocket.getOutputStream());
            serverIn=new DataInputStream(serverListenerSocket.getInputStream());
            serverOut=new DataOutputStream(serverListenerSocket.getOutputStream());
            showChat=new ShowChat();
            getChat=new GetChat();

        }
        catch (IOException i){

        }
    }

}
class ShowChat extends Thread{
    GameService gameService=Main.gameService;

    @Override
    public void run() {
        while (true){
                String chat = gameService.receiveChat();
                if (chat != null)
                    System.out.println(chat);

        }

    }
}
class GetChat extends Thread{
    GameService gameService=Main.gameService;

    @Override
    public void run() {
        while (!gameService.getRuntimeInfo().status.equals(GameStatus.Ended)) {
                    //System.out.println(gameService.player.talkative);
                    gameService.player.talk();

        }
    }
}
