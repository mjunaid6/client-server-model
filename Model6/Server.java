package basics.Model6;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private int PORT = 9000;
    private ServerSocket serverSocket;
    private ExecutorService executorPool;
    private Map<String, ClientHandler> clients;
    private Map<String, Set<String>> groups;

    public Server() {
        try{
            serverSocket = new ServerSocket(PORT);
            serverSocket.setReuseAddress(true);
            executorPool = Executors.newCachedThreadPool();
            clients = new ConcurrentHashMap<>();
            groups = new ConcurrentHashMap<>();
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Server is listening on PORT : " + PORT);
    } 

    public void start() {
        while(true) {
            try{
                Socket client = serverSocket.accept();
                System.out.println("New User connnection established with : " + client.getRemoteSocketAddress());

                ClientHandler clientHandler = new ClientHandler(client,this);
                executorPool.submit(clientHandler);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public Set<String> getAllUsers() {
        return clients.keySet();
    }

    public boolean registerUser(String username, ClientHandler clientHandler) {
        return clients.putIfAbsent(username, clientHandler) == null;
    }

    public void unregisterUser(String username) {
        clients.remove(username);
        for(String group : groups.keySet()) {
            Set<String> members = groups.get(group);
            members.remove(username);
            if(members.size() < 2) {
                groups.remove(group);
            }
        }
    }

    public ClientHandler getClientHandler(String username) {
        return clients.get(username);
    }

    public boolean createGroup(String groupName, Set<String> members, ClientHandler admin) {
        if(groups.containsKey(groupName)) return false;

        Set<String> validMembers = new ConcurrentSkipListSet<>();
        members.forEach((user) -> {
            if(clients.containsKey(user)) validMembers.add(user);
            else admin.send(user + " doesn't exist");
        });

        if(validMembers.size() < 2) {
            admin.send("Group must have at least 2 valid members");
            return false;
        }

        groups.put(groupName, validMembers);

        validMembers.forEach((member) -> {
            ClientHandler target = clients.get(member);
            target.send("Group " + groupName + " has been created and you have been added.");
        });

        return true;
    }

    public Set<String> getAllGroups() {
        return groups.keySet();
    }

    public Set<String> getGroupMembers(String groupName) {
        return groups.get(groupName);
    }

    public boolean sendPrivateMessage(String from, String toUser, String message) {
        if(!clients.containsKey(toUser)) return false;

        ClientHandler target = clients.get(toUser);
        if(target == null) return false;

        target.send("Message from "+ from + " : " + message);
        return true;
    }

    public boolean sendGroupMesage(String groupName, String from, String message) {
        if(!groups.containsKey(groupName)) return false;
        message = "Group Message from " + from + " : " + message;
        for(String member : groups.get(groupName)) {
            if(!member.equals(from)) {
                ClientHandler target = clients.get(member);
                if(target != null) target.send(message);
            }
        }
        return true;
    }

}
