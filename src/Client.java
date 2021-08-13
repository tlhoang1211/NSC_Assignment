import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args){
        final InetAddress clientIP;
        final Socket clientSocket;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner sc = new Scanner(System.in);

        try {
            clientIP = InetAddress.getLocalHost();
            clientSocket = new Socket(clientIP,8088);
            System.out.println("Connection accepted " + clientSocket.getInetAddress() + " :"  + clientSocket.getPort());
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            RSA rsa = new RSA();

            Thread sender = new Thread(new Runnable() {
                String msg;
                @Override
                public void run() {
                    while(true){
                        try {
                            msg = rsa.encrypt(sc.nextLine()); //encrypted client's messages
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
                        msg = rsa.decrypt(in.readLine()); // decrypted server's messages
                        while(msg != null){
                            System.out.println("Server: " + msg);
                            msg = in.readLine();
                        }
                        System.out.println("Server out of service!");
                        out.close();
                        clientSocket.close();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            receiver.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
