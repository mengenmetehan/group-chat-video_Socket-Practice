package com.mengen.groupchatvideo_SocketPractice.chatting;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mengen.groupchatvideo_SocketPractice.chatting.IOUtil.closeEverthing;

public class Client {

    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;

    private ExecutorService executorService;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e) {
            closeEverthing(socket, bufferedReader, bufferedWriter);
            System.out.println("Error: " + e.getMessage());
        }
        this.username = username;
    }

    private void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write("%s : %s".formatted(username, messageToSend));
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }
        catch (IOException e) {
            closeEverthing(socket, bufferedReader, bufferedWriter);
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void readMessage() {
        Executors.newSingleThreadExecutor().execute(() -> {
                String message;
                while (socket.isConnected()) {
                    try {
                        message = bufferedReader.readLine();
                        System.out.println(message);
                    }
                    catch (IOException e) {
                        closeEverthing(socket, bufferedReader, bufferedWriter);
                        System.out.println("Error: " + e.getMessage());
                    }
                }
        });
    }

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("172.31.80.1", 50551);
        Client client = new Client(socket, username);

        client.readMessage();
        client.sendMessage();
    }
}
