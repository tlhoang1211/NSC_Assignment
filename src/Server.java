import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Server {
    public static void main(String[] args){
        final ServerSocket serverSocket ;
        final Socket clientSocket ;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner sc = new Scanner(System.in);

        try {
            serverSocket = new ServerSocket(8088);
            clientSocket = serverSocket.accept();
            System.out.println("Created 1 connection to " + clientSocket.getInetAddress());
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader (new InputStreamReader(clientSocket.getInputStream()));
            RSA rsa = new RSA();

            Thread sender = new Thread(new Runnable() {
                String msg;
                @Override
                public void run() {
                    while(true){
                        try {
                            msg = rsa.encrypt(sc.nextLine()); // encrypted server's messages
                            out.println(msg);
                            out.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            sender.start();

            Thread receiver = new Thread(new Runnable() {
                String msg;
                @Override
                public void run() {
                    try {
                        msg = in.readLine();

                        while(msg!=null){
                            System.out.println(clientSocket.getInetAddress().getHostAddress() + " said: " + msg); // decrypted client's messages
                            msg = in.readLine();
                        }

                        System.out.println("Client disconnect!");

                        out.close();
                        clientSocket.close();
                        serverSocket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            receiver.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

