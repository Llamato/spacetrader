package com.cinua.spacetrader.view;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class JavaFX extends Application{

    public static void main(String[] args){
        launch(args);

    }
    public void init() throws Exception{
        System.out.println("init() called successfully");
    }

    @Override
    public void stop() throws Exception{
        System.out.println("stop() called successfully");
    }

    public void start(Stage stage) throws Exception{
        System.out.println("Application has started");
        HBox layout = new HBox();
        Label label = new Label("Hello world!");
        Button button1 = new Button("I am a button");
        button1.setOnAction(new ButtonHandler("button 1 pressed"));
        Button button2 = new Button("I am a new button");
        button2.setOnAction(new ButtonHandler("button 2 pressed"));
        layout.getChildren().add(label);
        layout.getChildren().add(button1);
        layout.getChildren().add(button2);
        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();
    }

    private class ButtonHandler implements EventHandler<ActionEvent>{
        private String text;

        public ButtonHandler(String onPressedText){
            text = onPressedText;
        }

        @Override
        public void handle(ActionEvent actionEvent){
            System.out.println(text);
        }
    }
}
