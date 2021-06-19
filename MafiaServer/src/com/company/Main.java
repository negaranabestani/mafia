package com.company;


import java.util.Scanner;

public class Main {
public static Values values=new Values();
    public static void main(String[] args) {
        System.out.println("number of players?");
        Scanner sc=new Scanner(System.in);
        values.playerNum=sc.nextInt();
        God m=new God();
        m.run();
    }
}
