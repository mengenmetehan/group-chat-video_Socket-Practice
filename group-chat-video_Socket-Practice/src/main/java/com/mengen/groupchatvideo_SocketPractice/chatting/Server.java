package com.mengen.groupchatvideo_SocketPractice.chatting;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private static final int SOCKET_TIMEOUT = 10000;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private final String CLIENT_MESSAGE = "A new client has connected !";

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() throws IOException {
        try {
            while (!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();
                System.out.println(CLIENT_MESSAGE);

                ClientHandler clientHandler = new ClientHandler(socket);

                executorService.execute(clientHandler);
            }
        }
        catch (IOException e) {
            serverSocket.close();
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(50551);
        Server server = new Server(serverSocket);
        System.out.println("server starts");
        server.startServer();
    }
}
