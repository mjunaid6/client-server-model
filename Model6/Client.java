package basics.Model6;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;

    public Client(String HOST, int PORT) {
        try {
            socket = new Socket(HOST, PORT);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        reader.start();
        writer.start();
    }

    Thread reader = new Thread() {
        public void run() {
            try {
                String line;
                while((line = input.readLine()) != null) {
                    System.out.println("[Server] : " + line);
                    System.out.print("> ");
                    System.out.flush();
                }
            } catch (IOException e) {
            
            }
        }
    };

    Thread writer = new Thread() {
        public void run() {
            try (Scanner sc = new Scanner(System.in)){
                while(sc.hasNextLine()) {
                    String line = sc.nextLine();
                    output.write(line + "\n");
                    output.flush();

                    if (line.equalsIgnoreCase("exit")) {
                        socket.close();  
                        break;           
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public static void main(String[] args) {
        // Scanner sc = new Scanner(System.in);

        // System.out.print("Enter server IP (e.g., localhost): ");
        // String HOST = sc.nextLine();

        // System.out.print("Enter server PORT (e.g., 9000): ");
        // int PORT = sc.nextInt();

        // sc.close();

        final String HOST = "localhost";
        final int PORT = 9000;

        Client client = new Client(HOST, PORT);
        client.run();
    }
}
