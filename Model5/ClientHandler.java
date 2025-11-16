package basics.Model5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Set;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {

        send("Welcome! Register: REG <username>");
        send("Commands:");
        send("Register: REG <username>");
        send("Message: MSG <user> <msg>");
        send("Broadcast Message: BM <msg>");
        send("Get all users: AU");
        send("Unregister: UR");
        send("Exit: EXIT");

        try {
            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(" ", 3);
                String command = parts[0].toLowerCase();

                switch (command) {
                    case "reg":
                        handleRegister(parts);
                        break;
                    case "msg":
                        handlePrivateMsg(parts);
                        break;
                    case "au":
                        handleListUsers();
                        break;
                    case "bm":
                        handleBroadcastMsg(line.substring(3));
                        break;
                    case "ur":
                        handleUnregister();
                        break;
                    case "exit":{
                        handleUnregister();
                        close();
                        return;}
                    default:
                        send("Unknown command!");
                }
            }
        } catch (Exception e) {
            if (username != null) server.unregisterUser(username);
        }
    }

    public void send(String msg) {
        try {
            writer.write(msg + "\r\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRegister(String[] parts) {
        if (parts.length < 2) {
            send("Wrong Usage: REG <username>");
            return;
        }

        if (server.registerUser(parts[1], this)) {
            username = parts[1];
            send("Registered as " + username);
        } else {
            send("Username exists");
        }
    }

    private void handleUnregister() {
        if (username != null) {
            server.unregisterUser(username);
            send("Unregistered " + username);
            username = null;
        }
        else{
            send("You are not registered.");
        }
    }

    private void handlePrivateMsg(String[] parts) {
        if (parts.length < 3) {
            send("Wrong Usage of MSG <user> <message>");
            return;
        }
        if (username == null) {
            send("Register first!");
            return;
        }

        if (server.sendMessage(username, parts[1], parts[2])) {
            send("Message sent to " + parts[1]);
        } else {
            send("User not found");
        }
    }

    private void handleBroadcastMsg(String msg) {
        if (username == null) {
            send("Register first!");
            return;
        }

        if(server.sendBroadcast(username,msg)) send("Broadcast message sent.");
        else send("No users online to send broadcast.");
    }

    private void handleListUsers() {
        Set<String> users = server.getAllUsers();
        send("Users:");
        users.forEach(user -> send(user));
    }

    public void close() {
        try {
            if(!socket.isClosed()) socket.close();
            reader.close();
            writer.close();
        } catch (Exception ignored) {}
    }
}
