package com.mengen.groupchatvideo_SocketPractice.chatting;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static com.mengen.groupchatvideo_SocketPractice.chatting.IOUtil.closeEverthing;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private static final int SOCKET_TIMEOUT = 10000;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyy_HH-mm-ss");
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            //socket.setSoTimeout(SOCKET_TIMEOUT);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: %s  has entered the chat".formatted(clientUsername));
        }
        catch (IOException e) {
            closeEverthing(socket, bufferedReader, bufferedWriter);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        String clientMessage;
        while (socket.isConnected()) {
            try {
                clientMessage = bufferedReader.readLine();
                System.out.println("message :::" + clientMessage);
                broadcastMessage(clientMessage);
            }
            catch (IOException e) {
                closeEverthing(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    private void broadcastMessage(String clientMessage) {
        for (ClientHandler client : clientHandlers) {
            System.out.println(client.clientUsername);
            try {
                String date = LocalDateTime.now().format(FORMATTER);
                if (!client.clientUsername.equals(clientUsername)) {
                    client.bufferedWriter.write("%s : %s".formatted(date, clientMessage));
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
            }
            catch (IOException e) {
                removeClientHandler();
                closeEverthing(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    private void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("%s has left the chat!".formatted(clientUsername));
    }




}
