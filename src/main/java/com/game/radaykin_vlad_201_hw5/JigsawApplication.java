package com.game.radaykin_vlad_201_hw5;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class JigsawApplication{

    public static void main(String[] args){
        try {
            String message = "Hello, my dear friends !!!";
            if (args.length > 0)
                message = args[0];

            int multicastPort = 5000;
            InetAddress group = InetAddress.getByName("224.0.0.1");
            MulticastSocket socket = new MulticastSocket(multicastPort);

            ReadThread rt = new ReadThread(group, multicastPort);
            rt.start();

            byte[] msg = message.getBytes();
            DatagramPacket packet = new DatagramPacket(msg, msg.length, group, multicastPort);

            System.out.print("Hit return to send message\n\n");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.readLine();
            socket.send(packet);

            socket.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}

class ReadThread extends Thread {
    public static final int MAX_MSG_LEN = 100;
    private final InetAddress group;
    private final int multicastPort;

    ReadThread(InetAddress g, int port){
        group = g;
        multicastPort = port;
    }

    public void run(){
        try {
            MulticastSocket readSocket = new MulticastSocket(multicastPort);
            readSocket.joinGroup(group);
            while (true){
                byte[] message = new byte[MAX_MSG_LEN];
                DatagramPacket packet = new DatagramPacket(message, message.length, group, multicastPort);
                readSocket.receive(packet);

                String msg = new String(packet.getData());
                System.out.println(msg);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}