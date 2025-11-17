package basics.Model5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListMap;

public class Client {
    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;
    private final ConcurrentSkipListMap<Integer, String> messages;

    private final int SCREEN_HEIGHT = 20;

    public Client(String IP, int PORT) {
        try {
            socket = new Socket(IP, PORT);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        messages = new ConcurrentSkipListMap<>();
    }

    public void run() {
        reader.start();;
        writer.start();
    }

    Thread reader = new Thread() {
        public void run() {
            try{
                String line;
                while((line = input.readLine()) != null) {
                    if(line.startsWith("MSG#")) {
                        int del = line.indexOf(' ');
                        int id = Integer.parseInt(line.substring(4,del));
                        String msg = line.substring(del+1);

                        messages.put(id, msg);
                        trim();
                        redraw();
                    }
                    else if(line.startsWith("DELETE#")) {
                        int id = Integer.parseInt(line.substring(7));

                        messages.remove(id);
                        redraw();
                    }
                    else {
                        System.out.println("[Server] : " + line);
                        System.out.print("> ");
                        System.out.flush();
                    }
                }
            } catch (IOException ignored) {
                System.out.println("Disconnected from server.");
            }
        }
    };

    Thread writer = new Thread() {
        public void run() {
            try(Scanner sc = new Scanner(System.in)) {
                String line;
                while(sc.hasNextLine()) {
                    line = sc.nextLine();
                    output.write(line);
                    output.newLine();
                    output.flush();

                    if (line.equalsIgnoreCase("exit")) {
                        socket.close();  
                        break;           
                    }
                }
            } catch(IOException e) {
                System.out.println("Connection closed.");
            }
        }
    };

    private void trim() {
        while (messages.size() > SCREEN_HEIGHT){
            int lastId = messages.firstKey();
            messages.remove(lastId);
        }   
    }

    private void redraw() {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        System.out.println("\n\n");
        System.out.println("====== CHAT ======\n");

        for (String msg : new ArrayList<>(messages.values())) {
            System.out.println(msg);
        }

        System.out.print("\n> ");
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter server IP (e.g., localhost): ");
        String HOST = sc.nextLine();

        System.out.print("Enter server PORT (e.g., 9000): ");
        int PORT = sc.nextInt();

        sc.close();

        // final String HOST = "localhost";
        // final int PORT = 9000;

        Client client = new Client(HOST, PORT);
        client.run();
    }
}
