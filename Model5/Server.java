package basics.Model5;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private int PORT = 9000;
    private ServerSocket serverSocket;
    private ExecutorService executorPool;
    private Map<String, ClientHandler> clients;
    public static final int DELETE_TIMER = 30;  
    private ScheduledExecutorService scheduler;
    private static int MESSAGE_ID = 1;
    private Map<Integer, String> messages;


    public Server() {
        try{
            serverSocket = new ServerSocket(PORT);
            serverSocket.setReuseAddress(true);
            executorPool = Executors.newCachedThreadPool();
            clients = new ConcurrentHashMap<>();
            messages = new ConcurrentHashMap<>();
            scheduler = Executors.newScheduledThreadPool(5);
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
    }

    public boolean sendMessage(String from, String toUser, String message) {
        if(!clients.containsKey(toUser)) return false;

        ClientHandler target = clients.get(toUser);
        if(target == null) return false;

        int msgId = MESSAGE_ID++;

        messages.put(msgId, message);
        target.send("MSG#" + msgId + " Private Message from " + from + ": " + message);

        scheduler.schedule(() -> {
            target.send("DELETE#" + msgId);
            messages.remove(msgId);
        }, DELETE_TIMER, TimeUnit.SECONDS);

        return true;
    }

    public boolean sendBroadcast(String from, String message) {
        Set<String> users = clients.keySet();
        if(users.size() < 2) return false;

        for (String user : users) {
            if (!user.equals(from)) {
                ClientHandler target = clients.get(user);
                int msgId = MESSAGE_ID++;
                String finalMessage = "MSG#" + msgId + " Broadcast Message from " + from + ": " + message;
                messages.put(msgId, finalMessage);

                target.send(finalMessage);

                scheduler.schedule(() -> {
                    target.send("DELETE#" + msgId);
                    messages.remove(msgId);
                }, DELETE_TIMER, TimeUnit.SECONDS);
            }
        }

        return true;
    }
}
