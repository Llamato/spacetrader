package com.cinua.spacetrader.view;

import com.cinua.spacetrader.gamedata.SingleplayerGame;
import com.cinua.spacetrader.database.DatabaseInterface;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;

public class Console{

    private static String input(String prompt){
        System.out.print(prompt);
        return new Scanner(System.in).nextLine();
    }

    private static String mainMenu(String[] menuItems){
        for(int index = 1; index <= menuItems.length; index++){
            System.out.println(index + ") " + menuItems[index-1]);
        }
        return input("Please choose: ");
    }

    private static void singleplayer(SingleplayerGame savegame){

    }

    private static void multiplayer(DatabaseInterface server){

    }

    public static void main(String[] args){
        final String[] menuItems = {"Connect to multiplayer game","New game", "Load game", "Delete game", "Exit"};
        boolean invalidMenuItemSelected = true;
        while(invalidMenuItemSelected){
            invalidMenuItemSelected = false;
            String selection = mainMenu(menuItems);
            try{
                int selectedIndex;
                try{
                    selectedIndex = Integer.parseInt(selection) -1;
                }catch(NumberFormatException e){
                    selectedIndex = Arrays.binarySearch(menuItems, selection);
                }
                switch(selectedIndex){
                    case 0:
                         multiplayer(DatabaseInterface.connect(input("Please enter the url of the server you would like to join "), DatabaseInterface.multiplayer));
                        break;
                    case 1:
                        String name = input("Please enter a name for the new game ");
                        SingleplayerGame.newGame(name);
                        singleplayer(SingleplayerGame.load(name));
                        break;
                    case 2:
                        Arrays.stream(SingleplayerGame.getSavegamesList()).forEach(savegame -> System.out.println(savegame.substring(savegame.lastIndexOf(File.separator)+1)));
                        singleplayer(SingleplayerGame.load(input("Please enter the name of the game you would like to load ")));
                        break;
                    case 3:
                        Arrays.stream(SingleplayerGame.getSavegamesList()).forEach(savegame -> System.out.println(savegame.substring(savegame.lastIndexOf(File.separator)+1)));
                        SingleplayerGame.delete(input("Please enter the name of the game you would like to delete "));
                        break;
                    case 4:
                        System.exit(0);
                        break;
                }
            }catch(IOException e){
                System.out.println("Could not create new game file.");
                e.printStackTrace();
            }catch(SQLException e){
                System.out.println("Could not load game file.");
                e.printStackTrace();
            }
        }
    }
}
