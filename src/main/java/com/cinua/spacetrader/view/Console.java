package com.cinua.spacetrader.view;

import com.cinua.spacetrader.gamedata.MultiplayerGame;
import com.cinua.spacetrader.gamedata.Savegame;
import com.cinua.spacetrader.gamedata.SingleplayerGame;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import com.cinua.spacetrader.gameplay.gameLoop;

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
                Savegame game = null;
                switch (selectedIndex){
                    case 0 -> {
                        game = new MultiplayerGame(input("Please enter the url of the server you would like to join "));
                        String username = input("Username? ");
                        String password = input("Password? ");
                        game.login(username, password);
                        if (game.loggedIn()) {
                            System.out.println("Logged in"); //Debug!!!
                            game.load();
                        }else{
                            System.out.println("Username or password wrong.");
                            String response = input("Would you like to create an account with that name and password? [Yes/no] ").toLowerCase(Locale.ROOT);
                            if (response.equals("no")) {
                                System.exit(0);
                            } else if (response.equals("yes") || response.equals("y") || response.equals("")) {
                                game.create(username, password);
                            }
                        }
                    }
                    case 1 -> {
                        String name = input("Please enter a name for the new game ");
                        game = new SingleplayerGame(name);
                        game.create("", "");
                    }
                    case 2 -> {
                        Arrays.stream(SingleplayerGame.getSavegamesList()).forEach(savegame -> System.out.println(savegame.substring(savegame.lastIndexOf(File.separator) + 1)));
                        game = new SingleplayerGame(input("Please enter the name of the game you would like to load "));
                        game.login("", "");
                        game.load();
                    }
                    case 3 -> {
                        Arrays.stream(SingleplayerGame.getSavegamesList()).forEach(savegame -> System.out.println(savegame.substring(savegame.lastIndexOf(File.separator) + 1)));
                        SingleplayerGame.delete(input("Please enter the name of the game you would like to delete "));
                    }
                    case 4 -> System.exit(0);
                }
                gameLoop.loop(game);
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
