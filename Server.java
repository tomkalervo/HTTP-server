/**
 * Author: Tom Karlsson
 * Created: 06-03-2022
 * 
 * This application use javas ServerSocket class to 
 * run a webserver with HTTP.
 * 
 */

import java.net.*;
//import java.io.*;

public class Server {

    private static void server(int port_number){
        try(ServerSocket welcome_socket = new ServerSocket(port_number)){
            while(true){
                new Connection(welcome_socket.accept()).start();
                System.out.println("Starting thread");
            }

        } catch (Exception e){
            System.out.println("Exception when initializing server socket: " + e.toString());
        }
        
    }

    public static void main(String[] args){
        if (args.length > 0){
            try{
                // start server
                server(Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                System.out.println("Argument " + args[0] + " must be an integer.");
                System.out.println("Usage: Java ConcHTTPAsk <port number>");
                System.exit(1);
            }
        }

    }
}