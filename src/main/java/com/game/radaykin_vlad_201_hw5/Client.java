package com.game.radaykin_vlad_201_hw5;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class Client extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(JigsawApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 700);
        stage.setTitle("Jigsaw");
        stage.setScene(scene);
        stage.show();
        JigsawController controller = fxmlLoader.getController();
        stage.setOnCloseRequest(controller.getResultEventHandler());
    }

    public static void main(String[] args) {launch();}

//    public static void main(String[] args) throws IOException {
//        String serverHost = "localHost";
//        int serverPort = 3456;
//
//        if (args.length > 1) {
//            serverHost = args[0];
//            serverPort = Integer.parseInt(args[1]);
//        }
//
//        Socket socket = null;
//        PrintWriter out = null;
//        BufferedReader in = null;
//
//        try {
//            System.out.println("Client: started...");
//            socket = new Socket(serverHost, serverPort);
//            System.out.println("Client: socket created...");
//
//            out = new PrintWriter(socket.getOutputStream(), true);
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//        } catch (UnknownHostException e) {
//            System.err.println("Don't know about host: " + serverHost);
//            System.exit(1);
//        } catch (IOException e) {
//            System.err.println("Couldn't get I/O for " + "the connection to: " + serverHost);
//            System.exit(1);
//        }
//
//        //TODO: note, that the following is fixing the issue mentioned above....
////        System.out.println("Ready to work: " + in.readLine());
//
//        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
//        String userInput;
//
//        while ((userInput = stdIn.readLine()) != null) {
//            if (userInput.equalsIgnoreCase("bye"))
//                break;
//            out.println(userInput);
//            System.out.println("echo: " + in.readLine());
//        }
//
//        out.close();
//        in.close();
//        stdIn.close();
//        socket.close();
//    }
}
