# Client-Server Model

A Java implementation of the classic client-server architecture demonstrating networking fundamentals in Java.

## 🧠 Problem It Solves

This project illustrates how two separate programs — a server and a client — can communicate over a network using TCP sockets. It’s useful for learning networking basics such as request/response, multi-threaded servers, and input/output streams in Java.

## 📦 Project Structure

client-server-model/
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ ├── server/ # Server application
│ │ │ │ └── Server.java
│ │ │ └── client/ # Client application
│ │ │ └── Client.java
├── .gitignore
├── README.md
└── pom.xml (if Maven used) / build.gradle (if Gradle used)

pgsql
Copy code

## 🛠️ How It Works

1. **Server** listens on a port.
2. **Client** connects and sends messages.
3. Server processes and replies.
4. You can extend it with multiple clients using threads.

## 🚀 Run

```bash
# Compile
javac server/Server.java
javac client/Client.java

# Run Server
java server.Server

# Run Client (in separate terminal)
java client.Client
