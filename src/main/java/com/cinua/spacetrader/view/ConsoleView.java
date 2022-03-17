package com.cinua.spacetrader.view;
import com.glabs.tables.SimpleTable;
import java.util.Scanner;

public class ConsoleView implements GameView{

    @Override
    public void msgOut(String msg){
        System.out.println(msg);
    }

    static String input(String prompt){
        System.out.print(prompt);
        return new Scanner(System.in).nextLine();
    }

    @Override
    public String msgIn(String prompt){
        return input(prompt);
    }

    @Override
    public void tableOut(SimpleTable table) {
        System.out.println(table.toString());
    }
}
