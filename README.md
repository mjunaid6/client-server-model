# 🚀 Client-Server Models

<div align="center">

**A collection of Java implementations demonstrating various client-server communication models.**

</div>

## 📖 Overview

This repository provides distinct Java projects, each designed to illustrate a particular client-server communication model. It serves as an educational resource to understand fundamental networking concepts, concurrency, and inter-process communication patterns in Java. Each `ModelX` directory contains a self-contained client and server implementation showcasing a specific approach to handling connections and data exchange.

## ✨ Features

This project demonstrates core principles of client-server architecture through multiple models, including (but not limited to, as inferred from directory names):

-   **Basic Client-Server Communication**: Simple message exchange between a single client and server.
-   **Concurrent Server Handling**: Servers capable of handling multiple client connections simultaneously.
-   **Variations in Communication Patterns**: Different approaches to data serialization, message protocols, or connection management.

## 🛠️ Tech Stack

**Runtime:**

![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white)

## 🚀 Quick Start

This section guides you through setting up and running each of the client-server models.

### Prerequisites
-   **Java Development Kit (JDK)**: Version 8 or higher is recommended to compile and run the Java applications.

### Installation

1.  **Clone the repository**
    ```bash
    git clone https://github.com/mjunaid6/client-server-model.git
    cd client-server-model
    ```

### Running the Models

Each model (`Model5`, `Model5b`, `Model6`) is a separate project. To run a specific model, you need to navigate into its directory, compile the Java source files, and then run the client and server applications, typically in separate terminal windows.

**General Steps to run any model (e.g., `ModelX`):**

1.  **Navigate to the model directory:**
    ```bash
    cd ModelX # Replace 'ModelX' with 'Model5', 'Model5b', or 'Model6'
    ```

2.  **Compile the Java source files:**
    ```bash
    javac *.java
    ```
    *This command compiles all `.java` files in the current directory into `.class` bytecode files.*

3.  **Run the Server (in one terminal):**
    ```bash
    java Server
    ```
    *The server will typically start and listen for incoming client connections.*

4.  **Run the Client (in a separate terminal):**
    ```bash
    cd ../ModelX # Go back to the model directory if you've opened a new terminal
    java Client
    ```
    *The client will attempt to connect to the server and initiate communication.*

**Example for `Model5`:**

```bash

# In your first terminal:
cd Model5
javac *.java
java Server

# In your second terminal:
cd Model5
java Client
```

Repeat these steps for `Model5b` and `Model6` to explore their respective implementations.

## 📁 Project Structure

```
client-server-model/
├── Model5/            # Implementation of Client-Server Model 5
│   ├── Client.java    # Client-side logic for Model 5
│   └── Server.java    # Server-side logic for Model 5
│   └── [...other .java files for Model 5]
├── Model5b/           # Implementation of Client-Server Model 5b
│   ├── Client.java    # Client-side logic for Model 5b
│   └── Server.java    # Server-side logic for Model 5b
│   └── [...other .java files for Model 5b]
├── Model6/            # Implementation of Client-Server Model 6
│   ├── Client.java    # Client-side logic for Model 6
│   └── Server.java    # Server-side logic for Model 6
│   └── [...other .java files for Model 6]
└── README.md          # This README file
```

## 🙏 Acknowledgments

-   Inspired by fundamental network programming concepts in Java.

## 📞 Support & Contact

-   🐛 Issues: [GitHub Issues](https://github.com/mjunaid6/client-server-model/issues)

---

<div align="center">

**⭐ Star this repo if you find it helpful for learning client-server models!**

Made with ❤️ by Mohammad Junaid

</div>

