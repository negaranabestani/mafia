package com.company;

import javax.xml.transform.TransformerException;
import java.awt.image.AffineTransformOp;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * the main controller of server
 */
class God implements Runnable{

    ServerSocket serverSocket;
    ServerSocket chatServerSocket;
    ServerSocket serverCodeSocket;
    Values values=Main.values;
    @Override
    public void run() {
        try {

            serverSocket=new ServerSocket(2300);
            chatServerSocket=new ServerSocket(12300);
            serverCodeSocket=new ServerSocket(3200);
            System.out.println("serve started");
            /**
             * accepts new players until they rich to the number told at the first
             */
            while (values.allPlayers.size()!=values.playerNum){
                Socket chatSocket;
                Socket socket=null;
                Socket codeSocket;
                codeSocket=serverCodeSocket.accept();
                //socket=serverSocket.accept();
                chatSocket=chatServerSocket.accept();
                Thread god=new ClientHandler(socket,chatSocket,codeSocket);
                god.start();
                System.out.println(values.playerNum+"p"+values.allPlayers.size());
                if (values.allPlayers.size()==values.playerNum)
                    break;
            }
            godResponsibility();
        }
        catch (IOException i){

        }

    }

    /**
     *
     * @param group the group that stat is going to be sent for
     * @param state
     */
    public void sendGroupState(ArrayList<ClientHandler>group,String state){
        for (ClientHandler c:group){
            c.sendPlayerState(state);
        }
    }
    public boolean isGodfatherLiving(){
        return values.allPlayers.contains(values.godFatherRole);
    }
    public void putGodFatherRole(){
        values.godFatherRole=values.allMafia.get(values.allMafia.size()-1);
        values.godFatherRole.sendGodFatherRole();
    }
    /**
     * sends winner for everyone
     * @param winner
     */
    public void sendWinner(String winner){
        for (ClientHandler c:values.allPlayers){
            c.sendWinner(winner);
        }
    }

    /**
     * sends knocked out players for everyone
     */
    public void sendKnockedOut(){
        for (ClientHandler c: values.allPlayers)
            if (c!=null)
            c.sendKnockedOut();
    }

    /**
     * sensds the stat of game for every one
     * @param state
     */
    public void sendState(String state){
        values.gameState=state;
        if (state.equals("night")){
            values.allActions=false;
            values.godfatherAction=false;
            values.actionDone=0;
            values.killedPlayer.clear();
            values.voteDone=false;
        }
        for (ClientHandler c:values.allPlayers){
            c.sendState(state);
        }
    }

    /**
     * the process of the game which is running till one side wins
     */
    public void godResponsibility() {
        while (!values.gameState.equals("ended")){
            try {
                if (values.allMafia.size()==values.allCitizen.size()){
                    sendState("ended");
                    sendWinner("mafia");
                    break;
                }else if (values.allMafia.size()==0){
                    sendState("ended");
                    sendWinner("citizen");
                    break;
                }
                sendState("night");
                if (!isGodfatherLiving())
                    putGodFatherRole();
                sendGroupState(values.allCitizen,"asleep");
                sendGroupState(values.allMafia,"awake");
                //values.godFather.askAction();
                while (!values.godfatherAction){
                    //System.out.println("waiting");
                }
                sendGroupState(values.allMafia,"asleep");
//                values.lector.askAction();
//                values.doctor.askAction();
//                values.detective.askAction();
//                values.sniper.askAction();
//                values.psychologist.askAction();
//                values.dieHard.askAction();
                for (ClientHandler c:values.allPlayers){
                    if (!c.equals(values.mayor)&&!c.equals(values.godFather))
                        c.askAction();
                }
                while (!values.allActions){
                    //System.out.println("waiting....... ");
                }
                for (ClientHandler c:values.killedPlayer)
                    c.knockOut(c);
                sendState("day");
                sendGroupState(values.allPlayers,"awake");
                sendKnockedOut();
                Thread.sleep(60000);
                sendState("voting");
                while (!values.voteDone){
                    //System.out.println("voting");
                }
                if (values.allPlayers.contains(values.mayor)){
                    values.mayor.askAction();
                    while (!values.mayorAllowance) {
                        //System.out.println("..............waiting");
                    }
                }else
                    values.mayor.runAction("no");

            }
            catch (InterruptedException e){

            }
        }




    }

}

class ClientHandler extends Thread implements Runnable {
   Values values;
   String name;
   String type;
   Socket socket;
   ObjectOutputStream oOut;
   ObjectInputStream oIn;
   DataOutputStream codeOut;
   DataInputStream codeIn;
   Socket chatSocket;
   Socket codeSocket;
   String state="";
   public ClientHandler(Socket socket,Socket chatSocket,Socket codeSocket) {
       values=Main.values;
       this.socket=socket;
       this.chatSocket=chatSocket;
       this.codeSocket=codeSocket;
   }

    /**
     * gets the voted player name from client
     */
    public void getVot(){
       String name;
       try {
           name=codeIn.readUTF();
           if (values.votes.containsKey(name)){
               int n=values.votes.get(name);
               values.votes.replace(name,n+1);
           }else {
               values.votes.put(name,1);
           }
           int v=0;
           for (String n: values.votes.keySet()){
               v+=values.votes.get(n);
           }
           if (v==values.allPlayers.size()){
               maxVote();
               values.voteDone=true;
           }

       }catch (IOException i){

       }
   }

    /**
     * finds which player has the most vote and sets voted player to it
     */
   public void maxVote(){
       String name=null;
       int max=0;
       for (String n:values.votes.keySet()){
           if (values.votes.get(n)>max){
               max=values.votes.get(n);
               name=n;
           }
       }
       values.votedPlayer=findPlayer(name);
//       if (!values.dead.contains(findPlayer(name)))
//       knockOut(findPlayer(name));

   }

    /**
     * ask client to do its action
     */
    public void askAction(){
       try {
           codeOut.writeUTF("do_action");
       }catch (IOException i){

       }
    }

    /**
     * creat new player
     */
    public void newPlayer(){
       try {
           String type=getRole();
           codeOut.writeUTF(type);
           //oOut.reset();
           name=codeIn.readUTF();
           System.out.println(type+" added by name: "+name);
           addToLists(type);
//           sendState("night");
           sendPlayerState("awake");
       }
       catch (IOException i){

       }

    }

    /**
     * send the conversation for client
     * @param text
     */
    public void sendChat(String text){
       try {
           System.out.println("send chat");
           DataOutputStream out=new DataOutputStream(chatSocket.getOutputStream());
           if (text!=null)
           out.writeUTF(text);
       }catch (IOException i){

       }


    }
    public void sendGodFatherRole(){
        try {
            codeOut.writeUTF("godfather_role");
        }catch (IOException i){

        }
    }
    /**
     * send the game state
     * @param state
     */
    public void sendState(String state){
       try {
           codeOut.writeUTF("state");
           codeOut.writeUTF(state);
           System.out.println(state);
       }
       catch (IOException i){

       }
    }

    /**
     * send player state
     * @param state
     */
    public void sendPlayerState(String state){
       try {
           this.state=state;
           codeOut.writeUTF("player_state");
           codeOut.writeUTF(state);
       }catch (IOException i){

       }
    }

    /**
     * handles the coming texts and send it to everyone
     */
    public void chatRoom(){
       String s= getChat();
       if (s!=null){
           values.chats.add(s);
           for (ClientHandler c: values.allPlayers){
               if (!c.equals(this)&&c.state.equals("awake"))
                   c.sendChat(s);
           }
           System.out.println("chat added");
       }
    }

    /**
     * gets the text from client
     * @return
     */
    public String getChat(){
       try {
           DataInputStream in=new DataInputStream(chatSocket.getInputStream());
           String s=in.readUTF();
           return name+": "+s;
       }
       catch (IOException i){

       }
       return null;
    }

    /**
     * handles the role of each player
     * @return
     */
    private String getRole(){
        Random random=new Random();
        if (values.roleNames.size()>0){
            System.out.println("getting role"+values.roleNames.size());
            String role=values.roleNames.get(random.nextInt(values.roleNames.size()));
                if (role!=null){
                values.roleNames.remove(role);
                return role;
                }
            }
        int m= values.playerNum/3;
        if (values.allMafia.size()!=m){
            return "mafia";
        }
        return "citizen";
    }

    /**
     * put player in the belonged lists
     * @param type the player type ; mafia ,citizen ...
     */
    private void addToLists(String type){
       values.allPlayers.add(this);
       values.attend.add(this);
        if (type.equals("doctor")) {
            values.doctor = this;
            this.type="c";
            values.allCitizen.add(values.doctor);
        }
        else if (type.equals("lector")) {
            values.lector = this;
            this.type="m";
            values.allMafia.add(values.lector);
        }
        else if (type.equals("detective")) {
            this.type="c";
            values.detective=this;
            values.allCitizen.add(values.detective);
        }

        else if (type.equals("mayor")) {
            values.mayor=this;
            this.type="c";
            values.allCitizen.add(values.mayor);
        }
        else if (type.equals("sniper")){
            values.sniper=this;
            this.type="c";
            values.allCitizen.add(values.sniper);
        }

        else if (type.equals("dieHard")) {
            values.dieHard=this;
            this.type="c";
            values.allCitizen.add(values.dieHard);
        }
        else if (type.equals("psychologist")) {
            values.psychologist=this;
            this.type="c";
            values.allCitizen.add(values.psychologist);
        }
        else if (type.equals("godFather")) {
            values.godFatherRole=this;
            this.type="m";
            sendGodFatherRole();
            values.godFather=this;
            values.allMafia.add(values.godFather);
        }
        else if (type.equals("citizen")) {
            this.type="c";
            values.allCitizen.add(this);
        }

        else if (type.equals("mafia")) {
            this.type="m";
            values.allMafia.add(this);
        }
    }

    /**
     * gets an answer related to the player action from client
     */
    public void getRoleAction(){
       try {
           String action=codeIn.readUTF();
           runAction(action);
       }catch (IOException i){

       }
    }

    /**
     * do the proper action according to their role
     * @param action the answer of player
     */
    public void runAction(String action){
       try {
           if (this.equals(values.godFatherRole)){
               if (!findPlayer(action).equals(values.dieHard)|| values.dieHardGotShot){
                   values.killedPlayer.add(findPlayer(action));
               }else if (findPlayer(action).equals(values.dieHard)&& !values.dieHardGotShot){
                   values.dieHardGotShot=true;
               }
               values.godfatherAction=true;
           }
           else if (this.equals(values.lector)||this.equals(values.doctor)){
               findPlayer(action).codeOut.writeUTF("healed");
               if (!values.allPlayers.contains(findPlayer(action))){
                   backIn(findPlayer(action));
               }

           }else if (this.equals(values.detective)){
               if (values.allCitizen.contains(findPlayer(action))||findPlayer(action).equals(values.godFather))
                   sendDetectiveAnswer("no");
               else
                   sendDetectiveAnswer("yes");

           }else if (this.equals(values.dieHard)){
               if (action.equals("yes")&&values.dieHardAction<2)
               {
                   sendDieHardAnswer();
                   values.dieHardAction++;
               }

           }else if (this.equals(values.mayor)){
                if (action.equals("no")) {
                    knockOut(values.votedPlayer);
                }
                values.mayorAllowance=true;
           }else if (this.equals(values.psychologist)){
               System.out.println("psych:"+action);
               if (!action.equals("no")){
                   // action=codeIn.readUTF();
                   findPlayer(action).codeOut.writeUTF("shut_up");
               }
           }else if (this.equals(values.sniper)){
               if(!action.equals("no")){
                   //action=codeIn.readUTF();
                   if (cOrM(action).equals("c")){
                       //knockOut(this);
                       values.killedPlayer.add(this);
                   }else {
                       //knockOut(findPlayer(action));
                       values.killedPlayer.add(findPlayer(action));
                   }
               }

           }
       }catch (IOException i){

       }
       values.actionDone++;
       if (values.actionDone==7)
           values.allActions=true;
        System.out.println("----------------------"+values.actionDone);

    }

    /**
     * put the player back ine game if it was dead used when someone is healed
     * @param c the player
     */
    public void backIn(ClientHandler c){
//       c.sendState("asleep");
//       values.dead.remove(c);
//       values.allPlayers.add(c);
       values.killedPlayer.remove(c);
    }

    /**
     * send the knocked out player of the recent night for client
     */
    public void sendKnockedOut(){
       try {
           codeOut.writeUTF("knocked_out");
           for (ClientHandler c:values.killedPlayer){
               if (c!=null)
                   codeOut.writeUTF(c.name+" knocked out");
           }
           codeOut.writeUTF("done");

       }
       catch (IOException i){

       }

    }

    public void knockOut(ClientHandler c) {
       c.sendPlayerState("dead");
       values.dead.add(c);
       values.allPlayers.remove(c);
       if (cOrM(c.name).equals("c"))
           values.allCitizen.remove(c);
       else
           values.allMafia.remove(c);
    }
    public void sendDieHardAnswer(){
       try {
           codeOut.writeUTF("dieHardAnswer");
           if (values.dieHardAction<2) {
               if (values.dead.size()!=0){
                   for (ClientHandler c : values.dead) {
                       codeOut.writeUTF(c.type);
                   }
               }else
                   codeOut.writeUTF("no one is dead yet!");
               values.dieHardAction++;
           }else {
               codeOut.writeUTF("no chance to ask more!");
           }
           codeOut.writeUTF("done");
       }catch (IOException i){

       }
    }
    public void sendDetectiveAnswer(String answer){
       try {
           codeOut.writeUTF("detectiveAnswer");
           codeOut.writeUTF(answer);
       }catch (IOException i){

       }

    }
    public ClientHandler findPlayer(String name){
       for (int i=0;i<values.attend.size();i++){
           if (values.attend.get(i).name.equals(name))
               return values.attend.get(i);
       }
       return null;
    }

    /**
     * checks if player is mafia or citizen
     * @param name name of the player
     * @return c (citizen) or m (mafia)
     */
    public String cOrM(String name){
//       for (int i=0;i<values.allCitizen.size();i++){
//           if (values.allCitizen.get(i).name.equals(name))
//           {
//              return "c";
//           }
//       }
        if (findPlayer(name)==null)
            return "null";
       return findPlayer(name).type;
//        for (int i=0;i<values.allMafia.size();i++){
//            if (values.allMafia.get(i).name.equals(name))
//            {
//                return "m";
//            }
//        }
    }
    public void sendCOrM(String type){
        try {
            codeOut.writeUTF(type);
        }catch (IOException i){

        }
    }
    public void sendWinner(String winner){
       try {
           codeOut.writeUTF(winner+" wins");

       }catch (IOException i){

       }
    }
    @Override
    public void run() {

       try {
//           oOut=new ObjectOutputStream(socket.getOutputStream());
//           oOut.flush();
//           oIn=new ObjectInputStream(socket.getInputStream());
           codeIn=new DataInputStream(codeSocket.getInputStream());
           codeOut=new DataOutputStream(codeSocket.getOutputStream());
           System.out.println("connected");
           //String s=oIn.readUTF();

//           if (values.allPlayers.size()==1){
//               askPlayerNum();
//           }

           String code;
           while (true){
               code=codeIn.readUTF();
               if (code.equals("new_player")){
                   newPlayer();
                   Thread chat=new ChatHandler(chatSocket,this);
                   chat.start();
               }
               else if (code.equals("action")){
                   getRoleAction();
               }else if (code.equals("corm")){
                   String nameToCheck=codeIn.readUTF();
                  // System.out.println("corm");
                   sendCOrM(cOrM(nameToCheck));
                   //codeOut.writeUTF("null");
               }else if (code.equals("vote")){
                   getVot();
               }

              // sendState("night");

           }


       }
       catch (IOException i){

       }
    }
}
class ChatHandler extends Thread{
    Socket socket;
    ClientHandler c;
    public ChatHandler(Socket socket,ClientHandler c){
        this.socket=socket;
        this.c=c;
    }

    @Override
    public void run() {
        while (true){
            c.chatRoom();
        }
    }
}

