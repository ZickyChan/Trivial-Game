/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author David
 */
import javax.swing.*;
import java.net.*;
import java.io.*;
import java.sql.*;

public class Server extends JFrame{
    int client=0;
    protected ServerSocket server;
    public Server() throws SQLException, Exception{
        setTitle("Server window");
        setSize(500,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(false);
        
        try {
            server = new ServerSocket(8000);
            
            while (true) {
                Socket socket = server.accept();
                client++;
                ClientHandler task = new ClientHandler(socket);
                task.start();
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    class ClientHandler extends Thread {
        private Socket socket;
        
        public ClientHandler(Socket socket){
            this.socket = socket;
        }
        
        public void run(){
            try{
            DataInputStream inpFromClient = new DataInputStream(socket.getInputStream());
            DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());

            String input, output;
            TriviaProtocol tp = new TriviaProtocol();
            String[] topic = tp.getTopic();
            for(int i=0;i<topic.length;i++){
                output = topic[i];
                outToClient.writeUTF(output);
            }
            String section= inpFromClient.readUTF();
            if(section.equalsIgnoreCase("bye")){
                outToClient.writeUTF("bye");
                client--;
                socket.close();
                inpFromClient.close();
                outToClient.close();
                if(client==0){
                    server.close();
                }
         }
            else{
                output = tp.processInput(null,section);
                outToClient.writeUTF(output);
                while ((input = inpFromClient.readUTF()) != null){   //until one of the hosts stops
                    if (input.equals("bye")){
                        outToClient.writeUTF("bye");
                        client--;
                        break;
                    }

                    else if(input.equals("change")){
                        section = inpFromClient.readUTF();
                        if(section.equals("bye")){
                            outToClient.writeUTF("bye");
                            client--;
                            break;
                        }
                        output = tp.processInput(null,section);
                        outToClient.writeUTF(output);
                    }
                    else if(input.equals("restart")){
                        section = inpFromClient.readUTF();
                        if(section.equals("bye")){
                            outToClient.writeUTF("bye");
                            client--;
                            break;
                        }
                        tp.setTotalRight(0);
                        output = tp.processInput(null,section);
                        outToClient.writeUTF(output);
                    }
                    else{
                    output = tp.processInput(input,section);
                    outToClient.writeUTF(output);
                    }
                }
                socket.close();
                inpFromClient.close();
                outToClient.close();
                if(client == 0){
                    server.close();
                }
            }
        }

        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        }
    }
    public static void main(String[] args) throws SQLException, Exception{
        new Server();  //new instance without instance
    }
}
