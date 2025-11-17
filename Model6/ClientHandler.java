package basics.Model6;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Server server;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;

    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            send("Welcome to the server! Please register using: REGISTER <username>");
            send("Select a command : ");
            send("-> Register: R <username>");
            send("-> Unregister: UR");
            send("-> Send Private Message: PM <username> <message>");
            send("-> Create Group: CG <groupName>");
            send("-> Send Group Message: GM <groupName> <message>");
            send("-> List All Users: AU");
            send("-> List All Groups: AG");
            send("-> List Members of Group: GGM <groupName>");
            send("-> Exit: exit");

            String line;
            while((line = reader.readLine()) != null) {
                String[] parts = line.strip().split(" ",3);
                String command = parts[0].toLowerCase();

                switch (command) {
                    case "r":
                        handleRegister(parts);
                        break;
                    case "ur":
                        handleUnregisterUser(parts);
                        break;
                    case "pm":
                        handlePrivateMessage(parts);
                        break;
                    case "cg":
                        handleCreateGroup(parts);
                        break;
                    case "gm":
                        handleGroupMessage(parts);
                        break;
                    case "au":
                        handleGetAllUsers(parts);
                        break;
                    case "ag":
                        handlegetAllGroups(parts);
                        break;
                    case "ggm":
                        handleGetAllGroupMembers(parts);
                        break;
                    case "exit":
                        handleUnregisterUser(new String[]{"ur"});
                        close();
                        break;
                    default:
                        send("Unknown command: " + command);
                        break;
                }
            }
        } catch (IOException e) {
            if (username != null) server.unregisterUser(username);
        }
    }

    public void send(String message) {
        try {
            writer.write(message + "\r\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if(!clientSocket.isClosed()) clientSocket.close();
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleRegister(String[] parts) {
        if(username != null) {
            send("You are already registered as " + username);
            return;
        }
        if(parts.length < 2) {
            send("Wrong usage of REGISTER command.");
            return;
        }
        username = parts[1];
        if(server.registerUser(username, this)) send("You Resgistered successfully!");
        else send("Username already taken. Try another one.");
    }

    public void handleUnregisterUser(String[] parts) {
        if(parts.length != 1) {
            send("Wrong usage of UNREGISTER command.");
            return;
        }
        if(username == null) {
            send("You are not registered yet.");
            return;
        }
        server.unregisterUser(username);
        close();
    }

    public void handlePrivateMessage(String[] parts) {
        if(parts.length < 3) {
            send("Wrong usage of PM command.");
            return;
        }
        if(username == null) {
            send("You are not registered yet.");
            return;
        }

        if(server.sendPrivateMessage(username, parts[1], parts[2])) send("Message sent to " + parts[1]);
        else send("Target User " + parts[1] + " does not exist.");
    }

    public void handleCreateGroup(String[] parts) {
        if(parts.length < 3) {
            send("Wrong usage of CG command.");
            return;
        }
        if(username == null) {
            send("You are not registered yet.");
            return;
        }

        Set<String> members = new HashSet<>(Arrays.asList(parts[2].split(",")));
        if(members.size() < 2) {
            send("Group must have at least 2 members.");
            return;
        }
        members.add(username);

        if(server.createGroup(parts[1], members, this)) send("Group " + parts[1] + " created successfully!");
        else send("Group creation failed. Group name might be already taken or members are invalid.");
    }

    public void handleGroupMessage(String[] parts) {
        if(parts.length < 3) {
            send("Wrong usage of GM command.");
            return;
        }
        if(username == null) {
            send("You are not registered yet.");
            return;
        }

        if(server.sendGroupMesage(parts[1], username, parts[2])) send("Message sent to group " + parts[1]);
        else send("Target Group " + parts[1] + " does not exist.");
    }

    public void handleGetAllUsers(String[] parts) {
        if(parts.length != 1) {
            send("Wrong usage of GE command.");
            return;
        }
        if(username == null) {
            send("You are not registered yet.");
            return;
        }

        Set<String> users = server.getAllUsers();
        if(users.isEmpty()) {
            send("No registered users.");
            return;
        }
        send("Registered Users: ");
        users.forEach((user) -> send(user));
    }

    public void handlegetAllGroups(String[] parts) {
        if(parts.length != 1) {
            send("Wrong usage of GG command.");
            return;
        }
        if(username == null) {
            send("You are not registered yet.");
            return;
        }

        Set<String> groups = server.getAllGroups();
        if(groups.isEmpty()) {
            send("No registered groups.");
            return;
        }
        send("Registered Groups: ");
        groups.forEach((group) -> send(group));
    }

    public void handleGetAllGroupMembers(String[] parts) {
        if(parts.length < 2) {
            send("Wrong usage of GGM command.");
            return;
        }
        if(username == null) {
            send("You are not registered yet.");
            return;
        }

        Set<String> members = server.getGroupMembers(parts[1]);
        if(members == null) {
            send("Group does not exist.");
            return;
        }

        send("Group members: ");
        members.forEach((member) -> send(member));
    }

    
    
}
